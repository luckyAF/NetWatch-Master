package com.luckyaf.netwatch.callBack;

import okhttp3.ResponseBody;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/30
 */

public interface SuccessCallBack {
    void onSuccess(ResponseBody responseBody);
}
