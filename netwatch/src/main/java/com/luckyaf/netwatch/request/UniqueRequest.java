package com.luckyaf.netwatch.request;

import android.support.annotation.NonNull;

import com.luckyaf.netwatch.NetWatch;
import com.luckyaf.netwatch.api.BaseApiService;
import com.luckyaf.netwatch.constant.ContentType;
import com.luckyaf.netwatch.utils.RxUtil;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/24
 */
public class UniqueRequest extends BaseRequest<UniqueRequest> {

    private static final long serialVersionUID = -8161965220206713920L;

    private transient Retrofit retrofit;
    protected transient Retrofit.Builder mRetrofitBuilder;
    protected transient OkHttpClient.Builder mClientBuilder;
    protected int retryCount;

    public UniqueRequest(@NonNull String url) {
        super(url);
        mRetrofitBuilder =  NetWatch.getRetrofit().newBuilder();
        mClientBuilder = NetWatch.getClient().newBuilder();
    }

    public UniqueRequest connectTimeout(int timeout, TimeUnit unit) {
        if (timeout != -1) {
            mClientBuilder.connectTimeout((long)timeout, unit);
        }
        return this;
    }
    public UniqueRequest retryOnConnectionFailure(boolean retry) {
        mClientBuilder.retryOnConnectionFailure(retry);
        return this;
    }

    public UniqueRequest setCache(){

        return this;
    }







    private void prepare(){
        retrofit = mRetrofitBuilder
                .client(mClientBuilder.build())
                .build();
    }

    public Observable<ResponseBody> get() {
        prepare();
        return retrofit.create(BaseApiService.class)
                .get(url, headers, params)
                .compose(RxUtil.<ResponseBody>observableSchedulerTransformer());


    }



    public Observable<ResponseBody> post() {
        prepare();
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
        return retrofit.create(BaseApiService.class)
                .post(url, headers, requestBody)
                .compose(RxUtil.<ResponseBody>observableSchedulerTransformer());
    }

    /**
     * create ApiService
     */
    public <T> T create(final Class<T> service) {
        prepare();
        return retrofit.create(service);
    }


}
