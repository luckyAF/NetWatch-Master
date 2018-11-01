package com.luckyaf.netwatch.upload;

import android.support.annotation.NonNull;

import com.luckyaf.netwatch.NetWatch;
import com.luckyaf.netwatch.callback.UploadCallBack;
import com.luckyaf.netwatch.utils.RxUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
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
 * @author Created by luckyAF on 2018/11/1
 */
public class UploadRequestBody extends RequestBody {

    private RequestBody requestBody;
    protected UploadCallBack callback;
    private CountingSink countingSink;
    private Disposable mLoopSend;
    private int notifyInterval = 300;
    private long currentSize;
    private long tempReadSize;
    private long contentLength = 0L;

    private List<Long> speedBuffer =  new ArrayList<>(4);


    public UploadRequestBody(RequestBody requestBody, UploadCallBack callback) {
        this.requestBody = requestBody;
        this.callback = callback;
        currentSize = 0;
        tempReadSize = 0;
        mLoopSend = RxUtil.loopDoing(notifyInterval, TimeUnit.MILLISECONDS, new RxUtil.Listener() {
            @Override
            public void doSomeThing() {
                sendProgress();
            }
        });

    }

    @Override
    public MediaType contentType() {
        if (requestBody == null) {
            return null;
        } else {
            return requestBody.contentType();
        }
    }

    @Override
    public long contentLength() {
        if (requestBody == null) {
            return -1;
        }
        try {
            return requestBody.contentLength();
        } catch (final IOException e) {
            clearDispose();

            NetWatch.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onError(e);
                }
            });

        }
        return -1;
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) {
        if (requestBody == null) {
            return;
        }
        try {
            countingSink = new CountingSink(sink);
            BufferedSink bufferedSink = Okio.buffer(countingSink);
            requestBody.writeTo(bufferedSink);
            bufferedSink.flush();
        } catch (final IOException e) {
            NetWatch.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onError(e);
                }
            });
        } finally {
            clearDispose();

            NetWatch.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onComplete();
                }
            });
        }
    }


    protected final class CountingSink extends ForwardingSink {


        CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(@NonNull Buffer source, long byteCount) {
            try {
                super.write(source, byteCount);
            } catch (final IOException e) {
                e.printStackTrace();
                NetWatch.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clearDispose();
                        callback.onError(e);
                    }
                });
            }
            if (contentLength == 0) {
                contentLength = contentLength();
            }
            currentSize += byteCount;
            tempReadSize+= byteCount;

            if (callback != null && currentSize == contentLength) {
                clearDispose();
                NetWatch.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess();
                    }
                });
            }
        }
    }

    private void clearDispose(){
        if(null != mLoopSend && !mLoopSend.isDisposed()){
            mLoopSend.dispose();
        }
    }

    private void sendProgress() {
        long speed = tempReadSize * 1000 / notifyInterval;
        speed = bufferSpeed(speed);
        tempReadSize = 0;
        float progress = currentSize/ (contentLength * 1.0f);
        callback.onProgress(progress,speed,currentSize,contentLength);
    }

    /** 平滑网速，避免抖动过大 */
    private long bufferSpeed(long speed) {
        speedBuffer.add(speed);
        if (speedBuffer.size() > 3) {
            speedBuffer.remove(0);
        }
        long sum = 0;
        for (float speedTemp : speedBuffer) {
            sum += speedTemp;
        }
        return sum / speedBuffer.size();
    }

}
