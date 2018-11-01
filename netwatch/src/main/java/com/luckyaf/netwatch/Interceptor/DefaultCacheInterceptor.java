package com.luckyaf.netwatch.Interceptor;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.luckyaf.netwatch.NetWatch;
import com.luckyaf.netwatch.utils.NetUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 类描述：默认缓存设置
 *
 * @author Created by luckyAF on 2018/8/28
 */
public class DefaultCacheInterceptor implements Interceptor{

    public static final String SINGLE_CACHE_TIME = "Single_Cache_Time";

    long cacheTime = 0;

    public DefaultCacheInterceptor(int cacheTime, TimeUnit unit){
        this.cacheTime = unit.convert(cacheTime,TimeUnit.SECONDS);
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();//获取请求
        //这里就是说判读我们的网络条件，要是有网络的话我么就直接获取网络上面的数据，
        // 要是没有网络的话我么就去缓存里面取数据
        if(!NetUtil.isNetworkAvailable(NetWatch.getContext())){
            request = request.newBuilder()
                    //这个的话内容有点多啊，大家记住这么写就是只从缓存取，想要了解这个东西我等下在
                    // 给大家写连接吧。大家可以去看下，获取大家去找拦截器资料的时候就可以看到这个方面的东西反正也就是缓存策略。
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }
        Response originalResponse = chain.proceed(request);

        String requestCacheTime = request.header(SINGLE_CACHE_TIME);

        if(TextUtils.isEmpty(requestCacheTime)){
            requestCacheTime = "" + cacheTime;
        }
        if(NetUtil.isNetworkAvailable(NetWatch.getContext())){
            //这里大家看点开源码看看.header .removeHeader做了什么操作很简答，就是的加字段和减字段的。
            return originalResponse.newBuilder()
                    //设置缓存时间
                    .header("Cache-Control", "public, max-age=" + requestCacheTime)
                    .removeHeader("Pragma")
                    .build();
        }else{
            int maxTime = 4*24*60*60;
            return originalResponse.newBuilder()
                    //这里的设置的是我们的没有网络的缓存时间，想设置多少就是多少。
                    .header("Cache-Control", "public, only-if-cached, max-stale="+maxTime)
                    .removeHeader("Pragma")
                    .build();
        }

    }
}
