package com.luckyaf.netwatch.download;

import com.luckyaf.netwatch.constant.ProgressConstant;
import com.luckyaf.netwatch.model.Progress;
import com.luckyaf.netwatch.utils.FileUtil;
import com.luckyaf.netwatch.utils.IOUtil;
import com.luckyaf.netwatch.utils.RxBus;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.SafeObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/20
 */
public class DownloadTask {

    private Progress taskProgress;
    private Disposable disposable;
    private ProgressListener progressListener;

    static DownloadTask createTask(Progress progress) {
        return new DownloadTask(progress);
    }

    private DownloadTask(Progress progress) {
        this.taskProgress = progress;
    }


    public void start(DownloadObserver downLoadObserver) {
        Observable.create(new DownloadSubscribe(taskProgress))
                .observeOn(AndroidSchedulers.mainThread())//在主线程回调
                .subscribeOn(Schedulers.io())//在子线程执行
                .subscribe(downLoadObserver);//添加观察者
    }

    public Observable<Progress> execute() {
        return Observable.create(
                new DownloadSubscribe(taskProgress))
                .observeOn(AndroidSchedulers.mainThread())//在主线程回调
                .subscribeOn(Schedulers.io());//在子线程执行
    }

    public DownloadTask setListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
        this.progressListener.setTag(taskProgress.getTag());
        return this;
    }

    public void start() {
        if(null != disposable) {
            Observable.create(
                    new DownloadSubscribe(taskProgress))
                    .observeOn(AndroidSchedulers.mainThread())//在主线程回调
                    .subscribeOn(Schedulers.io())//在子线程执行
                    .subscribe(new Observer<Progress>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable = d;
                        }

                        @Override
                        public void onNext(Progress progress) {
                            taskProgress = progress;
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
            this.progressListener.start();
        }
    }


    public void pause() {
        taskProgress.setStatus(ProgressConstant.WAITING);
        if(null != disposable && !disposable.isDisposed()) {
            disposable.dispose();
        }
        this.progressListener.clear();
        disposable = null;
        DownloadManager.getInstance().cancelCall(taskProgress.getTag());
    }


    public void remove() {
        pause();
        remove(false);
    }

    public void remove(boolean deleteFile) {
        if (deleteFile) {
            FileUtil.delFileOrFolder(taskProgress.getFileDir(), taskProgress.getFileName());
        }
        DownloadManager.getInstance().delete(taskProgress.getTag());

    }

}
