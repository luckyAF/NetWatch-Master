package com.luckyaf.okdownload.utils;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/22
 */
public final class Preconditions {
    private Preconditions(){

    }

    public static <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }
}
