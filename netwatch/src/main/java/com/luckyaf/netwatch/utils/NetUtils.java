package com.luckyaf.netwatch.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2017/8/14
 */
@SuppressWarnings("unused")
public class NetUtils {

    private static int TIMEOUT = 3000; // TIMEOUT

    private NetUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取网络状态
     *
     * @param context   the context
     * @return  -2  无法连接 -1未打开网络连接  0 数据网络  1 wifi
     */
    public static int getNetworkState(Context context) {
        if (!isConnected(context)) {
            return -1;
        } else if(!connectionNetwork()){
            return -2;
        } else {
            if (!isWifi(context)) {
                return ConnectivityManager.TYPE_MOBILE;
            } else {
                return ConnectivityManager.TYPE_WIFI;
            }
        }
    }

    /**
     *ping "http://www.baidu.com"
     * @return  是否连接到互联网
     */
    public static boolean connectionNetwork() {
        boolean result = false;
        HttpURLConnection httpUrl = null;
        try {
            httpUrl = (HttpURLConnection) new URL("http://www.baidu.com")
                    .openConnection();
            httpUrl.setConnectTimeout(TIMEOUT);
            httpUrl.connect();
            result = true;
        } catch (IOException e) {
            //do nothing
        } finally {
            if (null != httpUrl) {
                httpUrl.disconnect();
                httpUrl = null;
            }
        }
        return result;
    }

    /**
     * 判断网络是否连接
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivity) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是wifi连接
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;

    }

    /**
     * 判断当前网络是否是移动数据网络.
     * @param context the context
     * @return boolean
     */
    public static boolean isMobile(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Context context) {
        Intent intent;

        intent = new Intent();
        ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
        intent.setComponent(component);
        intent.setAction("android.intent.action.VIEW");

        context.startActivity(intent);
    }
}
