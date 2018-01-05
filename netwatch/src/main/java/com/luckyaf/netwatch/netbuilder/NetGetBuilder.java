package com.luckyaf.netwatch.netbuilder;

import android.content.Context;
import android.support.annotation.NonNull;

import com.luckyaf.netwatch.NetWatch;
import com.luckyaf.netwatch.callback.CancelCallBack;
import com.luckyaf.netwatch.callback.ErrorCallBack;
import com.luckyaf.netwatch.callback.StartCallBack;
import com.luckyaf.netwatch.callback.SuccessCallBack;
import com.luckyaf.netwatch.interceptor.HeadersInterceptor;
import com.luckyaf.netwatch.interceptor.ParamsInterceptor;
import com.luckyaf.netwatch.observer.CommonObserver;

import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.luckyaf.netwatch.NetWatch.putRequest;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/9/1
 */
@SuppressWarnings("unused")
public class NetGetBuilder extends NetBaseBuilder {

    private StartCallBack mStartCallBack;
    private CancelCallBack mCancelCallBack;
    private SuccessCallBack mSuccessCallBack;
    private ErrorCallBack mErrorCallBack;


    public NetGetBuilder(@NonNull String url) {
        if (NetWatch.getInstance() == null) {
            throw new NullPointerException("NetWatch has not be initialized");
        }
        this.url = url;
    }

    public NetGetBuilder onStart(StartCallBack callBack) {
        this.mStartCallBack = callBack;
        return this;
    }

    public NetGetBuilder onCancel(CancelCallBack callBack) {
        this.mCancelCallBack = callBack;
        return this;
    }

    public NetGetBuilder onSuccess(SuccessCallBack callBack) {
        this.mSuccessCallBack = callBack;
        return this;
    }

    public NetGetBuilder onError(ErrorCallBack callBack) {
        this.mErrorCallBack = callBack;
        return this;
    }

    @SuppressWarnings("unchecked")
    public void run() {
        if (!allReady()) {
            return;
        }
        CommonObserver commonObserver = new CommonObserver()
                .startCallBack(mStartCallBack)
                .cancelCallBack(mCancelCallBack)
                .successCallBack(mSuccessCallBack)
                .errorCallBack(mErrorCallBack);
        putRequest(tag, url, commonObserver);

        NetWatch.getService()
                .get(checkUrl(url), checkHeaders(headers), checkParams(params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(commonObserver);

    }

    public NetGetBuilder headersInterceptor(HeadersInterceptor headersInterceptor) {
        this.mHeadersInterceptor = headersInterceptor;
        return this;
    }

    public NetGetBuilder paramsInterceptor(ParamsInterceptor paramsInterceptor) {
        this.mParamsInterceptor = paramsInterceptor;
        return this;
    }


    /**
     * 检查网络是否连接，未连接跳转到网络设置界面
     */
    public NetGetBuilder checkNetWork(Context context) {
        this.mContext = context;
        checkNetWork = true;
        return this;
    }

    public NetGetBuilder tag(@NonNull Object tag) {
        this.tag = tag;
        return this;
    }

    public NetGetBuilder headers(@NonNull Map<String, Object> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public NetGetBuilder header(@NonNull String key, Object value) {
        this.headers.put(key, value);
        return this;
    }

    public NetGetBuilder params(@NonNull Map<String, Object> params) {
        this.params.putAll(params);
        return this;
    }

    public NetGetBuilder param(@NonNull String key, Object value) {
        this.params.put(key, value);
        return this;
    }

}
