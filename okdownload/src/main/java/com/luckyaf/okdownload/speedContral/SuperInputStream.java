package com.luckyaf.okdownload.speedContral;

import com.luckyaf.okdownload.callback.ReadCallback;
import com.luckyaf.okdownload.utils.BandWidthLimiter;

import java.io.IOException;
import java.io.InputStream;

/**
 * 类描述：超级inputStream  控制速度 反馈速度
 *
 * @author Created by luckyAF on 2018/8/23
 */
public class SuperInputStream  extends InputStream {

    private InputStream is;
    private ReadCallback mListener;
    private int limitSpeed;
    private BandWidthLimiter bandWidthLimiter;



    public SuperInputStream(InputStream is,int limitSpeed, ReadCallback listener) {
        this.is = is;
        this.limitSpeed = limitSpeed;
        mListener = listener;
        bandWidthLimiter = new BandWidthLimiter(limitSpeed);
    }

    @Override
    public int read() throws IOException {
        if (this.bandWidthLimiter != null) {
            this.bandWidthLimiter.limitNextBytes();
        }
        int read =  this.is.read();
        mListener.call(read);
        return read;
    }

    public int read(byte b[], int off, int len) throws IOException {
        if (bandWidthLimiter != null) {
            bandWidthLimiter.limitNextBytes(len);
        }
        int read =  this.is.read(b, off, len);
        mListener.call(read);
        return read;
    }
}
