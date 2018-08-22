package com.luckyaf.netwatch.request;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/20
 */
public abstract class BaseRequest <T, R extends BaseRequest> implements Serializable {

    private static final long serialVersionUID = 3035449110416235175L;
    protected transient Object tag;

    private String url;

    BaseRequest(@NonNull  String url){
        this.url = url;
    }

    @SuppressWarnings("unchecked")
    public R tag(Object tag){
        this.tag = tag;
        return (R)this;
    }

    public Object getTag() {
        return tag;
    }

    public String getUrl() {
        return url;
    }

    @SuppressWarnings("unchecked")
    public R setUrl(String url) {
        this.url = url;
        return (R)this;
    }
}
