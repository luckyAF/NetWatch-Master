package com.luckyaf.netwatch.callBack;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/15
 */

public interface DownloadCallBack {
    void onStart(String key);
    void onCancel();
    void onComplete();
    void onError(Throwable throwable);
    void onProgress(String key, int progress, long fileSizeDownloaded, long totalSize);
    void onSuccess(String key, String path, String name, long fileSize);

}
