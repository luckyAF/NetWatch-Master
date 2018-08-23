package com.luckyaf.okdownload;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.luckyaf.okdownload.constant.RequestConstant;
import com.luckyaf.okdownload.constant.StatusConstant;
import com.luckyaf.okdownload.subscribe.RequestInitSubscribe;

import java.io.File;
import java.io.Serializable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/22
 */
@SuppressWarnings("unused")
public class DownloadRequest implements Serializable {
    private static final long serialVersionUID = 4766115139933457228L;

    private String tag;
    private String url;
    private String fileName;
    private String fileDir;
    private long currentSize;
    private long totalSize;
    private boolean supportRange;
    private String lastModify;
    private int status;


    public DownloadRequest(String url) {
        this.url = url;
        fileDir = OkDownload.getInstance().getContext().getExternalFilesDir(null) + File.separator + "download";
        currentSize = 0;
        supportRange = true;
        status = StatusConstant.NONE;
    }

    private DownloadRequest() {

    }

    public boolean canStart(){
        return status == StatusConstant.NONE || status == StatusConstant.WAITING;
    }

    public String getTag() {
        return tag;
    }

    public DownloadRequest setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public DownloadRequest updateTag() {
        if (TextUtils.isEmpty(tag)) {
            this.tag = "download" + System.currentTimeMillis();
        }
        return this;
    }

    public String getUrl() {
        return url;
    }

    public String getFileName() {
        return fileName;
    }

    public DownloadRequest setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getFileDir() {
        return fileDir;
    }

    public DownloadRequest setFileDir(String fileDir) {
        this.fileDir = fileDir;
        return this;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public DownloadRequest setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
        return this;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public DownloadRequest setTotalSize(long totalSize) {
        this.totalSize = totalSize;
        return this;
    }

    public boolean isSupportRange() {
        return supportRange;
    }

    public DownloadRequest setSupportRange(boolean supportRange) {
        this.supportRange = supportRange;
        return this;
    }

    public String getLastModify() {
        return lastModify;
    }

    public DownloadRequest setLastModify(String lastModify) {
        this.lastModify = lastModify;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public DownloadRequest setStatus(int status) {
        this.status = status;
        return this;
    }

    public static ContentValues buildContentValues(DownloadRequest downloadRequest) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(RequestConstant.TAG, downloadRequest.tag);
        contentValues.put(RequestConstant.URL, downloadRequest.url);
        contentValues.put(RequestConstant.FILE_NAME, downloadRequest.fileName);
        contentValues.put(RequestConstant.FILE_DIR, downloadRequest.fileDir);
        contentValues.put(RequestConstant.CURRENT_SIZE, downloadRequest.currentSize);
        contentValues.put(RequestConstant.TOTAL_SIZE, downloadRequest.totalSize);
        contentValues.put(RequestConstant.SUPPORT_RANGE, downloadRequest.supportRange ? 1 : 0);
        contentValues.put(RequestConstant.LAST_MODIFY, downloadRequest.lastModify);
        contentValues.put(RequestConstant.STATUS, downloadRequest.status);
        return contentValues;
    }

    public static DownloadRequest parseCursorToBean(Cursor cursor) {
        DownloadRequest request = new DownloadRequest();
        request.tag = cursor.getString(cursor.getColumnIndex(RequestConstant.TAG));
        request.url = cursor.getString(cursor.getColumnIndex(RequestConstant.URL));
        request.fileName = cursor.getString(cursor.getColumnIndex(RequestConstant.FILE_NAME));
        request.fileDir = cursor.getString(cursor.getColumnIndex(RequestConstant.FILE_DIR));
        request.totalSize = cursor.getLong(cursor.getColumnIndex(RequestConstant.TOTAL_SIZE));
        request.currentSize = cursor.getLong(cursor.getColumnIndex(RequestConstant.CURRENT_SIZE));
        request.supportRange = cursor.getInt(cursor.getColumnIndex(RequestConstant.SUPPORT_RANGE)) == 1;
        request.lastModify = cursor.getString(cursor.getColumnIndex(RequestConstant.LAST_MODIFY));
        request.status = cursor.getInt(cursor.getColumnIndex(RequestConstant.STATUS));

        return request;
    }


    public static Observable<DownloadRequest> create(@NonNull String url) {
        return Observable.create(new RequestInitSubscribe(url))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public DownloadTask execute(){
        return DownloadManager.getInstance().getDownloadTask(this);
    }



    @Override
    public String toString() {
        return "DownloadRequest{" +
                "tag='" + tag + '\'' +
                ", url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileDir='" + fileDir + '\'' +
                ", currentSize=" + currentSize +
                ", totalSize=" + totalSize +
                ", supportRange=" + supportRange +
                ", lastModify='" + lastModify + '\'' +
                ", status=" + status +
                '}';
    }
}
