package com.luckyaf.netwatch;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.luckyaf.netwatch.utils.CheckUtils;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/20
 */
public class NetWatch {

    private Application context;            //全局上下文
    private NetWatch (){

    }

    public void init(Application application){
        this.context = application;
    }



//    /**
//     * create ApiService
//     */
//    public <T> T create(final Class<T> service) {
//
//        return retrofit.create(service);
//    }
//
//    /**
//     * @param subscriber
//     */
//    public <T> T call(Observable<T> observable, BaseSubscriber<T> subscriber) {
//        return (T) observable.compose(schedulersTransformer)
//                .compose(handleErrTransformer())
//                .subscribe(subscriber);
//    }






    /** 获取全局上下文 */
    public Context getContext() {
        CheckUtils.checkNotNull(context, "please call init() first in application!");
        return context;
    }




    public static  NetWatch getInstance() {
        return Holder.INSTANCE;
    }
    private static class Holder {
        private static final NetWatch INSTANCE = new NetWatch();
    }

}
