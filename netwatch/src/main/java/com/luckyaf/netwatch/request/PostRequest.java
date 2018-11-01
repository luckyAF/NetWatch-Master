package com.luckyaf.netwatch.request;

import android.support.annotation.NonNull;

import com.luckyaf.netwatch.NetWatch;
import com.luckyaf.netwatch.constant.ContentType;
import com.luckyaf.netwatch.utils.RxUtil;

import java.lang.reflect.Array;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * 类描述：post请求
 *
 * @author Created by luckyAF on 2018/8/27
 */
public class PostRequest extends BaseRequest<PostRequest>{
    private static final long serialVersionUID = -7694265732133311329L;

    public PostRequest(@NonNull String url) {
        super(url);
    }

    public Observable<ResponseBody> execute(){
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String,Object> entry:params.entrySet()){
            if(entry.getValue().getClass().isArray()){
                int length = Array.getLength(entry.getValue());
                Object[] os = new Object[length];
                for (int i = 0; i < os.length; i++) {
                    os[i] = Array.get(entry.getValue(), i);
                    builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create(null, os[i].toString()));
                }
            }else{
                builder.addFormDataPart(entry.getKey(),entry.getValue().toString());
            }
        }
        RequestBody requestBody = builder.build();
        return NetWatch.getService().post(url,headers,requestBody)
                .compose(RxUtil.<ResponseBody>observableSchedulerTransformer());
    }
}
