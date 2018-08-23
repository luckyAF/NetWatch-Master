package com.luckyaf.okdownload.speedContral;


import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/23
 */
public class NewResponseBody extends ResponseBody {
    private final ResponseBody responseBody;
    private final SpeedCallback speedListener;
    private int speedLimit;
    private BufferedSource progressSource;

    public static ResponseBody upgrade(ResponseBody responseBody,int speedLimit ,SpeedCallback speedListener){
        return new NewResponseBody(responseBody,speedLimit,speedListener);
    }



    private NewResponseBody(ResponseBody responseBody,int speedLimit ,SpeedCallback speedListener) {
        this.responseBody = responseBody;
        this.speedLimit = speedLimit;
        this.speedListener = speedListener;
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
        if (speedListener == null) {
            return responseBody.source();
        }
        SuperInputStream  inputStream = new SuperInputStream(responseBody.source().inputStream(), speedLimit, speedListener);
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