package com.luckyaf.netwatch.observer;

import com.luckyaf.netwatch.callBack.CancelCallBack;
import com.luckyaf.netwatch.callBack.ErrorCallBack;
import com.luckyaf.netwatch.callBack.StartCallBack;
import com.luckyaf.netwatch.callBack.SuccessCallBack;
import com.luckyaf.netwatch.callBack.UploadCallBack;
import com.luckyaf.netwatch.upload.UploadFileBody;
import com.luckyaf.netwatch.upload.UploadRequestBody;

import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/17
 */
@SuppressWarnings("unused")
public class UploadObserver<UploadRequestBody> extends BaseObserver<ResponseBody> {

    private UploadCallBack callBack;
    private StartCallBack mStartCallBack;
    private CancelCallBack mCancelCallBack;
    private SuccessCallBack mSuccessCallBack;
    private ErrorCallBack mErrorCallBack;
    private Boolean isCancel;
    private com.luckyaf.netwatch.upload.UploadRequestBody mUploadRequestBody;

    public UploadObserver(com.luckyaf.netwatch.upload.UploadRequestBody uploadRequestBody){
        this.mUploadRequestBody = uploadRequestBody;
        this.isCancel = false;
    }

    public UploadObserver uploadCallBack(UploadCallBack uploadCallBack) {
        this.callBack = uploadCallBack;
        return this;
    }

    public UploadObserver startCallBack(StartCallBack startCallBack){
        this.mStartCallBack = startCallBack;
        return this;
    }

    public UploadObserver cancelCallBack(CancelCallBack cancelCallBack){
        this.mCancelCallBack = cancelCallBack;
        return this;
    }

    public UploadObserver successCallBack(SuccessCallBack successCallBack) {
        this.mSuccessCallBack = successCallBack;
        return this;
    }
    public UploadObserver errorCallBack(ErrorCallBack errorCallBack) {
        this.mErrorCallBack = errorCallBack;
        return this;
    }

    @Override
    public void onSubscribe(Disposable d) {
        super.onSubscribe(d);
        if(callBack != null) {
            callBack.onStart();
        }
        if(mStartCallBack != null){
            mStartCallBack.onStart();
        }
    }

    @Override
    public void onNext(final ResponseBody body) {
        if(callBack == null && mSuccessCallBack == null){
            return ;
        }
        postRunnable(new Runnable() {
            @Override
            public void run() {
                if(callBack != null) {
                    callBack.onSuccess(body);
                }
                if(mSuccessCallBack != null){
                    mSuccessCallBack.onSuccess(body);
                }
            }
        });
    }

    @Override
    public void onError(final Throwable e) {
        if(callBack == null && mErrorCallBack == null){
            return ;
        }
        postRunnable(new Runnable() {
            @Override
            public void run() {
                if(callBack != null) {
                    callBack.onError(e);
                }
                if(mErrorCallBack != null){
                    mErrorCallBack.onError(e);
                }
            }
        });
    }

    @Override
    public void onComplete() {
        if(callBack == null) {
            return;
        }
        postRunnable(new Runnable() {
            @Override
            public void run() {
                    callBack.onComplete();

            }
        });

    }

    @Override
    public void cancel() {
        this.mUploadRequestBody.setCancel(true);
        super.cancel();
        if(callBack == null && mCancelCallBack == null){
            return ;
        }
        postRunnable(new Runnable() {
            @Override
            public void run() {
                if(callBack != null) {
                    callBack.onCancel();
                }
                if(mErrorCallBack != null){
                    mCancelCallBack.onCancel();
                }
            }
        });
    }
}
