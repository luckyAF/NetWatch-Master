package com.luckyaf.okdownload;

import android.app.Application;
import android.support.annotation.NonNull;

import com.luckyaf.okdownload.utils.Preconditions;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;


import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.OkHttpClient;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/22
 */
public class OkDownload {

    private Application context;
    private OkHttpClient mClient;//OKHttpClient;




    private OkDownload (){
        mClient = new OkHttpClient.Builder().build();
    }

    public OkHttpClient getOkHttpClient() {
        Preconditions.checkNotNull(mClient, "please call init() first in application!");
        return mClient;
    }

    public Observable<Progress> download(@NonNull String url){
        return DownloadRequest.create(url)
                .flatMap(new Function<DownloadRequest, ObservableSource<Progress>>() {
                    @Override
                    public Observable<Progress> apply(DownloadRequest request) throws Exception {
                        return request.execute().start().listenProgress();
                    }
                });
    }



    public void init(Application app){
        context = app;
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    public Application getContext() {
        Preconditions.checkNotNull(context, "please call init() first in application!");
        return context;
    }

    public static  OkDownload getInstance() {
        return Holder.INSTANCE;
    }
    private static class Holder {
        private static final OkDownload INSTANCE = new OkDownload();
    }
}
