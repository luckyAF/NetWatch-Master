package com.luckyaf.netwatch.callback;

import okhttp3.ResponseBody;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/11/1
 */
public interface UploadCallBack {
    /**
     * 开始
     */
    void onStart();

    /**
     * 取消
     */
    void onCancel();

    /**
     * 上传返回
     * @param responseBody
     */
    void onNext(ResponseBody responseBody);

    /**
     * 完成
     */
    void onComplete();

    /**
     * 错误
     * @param throwable
     */
    void onError(Throwable throwable);

    /**
     * 进度
     * @param progress   进度
     * @param speed      速度
     * @param transformed  已上传大小
     * @param total        总大小
     */
    void onProgress(float progress, long speed, long transformed, long total);


    /**
     * 成功
     */
    void onSuccess();
}
