package com.luckyaf.netwatch.request;

import android.support.annotation.NonNull;

import com.luckyaf.netwatch.download.DownloadInitSubscribe;
import com.luckyaf.netwatch.download.DownloadManager;
import com.luckyaf.netwatch.model.Progress;
import com.luckyaf.netwatch.download.DownloadTask;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/20
 */
public class DownloadRequest extends BaseRequest<File, DownloadRequest> {
    private static final long serialVersionUID = 8882144489515333062L;

    private String fileName;
    private String fileDir;
    private int speedLimit;


    public DownloadRequest(@NonNull String url) {
        super(url);
    }

    public DownloadRequest fileName(@NonNull String fileName) {
        this.fileName = fileName;
        return this;
    }

    public DownloadRequest fileDir(@NonNull String fileDir) {
        this.fileDir = fileDir;
        return this;
    }

    public DownloadRequest speedLimit(@NonNull int speedLimit) {
        this.speedLimit = speedLimit;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileDir() {
        return fileDir;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }



    public Observable<Progress> prepare() {
        return Observable
                .create(new DownloadInitSubscribe(this))
                .observeOn(AndroidSchedulers.mainThread())//在主线程回调
                .subscribeOn(Schedulers.io());//在子线程执行
    }


    public Observable<Progress> run() {
        return prepare()
                .flatMap(new Function<Progress, ObservableSource<Progress>>() {
                    @Override
                    public Observable<Progress> apply(Progress progress) throws Exception {
                        DownloadTask task = DownloadManager.getInstance().getDownloadTask(progress);
                        return task.execute();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//在主线程回调
                .subscribeOn(Schedulers.io());//在子线程执行;

    }


}
