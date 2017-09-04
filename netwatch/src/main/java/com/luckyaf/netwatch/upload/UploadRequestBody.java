package com.luckyaf.netwatch.upload;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.luckyaf.netwatch.callBack.ErrorCallBack;
import com.luckyaf.netwatch.callBack.ProgressCallBack;
import com.luckyaf.netwatch.callBack.UploadCallBack;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/17
 */
@SuppressWarnings("unused")
public class UploadRequestBody extends RequestBody {

    private long previousTime;
    private RequestBody requestBody;
    protected UploadCallBack callback;
    private ProgressCallBack mProgressCallBack;
    private ErrorCallBack mErrorCallBack;
    private Handler handler;
    private Boolean isCancel;


    public UploadRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
        isCancel = false;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public UploadRequestBody uploadCallBack(UploadCallBack callback){
        this.callback = callback;
        return this;
    }

    public UploadRequestBody progressCallBack(ProgressCallBack progressCallBack){
        this.mProgressCallBack = progressCallBack;
        return this;
    }

    public UploadRequestBody errorCallBack(ErrorCallBack errorCallBack){
        this.mErrorCallBack = errorCallBack;
        return this;
    }

    public void setCancel(Boolean isCancel){
        this.isCancel = isCancel;
    }


    @Override
    public MediaType contentType() {
        if(requestBody == null){
            return null;
        }
        return requestBody.contentType();
    }

    @Override
    public long contentLength() {
        if(requestBody == null){
            return -1;
        }
        try {
            return requestBody.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        if(requestBody == null){
            return;
        }
        try {
            previousTime = System.currentTimeMillis();
            CountingSink countingSink = new CountingSink(sink);
            BufferedSink bufferedSink = Okio.buffer(countingSink);
            requestBody.writeTo(bufferedSink);
            bufferedSink.flush();
        } catch (final IOException e) {
            onError(e);
        } finally {
            onComplete();

        }
    }


    private void onError(final Throwable e) {
        if (callback == null && mErrorCallBack == null || isCancel) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onError(e);
                }
                if (mErrorCallBack != null) {
                    mErrorCallBack.onError(e);
                }
            }
        });
    }

    private void onComplete() {
        if (callback == null || isCancel) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onComplete();

            }
        });
    }


    private void onProgress(final int progress, final long speed, final long downloadedSize, final long totalSize) {
        if(callback == null && mProgressCallBack == null){
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onProgress(progress, speed, downloadedSize, totalSize);
                }
                if (mProgressCallBack != null) {
                    mProgressCallBack.onProgress(progress, speed, downloadedSize, totalSize);
                }
            }
        });
    }

    private final class CountingSink extends ForwardingSink {

        private long bytesWritten = 0;
        long contentLength = 0L;

        CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(@NonNull Buffer source, long byteCount) throws IOException {
            try {
                super.write(source, byteCount);
            } catch (IOException e) {
                e.printStackTrace();
                onError(e);
                throw e;
            }
            if (contentLength == 0) {
                contentLength = contentLength();
            }
            bytesWritten += byteCount;
            if (callback != null || mProgressCallBack != null) {
                handler = new Handler(Looper.getMainLooper());
                long nowTime = System.currentTimeMillis();
                long intervalTime = (nowTime - previousTime) / 1000;
                previousTime = nowTime;
                if (intervalTime == 0) {
                    intervalTime += 1;
                }
                final long networkSpeed = byteCount / intervalTime;
                final int progress = (int) (bytesWritten * 100 / contentLength);
                onProgress(progress, networkSpeed, bytesWritten, contentLength);
            }
        }
    }
}