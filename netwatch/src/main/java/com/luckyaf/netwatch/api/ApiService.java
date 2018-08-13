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
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/5/25
 */
@SuppressWarnings("unused")
public interface ApiService {

    /**
     * 普通的 get 请求
     * @param url      路径
     * @param headers  head
     * @param params   参数
     * @return   ResponseBody
     */
    @GET()
    Observable<ResponseBody> get(@Url String url,
                                 @HeaderMap Map<String, Object> headers,
                                 @QueryMap Map<String, Object> params);

    /**
     * get 请求
     * @param url          路径
     * @param headers      head
     * @param body         body
     * @return   ResponseBody
     */
    @GET()
    Observable<ResponseBody> get(@Url String url,
                           @HeaderMap Map<String, Object> headers,
                           @Body RequestBody body);

    /**
     * post
     * @param url          路径
     * @param headers      header
     * @param params       参数
     * @return             ResponseBody
     */
    @FormUrlEncoded
    @POST()
    Observable<ResponseBody> post(@Url String url,
                            @HeaderMap Map<String, Object> headers,
                            @FieldMap Map<String, Object> params);

    /**
     * post
     * @param url          路径
     * @param headers      header
     * @param body       body
     * @return             ResponseBody
     */
    @POST()
    Observable<ResponseBody> post(@Url String url,
                            @HeaderMap Map<String, Object> headers,
                            @Body RequestBody body);

    /**
     * download
     * @param url          路径
     * @param headers      header
     * @param params       参数
     * @return             ResponseBody
     */
    @Streaming
    @GET()
    Observable<ResponseBody> download(@Url String url,
                                @HeaderMap Map<String, Object> headers,
                                @QueryMap Map<String, Object> params);
    /**
     * download
     * @param url          路径
     * @param headers      header
     * @param body       参数
     * @return             ResponseBody
     */
    @Streaming
    @GET()
    Observable<ResponseBody> download(@Url String url,
                                @HeaderMap Map<String, Object> headers,
                                @Body RequestBody body);

    /**
     * 上传
     * @param url        路径
     * @param headers     header
     * @param params        参数
     * @param body        body
     * @return         ResponseBody
     */
    @POST()
    Observable<ResponseBody> upload(@Url String url,
                              @HeaderMap Map<String, Object> headers,
                              @QueryMap Map<String, Object> params,
                              @Body RequestBody body);

    /**
     * upload
     * @param url          路径
     * @param headers      header
     * @param files       文件
     * @return             ResponseBody
     */
    @POST()
    Observable<ResponseBody> upload(@Url String url,
                              @HeaderMap Map<String, Object> headers,
                              @Body RequestBody files);

}
