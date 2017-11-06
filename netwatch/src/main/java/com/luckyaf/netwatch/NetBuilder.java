package com.luckyaf.netwatch;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.luckyaf.netwatch.callBack.CommonCallBack;
import com.luckyaf.netwatch.callBack.DownloadCallBack;
import com.luckyaf.netwatch.callBack.DownloadSuccessCallback;
import com.luckyaf.netwatch.callBack.ErrorCallBack;
import com.luckyaf.netwatch.callBack.ProgressCallBack;
import com.luckyaf.netwatch.callBack.SuccessCallBack;
import com.luckyaf.netwatch.callBack.UploadCallBack;
import com.luckyaf.netwatch.interceptor.HeadersInterceptor;
import com.luckyaf.netwatch.interceptor.ParamsInterceptor;
import com.luckyaf.netwatch.observer.CommonObserver;
import com.luckyaf.netwatch.observer.DownloadObserver;
import com.luckyaf.netwatch.observer.UploadObserver;
import com.luckyaf.netwatch.upload.UploadFileBody;
import com.luckyaf.netwatch.upload.UploadRequestBody;
import com.luckyaf.netwatch.utils.FileUtil;
import com.luckyaf.netwatch.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.luckyaf.netwatch.NetWatch.putRequest;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/14
 */
public class NetBuilder {
    /**
     * 头部参数
     */
    private Map<String, Object> headers = new HashMap<>();
    /**
     * 参数
     */
    private Map<String, Object> params = new HashMap<>();
    private String url;
    private Object tag;
    private Context mContext;
    /**
     * 是否检查网络连接
     */
    private boolean checkNetWork = false;
    private SuccessCallBack mSuccessCallBack;
    private ErrorCallBack mErrorCallBack;
    private ProgressCallBack mProgressCallBack;
    private DownloadSuccessCallback mDownloadSuccessCallback;
    private String filePath;
    private String fileName;

    private ParamsInterceptor mParamsInterceptor;
    private HeadersInterceptor mHeadersInterceptor;

    NetBuilder(@NonNull Context context, @NonNull String url) {
        if (NetWatch.getInstance() == null) {
            throw new NullPointerException("NetWatch has not be initialized");
        }
        this.url = url;
        this.mContext = context;
    }

    NetBuilder(@NonNull String url) {
        if (NetWatch.getInstance() == null) {
            throw new NullPointerException("NetWatch has not be initialized");
        }
        this.url = url;
    }

    NetBuilder(@NonNull Context context) {
        if (NetWatch.getInstance() == null) {
            throw new NullPointerException("NetWatch has not be initialized");
        }
        this.mContext = context;
    }

    public NetBuilder headersInterceptor(HeadersInterceptor headersInterceptor){
        this.mHeadersInterceptor = headersInterceptor;
        return this;
    }

    public NetBuilder paramsInterceptor(ParamsInterceptor paramsInterceptor){
        this.mParamsInterceptor = paramsInterceptor;
        return this;
    }

    public NetBuilder tag(@NonNull Object tag) {
        this.tag = tag;
        return this;
    }

    public NetBuilder headers(@NonNull Map<String, Object> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public NetBuilder header(@NonNull String key, Object value) {
        this.headers.put(key, value);
        return this;
    }

    public NetBuilder params(@NonNull Map<String, Object> params) {
        this.params.putAll(params);
        return this;
    }

    public NetBuilder param(@NonNull String key, Object value) {
        this.params.put(key, value);
        return this;
    }

    public NetBuilder success(SuccessCallBack successCallBack) {
        this.mSuccessCallBack = successCallBack;
        return this;
    }

    public NetBuilder error(ErrorCallBack errorCallBack) {
        this.mErrorCallBack = errorCallBack;
        return this;

    }

    public NetBuilder progress(ProgressCallBack progressCallBack){
        this.mProgressCallBack = progressCallBack;
        return this;
    }

    public NetBuilder downloadSuccess(DownloadSuccessCallback downloadSuccessCallback){
        this.mDownloadSuccessCallback = downloadSuccessCallback;
        return this;
    }

    public NetBuilder filePath(String filePath){
        this.filePath = filePath;
        return this;
    }

    public NetBuilder fileName(String fileName){
        this.fileName = fileName;
        return this;
    }


    /**
     * 检查网络是否连接，未连接跳转到网络设置界面
     */
    public NetBuilder checkNetWork(Context context) {
        this.mContext = context;
        checkNetWork = true;
        return this;
    }


    @CheckResult
    private String checkUrl(String url) {
        if (NetWatch.checkNULL(url)) {
            throw new NullPointerException("absolute url can not be empty");
        }
        return url;
    }


    /**
     * 请求前初始检查
     */
    private boolean allReady() {
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


    public void get(final Map<String, Object> headers,final Map<String, Object> params, final CommonCallBack commonCallBack) {
        if (!allReady()) {
            return;
        }
        if(TextUtils.isEmpty(url)){
            if(mErrorCallBack != null){
                mErrorCallBack.onError(message("url不能为空"));
            }
            if(commonCallBack != null){
                commonCallBack.onError(message("url不能为空"));
            }
            return;
        }
        if(headers != null){
            this.headers.putAll(headers);
        }
        if(params != null){
            this.params.putAll(params);
        }
        CommonObserver commonObserver = new CommonObserver()
                .commonCallBack(commonCallBack)
                .successCallBack(mSuccessCallBack)
                .errorCallBack(mErrorCallBack);
        putRequest(tag, url, commonObserver);
        NetWatch.getService()
                .get(checkUrl(this.url), checkHeaders(this.headers), checkParams(this.params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(commonObserver);
    }

    @SuppressWarnings("unchecked")
    public void get(final Map<String, Object> params, final CommonCallBack commonCallBack) {
        if (!allReady()) {
            return;
        }
        if(TextUtils.isEmpty(url)){
            if(mErrorCallBack != null){
                mErrorCallBack.onError(message("url不能为空"));
            }
            if(commonCallBack != null){
                commonCallBack.onError(message("url不能为空"));
            }
            return;
        }
        if(params != null){
            this.params.putAll(params);
        }
        CommonObserver commonObserver = new CommonObserver()
                .commonCallBack(commonCallBack)
                .successCallBack(mSuccessCallBack)
                .errorCallBack(mErrorCallBack);
        putRequest(tag, url, commonObserver);
        NetWatch.getService()
                .get(checkUrl(this.url), checkHeaders(this.headers), checkParams(this.params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(commonObserver);
    }


    @SuppressWarnings("unchecked")
    public void get() {
        if (!allReady()) {
            return;
        }
        if(TextUtils.isEmpty(url)){
            if(mErrorCallBack != null){
                mErrorCallBack.onError(message("url不能为空"));
            }
            return;
        }
        CommonObserver commonObserver = new CommonObserver()
                .successCallBack(mSuccessCallBack)
                .errorCallBack(mErrorCallBack);
        putRequest(tag, url, commonObserver);
        NetWatch.getService()
                .get(checkUrl(this.url), checkHeaders(this.headers), checkParams(this.params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(commonObserver);
    }

    @SuppressWarnings("unchecked")
    public void post(final Map<String, Object> headers,final Map<String, Object> params, final CommonCallBack commonCallBack) {
        if (!allReady()) {
            return;
        }
        if(TextUtils.isEmpty(url)){
            if(mErrorCallBack != null){
                mErrorCallBack.onError(message("url不能为空"));
            }
            if(commonCallBack != null){
                commonCallBack.onError(message("url不能为空"));
            }
            return;
        }
        if(headers != null){
            this.headers.putAll(headers);
        }
        if(params != null){
            this.params.putAll(params);
        }
        CommonObserver commonObserver = new CommonObserver()
                .commonCallBack(commonCallBack)
                .successCallBack(this.mSuccessCallBack)
                .errorCallBack(this.mErrorCallBack);
        putRequest(tag, url, commonObserver);
        NetWatch.getService()
                .post(checkUrl(this.url), checkHeaders(this.headers), checkParams(this.params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(commonObserver);
    }

    @SuppressWarnings("unchecked")
    public void post(final Map<String, Object> params, final CommonCallBack commonCallBack) {
        if (!allReady()) {
            return;
        }
        if(params != null){
            this.params.putAll(params);
        }
        if(TextUtils.isEmpty(url)){
            if(mErrorCallBack != null){
                mErrorCallBack.onError(message("url不能为空"));
            }
            if(commonCallBack != null){
                commonCallBack.onError(message("url不能为空"));
            }
            return;
        }
        CommonObserver commonObserver = new CommonObserver()
                .commonCallBack(commonCallBack)
                .successCallBack(this.mSuccessCallBack)
                .errorCallBack(this.mErrorCallBack);
        putRequest(tag, url, commonObserver);
        NetWatch.getService()
                .post(checkUrl(this.url), checkHeaders(this.headers), checkParams(this.params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(commonObserver);
    }

    @SuppressWarnings("unchecked")
    public void jsonPost(String jsonParam, final CommonCallBack commonCallBack){
        if (!allReady()) {
            return;
        }
        if(TextUtils.isEmpty(url)){
            if(mErrorCallBack != null){
                mErrorCallBack.onError(message("url不能为空"));
            }
            if(commonCallBack != null){
                commonCallBack.onError(message("url不能为空"));
            }
            return;
        }
        CommonObserver commonObserver = new CommonObserver()
                .commonCallBack(commonCallBack)
                .successCallBack(mSuccessCallBack)
                .errorCallBack(mErrorCallBack);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                jsonParam);
        putRequest(tag, url, commonObserver);
        NetWatch.getService()
                .post(checkUrl(url),checkHeaders(headers),requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(commonObserver);
    }

    @SuppressWarnings("unchecked")
    public void jsonPost(String jsonParam){
        if (!allReady()) {
            return;
        }
        if(TextUtils.isEmpty(url)){
            if(mErrorCallBack != null){
                mErrorCallBack.onError(message("url不能为空"));
            }
            return;
        }
        CommonObserver commonObserver = new CommonObserver()
                .successCallBack(mSuccessCallBack)
                .errorCallBack(mErrorCallBack);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                jsonParam);
        putRequest(tag, url, commonObserver);
        NetWatch.getService()
                .post(checkUrl(url),checkHeaders(headers),requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(commonObserver);
    }

    @SuppressWarnings("unchecked")
    public void post() {
        if (!allReady()) {
            return;
        }
        if(TextUtils.isEmpty(url)){
            if(mErrorCallBack != null){
                mErrorCallBack.onError(message("url不能为空"));
            }
            return;
        }
        CommonObserver commonObserver = new CommonObserver()
                .successCallBack(this.mSuccessCallBack)
                .errorCallBack(this.mErrorCallBack);
        putRequest(tag, url, commonObserver);
        NetWatch.getService()
                .post(checkUrl(this.url), checkHeaders(headers), checkParams(params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(commonObserver);
    }



    /**
     * NetWatch download
     * @param callBack 回调
     */
    public void download(@NonNull String url,DownloadCallBack callBack) {
        download(url,FileUtil.getFileNameWithURL(url), callBack);
    }

    /**
     * @param name     文件名
     * @param callBack 回调
     */
    @SuppressWarnings("WeakerAccess")
    public void download(@NonNull String url,String name, DownloadCallBack callBack) {
        download(url,null,name, callBack);
    }

    /**
     * @param savePath 保存路径
     * @param name     文件名
     * @param callBack 回调
     */
    @SuppressWarnings("WeakerAccess")
    public void download(@NonNull String url,String savePath, String name, DownloadCallBack callBack) {
        String key = FileUtil.generateFileKey(url, FileUtil.getFileNameWithURL(url));
        executeDownload(url,key, savePath, name, callBack);
    }

    /**
     * executeDownload
     * @param key      key
     * @param savePath 保存路径
     * @param name     文件名
     * @param callBack 回调
     */
    @SuppressWarnings("unchecked")
    private void executeDownload(@NonNull String url ,String key, String savePath, String name, final DownloadCallBack callBack) {
        if (!allReady()) {
            return;
        }

        DownloadObserver observer = new DownloadObserver<>(key, savePath, name, mContext)
                .downloadCallBack(callBack)
                .successCallBack(mDownloadSuccessCallback)
                .errorCallBack(mErrorCallBack)
                .progressCallBack(mProgressCallBack);
        NetWatch.putRequest(tag, url, observer);
        NetWatch.getService().download(url)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(observer);
    }

    @SuppressWarnings("unchecked")
    public void download(){
        if (!allReady()) {
            return;
        }
        if(TextUtils.isEmpty(url)){
            if(mErrorCallBack != null){
                mErrorCallBack.onError(message("url不能为空"));
            }
            return;
        }
        if(TextUtils.isEmpty(fileName)){
            fileName = FileUtil.getFileNameWithURL(url);
        }
        String key = FileUtil.generateFileKey(url, fileName);
        DownloadObserver observer = new DownloadObserver<>(key, filePath, fileName, mContext)
                .successCallBack(mDownloadSuccessCallback)
                .errorCallBack(mErrorCallBack)
                .progressCallBack(mProgressCallBack);
        NetWatch.putRequest(tag, this.url, observer);
        NetWatch.getService().download(this.url)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(observer);
    }


    /**
     * @param params   参数
     * @param fileMap  文件map
     * @param callBack 回调
     */
    @SuppressWarnings("unchecked")
    public void upload(String url,final Map<String, Object> params, final Map<String, UploadFileBody> fileMap, UploadCallBack callBack) {
        if (!allReady()) {
            return;
        }
        if(TextUtils.isEmpty(url)){
            if(mErrorCallBack != null){
                mErrorCallBack.onError(message("url不能为空"));
            }
            if(callBack != null){
                callBack.onError(message("url不能为空"));
            }
            return;
        }

        if(params != null){
            this.params.putAll(params);
        }
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, UploadFileBody> entry : fileMap.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue().getFileName(), RequestBody.create(entry.getValue().getMediaType(), entry.getValue().getFile()));
        }
        RequestBody requestBody = null;
        if(fileMap.size() > 0){
            requestBody = builder.build();
        }
        UploadRequestBody uploadRequestBody = new UploadRequestBody(requestBody)
                .uploadCallBack(callBack)
                .progressCallBack(this.mProgressCallBack)
                .errorCallBack(this.mErrorCallBack);

        UploadObserver observer = new UploadObserver<>(uploadRequestBody)
                .uploadCallBack(callBack)
                .successCallBack(this.mSuccessCallBack)
                .errorCallBack(this.mErrorCallBack);
        NetWatch.putRequest(tag, this.url, observer);

        NetWatch.getService().upload(checkUrl(url), checkHeaders(headers), checkParams(this.params), uploadRequestBody)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(observer);

    }

    /**
     * @param params   参数
     * @param fileMap  文件map
     * @param callBack 回调
     */
    @SuppressWarnings("unchecked")
    public void upload(final Map<String, Object> params, final Map<String, UploadFileBody> fileMap, UploadCallBack callBack) {
        if (!allReady()) {
            return;
        }
        if(TextUtils.isEmpty(url)){
            if(mErrorCallBack != null){
                mErrorCallBack.onError(message("url不能为空"));
            }
            if(callBack != null){
                callBack.onError(message("url不能为空"));
            }
            return;
        }
        if(params != null){
            this.params.putAll(params);
        }
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, UploadFileBody> entry : fileMap.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue().getFileName(), RequestBody.create(entry.getValue().getMediaType(), entry.getValue().getFile()));
        }
        RequestBody requestBody = null;
        if(fileMap.size() > 0){
            requestBody = builder.build();
        }
        UploadRequestBody uploadRequestBody = new UploadRequestBody(requestBody)
                .uploadCallBack(callBack)
                .progressCallBack(this.mProgressCallBack)
                .errorCallBack(this.mErrorCallBack);

        UploadObserver observer = new UploadObserver<>(uploadRequestBody)
                .uploadCallBack(callBack)
                .successCallBack(this.mSuccessCallBack)
                .errorCallBack(this.mErrorCallBack);
        NetWatch.putRequest(tag, this.url, observer);

        NetWatch.getService().upload(checkUrl(this.url), checkHeaders(headers), checkParams(this.params), uploadRequestBody)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(observer);

    }

    @SuppressWarnings("unchecked")
    public void upload(final Map<String, UploadFileBody> fileMap){
        if (!allReady()) {
            return;
        }
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, UploadFileBody> entry : fileMap.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue().getFileName(), RequestBody.create(entry.getValue().getMediaType(), entry.getValue().getFile()));
        }
        RequestBody requestBody = builder.build();
        UploadRequestBody uploadRequestBody = new UploadRequestBody(requestBody)
                .progressCallBack(this.mProgressCallBack)
                .errorCallBack(this.mErrorCallBack);
        UploadObserver observer = new UploadObserver<>(uploadRequestBody)
                .successCallBack(this.mSuccessCallBack)
                .errorCallBack(this.mErrorCallBack);
        NetWatch.putRequest(tag, this.url, observer);
        NetWatch.getService().upload(checkUrl(this.url), checkHeaders(this.headers), checkParams(this.params), uploadRequestBody)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(observer);
    }



    @CheckResult
    private  Map<String, Object> checkParams(Map<String, Object> params) {
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
    private  Map<String, Object> checkHeaders(Map<String, Object> headers) {
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

    @CheckResult
    private Throwable message(String mes) {
        if (NetWatch.checkNULL(mes)) {
            mes = "服务器异常，请稍后再试";
        }
        if ("timeout".equals(mes) || "SSL handshake timed out".equals(mes)) {
            mes =  "网络请求超时";
        }
        return new Throwable(mes);

    }
}
