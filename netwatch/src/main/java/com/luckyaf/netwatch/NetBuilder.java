package com.luckyaf.netwatch;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.luckyaf.netwatch.callBack.CommonCallBack;
import com.luckyaf.netwatch.callBack.DownloadCallBack;
import com.luckyaf.netwatch.callBack.UploadCallBack;
import com.luckyaf.netwatch.observer.CommonObserver;
import com.luckyaf.netwatch.observer.DownloadObserver;
import com.luckyaf.netwatch.observer.UploadObserver;
import com.luckyaf.netwatch.upload.UploadFileBody;
import com.luckyaf.netwatch.upload.UploadRequestBody;
import com.luckyaf.netwatch.utils.FileUtil;
import com.luckyaf.netwatch.utils.NetUtils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import static com.luckyaf.netwatch.NetWatch.checkHeaders;
import static com.luckyaf.netwatch.NetWatch.checkParams;
import static com.luckyaf.netwatch.NetWatch.putRequest;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/14
 */
@SuppressWarnings("unused")
public class NetBuilder {
    private Map<String, String> headers = new HashMap<>();
    private String url;
    private Object tag;
    private Context mContext;
    private boolean checkNetConnected = false;

    private Map<Object, Observable<ResponseBody>> downMaps = new HashMap<Object, Observable<ResponseBody>>() {
    };

    NetBuilder(@NonNull Context context,@NonNull String url) {
        if (NetWatch.getInstance() == null) {
            throw new NullPointerException("HttpUtil has not be initialized");
        }
        this.url = url;
        this.mContext = context;
    }
    NetBuilder(@NonNull Context context) {
        if (NetWatch.getInstance() == null) {
            throw new NullPointerException("HttpUtil has not be initialized");
        }
        this.mContext = context;
    }


    public NetBuilder tag(@NonNull Object tag) {
        this.tag = tag;
        return this;
    }

    public NetBuilder headers(@NonNull Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public NetBuilder header(@NonNull String key, String value) {
        this.headers.put(key, value);
        return this;
    }


    /**
     * 检查网络是否连接，未连接跳转到网络设置界面
     */
    public NetBuilder isConnected(@NonNull Context context) {
        checkNetConnected = true;
        mContext = context;
        return this;
    }


    @CheckResult
    private String checkUrl(String url) {
        if (NetWatch.checkNULL(url)) {
            throw new NullPointerException("absolute url can not be empty");
        }
        return url;
    }

    @CheckResult
    public String message(String mes) {
        if (NetWatch.checkNULL(mes)) {
            mes = "服务器异常，请稍后再试";
        }
        if (mes.equals("timeout") || mes.equals("SSL handshake timed out")) {
            return "网络请求超时";
        } else {
            return mes;
        }

    }

    /**
     * 请求前初始检查
     */
    private boolean allReady() {
        if (checkNetConnected && mContext != null) {
            return true;
        }
        if (!NetUtils.isConnected(mContext)) {
            Toast.makeText(mContext, "检测到网络已关闭，请先打开网络", Toast.LENGTH_SHORT).show();
            NetUtils.openSetting(mContext);//跳转到网络设置界面
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public void get(final Map<String, Object> params, final CommonCallBack commonCallBack) {
        if (!allReady()) {
            return;
        }
        CommonObserver commonObserver = new CommonObserver(commonCallBack);
        putRequest(tag,url,commonObserver);
        NetWatch.getService()
                .get(checkUrl(this.url), checkHeaders(headers), checkParams(params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(commonObserver);
    }

    @SuppressWarnings("unchecked")
    public void post(final Map<String, Object> params, final CommonCallBack commonCallBack) {
        if (!allReady()) {
            return;
        }
        CommonObserver commonObserver = new CommonObserver(commonCallBack);
        putRequest(tag,url,commonObserver);
        NetWatch.getService()
                .post(checkUrl(this.url), checkHeaders(headers), checkParams(params))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(commonObserver);
    }

    /**
     * NetWatch download
     *
     * @param url   下载地址
     * @param callBack    回调
     */
    public void download(String url, DownloadCallBack callBack) {
        download(url, FileUtil.getFileNameWithURL(url), callBack);
    }

    /**
     * @param url             下载地址
     * @param name             文件名
     * @param callBack         回调
     */
    public void download(String url, String name, DownloadCallBack callBack) {
        download(url, null, name, callBack);
    }

    /**
     * @param url                 下载地址
     * @param savePath            保存路径
     * @param name                文件名
     * @param callBack            回调
     */
    public void download( String url, String savePath, String name, DownloadCallBack callBack) {
        String  key = FileUtil.generateFileKey(url, FileUtil.getFileNameWithURL(url));
        executeDownload(key, url, savePath, name, callBack);
    }

    /**
     * executeDownload
     *
     * @param key                key
     * @param url                下载地址
     * @param savePath           保存路径
     * @param name               文件名
     * @param callBack           回调
     */
    @SuppressWarnings("unchecked")
    private void executeDownload(String key, String url, String savePath, String name, final DownloadCallBack callBack) {
        if (!allReady()) {
            return;
        }
        Observable<ResponseBody> downObservable;
        if (downMaps.get(key) == null) {
            downObservable = NetWatch.getService().download(url);
        } else {
            downObservable = downMaps.get(key);
        }
        downMaps.put(key, downObservable);
        DownloadObserver observer = new DownloadObserver<>(key, savePath, name, callBack, mContext);
        NetWatch.putRequest(tag,url,observer);
        downObservable
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(observer);

    }


    /**
     *
     * @param url             上传地址
     * @param params          参数
     * @param fileMap         文件map
     * @param callBack        回调
     */
    @SuppressWarnings("unchecked")
    public void upload(String url, final Map<String, Object> params, final Map<String, UploadFileBody> fileMap, UploadCallBack callBack) {
        if (!allReady()) {
            return;
        }
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for(Map.Entry<String,UploadFileBody> entry: fileMap.entrySet()){
            builder.addFormDataPart(entry.getKey(), entry.getValue().getFile().getName(), RequestBody.create(entry.getValue().getMediaType(), entry.getValue().getFile()));
        }
        RequestBody requestBody = builder.build();
        UploadRequestBody uploadRequestBody = new UploadRequestBody(requestBody,callBack);

        UploadObserver observer = new UploadObserver<>(callBack);
        NetWatch.putRequest(tag,url,observer);

        NetWatch.getService().upload(checkUrl(url), checkHeaders(headers), checkParams(params),uploadRequestBody)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(observer);

    }




}
