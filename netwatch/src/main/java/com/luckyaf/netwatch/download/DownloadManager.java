package com.luckyaf.netwatch.download;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.luckyaf.netwatch.callBack.DownloadCallBack;
import com.luckyaf.netwatch.callBack.DownloadSuccessCallback;
import com.luckyaf.netwatch.callBack.ErrorCallBack;
import com.luckyaf.netwatch.callBack.ProgressCallBack;
import com.luckyaf.netwatch.exception.NetException;
import com.luckyaf.netwatch.utils.CommonUtils;
import com.luckyaf.netwatch.utils.FileUtil;
import com.luckyaf.netwatch.utils.Logger;
import com.luckyaf.netwatch.utils.MimeType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/16
 */
@SuppressWarnings("unused")
public class DownloadManager {
    public static final String TAG = "NetWatch:DownLoadManager";
    private DownloadCallBack callBack;
    private DownloadSuccessCallback mSuccessCallback;
    private ErrorCallBack mErrorCallBack;
    private ProgressCallBack mProgressCallBack;
    private static String fileSuffix = ".tmpl";
    private static String defPath = "";
    private Handler handler;
    private boolean isCancel = false;
    private String key;
    private long previousTime;
    private long nowTime;
    private long intervalTime;

    public DownloadManager() {
        handler = new Handler(Looper.getMainLooper());
    }

    public DownloadManager downloadCallBack(DownloadCallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    public DownloadManager successCallBack(DownloadSuccessCallback successCallback) {
        this.mSuccessCallback = successCallback;
        return this;
    }

    public DownloadManager progressCallBack(ProgressCallBack progressCallBack) {
        this.mProgressCallBack = progressCallBack;
        return this;
    }

    public DownloadManager errorCallBack(ErrorCallBack errorCallBack) {
        this.mErrorCallBack = errorCallBack;
        return this;
    }


    private static DownloadManager instance;


    /**
     * DownLoadManager getInstance
     */
    public static synchronized DownloadManager getInstance() {
        if (instance == null) {
            instance = new DownloadManager();
        }
        return instance;
    }

    private void onError(final Throwable e) {
        if (callBack == null && mErrorCallBack == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onError(NetException.handleException(e));
                }
                if (mErrorCallBack != null) {
                    mErrorCallBack.onError(NetException.handleException(e));
                }
            }
        });
    }

    public void onProgress(final String key, final int progress, final long speed, final long downloadedSize, final long totalSize) {
        if (callBack == null && mProgressCallBack == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onProgress(key, progress, speed, downloadedSize, totalSize);
                }
                if (mProgressCallBack != null) {
                    mProgressCallBack.onProgress(progress, speed, downloadedSize, totalSize);
                }
            }
        });
    }

    public void onSuccess(final String key, final String path, final String name, final long fileSize) {
        Logger.d(TAG, "handler sucess");

        if (callBack == null && mSuccessCallback == null) {
            return;
        }
        Logger.d(TAG, "handler sucess");
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onSuccess(key, path, name, fileSize);
                }
                if (mSuccessCallback != null) {
                    mSuccessCallback.onSuccess(key, path, name, fileSize);
                }
            }
        });

    }


    public void setCancel(Boolean isCancel) {
        this.isCancel = isCancel;
    }

    public boolean writeResponseBodyToDisk(final String key, String path, String name, Context context, ResponseBody body) {

        if (body == null) {
            Logger.e(TAG, key + " : ResponseBody is null");
            finalOnError(new NullPointerException("the " + key + " ResponseBody is null"));
            return false;
        }
        Logger.v(TAG, "Key:-->" + key);

        String type = "";
        if (body.contentType() != null) {
            type = body.contentType().toString();
        } else {
            Logger.d(TAG, "MediaType-->,无法获取");
        }

        if (!TextUtils.isEmpty(type)) {
            Logger.d(TAG, "contentType:>>>>" + body.contentType().toString());
            if (!TextUtils.isEmpty(MimeType.getInstance().getSuffix(type))) {
                fileSuffix = MimeType.getInstance().getSuffix(type);
            }
        }

        if (!TextUtils.isEmpty(name)) {
            if (!name.contains(".")) {
                name = name + fileSuffix;
            }
        }
        // FIx bug:filepath error,    by username @NBInfo  with gitHub
        if (path == null) {
            File filepath = new File(path = context.getExternalFilesDir(null) + File.separator + "DownLoads");
            if (!filepath.exists()) {
                filepath.mkdirs();
            }
            path = context.getExternalFilesDir(null) + File.separator + "DownLoads" + File.separator;
        }

        if (new File(path + name).exists()) {
            FileUtil.deleteFile(path);
        }
        Logger.d(TAG, "path:-->" + path);
        Logger.d(TAG, "name:->" + name);
        previousTime = System.currentTimeMillis();
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(path + name);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];

                final long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                int updateCount = 0;
                Logger.d(TAG, "file length: " + fileSize);
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    int progress;
                    if (fileSize == -1 || fileSize == 0) {
                        progress = 100;
                    } else {
                        progress = (int) (fileSizeDownloaded * 100 / fileSize);
                    }


                    if (updateCount == 0 || progress >= updateCount) {
                        updateCount += 1;
                        if (callBack != null || mProgressCallBack != null) {
                            handler = new Handler(Looper.getMainLooper());

                            nowTime = System.currentTimeMillis();
                            intervalTime = (nowTime - previousTime) / 1000;
                            previousTime = nowTime;
                            if (intervalTime == 0) {
                                intervalTime += 1;
                            }
                            final long networkSpeed = read / intervalTime;
                            onProgress(key, progress, networkSpeed, fileSizeDownloaded, fileSize);

                        }
                    }
                }

                outputStream.flush();
                Logger.d(TAG, "file downloaded: " + fileSizeDownloaded + " of " + fileSize);
                Logger.d(TAG,"callBack != null" + callBack != null + "!");
                Logger.d(TAG,"mSuccessCallback != null" + mSuccessCallback != null+ "!");

                Logger.d(TAG,"onSuccess");
                onSuccess(key, path, name, fileSize);
                Logger.d(TAG, "file downloaded: " + fileSizeDownloaded + " of " + fileSize);
                Logger.d(TAG, "file downloaded: is sucess");

                return true;
            } catch (IOException e) {
                finalOnError(e);
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            finalOnError(e);
            return false;
        }
    }


    private void finalOnError(final Exception e) {
        if ((callBack == null && mErrorCallBack == null )|| isCancel) {
            return;
        }
        onError(NetException.handleException(e));
    }
}
