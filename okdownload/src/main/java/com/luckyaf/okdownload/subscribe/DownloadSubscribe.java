package com.luckyaf.okdownload.subscribe;

import com.luckyaf.okdownload.DownloadManager;
import com.luckyaf.okdownload.DownloadRequest;
import com.luckyaf.okdownload.OkDownload;
import com.luckyaf.okdownload.Progress;
import com.luckyaf.okdownload.callback.ReadCallback;
import com.luckyaf.okdownload.constant.HttpConstant;
import com.luckyaf.okdownload.constant.StatusConstant;
import com.luckyaf.okdownload.exception.FileChangedException;
import com.luckyaf.okdownload.speedContral.NewResponseBody;
import com.luckyaf.okdownload.utils.BandWidthLimiter;
import com.luckyaf.okdownload.utils.IOUtil;
import com.luckyaf.okdownload.utils.LimitInputStream;
import com.luckyaf.okdownload.utils.RxUtils;
import com.orhanobut.logger.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/24
 */
public class DownloadSubscribe implements ObservableOnSubscribe<Progress> {

    private DownloadRequest downloadRequest;
    private int speedLimit;
    private int notifyInterval;
    private Disposable disposable;
    private ObservableEmitter<Progress> emitter;
    private long currentSize = 0;
    private long tempReadSize;
    private List<Long> speedBuffer =  new ArrayList<>(4);


    public DownloadSubscribe(DownloadRequest downloadRequest) {
        this.downloadRequest = downloadRequest;
        speedLimit = Integer.MAX_VALUE;
        notifyInterval = 300;
        tempReadSize = 0;
        currentSize = downloadRequest.getCurrentSize();
    }

    public DownloadSubscribe setSpeedLimit(int speed) {
        speedLimit = speed;
        return this;
    }

    public DownloadSubscribe setNotifyInterval(int interval) {
        notifyInterval = interval;
        return this;
    }

    @Override
    public void subscribe(ObservableEmitter<Progress> emitter) {
        this.emitter = emitter;
        disposable = RxUtils.loopDoing(notifyInterval, TimeUnit.MILLISECONDS, new RxUtils.Listener() {
            @Override
            public void doSomeThing() {
                sendProgress();
            }
        });
        try {
            if (StatusConstant.FINISHED == downloadRequest.getStatus()) {
                throw (new Exception("FINISHED"));
            } else if (StatusConstant.ERROR == downloadRequest.getStatus()) {
                throw (new Exception("error"));
            }

            // ready go
            downloadRequest.setStatus(StatusConstant.LOADING);
            updateRequest(downloadRequest);

            Request.Builder builder = new Request.Builder()
                    .url(downloadRequest.getUrl());

            if (downloadRequest.isSupportRange()) {
                //确定下载的范围,添加此头,则服务器就可以跳过已经下载好的部分
                builder.addHeader(HttpConstant.HEAD_KEY_RANGE, "bytes=" + downloadRequest.getCurrentSize() + "-" + downloadRequest.getTotalSize());
                if (null != downloadRequest.getLastModify()) {
                    builder.addHeader(HttpConstant.HEAD_KEY_IF_RANGE, downloadRequest.getLastModify());
                }
            }

            Request request = builder.build();
            Call call = OkDownload.getInstance().getOkHttpClient().newCall(request);
            Response response  = call.execute();

            if (downloadRequest.getCurrentSize() > 0) {
                if (null == response || response.code() != 206) {
                    downloadRequest.setStatus(StatusConstant.ERROR);
                    throw (new FileChangedException());
                }
            }

            ResponseBody body = response.body();
            ResponseBody responseBody = NewResponseBody.upgrade(body,speedLimit, new ReadCallback() {
                @Override
                public void call(int size) {
                    tempReadSize += size;
                }
            });

            File filePath = new File(downloadRequest.getFileDir());
            try {
                if (!filePath.exists()) {
                    filePath.mkdirs();
                }
            } catch (Exception e) {
                throw (e);
            }
            File file = new File(downloadRequest.getFileDir(), downloadRequest.getFileName());

            if(file.length() < downloadRequest.getCurrentSize()){
                throw new FileChangedException();
            }

            //start downloading
            RandomAccessFile randomAccessFile;
            try {
                randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(downloadRequest.getCurrentSize());
            } catch (Exception e) {
                throw (e);
            }

            InputStream responseStream = responseBody.byteStream();
            BufferedInputStream in = new BufferedInputStream(responseStream, 8 * 1024);
            byte[] buffer = new byte[1024 * 8];//缓冲数组8kB
            try {
                int len;
                while ((len = in.read(buffer)) != -1) {
                    randomAccessFile.write(buffer, 0, len);
                    currentSize += len;
                    downloadRequest.setCurrentSize(currentSize);
                }
                in.close();
                downloadRequest.setStatus(StatusConstant.FINISHED);
                sendProgress();
            } catch (Exception e) {
                //emitter.onError(e);
            } finally {
                //关闭IO流
                IOUtil.closeQuietly(randomAccessFile);
                IOUtil.closeQuietly(in);
                IOUtil.closeQuietly(responseStream);
                DownloadManager.getInstance().removeTask(downloadRequest.getTag());
            }
            if(null != disposable && !disposable.isDisposed()){
                disposable.dispose();
            }
            emitter.onComplete();//完成
        } catch (Exception e) {
            onError(e);
        }

    }

    private void sendProgress() {
        long speed = tempReadSize * 1000 / notifyInterval;
        speed = bufferSpeed(speed);
        tempReadSize = 0;
        float fraction = downloadRequest.getCurrentSize() / (downloadRequest.getTotalSize() * 1.0f);
        onProgress(new Progress(speed,fraction,downloadRequest.getCurrentSize(),downloadRequest.getTotalSize()));
    }

    /** 平滑网速，避免抖动过大 */
    private long bufferSpeed(long speed) {
        speedBuffer.add(speed);
        if (speedBuffer.size() > 3) {
            speedBuffer.remove(0);
        }
        long sum = 0;
        for (float speedTemp : speedBuffer) {
            sum += speedTemp;
        }
        return sum / speedBuffer.size();
    }




    private void onProgress(Progress progress) {
        if (null != this.emitter) {
            updateRequest(downloadRequest);
            this.emitter.onNext(progress);
        }

    }

    private void onError(Throwable throwable) {
        if (null != this.emitter) {
            this.emitter.onError(throwable);
        }
        if(null != disposable && !disposable.isDisposed()){
            disposable.dispose();
        }
    }


    private void updateRequest(DownloadRequest request) {
        DownloadManager.getInstance().replace(request);
    }

}
