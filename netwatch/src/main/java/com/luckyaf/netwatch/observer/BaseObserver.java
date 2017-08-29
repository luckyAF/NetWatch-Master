package com.luckyaf.netwatch.observer;

import android.os.Looper;

import java.util.logging.Handler;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/17
 */

public class BaseObserver<ResponseBody> implements Observer<ResponseBody> {

    private Disposable mDisposable;
    public Boolean isRunning;
    public android.os.Handler handler;

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
        isRunning = true;
        handler = new android.os.Handler(Looper.getMainLooper());

    }

    @Override
    public void onNext(ResponseBody body) {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }

    public void cancel(){
        mDisposable.dispose();
        isRunning = false;
    }

    public void postRunnable(Runnable runnable){
        handler.post(runnable);
    }

}
