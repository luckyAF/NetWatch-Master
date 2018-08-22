package com.luckyaf.netwatch.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/20
 */
public class IOUtil {

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
