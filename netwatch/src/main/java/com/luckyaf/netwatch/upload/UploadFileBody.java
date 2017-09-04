package com.luckyaf.netwatch.upload;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;

import okhttp3.MediaType;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/17
 */

public class UploadFileBody {
    private MediaType mediaType;
    private File file;
    private String fileName;
    public UploadFileBody(@NonNull MediaType mediaType, @NonNull File file){
        this.mediaType = mediaType;
        this.file = file;
    }

    public UploadFileBody(@NonNull MediaType mediaType, @NonNull File file,@NonNull String fileName){
        this.mediaType = mediaType;
        this.file = file;
        this.fileName = fileName;
    }
    public MediaType getMediaType(){
        return this.mediaType;
    }

    public File getFile(){
        return this.file;
    }

    public  String getFileName(){
        if(TextUtils.isEmpty(fileName)){
            return file.getName();
        }else{
            return fileName;
        }
    }
}
