package com.luckyaf.netwatch;

import okhttp3.MediaType;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/17
 */
@SuppressWarnings("unused")
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

    private String _type;
    ContentType(String type){
        _type = type;
    }


    @Override
    public String toString() {
        return _type;
    }

    public MediaType toMediaType(){
        return MediaType.parse(_type);
    }

}
