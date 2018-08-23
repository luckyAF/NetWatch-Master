package com.luckyaf.okdownload.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/23
 */
public class IOUtil {

    public static void closeQuietly(Closeable closeable) {
        if (closeable == null){
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void closeAll(Closeable... closeables){
        if(closeables == null){
            return;
        }
        for (Closeable closeable : closeables) {
            if(closeable!=null){
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
