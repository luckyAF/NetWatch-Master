package com.luckyaf.netwatch.callback;

import okhttp3.ResponseBody;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/17
 */

public interface CommonCallBack {
     /**
      * 取消
      */
     void onCancel();

     /**
      * 完成
      */
     void onComplete();

     /**
      * 错误
      * @param e
      */
     void onError(Throwable e);

     /**
      * 下一步
      * @param responseBody
      */
     void onNext(ResponseBody responseBody);
}
