package com.luckyaf.okdownload;

import com.luckyaf.okdownload.subscribe.DownloadSubscribe;
import com.luckyaf.okdownload.utils.FileUtil;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/22
 */
@SuppressWarnings("unused")
public class DownloadTask {

    private int limitSpeed;      // 最高速度限制  kb/s
    private int notifyInterval;  // 通知间隔     ms

    private Disposable disposable;
    private DownloadRequest downloadRequest;
    private PublishSubject<Progress> publishSubject;


    static DownloadTask createTask(DownloadRequest downloadRequest) {
        return new DownloadTask(downloadRequest);
    }


    private DownloadTask(DownloadRequest downloadRequest) {
        this.downloadRequest = downloadRequest;
        publishSubject = PublishSubject.create();
        limitSpeed = Integer.MAX_VALUE;
        notifyInterval = 300;
    }

    public DownloadTask setMaxSpeed(int speed) {
        if (speed > 0) {
            this.limitSpeed = speed;
        }
        return this;
    }

    public DownloadTask setNotifyInterval(int interval) {
        if (interval > 0) {
            this.notifyInterval = interval;
        }
        return this;
    }

    public DownloadTask start() {

        if (null == disposable) {
            disposable = Observable.create(
                    new DownloadSubscribe(downloadRequest)
                            .setNotifyInterval(notifyInterval)
                            .setSpeedLimit(limitSpeed))
                    .subscribeOn(Schedulers.io())//在子线程执行
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())//在主线程回调
                    .subscribe(new Consumer<Progress>() {
                        @Override
                        public void accept(Progress progress) {
                            publishSubject.onNext(progress);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            publishSubject.onError(throwable);
                        }
                    }, new Action() {
                        @Override
                        public void run() {
                            publishSubject.onComplete();
                        }
                    });

        }
        return this;
    }

    public void pause() {
        if (null != disposable && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }

    public Observable<Progress> listenProgress() {
        return publishSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io());
    }


    public void remove() {
        remove(false);
    }

    @SuppressWarnings("WeakerAccess")
    public void remove(boolean deleteFile) {
        pause();
        if (deleteFile) {
            FileUtil.delFileOrFolder(downloadRequest.getFileDir(), downloadRequest.getFileName());
        }
        DownloadManager.getInstance().delete(downloadRequest.getTag());
    }


}
