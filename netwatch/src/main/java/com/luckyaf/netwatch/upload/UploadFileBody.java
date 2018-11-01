package com.luckyaf.netwatch.upload;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.io.Serializable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 类描述：上传文件
 *
 * @author Created by luckyAF on 2018/10/31
 */
public class UploadFileBody implements Serializable {
    private static final long serialVersionUID = -5737317136784102713L;
    private String key;
    private MediaType mediaType;
    private File file;
    private String fileName;
    public UploadFileBody(@NonNull String key,MediaType mediaType, @NonNull File file) {
        this.key = key;
        this.mediaType = mediaType;
        this.file = file;
    }

    public UploadFileBody(@NonNull String key,MediaType mediaType, @NonNull File file, String fileName) {
        this.key = key;
        this.mediaType = mediaType;
        this.file = file;
        this.fileName = fileName;
    }

    public String getKey() {
        return key;
    }


    public MediaType getMediaType() {
        return this.mediaType;
    }

    public File getFile() {
        return this.file;
    }

    public String getFileName() {
        return TextUtils.isEmpty(fileName)?file.getName():fileName;
    }

    public RequestBody toRequestBody(){
        return RequestBody.create(mediaType,file);
    }
}
