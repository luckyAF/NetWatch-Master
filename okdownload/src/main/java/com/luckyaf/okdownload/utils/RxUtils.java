package com.luckyaf.okdownload.utils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/24
 */
public class RxUtils {
    public static Disposable loopDoing(int interval, TimeUnit unit, final RxUtils.Listener listener) {
        return Observable.interval((long)interval, unit)
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        listener.doSomeThing();
                    }
                });
    }

    public interface Listener {
        void doSomeThing();
    }
}
