package com.luckyaf.netwatch;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.luckyaf.netwatch.api.RetrofitHttpService;
import com.luckyaf.netwatch.netbuilder.NetDownloadBuilder;
import com.luckyaf.netwatch.netbuilder.NetGetBuilder;
import com.luckyaf.netwatch.netbuilder.NetPostBuilder;
import com.luckyaf.netwatch.netbuilder.NetUploadBuilder;
import com.luckyaf.netwatch.observer.BaseObserver;
import com.luckyaf.netwatch.utils.HttpsFactory;
import com.luckyaf.netwatch.utils.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/11
 */
@SuppressWarnings("unused")
public class NetWatch {
    private static String TAG = NetWatch.class.getName();

    private static  NetWatch mInstance;
    private static volatile RetrofitHttpService mService;
    private static Context applicationContext;


    private NetWatch(Context context, RetrofitHttpService service) {
        mService = service;
        applicationContext = context;
    }

    @SuppressWarnings("WeakerAccess")
    public static RetrofitHttpService getService() {
        if (mInstance == null) {
            throw new NullPointerException("NetWatch has not be initialized");
        }
        return mService;
    }

    @SuppressWarnings("WeakerAccess")
    public static NetWatch getInstance() {
        if (mInstance == null) {
            throw new NullPointerException("NetWatch has not be initialized");
        }
        return mInstance;
    }

    public static Logger.Builder getLoggerBuilder(Context context) {
        return new Logger.Builder(context);
    }

    public static Context getApplicationContext() {
        return applicationContext;
    }


    public static SingletonBuilder init(Context context, String baseUrl) {
        return new SingletonBuilder(context, baseUrl);
    }

    public static NetBuilder open(@NonNull Context context, @NonNull String url) {
        return new NetBuilder(context, url);
    }

    public static NetBuilder open(@NonNull String url) {
        return new NetBuilder(url);
    }

    public static NetGetBuilder get(@NonNull String url) {
        return new NetGetBuilder(url);
    }

    public static NetBuilder getNetBuilder(@NonNull Context context) {
        return new NetBuilder((context));
    }

    public static NetPostBuilder post(@NonNull String url) {
        return new NetPostBuilder(url);
    }

    public static NetUploadBuilder upload(@NonNull String url) {
        return new NetUploadBuilder(url);
    }

    public static NetDownloadBuilder download(@NonNull String url) {
        return new NetDownloadBuilder(url);
    }

    @SuppressWarnings("WeakerAccess")
    public static class SingletonBuilder {
        private Context applicationContext;
        private String baseUrl;
        private List<Converter.Factory> converterFactories = new ArrayList<>();
        private List<CallAdapter.Factory> adapterFactories = new ArrayList<>();
        OkHttpClient.Builder clientBuilder;
        Logger.Builder loggerBuilder;

        private SingletonBuilder(Context context, String baseUrl) {
            applicationContext = context.getApplicationContext();
            this.baseUrl = baseUrl;
            this.clientBuilder = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS);
            this.loggerBuilder = new Logger.Builder(context)
                    .setLogSwitch(BuildConfig.DEBUG)// 设置log总开关，包括输出到控制台和文件，默认开
                    .setConsoleSwitch(BuildConfig.DEBUG)// 设置是否输出到控制台开关，默认开
                    .setGlobalTag(TAG)// 设置log全局标签，默认为空
                    // 当全局标签不为空时，我们输出的log全部为该tag，
                    // 为空时，如果传入的tag为空那就显示类名，否则显示tag
                    //.setLogHeadSwitch(true)// 设置log头信息开关，默认为开
                    .setLog2FileSwitch(false)// 打印log时是否存到文件的开关，默认关
                    .setDir("")// 当自定义路径为空时，写入应用的/cache/log/目录中
                    .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
                    .setConsoleFilter(Logger.V)// log的控制台过滤器，和logcat过滤器同理，默认Verbose
                    .setFileFilter(Logger.V);// log文件过滤器，和logcat过滤器同理，默认Verbose
        }

        public SingletonBuilder openSimpleLog(Boolean open) {
            this.loggerBuilder.setLogSwitch(open)
                    .setConsoleSwitch(open);

            return this;
        }

        public SingletonBuilder openOkHttpLog(HttpLoggingInterceptor.Level level) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(level);
            this.clientBuilder.addInterceptor(logging);
            return this;
        }

        public SingletonBuilder clientBuilder(OkHttpClient.Builder clientBuilder) {
            this.clientBuilder = clientBuilder;
            return this;
        }

        /**
         * 自动重连
         */
        public SingletonBuilder retryOnConnectionFailure(Boolean retry) {
            this.clientBuilder.retryOnConnectionFailure(retry);
            return this;
        }

        public SingletonBuilder connectTimeout(long time, TimeUnit unit) {
            this.clientBuilder.connectTimeout(time, unit);
            return this;
        }

        /**
         * 全局head
         *
         * @param map head map
         */
        public SingletonBuilder addCommonHeaders(final Map<String, Object> map) {
            this.clientBuilder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();
                    Request.Builder builder = originalRequest.newBuilder();
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        builder.header(entry.getKey(), entry.getValue().toString());
                    }
                    Request.Builder requestBuilder = builder.method(originalRequest.method(), originalRequest.body());
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
            return this;
        }

        /**
         * 全局参数
         *
         * @param map 参数map
         */
        public SingletonBuilder addCommonParams(final Map<String, Object> map) {
            this.clientBuilder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request originRequest = chain.request();
                    Request request;
                    HttpUrl.Builder builder = originRequest.url().newBuilder();
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        builder.addQueryParameter(entry.getKey(), entry.getValue().toString());
                    }
                    request = originRequest.newBuilder().url(builder.build()).build();
                    return chain.proceed(request);
                }
            });
            return this;
        }

        public SingletonBuilder readTimeout(long time, TimeUnit unit) {
            this.clientBuilder.readTimeout(time, unit);
            return this;
        }

        public SingletonBuilder writeTimeout(long time, TimeUnit unit) {
            this.clientBuilder.writeTimeout(time, unit);
            return this;
        }

        public SingletonBuilder cache(Cache cache) {
            this.clientBuilder.cache(cache);
            return this;
        }


        /**
         * 添加证书
         *
         * @param hosts        host
         * @param certificates 证书
         */
        public SingletonBuilder andSSL(String[] hosts, int[] certificates) {
            if (hosts == null) {
                throw new NullPointerException("hosts == null");
            }
            if (certificates == null) {
                throw new NullPointerException("ids == null");
            }
            SSLSocketFactory factory = HttpsFactory.getSSLSocketFactory(applicationContext,certificates);
            if(null != factory) {
                this.clientBuilder.sslSocketFactory(factory, HttpsFactory.getTrustManager());
            }
            this.clientBuilder.hostnameVerifier(HttpsFactory.getHostnameVerifier(hosts));
            return this;
        }


        public SingletonBuilder converterFactory(Converter.Factory factory) {
            this.converterFactories.add(factory);
            return this;
        }

        public SingletonBuilder callFactory(CallAdapter.Factory factory) {
            this.adapterFactories.add(factory);
            return this;
        }

        public void build() {
            if (checkNULL(this.baseUrl)) {
                throw new NullPointerException("BASE_URL can not be null");
            }
            if (converterFactories.size() == 0) {
                converterFactories.add(ScalarsConverterFactory.create());
            }
            if (adapterFactories.size() == 0) {
                adapterFactories.add(RxJava2CallAdapterFactory.create());
            }
            Retrofit.Builder builder = new Retrofit.Builder();
            for (Converter.Factory converterFactory : converterFactories) {
                builder.addConverterFactory(converterFactory);
            }
            for (CallAdapter.Factory adapterFactory : adapterFactories) {
                builder.addCallAdapterFactory(adapterFactory);
            }
            Retrofit retrofit = builder
                    .baseUrl(baseUrl + "/")
                    .client(clientBuilder.build())
                    .build();
            RetrofitHttpService retrofitHttpService =
                    retrofit.create(RetrofitHttpService.class);

            mInstance = new NetWatch(applicationContext, retrofitHttpService);
        }
    }

    @CheckResult
    @SuppressWarnings("WeakerAccess")
    public static boolean checkNULL(String str) {
        return str == null || "null".equals(str) || "".equals(str);

    }


    private final static Map<String, BaseObserver> OBSERVER_MAP = new HashMap<>();

    /**
     * 添加某个请求
     *
     * @param tag      标签
     * @param url      url
     * @param observer 请求
     */
    @SuppressWarnings("WeakerAccess")
    public static synchronized void putRequest(Object tag, String url, BaseObserver observer) {
        if (tag == null) {
            return;
        }
        synchronized (OBSERVER_MAP) {
            OBSERVER_MAP.put(tag.toString() + url, observer);
        }
    }


    /**
     * 取消某个界面都所有请求，或者是取消某个tag的所有请求
     * 如果要取消某个tag单独请求，tag需要转入tag+url
     *
     * @param tag 标签
     */
    public static synchronized void cancelRequest(Object tag) {
        if (tag == null) {
            return;
        }
        List<String> list = new ArrayList<>();
        synchronized (OBSERVER_MAP) {
            for (Map.Entry<String, BaseObserver> entry : OBSERVER_MAP.entrySet()) {
                if (entry.getKey().startsWith(tag.toString())) {
                    entry.getValue().cancel();
                    list.add(entry.getKey());
                }
            }
        }
        for (String s : list) {
            removeRequest(s);
        }
    }

    /**
     * 取消单个url请求
     *
     * @param url url
     */
    public static synchronized void cancelSingleRequest(String url) {
        List<String> list = new ArrayList<>();
        synchronized (OBSERVER_MAP) {
            for (Map.Entry<String, BaseObserver> entry : OBSERVER_MAP.entrySet()) {
                if (entry.getKey().endsWith(url)) {
                    entry.getValue().cancel();
                    list.add(entry.getKey());
                }
            }
        }
        for (String s : list) {
            removeRequest(s);
        }
    }


    /**
     * 移除某个请求
     *
     * @param url url
     */
    @SuppressWarnings("WeakerAccess")
    private static synchronized void removeRequest(String url) {
        synchronized (OBSERVER_MAP) {
            for (String key : OBSERVER_MAP.keySet()) {
                if (key.contains(url)) {
                    url = key;
                    break;
                }
            }
            OBSERVER_MAP.remove(url);
        }
    }

}
