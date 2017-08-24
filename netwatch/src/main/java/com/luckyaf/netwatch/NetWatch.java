package com.luckyaf.netwatch;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.luckyaf.netwatch.api.RetrofitHttpService;
import com.luckyaf.netwatch.interceptor.HeadersInterceptor;
import com.luckyaf.netwatch.interceptor.ParamsInterceptor;
import com.luckyaf.netwatch.observer.BaseObserver;
import com.luckyaf.netwatch.provider.OkHttpProvider;
import com.luckyaf.netwatch.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Call;
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

    private static volatile NetWatch mInstance;
    private static volatile RetrofitHttpService mService;
    private ParamsInterceptor mParamsInterceptor;
    private HeadersInterceptor mHeadersInterceptor;


    private NetWatch(RetrofitHttpService service, ParamsInterceptor mParamsInterceptor, HeadersInterceptor mHeadersInterceptor) {
        mService = service;
        this.mParamsInterceptor = mParamsInterceptor;
        this.mHeadersInterceptor = mHeadersInterceptor;
    }

    @CheckResult
    public static RetrofitHttpService getService() {
        if (mInstance == null) {
            throw new NullPointerException("HttpUtil has not be initialized");
        }
        return mService;
    }

    @CheckResult
    public static NetWatch getInstance() {
        if (mInstance == null) {
            throw new NullPointerException("HttpUtil has not be initialized");
        }
        return mInstance;
    }

    public static Logger.Builder getLoggerBuilder(Context context){
        return new Logger.Builder(context);
    }


    public static SingletonBuilder init(Context context, String baseUrl){
            Logger.Builder builder = new Logger.Builder(context)
            .setLogSwitch(BuildConfig.DEBUG)// 设置log总开关，包括输出到控制台和文件，默认开
            .setConsoleSwitch(BuildConfig.DEBUG)// 设置是否输出到控制台开关，默认开
            .setGlobalTag(null)// 设置log全局标签，默认为空
            // 当全局标签不为空时，我们输出的log全部为该tag，
            // 为空时，如果传入的tag为空那就显示类名，否则显示tag
            .setLogHeadSwitch(true)// 设置log头信息开关，默认为开
            .setLog2FileSwitch(false)// 打印log时是否存到文件的开关，默认关
            .setDir("")// 当自定义路径为空时，写入应用的/cache/log/目录中
            .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
            .setConsoleFilter(Logger.V)// log的控制台过滤器，和logcat过滤器同理，默认Verbose
            .setFileFilter(Logger.V);// log文件过滤器，和logcat过滤器同理，默认Verbose
        return new SingletonBuilder(context,baseUrl);
    }

    public static NetBuilder open(@NonNull Context context,@NonNull String url){
        return new NetBuilder(context,url);
    }

    public static NetBuilder getNetBuilder(@NonNull Context context){
        return new NetBuilder(context);
    }

    public static class SingletonBuilder {
        private Context applicationContext;
        private String baseUrl;
        private List<String> servers = new ArrayList<>();
        private ParamsInterceptor paramsInterceptor;
        private HeadersInterceptor headersInterceptor;
        private List<Converter.Factory> converterFactories = new ArrayList<>();
        private List<CallAdapter.Factory> adapterFactories = new ArrayList<>();
        OkHttpClient client;

        public SingletonBuilder(Context context, String baseUrl) {
            applicationContext = context.getApplicationContext();
            this.baseUrl = baseUrl;
            client = OkHttpProvider.okHttpClient(applicationContext, baseUrl);
        }

        public SingletonBuilder client(OkHttpClient client) {
            this.client = client;
            return this;
        }


        public SingletonBuilder headersInterceptor(HeadersInterceptor interceptor) {
            this.headersInterceptor = interceptor;
            return this;
        }

        public SingletonBuilder paramsInterceptor(ParamsInterceptor interceptor) {
            this.paramsInterceptor = interceptor;
            return this;
        }


        public SingletonBuilder addServerUrl(String ipUrl) {
            this.servers.add(ipUrl);
            return this;
        }

        public SingletonBuilder serverUrls(List<String> servers) {
            this.servers = servers;
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
                    //.baseUrl("")
                    .client(client).build();
            RetrofitHttpService retrofitHttpService =
                    retrofit.create(RetrofitHttpService.class);

            mInstance = new NetWatch(retrofitHttpService, paramsInterceptor, headersInterceptor);

        }

    }

    @SuppressWarnings("unchecked")
    @CheckResult
    public static Map<String, Object> checkParams(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        if (mInstance.mParamsInterceptor != null) {
            params = mInstance.mParamsInterceptor.checkParams(params);
        }
        //retrofit的params的值不能为null，此处做下校验，防止出错
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() == null) {
                params.put(entry.getKey(), "");
            }
        }
        return params;
    }
    @SuppressWarnings("unchecked")
    public static Map<String, String> checkHeaders(Map<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        if (mInstance.mHeadersInterceptor != null) {
            headers = mInstance.mHeadersInterceptor.checkHeaders(headers);
        }
        //retrofit的params的值不能为null，此处做下校验，防止出错
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getValue() == null) {
                headers.put(entry.getKey(), "");
            }
        }
        return headers;
    }


    @CheckResult
    public static boolean checkNULL(String str) {
        return str == null || "null".equals(str) || "".equals(str);

    }


    private final static Map<String, Call> CALL_MAP = new HashMap<>();

    private final static Map<String, BaseObserver> OBSERVER_MAP = new HashMap<>();


    /**
     * 添加某个请求
     * @param tag  标签
     * @param url url
     * @param observer 请求
     */
    public static synchronized void putRequest(Object tag, String url, BaseObserver observer) {
        if (tag == null)
            return;
        synchronized (OBSERVER_MAP) {
            OBSERVER_MAP.put(tag.toString() + url, observer);
        }
    }


    /**
     * 取消某个界面都所有请求，或者是取消某个tag的所有请求
     * 如果要取消某个tag单独请求，tag需要转入tag+url
     * @param tag  标签
     */
    public static synchronized void cancelRequest(Object tag) {
        if (tag == null)
            return;
        List<String> list = new ArrayList<>();
        synchronized (OBSERVER_MAP) {
            for (Map.Entry<String,BaseObserver> entry:OBSERVER_MAP.entrySet()){
                if(entry.getKey().startsWith(tag.toString())){
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
     * @param url url
     */
    public static synchronized void removeRequest(String url) {
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
