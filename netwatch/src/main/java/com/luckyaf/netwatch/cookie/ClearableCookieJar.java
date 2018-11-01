package com.luckyaf.netwatch.cookie;

import okhttp3.CookieJar;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/27
 */
public interface ClearableCookieJar extends CookieJar {

    void clearSession();

    void clear();
}
