package com.luckyaf.okdownload;

import android.os.SystemClock;

import com.luckyaf.okdownload.constant.HttpConstant;
import com.luckyaf.okdownload.constant.StatusConstant;
import com.luckyaf.okdownload.exception.FileChangedException;
import com.luckyaf.okdownload.utils.BandWidthLimiter;
import com.luckyaf.okdownload.utils.FileUtil;
import com.luckyaf.okdownload.utils.IOUtil;
import com.luckyaf.okdownload.utils.LimitInputStream;
import com.orhanobut.logger.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/22
 */
public class DownloadTask {

    private int limitSpeed;      // 最高速度限制  kb/s
    private int notifyInterval;  // 通知间隔     ms

    private Disposable disposable;
    private DownloadRequest downloadRequest;
    private PublishSubject<Progress> publishSubject;


    static DownloadTask createTask(DownloadRequest downloadRequest) {
        return new DownloadTask(downloadRequest);
    }


    private DownloadTask(DownloadRequest downloadRequest){
        this.downloadRequest = downloadRequest;
        publishSubject = PublishSubject.create();
        limitSpeed = Integer.MAX_VALUE;
        notifyInterval = 300;
    }

    public DownloadTask setMaxSpeed(int speed){
        if(speed > 0){
            this.limitSpeed = speed;
        }
        return this;
    }
    public DownloadTask setNotifyInterval(int interval){
        if(interval > 0){
            this.notifyInterval = interval;
        }
        return this;
    }

    public DownloadTask start(){
        Logger.i("Observable start");

        if(null == disposable){
            Logger.i("Observable.create");
            disposable = Observable.create(new DownloadSubscribe())
                    .subscribeOn(Schedulers.io())//在子线程执行
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())//在主线程回调
                    .subscribe(new Consumer<Progress>() {
                        @Override
                        public void accept(Progress progress) throws Exception {
                            Logger.d("publishSubject onNext",progress);

                            publishSubject.onNext(progress);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Logger.d("publishSubject onError",throwable);

                            publishSubject.onError(throwable);
                        }
                    }, new Action() {
                        @Override
                        public void run() throws Exception {
                            Logger.d("publishSubject onComplete");

                            publishSubject.onComplete();
                        }
                    });

        }
        return this;
    }

    public void pause(){
        if(null != disposable && !disposable.isDisposed()){
            disposable.dispose();
            disposable = null;
        }
    }

    public Observable<Progress> listenProgress(){
        Logger.i("listenProgress");

        return publishSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io());
    }



    public void remove(){
        remove(false);
    }

    public void remove(boolean deleteFile){
        pause();
        if(deleteFile){
            FileUtil.delFileOrFolder(downloadRequest.getFileDir(), downloadRequest.getFileName());
        }
        DownloadManager.getInstance().delete(downloadRequest.getTag());
    }


    class DownloadSubscribe implements ObservableOnSubscribe<Progress>{

        public DownloadSubscribe(){
            speedBuffer = new ArrayList<>(12);
        }

        List<Long> speedBuffer;       //网速做平滑的缓存，避免抖动过快

        @Override
        public void subscribe(ObservableEmitter<Progress> emitter) {
            try {
                Logger.d("downloadRequest.getStatus() = " + downloadRequest.getStatus());
                if (StatusConstant.FINISHED == downloadRequest.getStatus()) {

                   throw(new Exception("FINISHED"));
                } else if (StatusConstant.ERROR == downloadRequest.getStatus()) { throw(new Exception("error"));

                }
                long downloadLength = downloadRequest.getCurrentSize();//已经下载好的长度
                long totalLength = downloadRequest.getTotalSize();//文件的总长度
                long lastTime = SystemClock.elapsedRealtime();
                long lastNotifySize = 0;
                long lastNotifyTime = lastTime;
                long nowTime = SystemClock.elapsedRealtime();
                long speed;
                float fraction;

                // ready go
                downloadRequest.setStatus(StatusConstant.LOADING);
                updateRequest(downloadRequest);

                Request.Builder builder = new Request.Builder()
                        .url(downloadRequest.getUrl());

                if (downloadRequest.isSupportRange()) {
                    //确定下载的范围,添加此头,则服务器就可以跳过已经下载好的部分
                    builder.addHeader(HttpConstant.HEAD_KEY_RANGE, "bytes=" + downloadLength + "-" + totalLength);
                    if (null != downloadRequest.getLastModify()) {
                        builder.addHeader(HttpConstant.HEAD_KEY_IF_RANGE, downloadRequest.getLastModify());
                    }
                }

                Request request = builder.build();
                Call call = OkDownload.getInstance().getOkHttpClient().newCall(request);
                Response response = null;

                response = call.execute();


                if (downloadRequest.getCurrentSize() > 0) {
                    Logger.d("response.code() " + response.code());
                    if (null == response || response.code() != 206) {
                        downloadRequest.setStatus(StatusConstant.ERROR);
                        throw(new FileChangedException());
                    }
                }

                File filePath = new File(downloadRequest.getFileDir());
                try {
                    if (!filePath.exists()) {
                        filePath.mkdirs();
                    }
                }catch (Exception e){
                    throw(e);
                }
                File file = new File(downloadRequest.getFileDir(), downloadRequest.getFileName());

                //start downloading
                RandomAccessFile randomAccessFile = null;
                Logger.d("file", " exist " + file.exists());
                try {
                    randomAccessFile = new RandomAccessFile(file, "rw");
                    randomAccessFile.seek(downloadRequest.getCurrentSize());
                } catch (Exception e) {
                    throw(e);
                }
                LimitInputStream inputStream = null;
                InputStream responseStream = response.body().byteStream();
                inputStream = new LimitInputStream(responseStream, new BandWidthLimiter(limitSpeed));
                BufferedInputStream in = new BufferedInputStream(inputStream, 8 * 1024);
                byte[] buffer = new byte[1024 * 8];//缓冲数组8kB
                try {

                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        nowTime = SystemClock.elapsedRealtime();
                        randomAccessFile.write(buffer, 0, len);
                        downloadLength += len;
                        downloadRequest.setCurrentSize(downloadLength);
                        if (nowTime - lastNotifyTime >= notifyInterval) {
                            speed = bufferSpeed((downloadRequest.getCurrentSize() - lastNotifySize) * 1000 / (nowTime - lastNotifyTime));
                            fraction = (float) downloadRequest.getCurrentSize() * 1.0f / (downloadRequest.getTotalSize() * 1.0f);
                            updateRequest(downloadRequest);
                            emitter.onNext(new Progress(speed, fraction, downloadRequest.getCurrentSize(), downloadRequest.getTotalSize()));
                            lastNotifyTime = nowTime;
                            lastNotifySize = downloadRequest.getCurrentSize();
                        }
                    }
                    in.close();

                    downloadRequest.setStatus(StatusConstant.FINISHED);
                    speed = bufferSpeed((downloadRequest.getCurrentSize() - lastNotifySize) * 1000 / (nowTime - lastNotifyTime));
                    fraction = (float) downloadRequest.getCurrentSize() * 1.0f / (downloadRequest.getTotalSize() * 1.0f);
                    updateRequest(downloadRequest);
                    Logger.d("DownloadSubscribe", "onNext");
                    emitter.onNext(new Progress(speed, fraction, downloadRequest.getCurrentSize(), downloadRequest.getTotalSize()));

                } catch (Exception e) {
                    //emitter.onError(e);
                } finally {
                    //关闭IO流
                    IOUtil.closeQuietly(randomAccessFile);
                    IOUtil.closeQuietly(in);
                    IOUtil.closeQuietly(inputStream);
                    DownloadManager.getInstance().removeTask(downloadRequest.getTag());
                }
                emitter.onComplete();//完成
            }catch (Exception e){
                emitter.onError(e);
            }

        }

        /** 平滑网速，避免抖动过大 */
        private long bufferSpeed(long speed) {
            speedBuffer.add(speed);
            if (speedBuffer.size() > 10) {
                speedBuffer.remove(0);
            }
            long sum = 0;
            for (float speedTemp : speedBuffer) {
                sum += speedTemp;
            }
            return sum / speedBuffer.size();
        }
    }


    private void updateRequest(DownloadRequest request){
        DownloadManager.getInstance().replace(request);
    }



}
