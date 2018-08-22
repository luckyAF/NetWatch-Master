package com.luckyaf.netwatch.download;

import com.luckyaf.netwatch.model.Progress;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/20
 */
public abstract class DownloadObserver implements Observer<Progress> {
    protected Disposable d;//可以用于取消注册的监听者
    @Override
    public void onSubscribe(Disposable d) {
        this.d = d;
    }


    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

}
