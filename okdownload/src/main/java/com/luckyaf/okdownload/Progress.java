package com.luckyaf.okdownload;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/22
 */
@SuppressWarnings("unused")
public class Progress {

    private long speed;
    private long currentSize;
    private long totalSize;
    private float fraction;


    public Progress(long speed,float fraction,long currentSize,long totalSize){
        this.speed = speed;
        this.fraction = fraction;
        this.currentSize = currentSize;
        this.totalSize = totalSize;
    }

    public long getSpeed() {
        return speed;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public float getFraction() {
        return fraction;
    }
}
