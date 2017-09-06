package com.luckyaf.netwatch.interceptor;

import java.util.Map;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/14
 */
public interface ParamsInterceptor {
    Map<String, Object> checkParams(Map<String, Object> params);
}
