package com.luckyaf.netwatch.provider;

import android.content.Context;

import okhttp3.Cache;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/14
 */

public class CacheProvider {
    Context mContext;

    public CacheProvider(Context context) {
        mContext = context;
    }

    public Cache provideCache() {
        return new Cache(mContext.getCacheDir(), 50*1024 * 1024);
    }
}
