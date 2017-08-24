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
    public void onNext(ResponseBody value) {
        if(isRunning) {
            this.callBack.onNext(value);
        }
    }

    @Override
    public void onError(Throwable e) {
        if(isRunning) {
            this.callBack.onError(e);
        }
    }

    @Override
    public void onComplete() {
        if(isRunning) {
            this.callBack.onComplete();
        }
    }

    @Override
    public void cancel(){
        super.cancel();
        callBack.onCancel();
    }
}
