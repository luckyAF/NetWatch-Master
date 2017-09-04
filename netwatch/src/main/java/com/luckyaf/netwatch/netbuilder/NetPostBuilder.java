package com.luckyaf.netwatch.netbuilder;

import android.content.Context;
import android.support.annotation.NonNull;

import com.luckyaf.netwatch.NetWatch;
import com.luckyaf.netwatch.callBack.CancelCallBack;
import com.luckyaf.netwatch.callBack.ErrorCallBack;
import com.luckyaf.netwatch.callBack.StartCallBack;
import com.luckyaf.netwatch.callBack.SuccessCallBack;
import com.luckyaf.netwatch.interceptor.HeadersInterceptor;
import com.luckyaf.netwatch.interceptor.ParamsInterceptor;
import com.luckyaf.netwatch.observer.CommonObserver;

import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.luckyaf.netwatch.NetWatch.putRequest;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/9/1
 */
@SuppressWarnings("unused")
public class NetPostBuilder extends NetBaseBuilder{

    private StartCallBack mStartCallBack;
    private CancelCallBack mCancelCallBack;
    private SuccessCallBack mSuccessCallBack;
    private ErrorCallBack mErrorCallBack;

    private String jsonString;
    private Boolean usingJson = false;

    public NetPostBuilder(@NonNull String url) {
        if (NetWatch.getInstance() == null) {
            throw new NullPointerException("NetWatch has not be initialized");
        }
        this.url = url;
    }

    public NetPostBuilder onStart(StartCallBack callBack){
        this.mStartCallBack = callBack;
        return this;
    }

    public NetPostBuilder onCancel(CancelCallBack callBack){
        this.mCancelCallBack = callBack;
        return this;
    }

    public NetPostBuilder onSuccess(SuccessCallBack callBack){
        this.mSuccessCallBack = callBack;
        return this;
    }

    public NetPostBuilder onError(ErrorCallBack callBack){
        this.mErrorCallBack = callBack;
        return this;
    }

    @SuppressWarnings("unchecked")
    public void run(){
        if (!allReady()) {
            return;
        }
        CommonObserver commonObserver = new CommonObserver()
                .startCallBack(mStartCallBack)
                .cancelCallBack(mCancelCallBack)
                .successCallBack(mSuccessCallBack)
                .errorCallBack(mErrorCallBack);
        putRequest(tag, url, commonObserver);
        if(usingJson){
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                    jsonString);
            NetWatch.getService()
                    .post(checkUrl(url),checkHeaders(headers),requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(commonObserver);
        }else{
            NetWatch.getService()
                    .post(checkUrl(url), checkHeaders(headers), checkParams(params))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(commonObserver);
        }
    }

    public NetPostBuilder headersInterceptor(HeadersInterceptor headersInterceptor){
        this.mHeadersInterceptor = headersInterceptor;
        return this;
    }

    public NetPostBuilder paramsInterceptor(ParamsInterceptor paramsInterceptor){
        this.mParamsInterceptor = paramsInterceptor;
        return this;
    }


    /**
     * 检查网络是否连接，未连接跳转到网络设置界面
     */
    public NetPostBuilder checkNetWork(Context context) {
        this.mContext = context;
        checkNetWork = true;
        return this;
    }

    public NetPostBuilder tag(@NonNull Object tag) {
        this.tag = tag;
        return this;
    }
    public NetPostBuilder headers(@NonNull Map<String, Object> headers) {
        this.headers.putAll(headers);
        return this;
    }
    public NetPostBuilder header(@NonNull String key, Object value) {
        this.headers.put(key, value);
        return this;
    }
    public NetPostBuilder params(@NonNull Map<String, Object> params) {
        this.params.putAll(params);
        return this;
    }
    public NetPostBuilder param(@NonNull String key, Object value) {
        this.params.put(key, value);
        return this;
    }

    public NetPostBuilder jsonString(String jsonParam){
        this.jsonString = jsonParam;
        this.usingJson = true;
        return this;
    }
}
