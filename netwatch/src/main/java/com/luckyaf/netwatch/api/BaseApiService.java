package com.luckyaf.netwatch.api;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * 类描述：最基础的请求
 *
 * @author Created by luckyAF on 2018/8/20
 */
public interface BaseApiService {


    @GET()
    Observable<ResponseBody> get(@Url String url,
                                 @HeaderMap Map<String, Object> headers,
                                 @QueryMap Map<String, Object> params);


    @POST()
    Observable<ResponseBody> post(@Url String url,
                                  @HeaderMap Map<String, Object> headers,
                                  @Body RequestBody body);

    @POST()
    Call<ResponseBody> upload(@Url String url,
                              @HeaderMap Map<String, Object> headers,
                              @Body RequestBody body);

}
