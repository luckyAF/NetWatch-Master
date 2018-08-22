package com.luckyaf.netwatch.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.luckyaf.netwatch.NetWatch;
import com.luckyaf.netwatch.constant.ProgressConstant;
import com.luckyaf.netwatch.request.DownloadRequest;

import java.io.File;
import java.util.Random;
import java.util.RandomAccess;

import static com.luckyaf.netwatch.constant.ProgressConstant.*;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/20
 */
public class Progress {

    private String tag;                              // tag
    private String url;                              // url
    private float fraction;                          // 进度 小数
    private int status;                              // 状态
    private long notifyInterval;                    // 更新状态周期
    private int speedLimit;                         // 速度限制      kb / s
    private long totalSize;                          // 文件总大小     byte
    private long currentSize;                        // 文件当前大小   byte
    private String fileName;                         // 文件名
    private String fileDir;                          // 保存文件路径
    private boolean supportRange;                    // 是否支持断点下载
    private String lastModify;                       // 服务器上次修改时间
    private transient long nowSpeed;                 // 当前速度
    private transient Exception error;               // 错误


    public void restart() {
        fraction = 0;
        notifyInterval = 300;
        status = ProgressConstant.WAITING;
        currentSize = 0;
        supportRange = true;
    }


    public Progress(String url) {
        this.url = url;
        restart();
        fileDir = NetWatch.getInstance().getContext().getExternalFilesDir(null) + File.separator + "downloads";
        speedLimit = Integer.MAX_VALUE;
    }

    public Progress(@NonNull DownloadRequest request) {
        this(request.getUrl());
        if (null != request.getTag()) {
            this.tag = request.getTag().toString();
        }
        setFileDir(request.getFileDir());
        setFileName(request.getFileName());
        setSpeedLimit(request.getSpeedLimit());
    }


    private Progress() {
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        if (null == tag) {
            updateTag();
        }
        return tag;
    }

    public void updateTag() {
        if (tag == null) {
            if (null != fileDir && null != fileName) {
                tag = url.concat(fileDir).concat(fileName);
            } else {
                tag = url.concat("" + System.currentTimeMillis());
            }
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public float getFraction() {
        return fraction;
    }

    public void setFraction(float fraction) {
        this.fraction = fraction;
    }

    public long getNowSpeed() {
        return nowSpeed;
    }

    public void setNowSpeed(long nowSpeed) {
        this.nowSpeed = nowSpeed;
    }

    public long getNotifyInterval() {
        return notifyInterval;
    }

    public void setNotifyInterval(long notifyInterval) {
        this.notifyInterval = notifyInterval;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        if (!TextUtils.isEmpty(fileDir)) {
            this.fileName = fileName;
        }
        updateTag();

    }

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        if (!TextUtils.isEmpty(fileDir)) {
            this.fileDir = fileDir;
        }
        updateTag();

    }

    public String getLastModify() {
        return lastModify;
    }

    public void setLastModify(String lastModify) {
        this.lastModify = lastModify;
    }

    public boolean isSupportRange() {
        return supportRange;
    }

    public void setSupportRange(boolean supportRange) {
        this.supportRange = supportRange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Progress progress = (Progress) o;
        return tag != null ? tag.equals(progress.tag) : progress.tag == null;
    }

    @Override
    public int hashCode() {
        return tag != null ? tag.hashCode() : 0;
    }


    public Exception getError() {
        return error;
    }

    public void setError(Exception error) {
        this.error = error;
    }

    public static ContentValues buildContentValues(Progress progress) {
        ContentValues values = new ContentValues();
        values.put(TAG, progress.tag);
        values.put(URL, progress.url);
        values.put(FILE_DIR, progress.fileDir);
        values.put(FILE_NAME, progress.fileName);
        values.put(FRACTION, progress.fraction);
        values.put(TOTAL_SIZE, progress.totalSize);
        values.put(CURRENT_SIZE, progress.currentSize);
        values.put(STATUS, progress.status);
        values.put(NOTIFY_INTERVAL, progress.notifyInterval);
        values.put(SPEED_LIMIT, progress.speedLimit);
        values.put(SUPPORT_RANGE, progress.supportRange ? 1 : 0);
        values.put(LAST_MODIFY, progress.lastModify);
        return values;
    }

    public static ContentValues buildUpdateContentValues(Progress progress) {
        ContentValues values = new ContentValues();
        values.put(FRACTION, progress.fraction);
        values.put(TOTAL_SIZE, progress.totalSize);
        values.put(CURRENT_SIZE, progress.currentSize);
        values.put(STATUS, progress.status);
        values.put(NOTIFY_INTERVAL, progress.notifyInterval);
        values.put(SPEED_LIMIT, progress.speedLimit);
        values.put(SUPPORT_RANGE, progress.supportRange);
        values.put(LAST_MODIFY, progress.lastModify);
        return values;
    }

    public static Progress parseCursorToBean(Cursor cursor) {
        Progress progress = new Progress();
        progress.tag = cursor.getString(cursor.getColumnIndex(TAG));
        progress.url = cursor.getString(cursor.getColumnIndex(URL));
        progress.fileDir = cursor.getString(cursor.getColumnIndex(FILE_DIR));
        progress.fileName = cursor.getString(cursor.getColumnIndex(FILE_NAME));
        progress.fraction = cursor.getFloat(cursor.getColumnIndex(FRACTION));
        progress.notifyInterval = cursor.getLong(cursor.getColumnIndex(NOTIFY_INTERVAL));
        progress.totalSize = cursor.getLong(cursor.getColumnIndex(TOTAL_SIZE));
        progress.currentSize = cursor.getLong(cursor.getColumnIndex(CURRENT_SIZE));
        progress.status = cursor.getInt(cursor.getColumnIndex(STATUS));
        progress.speedLimit = cursor.getInt(cursor.getColumnIndex(SPEED_LIMIT));
        progress.supportRange = cursor.getInt(cursor.getColumnIndex(SUPPORT_RANGE)) == 1;
        progress.lastModify = cursor.getString(cursor.getColumnIndex(LAST_MODIFY));
        return progress;
    }

    @Override
    public String toString() {
        return "Progress{" +
                "tag='" + tag + '\'' +
                ", url='" + url + '\'' +
                ", fraction=" + fraction +
                ", status=" + status +
                ", notifyInterval=" + notifyInterval +
                ", speedLimit=" + speedLimit +
                ", totalSize=" + totalSize +
                ", currentSize=" + currentSize +
                ", fileName='" + fileName + '\'' +
                ", fileDir='" + fileDir + '\'' +
                ", supportRange=" + supportRange +
                ", lastModify='" + lastModify + '\'' +
                ", nowSpeed=" + nowSpeed +
                ", error=" + error +
                '}';
    }
}
