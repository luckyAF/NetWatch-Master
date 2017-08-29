package com.luckyaf.netwatch.observer;

import com.luckyaf.netwatch.callBack.CommonCallBack;

import io.reactivex.disposables.Disposable;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/16
 */

public class CommonObserver<ResponseBody extends okhttp3.ResponseBody> extends BaseObserver<ResponseBody> {

    private CommonCallBack callBack;

    public CommonObserver(CommonCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onSubscribe(Disposable d) {
        super.onSubscribe(d);
    }

    @Override
    public void onNext(final ResponseBody value) {
        if(isRunning) {
           postRunnable(new Runnable() {
               @Override
               public void run() {
                   callBack.onNext(value);
               }
           });
        }
    }

    @Override
    public void onError(final Throwable e) {
        if(isRunning) {
            postRunnable(new Runnable() {
                @Override
                public void run() {
                    callBack.onError(e);
                }
            });
        }
    }

    @Override
    public void onComplete() {
        if(isRunning) {
            callBack.onComplete();
        }
    }

    @Override
    public void cancel(){
        super.cancel();
        callBack.onCancel();
    }
}
