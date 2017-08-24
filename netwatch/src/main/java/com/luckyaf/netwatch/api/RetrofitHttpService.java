package com.luckyaf.netwatch.api;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/11
 */

public interface RetrofitHttpService {

    @GET()
    Observable<ResponseBody> get(@Url String url,
                                 @HeaderMap Map<String, String> headers,
                                 @QueryMap Map<String, Object> params);

    @FormUrlEncoded
    @POST()
    Observable<ResponseBody> post(@Url String url,
                                  @HeaderMap Map<String, String> headers,
                                  @FieldMap Map<String, Object> params);

    @Streaming
    @GET()
    Observable<ResponseBody> download(@Url String url);

    @POST()
    Observable<ResponseBody> upload(@Url String url,
                                    @HeaderMap Map<String, String> headers,
                                    @QueryMap Map<String, Object> params,
                                    @Body RequestBody Body);


}
