package com.luckyaf.netwatch.upload;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

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
    private CountingSink countingSink;
    private Handler handler;

    public UploadRequestBody(RequestBody requestBody, UploadCallBack callback) {
        this.requestBody = requestBody;
        this.callback = callback;
        this.handler = new Handler(Looper.getMainLooper());

    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return requestBody.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        try {
            previousTime = System.currentTimeMillis();
            countingSink = new CountingSink(sink);
            BufferedSink bufferedSink = Okio.buffer(countingSink);
            requestBody.writeTo(bufferedSink);
            bufferedSink.flush();
        }catch (IOException e){
            callback.onError(e);
        }finally {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onComplete();
                }
            });
        }
    }


    protected final class CountingSink extends ForwardingSink {

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
                callback.onError(e);
                throw e;
            }
            if (contentLength == 0) {
                contentLength = contentLength();
            }
            bytesWritten += byteCount;
            if (callback != null) {
                handler = new Handler(Looper.getMainLooper());
                long totalTime = (System.currentTimeMillis() - previousTime) / 1000;
                if (totalTime == 0) {
                    totalTime += 1;
                }
                final long networkSpeed = bytesWritten / totalTime;
                final int progress = (int) (bytesWritten * 100 / contentLength);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onProgress(progress, networkSpeed, bytesWritten, contentLength);
                    }
                });
            }
            if(callback != null && bytesWritten == contentLength){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess();
                    }
                });
            }
        }
    }
}