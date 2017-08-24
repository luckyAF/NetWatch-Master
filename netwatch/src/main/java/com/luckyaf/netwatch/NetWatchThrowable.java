package com.luckyaf.netwatch;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/17
 */
@SuppressWarnings("unused")
public class NetWatchThrowable extends Exception {

    private int code;
    private String message;

    public NetWatchThrowable(java.lang.Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }

    public NetWatchThrowable(java.lang.Throwable throwable, int code, String message) {
        super(throwable);
        this.code = code;
        this.message = message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
