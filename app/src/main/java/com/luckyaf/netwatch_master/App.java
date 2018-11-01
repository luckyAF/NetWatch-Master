package com.luckyaf.netwatch_master;

import android.app.Application;

import com.luckyaf.netwatch.NetWatch;
import com.luckyaf.okdownload.OkDownload;

import java.util.HashMap;
import java.util.Map;


/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/17
 */
public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

//        System.setProperty("http.proxyHost", "192.168.1.104");   //个人测试网络时用的，删掉即可
//        System.setProperty("http.proxyPort", "8888");

        initNet();
    }

    private void initNet(){
        NetWatch.init(this,"http://www.baidu.com")
                .build();
        OkDownload.getInstance().init(this);
    }
}
