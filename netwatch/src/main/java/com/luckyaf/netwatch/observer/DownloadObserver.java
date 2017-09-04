package com.luckyaf.netwatch.observer;

import android.content.Context;

import com.luckyaf.netwatch.callBack.CancelCallBack;
import com.luckyaf.netwatch.callBack.DownloadCallBack;
import com.luckyaf.netwatch.callBack.DownloadSuccessCallback;
import com.luckyaf.netwatch.callBack.ErrorCallBack;
import com.luckyaf.netwatch.callBack.ProgressCallBack;
import com.luckyaf.netwatch.callBack.StartCallBack;
import com.luckyaf.netwatch.callBack.SuccessCallBack;
import com.luckyaf.netwatch.download.DownloadManager;
import com.luckyaf.netwatch.utils.Logger;

import io.reactivex.disposables.Disposable;

import static com.luckyaf.netwatch.download.DownloadManager.TAG;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/16
 */
public class DownloadObserver<ResponseBody extends okhttp3.ResponseBody> extends BaseObserver<ResponseBody> {

    private DownloadCallBack callBack;
    private StartCallBack mStartCallBack;
    private CancelCallBack mCancelCallBack;
    private DownloadSuccessCallback mSuccessCallBack;
    private ErrorCallBack mErrorCallBack;
    private ProgressCallBack mProgressCallBack;
    private Context context;
    private String path;
    private String name;
    private String key;

    private DownloadManager mDownloadManager;
    private Boolean isCancel;

    /**
     *
     * @param key   key
     * @param path    保存路径
     * @param name    保存文件名
     * @param context   上下文
     */
    public DownloadObserver(String key, String path, String name,Context context) {
        this.key = key;
        this.path = path;
        this.name = name;
        this.context = context;
        this.isCancel = false;
    }

    public DownloadObserver downloadCallBack(DownloadCallBack callBack){
        this.callBack = callBack;
        return this;
    }

    public DownloadObserver startCallBack(StartCallBack startCallBack){
        this.mStartCallBack = startCallBack;
        return this;
    }

    public DownloadObserver cancelCallBack(CancelCallBack cancelCallBack){
        this.mCancelCallBack = cancelCallBack;
        return this;
    }

    public DownloadObserver successCallBack(DownloadSuccessCallback successCallBack){
        this.mSuccessCallBack = successCallBack;
        return this;
    }

    public DownloadObserver progressCallBack(ProgressCallBack progressCallBack){
        this.mProgressCallBack= progressCallBack;
        return this;
    }

    public DownloadObserver errorCallBack(ErrorCallBack errorCallBack){
        this.mErrorCallBack = errorCallBack;
        return this;
    }



    @Override
    public void onSubscribe(Disposable d) {
        super.onSubscribe(d);
        if(callBack != null) {
            callBack.onStart(key);
        }
        if(mStartCallBack != null){
            mStartCallBack.onStart();
        }
        mDownloadManager = new DownloadManager()
        .downloadCallBack(callBack)
        .successCallBack(mSuccessCallBack)
        .errorCallBack(mErrorCallBack)
        .progressCallBack(mProgressCallBack);


    }

    @Override
    public void onNext(ResponseBody value) {
        Logger.d(TAG, "DownSubscriber:>>>> onNext");
        mDownloadManager.writeResponseBodyToDisk(key, path, name, context, value);

    }

    @Override
    public void onError(final Throwable e) {
        if(callBack == null && mErrorCallBack == null){
            return;
        }
        if(!this.isCancel){
            postRunnable(new Runnable() {
                @Override
                public void run() {
                    if(callBack!= null) {
                        callBack.onError(e);
                    }
                    if(mErrorCallBack!= null){
                        mErrorCallBack.onError(e);
                    }
                }
            });
        }
    }

    @Override
    public void onComplete() {
        if(callBack != null) {
            callBack.onComplete();
        }
    }

    @Override
    public void cancel(){
        mDownloadManager.setCancel(true);
        super.cancel();
        if(mCancelCallBack!= null) {
            mCancelCallBack.onCancel();
        }
        if(callBack!= null) {
            callBack.onCancel();
        }
        this.isCancel = true;
    }

}
