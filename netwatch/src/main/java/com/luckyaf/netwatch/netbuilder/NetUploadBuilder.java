package com.luckyaf.netwatch.netbuilder;

import android.content.Context;
import android.support.annotation.NonNull;

import com.luckyaf.netwatch.NetWatch;
import com.luckyaf.netwatch.callBack.CancelCallBack;
import com.luckyaf.netwatch.callBack.ErrorCallBack;
import com.luckyaf.netwatch.callBack.ProgressCallBack;
import com.luckyaf.netwatch.callBack.StartCallBack;
import com.luckyaf.netwatch.callBack.SuccessCallBack;
import com.luckyaf.netwatch.interceptor.HeadersInterceptor;
import com.luckyaf.netwatch.interceptor.ParamsInterceptor;
import com.luckyaf.netwatch.observer.UploadObserver;
import com.luckyaf.netwatch.upload.UploadFileBody;
import com.luckyaf.netwatch.upload.UploadRequestBody;

import java.util.HashMap;
import java.util.Map;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/9/1
 */

@SuppressWarnings("unused")
public class NetUploadBuilder extends NetBaseBuilder{

    private StartCallBack mStartCallBack;
    private CancelCallBack mCancelCallBack;
    private SuccessCallBack mSuccessCallBack;
    private ProgressCallBack mProgressCallBack;
    private ErrorCallBack mErrorCallBack;
    private Map<String,UploadFileBody> mFileBodyMap = new HashMap<>();

    public NetUploadBuilder(@NonNull String url) {
        if (NetWatch.getInstance() == null) {
            throw new NullPointerException("NetWatch has not be initialized");
        }
        this.url = url;
    }

    public NetUploadBuilder onStart(StartCallBack callBack){
        this.mStartCallBack = callBack;
        return this;
    }

    public NetUploadBuilder onCancel(CancelCallBack callBack){
        this.mCancelCallBack = callBack;
        return this;
    }

    public NetUploadBuilder onSuccess(SuccessCallBack callBack){
        this.mSuccessCallBack = callBack;
        return this;
    }

    public NetUploadBuilder onProgress(ProgressCallBack callBack){
        this.mProgressCallBack = callBack;
        return this;
    }

    public NetUploadBuilder onError(ErrorCallBack callBack){
        this.mErrorCallBack = callBack;
        return this;
    }

    @SuppressWarnings("unchecked")
    public void run(){
        if (!allReady()) {
            return;
        }
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, UploadFileBody> entry : this.mFileBodyMap.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue().getFileName(), RequestBody.create(entry.getValue().getMediaType(), entry.getValue().getFile()));
        }
        RequestBody requestBody = null;
        if(this.mFileBodyMap.size() > 0){
            requestBody = builder.build();
        }
        UploadRequestBody uploadRequestBody = new UploadRequestBody(requestBody)
                .progressCallBack(this.mProgressCallBack)
                .errorCallBack(this.mErrorCallBack);

        UploadObserver observer = new UploadObserver<>(uploadRequestBody)
                .startCallBack(mStartCallBack)
                .cancelCallBack(mCancelCallBack)
                .successCallBack(mSuccessCallBack)
                .errorCallBack(mErrorCallBack);
        NetWatch.putRequest(tag, this.url, observer);

        NetWatch.getService().upload(checkUrl(this.url), checkHeaders(headers), checkParams(this.params), uploadRequestBody)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(observer);
    }

    public NetUploadBuilder headersInterceptor(HeadersInterceptor headersInterceptor){
        this.mHeadersInterceptor = headersInterceptor;
        return this;
    }

    public NetUploadBuilder paramsInterceptor(ParamsInterceptor paramsInterceptor){
        this.mParamsInterceptor = paramsInterceptor;
        return this;
    }


    /**
     * 检查网络是否连接，未连接跳转到网络设置界面
     */
    public NetUploadBuilder checkNetWork(Context context) {
        this.mContext = context;
        checkNetWork = true;
        return this;
    }

    public NetUploadBuilder tag(@NonNull Object tag) {
        this.tag = tag;
        return this;
    }
    public NetUploadBuilder headers(@NonNull Map<String, Object> headers) {
        this.headers.putAll(headers);
        return this;
    }
    public NetUploadBuilder header(@NonNull String key, Object value) {
        this.headers.put(key, value);
        return this;
    }
    public NetUploadBuilder params(@NonNull Map<String, Object> params) {
        this.params.putAll(params);
        return this;
    }
    public NetUploadBuilder param(@NonNull String key, Object value) {
        this.params.put(key, value);
        return this;
    }

    public NetUploadBuilder files(@NonNull Map<String,UploadFileBody> fileBodyMap){
        this.mFileBodyMap.putAll(fileBodyMap);
        return this;
    }

    public NetUploadBuilder file(@NonNull String key,UploadFileBody fileBody ){
        this.mFileBodyMap.put(key,fileBody);
        return this;
    }

}
