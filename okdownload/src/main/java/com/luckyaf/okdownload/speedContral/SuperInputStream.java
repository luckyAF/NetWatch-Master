package com.luckyaf.okdownload.speedContral;

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
    private BandWidthLimiter bandWidthLimiter;
    private SpeedCallback mListener;

    public SuperInputStream(InputStream is, int limitSpeed, SpeedCallback listener) {
        this.is = is;
        this.bandWidthLimiter = new BandWidthLimiter(limitSpeed);
        mListener = listener;
    }

    @Override
    public int read() throws IOException {
        if (this.bandWidthLimiter != null){
            this.bandWidthLimiter.limitNextBytes();
        }

        return this.is.read();
    }

    public int read(byte b[], int off, int len) throws IOException {
        if (bandWidthLimiter != null) {
            bandWidthLimiter.limitNextBytes(len);
        }

        return this.is.read(b, off, len);
    }
}
