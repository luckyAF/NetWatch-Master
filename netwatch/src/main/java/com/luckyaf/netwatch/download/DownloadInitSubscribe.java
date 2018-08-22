package com.luckyaf.netwatch.download;

import com.luckyaf.netwatch.model.Progress;
import com.luckyaf.netwatch.request.DownloadRequest;
import com.luckyaf.netwatch.utils.FileUtil;
import com.luckyaf.netwatch.utils.Logger;

import java.io.File;
import java.io.IOException;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/22
 */
public class DownloadInitSubscribe implements ObservableOnSubscribe<Progress> {

    private Progress progress;

    public DownloadInitSubscribe(DownloadRequest request) {
        progress = new Progress(request);
    }


    @Override
    public void subscribe(ObservableEmitter<Progress> emitter) throws Exception {
        Request request = new Request.Builder()
                .url(progress.getUrl())
                .build();
        try {
            Response response = DownloadManager.getInstance().getClient().newCall(request).execute();

            if (response != null && response.isSuccessful()) {
                Logger.d("response",response.headers());
                String filename = FileUtil.getNetFileName(response,progress.getUrl());
                if(null == progress.getFileName()) {
                    progress.setFileName(filename);
                }
                long totalSize = -1;
                if(response.body() != null){
                    totalSize = response.body().contentLength();
                }
                if(totalSize == 0){
                    emitter.onError(new Exception("error file"));
                }
                if(null == response.headers().get("Content-Range") && null == response.headers().get("Accept-Ranges")){
                    progress.setSupportRange(false);
                }else{
                    if(null != response.headers().get("Last-Modified")){
                        progress.setLastModify(response.headers().get("Last-Modified"));
                    }
                    if(null != response.headers().get("ETag")){
                        progress.setLastModify(response.headers().get("ETag"));
                    }
                }
                progress.setTotalSize(totalSize);
                response.close();
                updateProgress();
                Logger.d("getProgress",progress.toString());
                emitter.onNext(progress);
                emitter.onComplete();
            }else {
                emitter.onError(new Exception("can not get information"));
            }

        } catch (IOException e) {
            emitter.onError(e);
            e.printStackTrace();
        }
    }

    private void updateProgress() {
        String fileName = progress.getFileName();
        long downloadLength = 0, totalSize = progress.getTotalSize();
        File file = new File(progress.getFileDir(), fileName);

        if (file.exists()) {
            Logger.d("file exists",progress.getFileDir(), fileName);

            //找到了文件,代表已经下载过,则获取其长度
            downloadLength = file.length();
        }
        //之前下载过,需要重新来一个文件
        int i = 1;
        while (downloadLength >= totalSize) {
            int dotIndex = fileName.lastIndexOf(".");
            String fileNameOther;
            if (dotIndex == -1) {
                fileNameOther = fileName + "(" + i + ")";
            } else {
                fileNameOther = fileName.substring(0, dotIndex)
                        + "(" + i + ")" + fileName.substring(dotIndex);
            }
            File newFile = new File(progress.getFileDir(), fileNameOther);
            Logger.d("new file",progress.getFileDir(), fileNameOther);

            file = newFile;
            downloadLength = newFile.length();
            i++;
        }
        //设置改变过的文件名/大小
        progress.setCurrentSize(downloadLength);
        progress.setFileName(file.getName());

    }

}
