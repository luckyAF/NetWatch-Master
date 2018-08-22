package com.luckyaf.netwatch.download;

import android.support.annotation.NonNull;

import com.luckyaf.netwatch.constant.ProgressConstant;
import com.luckyaf.netwatch.model.Progress;
import com.luckyaf.netwatch.utils.RxBus;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/21
 */
public abstract class ProgressListener {

    private Disposable disposable;
    private String tag;

    public ProgressListener() {
    }

    public ProgressListener setTag(String tag){
        this.tag = tag;
        return this;
    }

    public void start(){
        if(null == disposable) {
            disposable = RxBus.getInstance().toDefaultFlowable(Progress.class, new Consumer<Progress>() {
                @Override
                public void accept(Progress progress) throws Exception {
                    if (tag.equals(progress.getTag())) {
                        dealProgress(progress);
                    }
                }
            });
        }
    }


    private void dealProgress(Progress progress) {
        switch (progress.getStatus()) {
            case ProgressConstant.LOADING:
                onProgress(progress);
                break;
            case ProgressConstant.ERROR:
                onError(progress.getError());
                break;
            case ProgressConstant.FINISHED:
                onComplete(progress);
                break;
            default:
                break;
        }
    }

    public void clear() {
        if (null != disposable && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }

    public abstract void onProgress(Progress progress);

    public abstract void onError(Exception e);

    public abstract void onComplete(Progress progress);

}
