package com.luckyaf.netwatch.utils;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/24
 */
public class CheckUtils {
    private CheckUtils(){

    }

    public static <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }
}
