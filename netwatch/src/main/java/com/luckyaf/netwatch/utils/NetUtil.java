package com.luckyaf.netwatch.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/28
 */
public class NetUtil {

    /**
     * 没有连接网络
     */
    public static final int NETWORK_NONE = -1;

    /**
     *  网络状态未知
     */
    public static final int NETWORK_UNKNOWN = 0;

    /**
     * 移动网络
     */
    public static final int NETWORK_MOBILE = 1;
    /**
     * 无线网络
     */
    public static final int NETWORK_WIFI = 2;


    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public static int getNetWorkState(@NonNull Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if(null == connectivityManager){
            return NETWORK_UNKNOWN;
        }

        // 得到连接管理器对象
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {

            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                    return NETWORK_WIFI;
                } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                    return NETWORK_MOBILE;
                }
            } else {
                return NETWORK_NONE;
            }

        } else {
            //获取所有网络连接的信息
            Network[] networks = connectivityManager.getAllNetworks();
            //通过循环将网络信息逐个取出来
            for (int i = 0; i < networks.length; i++) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(networks[i]);
                if (networkInfo.isConnected()) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        return NETWORK_MOBILE;
                    } else {
                        return NETWORK_WIFI;
                    }
                }
            }
        }
        return NETWORK_NONE;
    }

    public static  boolean isNetworkAvailable(@NonNull Context context){
        return getNetWorkState(context) > 0;
    }
}
