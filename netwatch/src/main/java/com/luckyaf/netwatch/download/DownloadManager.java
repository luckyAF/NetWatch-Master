package com.luckyaf.netwatch.download;

import android.content.ContentValues;
import android.database.Cursor;

import com.luckyaf.netwatch.constant.ProgressConstant;
import com.luckyaf.netwatch.db.BaseDao;
import com.luckyaf.netwatch.db.DBHelper;
import com.luckyaf.netwatch.model.Progress;

import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/20
 */
public class DownloadManager extends BaseDao<Progress> {

    private HashMap<Object, DownloadTask> downloadTasks;//用来存放各个下载的请求
    private HashMap<Object, Call> downloadCalls;
    private OkHttpClient mClient;//OKHttpClient;

    private DownloadManager (){
        super(new DBHelper());
        downloadTasks = new HashMap<>();
        downloadCalls = new HashMap<>();
        mClient = new OkHttpClient.Builder().build();
    }
    public static  DownloadManager getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public String getTableName() {
        return DBHelper.TABLE_DOWNLOAD;
    }

    @Override
    public void unInit() {

    }

    @Override
    public Progress parseCursorToBean(Cursor cursor) {
        return Progress.parseCursorToBean(cursor);
    }

    @Override
    public ContentValues getContentValues(Progress progress) {
        return Progress.buildContentValues(progress);
    }
    private static class Holder {
        private static final DownloadManager INSTANCE = new DownloadManager();
    }


    public OkHttpClient getClient(){
        return mClient;
    }

    public DownloadTask getDownloadTask(Progress progress){
        if(downloadTasks.containsKey(progress.getTag())){
            return downloadTasks.get(progress.getTag());
        } else{
            DownloadTask downloadTask = DownloadTask.createTask(progress);
            downloadTasks.put(progress.getTag(),downloadTask);
            return downloadTask;
        }
    }


    /**
     * 暂停任务
     * @param tag  tag
     */
    public void cancelRequest(Object tag){
        DownloadTask task = downloadTasks.get(tag);
        if (task != null) {
            task.pause();//取消
        }
    }

    /**
     * 删除任务
     * @param tag  tag
     * @param deleteFile  是否删除源文件
     */
    public void removeRequest(Object tag,boolean deleteFile){
        DownloadTask task = downloadTasks.get(tag);
        if (task != null) {
            task.remove(deleteFile);
        }
    }


    public void addCall(Object tag,Call call){
        downloadCalls.put(tag,call);
    }

    public void cancelCall(Object tag) {
        Call call = downloadCalls.get(tag);
        if (call != null) {
            call.cancel();//取消
        }
    }


    public void removeCal(Object tag) {
        downloadCalls.remove(tag);
    }


    /** 获取下载任务 */
    public Progress get(String tag) {
        return queryOne(ProgressConstant.TAG + "=?", new String[]{tag});
    }

    /** 移除下载任务 */
    public void delete(String taskKey) {
        delete(ProgressConstant.TAG + "=?", new String[]{taskKey});
    }

    /** 更新下载任务 */
    public boolean update(Progress progress) {
        return update(progress, ProgressConstant.TAG + "=?", new String[]{progress.getTag()});
    }

    /** 更新下载任务 */
    public boolean update(ContentValues contentValues, String tag) {
        return update(contentValues, ProgressConstant.TAG + "=?", new String[]{tag});
    }

    /** 获取所有下载信息 */
    public List<Progress> getAll() {
        return query(null, null, null, null, null, null , null);
    }

    /** 获取已所有下载信息 */
    public List<Progress> getFinished() {
        return query(null, "status=?", new String[]{ProgressConstant.FINISHED + ""}, null, null, null, null);
    }

    /** 获取未完成下载信息 */
    public List<Progress> getDownloading() {
        return query(null, "status not in(?)", new String[]{ProgressConstant.FINISHED + ""}, null, null, null, null);
    }

    /** 清空下载任务 */
    public boolean clear() {
        return deleteAll();
    }




}
