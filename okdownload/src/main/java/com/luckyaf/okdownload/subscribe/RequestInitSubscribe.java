package com.luckyaf.okdownload.subscribe;

import com.luckyaf.okdownload.DownloadRequest;
import com.luckyaf.okdownload.OkDownload;
import com.luckyaf.okdownload.constant.HttpConstant;
import com.luckyaf.okdownload.utils.FileUtil;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 类描述：初始化request
 *
 * @author Created by luckyAF on 2018/8/22
 */
@SuppressWarnings("unused")
public class RequestInitSubscribe implements ObservableOnSubscribe<DownloadRequest> {

    private DownloadRequest downloadRequest;

    public RequestInitSubscribe(String url){
        downloadRequest = new DownloadRequest(url);
    }

    @Override
    public void subscribe(ObservableEmitter<DownloadRequest> emitter){
        Request request = new Request.Builder()
                .url(downloadRequest.getUrl())
                .build();
        try {
            Response response = OkDownload.getInstance().getOkHttpClient().newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                String filename = FileUtil.getNetFileName(response,downloadRequest.getUrl());
                downloadRequest.setFileName(filename);
                long totalSize = -1;
                if(null != response.body()){
                    ResponseBody body = response.body();
                    if(null != body){
                        totalSize = body.contentLength();
                    }
                }
                if(totalSize == 0){
                    emitter.onError(new Exception("error file"));
                }
                if(null == response.headers().get(HttpConstant.HEAD_KEY_CONTENT_RANGE)
                        && null == response.headers().get(HttpConstant.HEAD_KEY_ACCEPT_RANGES)){
                    downloadRequest.setSupportRange(false);
                }else{
                    if(null != response.headers().get(HttpConstant.HEAD_KEY_LAST_MODIFIED)){
                        downloadRequest.setLastModify(response.headers().get(HttpConstant.HEAD_KEY_LAST_MODIFIED));
                    }
                    if(null != response.headers().get(HttpConstant.HEAD_KEY_E_TAG)){
                        downloadRequest.setLastModify(response.headers().get(HttpConstant.HEAD_KEY_E_TAG));
                    }
                }
                downloadRequest.setTotalSize(totalSize);
                response.close();
                updateFileName();
                Logger.d("getRequest",downloadRequest.toString());
                emitter.onNext(downloadRequest);
                emitter.onComplete();
            }else {
                emitter.onError(new Exception("can not get information"));
            }

        } catch (IOException e) {
            emitter.onError(e);
            e.printStackTrace();
        }
    }

    private void updateFileName() {
        String fileName = downloadRequest.getFileName();
        long downloadLength = 0, totalSize = downloadRequest.getTotalSize();
        File file = new File(downloadRequest.getFileDir(), fileName);

        if (file.exists()) {
            Logger.d("file exists",downloadRequest.getFileDir(), fileName);

            //找到了文件,代表已经下载过,则获取其长度
            downloadLength = file.length();
        }
        //之前下载过,需要重新来一个文件
        int i = 1;
        while (downloadLength >= totalSize) {
            int dotIndex = fileName.lastIndexOf(".");
            String fileNameOther;
            fileNameOther = dotIndex == -1
                    ? fileName + "(" + i + ")"
                    : fileName.substring(0, dotIndex)
                    + "(" + i + ")" + fileName.substring(dotIndex);

            File newFile = new File(downloadRequest.getFileDir(), fileNameOther);
            Logger.d("new file",downloadRequest.getFileDir(), fileNameOther);

            file = newFile;
            downloadLength = newFile.length();
            i++;
        }
        downloadRequest.setFileName(file.getName());
        downloadRequest.updateTag();

    }
}
