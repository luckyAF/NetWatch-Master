package com.luckyaf.netwatch.cookie;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/27
 */
public class NetCookieManager implements ClearableCookieJar {

    @Override
    public void clearSession() {

    }

    @Override
    public void clear() {

    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {

    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        return null;
    }
}
