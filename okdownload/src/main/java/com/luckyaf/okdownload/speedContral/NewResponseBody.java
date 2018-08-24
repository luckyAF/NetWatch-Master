package com.luckyaf.okdownload.speedContral;


import com.luckyaf.okdownload.callback.ReadCallback;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;

/**
 * 类描述：新的ResponseBody
 *
 * @author Created by luckyAF on 2018/8/23
 */
public class NewResponseBody extends ResponseBody {
    private final ResponseBody responseBody;
    private final ReadCallback readListener;
    private int limitSpeed;
    private BufferedSource progressSource;
    public static ResponseBody upgrade(ResponseBody responseBody ,int limitSpeed,ReadCallback readListener){
        return new NewResponseBody(responseBody,limitSpeed,readListener);
    }



    private NewResponseBody(ResponseBody responseBody,int limitSpeed,ReadCallback readListener) {
        this.responseBody = responseBody;
        this.limitSpeed = limitSpeed;
        this.readListener = readListener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (readListener == null) {
            return responseBody.source();
        }
        SuperInputStream  inputStream = new SuperInputStream(responseBody.source().inputStream(),limitSpeed,readListener);
        progressSource = Okio.buffer(Okio.source(inputStream));
        return progressSource;
    }

    @Override
    public void close() {
        if (progressSource != null) {
            try {
                progressSource.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}