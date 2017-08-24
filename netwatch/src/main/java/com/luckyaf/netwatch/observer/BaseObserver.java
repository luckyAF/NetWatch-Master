package com.luckyaf.netwatch.observer;

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

    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
        isRunning = true;
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


}
