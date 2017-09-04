package com.luckyaf.netwatch.netbuilder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.luckyaf.netwatch.NetWatch;
import com.luckyaf.netwatch.callBack.CancelCallBack;
import com.luckyaf.netwatch.callBack.DownloadSuccessCallback;
import com.luckyaf.netwatch.callBack.ErrorCallBack;
import com.luckyaf.netwatch.callBack.ProgressCallBack;
import com.luckyaf.netwatch.callBack.StartCallBack;
import com.luckyaf.netwatch.callBack.SuccessCallBack;
import com.luckyaf.netwatch.interceptor.HeadersInterceptor;
import com.luckyaf.netwatch.interceptor.ParamsInterceptor;
import com.luckyaf.netwatch.observer.DownloadObserver;
import com.luckyaf.netwatch.observer.UploadObserver;
import com.luckyaf.netwatch.upload.UploadFileBody;
import com.luckyaf.netwatch.upload.UploadRequestBody;
import com.luckyaf.netwatch.utils.FileUtil;

import java.util.Map;

import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/9/1
 */

public class NetDownloadBuilder extends NetBaseBuilder {

    private StartCallBack mStartCallBack;
    private CancelCallBack mCancelCallBack;
    private DownloadSuccessCallback mSuccessCallBack;
    private ProgressCallBack mProgressCallBack;
    private ErrorCallBack mErrorCallBack;
    private String filePath;
    private String fileName;

    public NetDownloadBuilder(@NonNull String url) {
        if (NetWatch.getInstance() == null) {
            throw new NullPointerException("NetWatch has not be initialized");
        }
        this.url = url;
    }

    public NetDownloadBuilder onStart(StartCallBack callBack){
        this.mStartCallBack = callBack;
        return this;
    }

    public NetDownloadBuilder onCancel(CancelCallBack callBack){
        this.mCancelCallBack = callBack;
        return this;
    }

    public NetDownloadBuilder onSuccess(DownloadSuccessCallback callBack){
        this.mSuccessCallBack = callBack;
        return this;
    }

    public NetDownloadBuilder onProgress(ProgressCallBack callBack){
        this.mProgressCallBack = callBack;
        return this;
    }

    public NetDownloadBuilder onError(ErrorCallBack callBack){
        this.mErrorCallBack = callBack;
        return this;
    }

    @SuppressWarnings("unchecked")
    public void run(){
        if (!allReady()) {
            return;
        }
        if(TextUtils.isEmpty(fileName)){
            fileName = FileUtil.getFileNameWithURL(url);
        }
        if(mContext == null){
            mContext = NetWatch.getApplicationContext();
        }
        String key = FileUtil.generateFileKey(url, fileName);
        DownloadObserver observer = new DownloadObserver<>(key, filePath, fileName, mContext)
                .startCallBack(mStartCallBack)
                .cancelCallBack(mCancelCallBack)
                .errorCallBack(mErrorCallBack)
                .successCallBack(mSuccessCallBack)
                .progressCallBack(mProgressCallBack);
        NetWatch.putRequest(tag, this.url, observer);
        NetWatch.getService().download(this.url)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(observer);
    }

    public NetDownloadBuilder headersInterceptor(HeadersInterceptor headersInterceptor){
        this.mHeadersInterceptor = headersInterceptor;
        return this;
    }

    public NetDownloadBuilder paramsInterceptor(ParamsInterceptor paramsInterceptor){
        this.mParamsInterceptor = paramsInterceptor;
        return this;
    }


    /**
     * 检查网络是否连接，未连接跳转到网络设置界面
     */
    public NetDownloadBuilder checkNetWork(Context context) {
        this.mContext = context;
        checkNetWork = true;
        return this;
    }

    public NetDownloadBuilder tag(@NonNull Object tag) {
        this.tag = tag;
        return this;
    }
    public NetDownloadBuilder headers(@NonNull Map<String, Object> headers) {
        this.headers.putAll(headers);
        return this;
    }
    public NetDownloadBuilder header(@NonNull String key, Object value) {
        this.headers.put(key, value);
        return this;
    }
    public NetDownloadBuilder params(@NonNull Map<String, Object> params) {
        this.params.putAll(params);
        return this;
    }
    public NetDownloadBuilder param(@NonNull String key, Object value) {
        this.params.put(key, value);
        return this;
    }

    public NetDownloadBuilder filePath(String filePath){
        this.filePath = filePath;
        return this;
    }

    public NetDownloadBuilder fileName(String fileName){
        this.fileName = fileName;
        return this;
    }

}
