package com.luckyaf.netwatch.interceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/9/5
 */

public class CommonParams implements ParamsInterceptor {

    private Map<String, Object> mCommonMap = new HashMap<>();

    public CommonParams(Map<String, Object> map) {
        this.mCommonMap = map;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> checkParams(Map<String, Object> params) {
        for (Map.Entry entry : mCommonMap.entrySet()) {
            params.put(entry.getKey().toString(), entry.getValue());
        }
        return params;
    }
}
