package com.luckyaf.okdownload.speedContral;

import com.luckyaf.okdownload.callback.ReadCallback;
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



    public SuperInputStream(InputStream is, ReadCallback listener) {
        this.is = is;
        mListener = listener;
    }

    @Override
    public int read() throws IOException {

        int read =  this.is.read();
        mListener.call(read);
        return read;
    }

    public int read(byte b[], int off, int len) throws IOException {
//        if (bandWidthLimiter != null) {
//            bandWidthLimiter.limitNextBytes(len);
//        }
        int read =  this.is.read(b, off, len);
        mListener.call(read);
        return read;
    }
}
