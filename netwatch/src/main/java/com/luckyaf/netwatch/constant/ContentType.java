package com.luckyaf.netwatch.constant;

import okhttp3.MediaType;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/27
 */
public enum ContentType {

    /**
     * application/json
     */
    JSON("application/json; charset=utf-8"),
    /**
     *text
     */
    TEXT("text/plain"),
    /**
     *AUDIO
     */
    AUDIO("audio/*"),
    /**
     *
     */
    VIDEO("video/*"),
    /**
     *image
     */
    IMAGE("image/*; charset=utf-8"),
    /**
     *java
     */
    JAVA("java/*"),
    /**
     *msg
     */
    MESSAGE("message/rfc822"),
    /**
     *application/vnd.android.package-archive
     */
    APK("application/vnd.android.package-archive"),
    /**
     *multipart/form-data
     */
    FORM("multipart/form-data;");

    private String type;
    ContentType(String type){
        this.type = type;
    }


    @Override
    public String toString() {
        return type;
    }

    public MediaType toMediaType(){
        return MediaType.parse(type);
    }
}
