package com.luckyaf.netwatch.upload;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.luckyaf.netwatch.ContentType;

import java.io.File;

import okhttp3.MediaType;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/17
 */

public class UploadFileBody {
    private ContentType mContentType;
    private File file;
    private String fileName;
    public UploadFileBody(@NonNull ContentType contentType, @NonNull File file){
        this.mContentType = contentType;
        this.file = file;
    }

    public UploadFileBody(@NonNull ContentType contentType, @NonNull File file,@NonNull String fileName){
        this.mContentType = contentType;

        this.file = file;
        this.fileName = fileName;
    }
    public MediaType getMediaType(){
        return this.mContentType.toMediaType();
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
