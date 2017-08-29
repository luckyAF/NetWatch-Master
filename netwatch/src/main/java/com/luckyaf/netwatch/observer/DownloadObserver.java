package com.luckyaf.netwatch.observer;

import android.content.Context;

import com.luckyaf.netwatch.callBack.DownloadCallBack;
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
     * @param callBack   下载回调
     * @param context   上下文
     */
    public DownloadObserver(String key, String path, String name, DownloadCallBack callBack, Context context) {
        this.key = key;
        this.path = path;
        this.name = name;
        this.callBack = callBack;
        this.context = context;
        this.isCancel = false;
    }


    @Override
    public void onSubscribe(Disposable d) {
        super.onSubscribe(d);
        callBack.onStart(key);
        mDownloadManager = new DownloadManager(callBack);

    }

    @Override
    public void onNext(ResponseBody value) {
        Logger.d(TAG, "DownSubscriber:>>>> onNext");
        mDownloadManager.writeResponseBodyToDisk(key, path, name, context, value);

    }

    @Override
    public void onError(final Throwable e) {
        if(!this.isCancel){
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
        callBack.onComplete();
    }

    @Override
    public void cancel(){
        mDownloadManager.setCancel(true);
        super.cancel();
        callBack.onCancel();
        this.isCancel = true;
    }

}
