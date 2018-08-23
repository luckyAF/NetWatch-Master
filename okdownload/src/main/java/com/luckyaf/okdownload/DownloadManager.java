package com.luckyaf.okdownload;

import android.content.ContentValues;
import android.database.Cursor;

import com.luckyaf.okdownload.constant.RequestConstant;
import com.luckyaf.okdownload.constant.StatusConstant;
import com.luckyaf.okdownload.db.BaseDao;
import com.luckyaf.okdownload.db.DBHelper;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/22
 */
@SuppressWarnings({"unused","WeakerAccess"})
public class DownloadManager extends BaseDao<DownloadRequest>{
    
    private HashMap<Object, DownloadTask> downloadTasks;//用来存放正在下载的请求


    private DownloadManager (){
        super(new DBHelper());
        downloadTasks = new HashMap<>();

    }
    public static  DownloadManager getInstance() {
        return Holder.INSTANCE;
    }
    private static class Holder {
        private static final DownloadManager INSTANCE = new DownloadManager();
    }

     public DownloadTask getDownloadTask(DownloadRequest request){
        if(downloadTasks.containsKey(request.getTag())){
            return downloadTasks.get(request.getTag());
        } else{
            DownloadTask downloadTask = DownloadTask.createTask(request);
            downloadTasks.put(request.getTag(),downloadTask);
            return downloadTask;
        }
    }

    public Observable<Progress> listen(String tag){
        if(downloadTasks.containsKey(tag)){
            return downloadTasks.get(tag).listenProgress();
        }else{
            return null;
        }
    }

    public  void cancelTask(String tag){
        if(downloadTasks.containsKey(tag)){
            DownloadTask downloadTask = downloadTasks.get(tag);
            downloadTask.pause();
        }
    }
    public  void removeTask(String tag){
        if(downloadTasks.containsKey(tag)){
            downloadTasks.remove(tag);
        }
    }



    @Override
    public String getTableName() {
        return DBHelper.TABLE_DOWNLOAD;
    }

    @Override
    public void unInit() {

    }

    @Override
    public DownloadRequest parseCursorToBean(Cursor cursor) {
        return DownloadRequest.parseCursorToBean(cursor);
    }

    @Override
    public ContentValues getContentValues(DownloadRequest downloadRequest) {
        return DownloadRequest.buildContentValues(downloadRequest);
    }


    /** 获取下载任务 */
    public DownloadRequest get(String tag) {
        return queryOne(RequestConstant.TAG + "=?", new String[]{tag});
    }

    /** 移除下载任务 */
    public void delete(String taskKey) {
        delete(RequestConstant.TAG + "=?", new String[]{taskKey});
    }

    /** 更新下载任务 */
    public boolean update(DownloadRequest request) {
        return update(request, RequestConstant.TAG + "=?", new String[]{request.getTag()});
    }

    /** 更新下载任务 */
    public boolean update(ContentValues contentValues, String tag) {
        return update(contentValues, RequestConstant.TAG + "=?", new String[]{tag});
    }

    /** 获取所有下载信息 */
    public List<DownloadRequest> getAll() {
        return query(null, null, null, null, null, null , null);
    }

    /** 获取已所有下载信息 */
    public List<DownloadRequest> getFinished() {
        return query(null, "status=?", new String[]{StatusConstant.FINISHED + ""}, null, null, null, null);
    }

    /** 获取未完成下载信息 */
    public List<DownloadRequest> getDownloading() {
        return query(null, "status not in(?)", new String[]{StatusConstant.FINISHED + ""}, null, null, null, null);
    }

    /** 清空下载任务 */
    public boolean clear() {
        return deleteAll();
    }


}
