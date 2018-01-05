package com.luckyaf.netwatch.interceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/9/5
 */

public class CommonHeaders implements HeadersInterceptor {

    private Map<String, Object> mCommonMap = new HashMap<>();

    public CommonHeaders(Map<String, Object> map) {
        this.mCommonMap = map;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> checkHeaders(Map<String, Object> headers) {
        for (Map.Entry entry : mCommonMap.entrySet()) {
            headers.put(entry.getKey().toString(), entry.getValue());
        }
        return headers;
    }
}
