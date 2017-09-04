package com.luckyaf.netwatch.callBack;

import okhttp3.ResponseBody;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/17
 */

public interface UploadCallBack {
    void onStart();
    void onCancel();
    void onComplete();
    void onError(Throwable throwable);
    void onProgress(int progress, long speed, long transformed, long total);
    void onSuccess(ResponseBody responseBody);


}
