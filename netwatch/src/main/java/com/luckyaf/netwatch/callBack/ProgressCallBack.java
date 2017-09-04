package com.luckyaf.netwatch.callBack;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/30
 */

public interface ProgressCallBack {
    void onProgress(int progress,long speed, long downloadedSize, long totalSize);

}
