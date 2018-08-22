package com.luckyaf.netwatch.constant;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/20
 */
public class ProgressConstant {

    public static final String TAG = "tag";                             // 标示键
    public static final String URL = "url";                             // url
    public static final String FRACTION = "fraction";                   // 下载进度
    public static final String STATUS = "status";                       // 状态
    public static final String NOTIFY_INTERVAL = "notifyInterval";   // 更新状态周期
    public static final String SPEED_LIMIT = "speedLimit";              // 下载速度限制
    public static final String TOTAL_SIZE = "totalSize";                // 文件大小 byte
    public static final String CURRENT_SIZE = "currentSize";            // 已下载大小
    public static final String FILE_DIR = "fileDir";                    // 下载保存文件夹
    public static final String FILE_NAME = "fileName";                  // 下载保存文件名
    public static final String SUPPORT_RANGE = "supportRange";          // 是否支持断点下载
    public static final String LAST_MODIFY = "lastModify";              // 服务器上次修改时间



    public static final int START = 0;          // 开始状态
    public static final int WAITING = 1;        // 未开始，等待
    public static final int LOADING = 2;        // 下载中
    public static final int ERROR = 3;          // 错误
    public static final int FINISHED = 4;       // 已完成


}
