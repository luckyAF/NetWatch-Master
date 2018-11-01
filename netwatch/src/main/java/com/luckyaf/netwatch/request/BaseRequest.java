package com.luckyaf.netwatch.request;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.text.TextUtils;


import com.luckyaf.netwatch.NetWatch;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.luckyaf.netwatch.Interceptor.DefaultCacheInterceptor.SINGLE_CACHE_TIME;
import static com.luckyaf.netwatch.Interceptor.StupidCacheInterceptor.STUPID_CACHE_TIME;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/20
 */
@SuppressWarnings("unused")
public abstract class BaseRequest <R extends BaseRequest> implements Serializable {

    private static final long serialVersionUID = 3035449110416235175L;
    protected transient Object tag;
    protected String url;

    /**
     * 头部参数
     */
    protected LinkedHashMap<String, Object> headers = new LinkedHashMap<>();


    protected LinkedHashMap<String, Object> params = new LinkedHashMap<>();


    BaseRequest(@NonNull String url){
        this.url = url;
    }

    @SuppressWarnings("unchecked")
    public R tag(@NonNull Object tag){
        this.tag = tag;
        return (R)this;
    }

    @SuppressWarnings("unchecked")
    public R headers(@NonNull Map<String, Object> headers) {
        this.headers.putAll(headers);
        return (R)this;
    }
    @SuppressWarnings("unchecked")
    public R header(@NonNull String key, Object value) {
        this.headers.put(key, value);
        return (R)this;
    }
    @SuppressWarnings("unchecked")
    public R params(@NonNull Map<String, Object> params) {
        this.params.putAll(params);
        return (R)this;
    }
    @SuppressWarnings("unchecked")
    public R param(@NonNull String key, Object value) {
        this.params.put(key,value);
        return (R)this;
    }

    /**
     * 强制不使用cache
     */
    @SuppressWarnings("unchecked")
    public R forceNew(boolean required){
        if(required) {
            this.headers.put("Cache-Control", "no-cache");
        }
        return (R)this;
    }

    @SuppressWarnings("unchecked")
    public R cacheTime(int seconds){
        this.headers.put(SINGLE_CACHE_TIME, seconds);
        return (R)this;
    }

    @SuppressWarnings("unchecked")
    public R stupidCacheTime(int seconds){
        this.headers.put(STUPID_CACHE_TIME, seconds);
        return (R)this;
    }

    @CheckResult
    protected Throwable message(String mes) {
        if (TextUtils.isEmpty(mes)) {
            mes = "服务器异常，请稍后再试";
        }
        if ("timeout".equals(mes) || "SSL handshake timed out".equals(mes)) {
            mes = "网络请求超时";
        }
        return new Throwable(mes);

    }


}
