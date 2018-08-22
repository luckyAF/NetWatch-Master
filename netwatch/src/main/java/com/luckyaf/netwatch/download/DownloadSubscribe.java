package com.luckyaf.netwatch.download;

import android.os.SystemClock;

import com.luckyaf.netwatch.constant.ProgressConstant;
import com.luckyaf.netwatch.exception.FileChangedException;
import com.luckyaf.netwatch.model.Progress;
import com.luckyaf.netwatch.utils.BandWidthLimiter;
import com.luckyaf.netwatch.utils.IOUtil;
import com.luckyaf.netwatch.utils.LimitInputStream;
import com.luckyaf.netwatch.utils.Logger;
import com.luckyaf.netwatch.utils.RxBus;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/20
 */
public class DownloadSubscribe implements ObservableOnSubscribe<Progress> {
    private Progress progress;
    private ObservableEmitter<Progress> observableEmitter;

    public DownloadSubscribe(Progress progress) {
        this.progress = progress;
    }

    @Override
    public void subscribe(ObservableEmitter<Progress> emitter) throws Exception {
        observableEmitter = emitter;
        if(ProgressConstant.FINISHED == progress.getStatus()){
            onError(new Exception("task already completed"));
        }else if(ProgressConstant.ERROR == progress.getStatus() ){
            onError(new Exception("task error, try restart ?"));
        }
        String url = progress.getUrl();
        if (!progress.isSupportRange()) {
            progress.setCurrentSize(0);
        }
        long downloadLength = progress.getCurrentSize();//已经下载好的长度
        long totalLength = progress.getTotalSize();//文件的总长度
        long lastTime = SystemClock.elapsedRealtime();
        long lastNotifySize = 0;
        long lastNotifyTime = lastTime;
        long nowTime;
        long speed;

        //初始进度信息
        progress.setStatus(ProgressConstant.LOADING);
        onProgress(progress);

        Request.Builder builder = new Request.Builder()
                 .url(url);

        if(progress.isSupportRange()){
            //确定下载的范围,添加此头,则服务器就可以跳过已经下载好的部分
            builder.addHeader("RANGE", "bytes=" + downloadLength + "-" + totalLength);
            if(null != progress.getLastModify()) {
                builder.addHeader("If-Range", progress.getLastModify());
            }
        }

        Request request = builder.build();

        Call call = DownloadManager.getInstance().getClient().newCall(request);
        DownloadManager.getInstance().addCall(progress.getTag(), call);//把这个添加到call里,方便取消
        Response response = call.execute();



        if (progress.getCurrentSize() > 0) {
            if (response.code() != 206) {
                 progress.setStatus(ProgressConstant.ERROR);
                 onError(new FileChangedException());
            }
        }

        File filePath = new File(progress.getFileDir());
        if(!filePath.exists()){
           filePath.mkdirs();
        }
        File file = new File(progress.getFileDir(),progress.getFileName());
        //start downloading
        RandomAccessFile randomAccessFile;
        Logger.d("file"," exist " + file.exists());

        randomAccessFile = new RandomAccessFile(file, "rw");

        randomAccessFile.seek(progress.getCurrentSize());
        LimitInputStream inputStream = null;
        try {
            InputStream responseStream = response.body().byteStream();
            inputStream = new LimitInputStream(responseStream, new BandWidthLimiter(progress.getSpeedLimit()));
            BufferedInputStream in = new BufferedInputStream(inputStream, 8 * 1024);

            byte[] buffer = new byte[1024 * 8];//缓冲数组8kB
            int len;
            while ((len = in.read(buffer)) != -1) {
                nowTime = SystemClock.elapsedRealtime();
                randomAccessFile.write(buffer, 0, len);
                downloadLength += len;
                progress.setCurrentSize(downloadLength);
                if(nowTime - lastNotifyTime >= progress.getNotifyInterval()){
                    speed = (progress.getCurrentSize() - lastNotifySize) * 1000 / (nowTime - lastNotifyTime);
                    progress.setNowSpeed(speed);
                    progress.setFraction((float) progress.getCurrentSize()  * 1.0f /(progress.getTotalSize() * 1.0f));
                    onProgress(progress);
                    lastNotifyTime = nowTime;
                    lastNotifySize = progress.getCurrentSize();
                }
            }
            in.close();
            progress.setStatus(ProgressConstant.FINISHED);
            progress.setCurrentSize(totalLength);
            onProgress(progress);
        } finally {
            //关闭IO流
            IOUtil.closeAll(inputStream, randomAccessFile);
            DownloadManager.getInstance().removeCal(progress.getTag());
        }
        onComplete(progress);//完成
    }



    private void onError(Exception e){
        progress.setError(e);
        DownloadManager.getInstance().replace(progress);
        observableEmitter.onError(e);
        RxBus.getInstance().post(progress);
    }

    private void onProgress(Progress progress){
        DownloadManager.getInstance().replace(progress);
        observableEmitter.onNext(progress);
        RxBus.getInstance().post(progress);
    }

    private void onComplete(Progress progress){
        DownloadManager.getInstance().replace(progress);
        observableEmitter.onNext(progress);
        RxBus.getInstance().post(progress);
    }



}