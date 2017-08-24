package com.luckyaf.netwatch.download;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.luckyaf.netwatch.callBack.DownloadCallBack;
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
    private static String fileSuffix = ".tmpl";
    private static String defPath = "";
    private Handler handler;
    private  boolean isCancel = false;
    private String key;

    public DownloadManager(DownloadCallBack callBack) {
        this.callBack = callBack;
        handler = new Handler(Looper.getMainLooper());
    }

    private static DownloadManager instance;


    /**
     * DownLoadManager getInstance
     */
    public static synchronized DownloadManager getInstance(DownloadCallBack callBack) {
        if (instance == null) {
            instance = new DownloadManager(callBack);
        }
        return instance;
    }

    public void setCancel(Boolean isCancel){
        this.isCancel = isCancel;
    }

    public boolean writeResponseBodyToDisk(final String key, String path, String name, Context context, ResponseBody body) {

        if (body == null) {
            Logger.e(TAG,  key + " : ResponseBody is null");
            finalOnError(new NullPointerException("the "+ key + " ResponseBody is null"));
            return false;
        }
        Logger.v(TAG,  "Key:-->" + key);

        String type ="";
        if (body.contentType() != null) {
            type = body.contentType().toString();
        } else {
            Logger.d(TAG, "MediaType-->,无法获取");
        }

        if (!TextUtils.isEmpty(type)) {
            Logger.d(TAG, "contentType:>>>>" + body.contentType().toString());
            if (!TextUtils.isEmpty(MimeType.getInstance().getSuffix(type))){
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
            File filepath = new File(path = context.getExternalFilesDir(null) + File.separator +"DownLoads");
            if (!filepath.exists()){
                filepath.mkdirs();
            }
            path = context.getExternalFilesDir(null) + File.separator +"DownLoads" + File.separator;
        }

        if (new File(path + name).exists()) {
            FileUtil.deleteFile(path);
        }
        Logger.d(TAG, "path:-->" + path);
        Logger.d(TAG, "name:->" + name);
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
                    Logger.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                    int  progress;
                    if (fileSize == -1 || fileSize ==  0) {
                        progress = 100;
                    } else {
                        progress = (int) (fileSizeDownloaded * 100 / fileSize);
                    }

                    Logger.d(TAG, "file download progress : " + progress);
                    if (updateCount == 0 || progress >= updateCount) {
                        updateCount += 1;
                        if (callBack != null) {
                            handler = new Handler(Looper.getMainLooper());
                            final long finalFileSizeDownloaded = fileSizeDownloaded;
                            final int finalProgress = progress;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.onProgress(key, finalProgress, finalFileSizeDownloaded, fileSize);
                                }
                            });
                        }
                    }
                }

                outputStream.flush();
                Logger.d(TAG, "file downloaded: " + fileSizeDownloaded + " of " + fileSize);
                if (callBack != null) {
                    final String finalName = name;
                    final String finalPath = path;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(key, finalPath, finalName, fileSize);
                        }
                    });
                    Logger.d(TAG, "file downloaded: " + fileSizeDownloaded + " of " + fileSize);
                    Logger.d(TAG, "file downloaded: is sucess");
                }
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
        if (callBack == null || isCancel) {
            return;
        }

        if (CommonUtils.checkMain()) {
            callBack.onError(NetException.handleException(e));
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callBack.onError(NetException.handleException(e));
                }
            });
        }
    }
}
