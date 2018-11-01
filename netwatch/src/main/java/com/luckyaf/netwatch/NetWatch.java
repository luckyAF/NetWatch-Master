package com.luckyaf.netwatch;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.luckyaf.netwatch.Interceptor.DefaultCacheInterceptor;
import com.luckyaf.netwatch.Interceptor.StupidCacheInterceptor;
import com.luckyaf.netwatch.api.BaseApiService;
import com.luckyaf.netwatch.request.GetRequest;
import com.luckyaf.netwatch.request.PostRequest;
import com.luckyaf.netwatch.request.UniqueRequest;
import com.luckyaf.netwatch.utils.CheckUtils;
import com.luckyaf.netwatch.utils.HttpsFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/20
 */
@SuppressWarnings("unused")
public class NetWatch {


    private static NetWatch mInstance;
    private static Application mContext;
    private static OkHttpClient mClient;
    private static Retrofit mRetrofit;
    private static volatile BaseApiService mService;
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());


    NetWatch(Application context, OkHttpClient client, Retrofit retrofit, BaseApiService service) {
        mContext = context;
        mClient = client;
        mRetrofit = retrofit;
        mService = service;
    }

    public static void runOnUiThread(Runnable runnable){
        getInstance().mainThreadHandler.post(runnable);
    }


    @CheckResult
    public static NetWatch getInstance() {
        if (mInstance == null) {
            throw new NullPointerException("HttpUtil has not be initialized");
        }
        return mInstance;
    }
    /**
     * 获取全局上下文
     */
    public static Context getContext() {
        CheckUtils.checkNotNull(mContext, "please call init() first in application!");
        return mContext;
    }

    @SuppressWarnings("WeakerAccess")
    public static BaseApiService getService() {
        if (mInstance == null) {
            throw new NullPointerException("NetWatch has not be initialized");
        }
        return mService;
    }

    public static OkHttpClient getClient() {
        if (mInstance == null) {
            throw new NullPointerException("NetWatch has not be initialized");
        }
        return mClient;
    }

    public static Retrofit getRetrofit() {
        if (mInstance == null) {
            throw new NullPointerException("NetWatch has not be initialized");
        }
        return mRetrofit;
    }


    public static UniqueRequest unique(@NonNull String url) {
        return new UniqueRequest(url);
    }

    public static GetRequest get(@NonNull String url) {
        return new GetRequest(url);
    }

    public static PostRequest post(@NonNull String url) {
        return new PostRequest(url);
    }

    public static NetWatchBuilder init(Application application,String baseUrl) {
        return new NetWatchBuilder(application,baseUrl);
    }

     public static  class NetWatchBuilder {

        private static final int DEFAULT_TIMEOUT = 15;                  // 默认超时时间
        private static final int DEFAULT_MAX_IDLE_CONNECTIONS = 5;       // 默认连接数
        private static final long DEFAULT_KEEP_ALIVE_DURATION = 8;       //

        private static final long cacheSize = 1024 * 1024 * 20;// 缓存文件最大限制大小20M
        private static String cacheDirectory = Environment.getExternalStorageState() + "/okttpcaches"; // 设置缓存文件路径
        private static Cache cache = new Cache(new File(cacheDirectory), cacheSize);  //


        private okhttp3.OkHttpClient.Builder okhttpBuilder;
        private Retrofit.Builder retrofitBuilder;
        private Application context;
        private String baseUrl;
         private List<Converter.Factory> converterFactories = new ArrayList<>();
         private List<CallAdapter.Factory> adapterFactories = new ArrayList<>();


         NetWatchBuilder(Application context,String baseUrl) {
            okhttpBuilder = new okhttp3.OkHttpClient.Builder();
            retrofitBuilder = new Retrofit.Builder();
            this.context = context;
            this.baseUrl = baseUrl;
        }

        public NetWatchBuilder writeTimeout(int timeout, TimeUnit unit) {
            if (timeout != -1) {
                okhttpBuilder.writeTimeout((long) timeout, unit);
            } else {
                okhttpBuilder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return this;
        }

        public NetWatchBuilder readTimeout(int timeout, TimeUnit unit) {
            if (timeout != -1) {
                okhttpBuilder.readTimeout(timeout, unit);
            } else {
                okhttpBuilder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return this;
        }

        public NetWatchBuilder connectTimeout(int timeout, TimeUnit unit) {
            if (timeout != -1) {
                okhttpBuilder.connectTimeout((long) timeout, unit);
            } else {
                okhttpBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return this;
        }
         public NetWatchBuilder converterFactory(Converter.Factory factory) {
             this.converterFactories.add(factory);
             return this;
         }

         public NetWatchBuilder callFactory(CallAdapter.Factory factory) {
             this.adapterFactories.add(factory);
             return this;
         }

        public NetWatchBuilder cache(Cache cache) {
            this.okhttpBuilder.cache(cache);
            return this;
        }

        public NetWatchBuilder stupidCache(boolean able) {
            if (able) {
                this.okhttpBuilder.addNetworkInterceptor(new StupidCacheInterceptor());
            }
            return this;
        }
        public NetWatchBuilder defaultCache(int cacheTime, TimeUnit unit) {
            this.okhttpBuilder
                    .addNetworkInterceptor(new DefaultCacheInterceptor(cacheTime, unit))
                    .addInterceptor(new DefaultCacheInterceptor(cacheTime, unit));
            return this;
        }

        /**
         * 添加证书
         *
         * @param hosts        host
         * @param certificates 证书
         */
        public NetWatchBuilder andSSL(String[] hosts, int[] certificates) {
            if (hosts == null) {
                throw new NullPointerException("hosts == null");
            }
            if (certificates == null) {
                throw new NullPointerException("ids == null");
            }
            SSLSocketFactory factory = HttpsFactory.getSSLSocketFactory(context, certificates);
            if (null != factory) {
                this.okhttpBuilder.sslSocketFactory(factory, HttpsFactory.getTrustManager());
            }
            this.okhttpBuilder.hostnameVerifier(HttpsFactory.getHostnameVerifier(hosts));
            return this;
        }


        public void build() {
            if (converterFactories.size() == 0) {
                converterFactories.add(ScalarsConverterFactory.create());
            }
            if (adapterFactories.size() == 0) {
                adapterFactories.add(RxJava2CallAdapterFactory.create());
            }
            for (Converter.Factory converterFactory : converterFactories) {
                retrofitBuilder.addConverterFactory(converterFactory);
            }
            for (CallAdapter.Factory adapterFactory : adapterFactories) {
                retrofitBuilder.addCallAdapterFactory(adapterFactory);
            }
            OkHttpClient client = okhttpBuilder
                    .build();
            Retrofit retrofit = retrofitBuilder
                    .baseUrl(baseUrl + "/")
                    .client(client)
                    .build();

            BaseApiService apiService =
                    retrofit.create(BaseApiService.class);
            mInstance = new NetWatch(context, client, retrofit, apiService);
        }


    }


}
