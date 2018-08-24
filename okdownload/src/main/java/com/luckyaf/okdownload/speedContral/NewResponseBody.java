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
    private BufferedSource progressSource;
    public static ResponseBody upgrade(ResponseBody responseBody ,ReadCallback readListener){
        return new NewResponseBody(responseBody,readListener);
    }



    private NewResponseBody(ResponseBody responseBody,ReadCallback readListener) {
        this.responseBody = responseBody;
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
        SuperInputStream  inputStream = new SuperInputStream(responseBody.source().inputStream(),readListener);
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