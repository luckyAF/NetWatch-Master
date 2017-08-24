package com.luckyaf.netwatch.utils;

import android.os.Looper;
import android.support.annotation.NonNull;

import com.luckyaf.netwatch.ContentType;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/17
 */
@SuppressWarnings("unused")
public class CommonUtils {
    public static final String MULTIPART_FORM_DATA = "multipart/form-data;";
    public static final String MULTIPART_IMAGE_DATA = "image/*; charset=utf-8";
    public static final String MULTIPART_JSON_DATA = "application/json; charset=utf-8";
    public static final String MULTIPART_VIDEO_DATA = "video/*";
    public static final String MULTIPART_AUDIO_DATA = "audio/*";
    public static final String MULTIPART_TEXT_DATA = "text/plain";
    public static final String MULTIPART_APK_DATA = "application/vnd.android.package-archive";
    public static final String MULTIPART_JAVA_DATA = "java/*";
    public static final String MULTIPART_MESSAGE_DATA = "message/rfc822";


    public static <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    public static boolean checkMain() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }


    /**
     * ContentType To String
     * @param type see {@link ContentType}
     * @return String mediaType
     */
    @NonNull
    public static String typeToString(@NonNull ContentType type) {
        switch (type) {
            case APK:
                return MULTIPART_APK_DATA;

            case VIDEO:
                return MULTIPART_VIDEO_DATA;

            case AUDIO:
                return MULTIPART_AUDIO_DATA;

            case JAVA:
                return MULTIPART_JAVA_DATA;

            case IMAGE:
                return MULTIPART_IMAGE_DATA;

            case TEXT:
                return MULTIPART_TEXT_DATA;

            case JSON:
                return MULTIPART_JSON_DATA;

            case FORM:
                return MULTIPART_FORM_DATA;
            case MESSAGE:
                return MULTIPART_MESSAGE_DATA;
            default:
                return MULTIPART_IMAGE_DATA;
        }

    }
}
