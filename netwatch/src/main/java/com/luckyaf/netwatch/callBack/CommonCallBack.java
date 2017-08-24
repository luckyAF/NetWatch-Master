package com.luckyaf.netwatch.callBack;

import okhttp3.ResponseBody;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/17
 */

public interface CommonCallBack {
     void onCancel();
     void onComplete();
     void onError(Throwable e);
     void onNext(ResponseBody responseBody);
}
