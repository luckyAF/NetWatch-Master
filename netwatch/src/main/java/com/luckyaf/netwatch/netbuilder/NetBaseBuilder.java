package com.luckyaf.netwatch.netbuilder;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.luckyaf.netwatch.NetBuilder;
import com.luckyaf.netwatch.NetWatch;
import com.luckyaf.netwatch.interceptor.HeadersInterceptor;
import com.luckyaf.netwatch.interceptor.ParamsInterceptor;
import com.luckyaf.netwatch.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/9/1
 */

public class NetBaseBuilder {
    /**
     * 头部参数
     */
    protected Map<String, Object> headers = new HashMap<>();
    /**
     * 参数
     */
    protected Map<String, Object> params = new HashMap<>();
    protected String url;
    protected Object tag;

    protected Context mContext;
    /**
     * 是否检查网络连接
     */
    protected boolean checkNetWork = false;

    protected ParamsInterceptor mParamsInterceptor;
    protected HeadersInterceptor mHeadersInterceptor;


//    public NetBaseBuilder headersInterceptor(HeadersInterceptor headersInterceptor){
//        this.mHeadersInterceptor = headersInterceptor;
//        return this;
//    }
//
//    public NetBaseBuilder paramsInterceptor(ParamsInterceptor paramsInterceptor){
//        this.mParamsInterceptor = paramsInterceptor;
//        return this;
//    }
//    public NetBaseBuilder tag(@NonNull Object tag) {
//        this.tag = tag;
//        return this;
//    }
//
//    public NetBaseBuilder headers(@NonNull Map<String, Object> headers) {
//        this.headers.putAll(headers);
//        return this;
//    }
//
//    public NetBaseBuilder header(@NonNull String key, Object value) {
//        this.headers.put(key, value);
//        return this;
//    }
//
//    public NetBaseBuilder params(@NonNull Map<String, Object> params) {
//        this.params.putAll(params);
//        return this;
//    }
//
//    public NetBaseBuilder param(@NonNull String key, Object value) {
//        this.params.put(key, value);
//        return this;
//    }
//
//    public NetBaseBuilder newClient(OkHttpClient client){
//        this.mClient = client;
//        return this;
//    }
//
//    /**
//     * 检查网络是否连接，未连接跳转到网络设置界面
//     */
//    public NetBaseBuilder checkNetWork(Context context) {
//        this.mContext = context;
//        checkNetWork = true;
//        return this;
//    }


    /**
     * 请求前初始检查
     */
    protected boolean allReady() {
        //如果不需要检查 或者context 为null   不作检查
        if (!checkNetWork || mContext == null) {
            return true;
        }
        if (!NetUtils.isConnected(mContext)) {
            Toast.makeText(mContext, "检测到网络已关闭，请先打开网络", Toast.LENGTH_SHORT).show();
            NetUtils.openSetting(mContext);//跳转到网络设置界面
            return false;
        }
        return true;
    }

    @CheckResult
    protected String checkUrl(String url) {
        if (NetWatch.checkNULL(url)) {
            throw new NullPointerException("absolute url can not be empty");
        }
        return url;
    }


    @SuppressWarnings("unchecked")
    @CheckResult
    protected   Map<String, Object> checkParams(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        if (mParamsInterceptor != null) {
            params = mParamsInterceptor.checkParams(params);
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
    protected  Map<String, Object> checkHeaders(Map<String, Object> headers) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        if (mHeadersInterceptor != null) {
            headers = mHeadersInterceptor.checkHeaders(headers);
        }
        //retrofit的params的值不能为null，此处做下校验，防止出错
        for (Map.Entry<String, Object> entry : headers.entrySet()) {
            if (entry.getValue() == null) {
                headers.put(entry.getKey(), "");
            }
        }
        return headers;
    }


}
