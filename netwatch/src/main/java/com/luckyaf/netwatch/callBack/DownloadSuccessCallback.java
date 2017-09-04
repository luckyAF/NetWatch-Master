package com.luckyaf.netwatch.callBack;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/30
 */

public interface DownloadSuccessCallback {
    void onSuccess(String key, String path, String name, long fileSize);
}
