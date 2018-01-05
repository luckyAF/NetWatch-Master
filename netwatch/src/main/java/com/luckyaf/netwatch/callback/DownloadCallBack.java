package com.luckyaf.netwatch.callback;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/15
 */

public interface DownloadCallBack {
    /**
     * 开始
     * @param key
     */
    void onStart(String key);

    /**
     * 取消
     */
    void onCancel();

    /**
     * 结束
     */
    void onComplete();

    /**
     * 错误
     * @param throwable
     */
    void onError(Throwable throwable);

    /**
     * 进行中
     * @param key
     * @param progress
     * @param speed
     * @param downloadedSize
     * @param totalSize
     */
    void onProgress(String key, int progress, long speed, long downloadedSize, long totalSize);

    /**
     * 成功
     * @param key
     * @param path
     * @param name
     * @param fileSize
     */
    void onSuccess(String key, String path, String name, long fileSize);

}
