package com.luckyaf.netwatch.provider;

import android.content.Context;
import android.support.compat.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/14
 */

public class OkHttpProvider {
    static OkHttpClient okHttpClient;

    public static OkHttpClient okHttpClient(final Context context, String BASE_URL) {
        if (okHttpClient == null) {
            synchronized (OkHttpClient.class) {
                if (okHttpClient == null) {
                    OkHttpClient client = new OkHttpClient.Builder()
                            //.addInterceptor(new DownLoadInterceptor(BASE_URL))
                            //.addNetworkInterceptor(new CacheInterceptor())
                            //.cache(new CacheProvide(context).provideCache())
                            .retryOnConnectionFailure(true)
                            .connectTimeout(5, TimeUnit.SECONDS)
                            .readTimeout(8, TimeUnit.SECONDS)
                            .writeTimeout(8, TimeUnit.SECONDS)
                            .build();
                    if (BuildConfig.DEBUG) {//printf logs while  debug
                        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                        client = client.newBuilder().addInterceptor(logging).build();
                    }
                    okHttpClient = client;
                }

            }

        }
        return okHttpClient;

    }
}
