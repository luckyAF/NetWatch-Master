package com.luckyaf.netwatch.observer;

import com.luckyaf.netwatch.callBack.UploadCallBack;

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

    public UploadObserver(UploadCallBack uploadCallBack){
        this.callBack = uploadCallBack;
    }

    @Override
    public void onSubscribe(Disposable d) {
        super.onSubscribe(d);
        callBack.onStart();
    }

    @Override
    public void onNext(ResponseBody body) {
        callBack.onNext(body);
    }

    @Override
    public void onError(Throwable e) {
        callBack.onError(e);
    }

    @Override
    public void onComplete() {
        callBack.onComplete();
    }
    @Override
    public void cancel(){
        super.cancel();
        callBack.onCancel();
    }
}
