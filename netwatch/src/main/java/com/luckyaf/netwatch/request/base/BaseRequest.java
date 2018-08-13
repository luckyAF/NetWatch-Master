package com.luckyaf.netwatch.request.base;

import com.luckyaf.netwatch.constant.CacheMode;

import java.io.Serializable;

import okhttp3.OkHttpClient;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/5/28
 */
@SuppressWarnings("unused")
public abstract class BaseRequest <R extends BaseRequest> implements Serializable {
    /**
     * url
     */
    protected String url;
    /**
     * base url
     */
    protected String baseUrl;
    /**
     *  okhttp  客户端
     */
    protected transient OkHttpClient client;
    /**
     * 标签
     */
    protected transient Object tag;
    /**
     * 重连次数
     */
    protected int retryCount;
    /**
     * 缓存模式
     */
    protected CacheMode cacheMode;
    /**
     *  缓存key
     */
    protected String cacheKey;
    /**
     * 缓存时间
     */
    protected long cacheTime;


}
