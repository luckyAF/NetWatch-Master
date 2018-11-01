package com.luckyaf.netwatch.request;

import android.support.annotation.NonNull;

import com.luckyaf.netwatch.NetWatch;
import com.luckyaf.netwatch.utils.RxUtil;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * 类描述：get请求
 *
 * @author Created by luckyAF on 2018/8/27
 */
public class GetRequest extends BaseRequest<GetRequest>{
    private static final long serialVersionUID = 7659199364768002163L;

    public GetRequest(@NonNull String url) {
        super(url);
    }

    public Observable<ResponseBody> execute(){
        return NetWatch.getService().get(url,headers,params)
                .compose(RxUtil.<ResponseBody>observableSchedulerTransformer());
    }
}
