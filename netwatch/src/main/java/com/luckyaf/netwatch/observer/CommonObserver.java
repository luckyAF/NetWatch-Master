package com.luckyaf.netwatch.observer;

import com.luckyaf.netwatch.callback.CancelCallBack;
import com.luckyaf.netwatch.callback.CommonCallBack;
import com.luckyaf.netwatch.callback.ErrorCallBack;
import com.luckyaf.netwatch.callback.StartCallBack;
import com.luckyaf.netwatch.callback.SuccessCallBack;
import com.luckyaf.netwatch.exception.NetException;

import io.reactivex.disposables.Disposable;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/16
 */

public class CommonObserver<ResponseBody extends okhttp3.ResponseBody> extends BaseObserver<ResponseBody> {

    private CommonCallBack callBack;
    private StartCallBack mStartCallBack;
    private CancelCallBack mCancelCallBack;
    private SuccessCallBack mSuccessCallBack;
    private ErrorCallBack mErrorCallBack;

    public CommonObserver(){

    }

    public CommonObserver commonCallBack(CommonCallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    public CommonObserver startCallBack(StartCallBack startCallBack){
        this.mStartCallBack = startCallBack;
        return this;
    }

    public CommonObserver cancelCallBack(CancelCallBack cancelCallBack){
        this.mCancelCallBack = cancelCallBack;
        return this;
    }

    public CommonObserver successCallBack(SuccessCallBack successCallBack){
        this.mSuccessCallBack = successCallBack;
        return this;
    }

    public CommonObserver errorCallBack(ErrorCallBack errorCallBack){
        this.mErrorCallBack = errorCallBack;
        return this;
    }

    @Override
    public void onSubscribe(Disposable d) {
        super.onSubscribe(d);
        if(null != mStartCallBack) {
            mStartCallBack.onStart();
        }

    }

    @Override
    public void onNext(final ResponseBody value) {
        if(isRunning &&(callBack != null || mSuccessCallBack != null)) {
           postRunnable(new Runnable() {
               @Override
               public void run() {
                   if(callBack != null){
                       callBack.onNext(value);
                   }
                   if(mSuccessCallBack != null) {
                       mSuccessCallBack.onSuccess(value);
                   }
               }
           });
        }
    }

    @Override
    public void onError(final Throwable e) {
        if(isRunning && (callBack != null || mSuccessCallBack != null)) {
            postRunnable(new Runnable() {
                @Override
                public void run() {
                    if(callBack != null){
                        callBack.onError(NetException.handleException(e));
                    }
                    if(mErrorCallBack != null) {
                        mErrorCallBack.onError(NetException.handleException(e));
                    }
                }
            });
        }
    }

    @Override
    public void onComplete() {
        if(isRunning && callBack != null) {
            callBack.onComplete();
        }
    }

    @Override
    public void cancel(){
        super.cancel();
        if(callBack != null) {
            callBack.onCancel();
        }
        if(mCancelCallBack != null){
            mCancelCallBack.onCancel();
        }
    }
}
