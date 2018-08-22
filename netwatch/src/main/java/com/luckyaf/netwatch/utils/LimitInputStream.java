package com.luckyaf.netwatch.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/21
 */
public class LimitInputStream extends InputStream {
    private InputStream is;
    private BandWidthLimiter bandWidthLimiter;

    public LimitInputStream(InputStream is, BandWidthLimiter bandWidthLimiter) {
        this.is = is;
        this.bandWidthLimiter = bandWidthLimiter;
    }

    @Override
    public int read() throws IOException {
        if (this.bandWidthLimiter != null)
            this.bandWidthLimiter.limitNextBytes();
        return this.is.read();
    }

    public int read(byte b[], int off, int len) throws IOException {
        if (bandWidthLimiter != null)
            bandWidthLimiter.limitNextBytes(len);
        return this.is.read(b, off, len);
    }
}
