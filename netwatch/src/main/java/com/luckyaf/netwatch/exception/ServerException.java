package com.luckyaf.netwatch.exception;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/17
 */

public class ServerException extends RuntimeException {

    public int code;
    public String message;

    public ServerException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }


}