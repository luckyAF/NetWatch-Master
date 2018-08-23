package com.luckyaf.okdownload.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.luckyaf.okdownload.OkDownload;
import com.luckyaf.okdownload.constant.RequestConstant;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/20
 */
@SuppressWarnings({"unused","WeakerAccess"})
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "okdownload.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE_DOWNLOAD = "download";

    static final Lock lock = new ReentrantLock();

    private TableEntity downloadTableEntity = new TableEntity(TABLE_DOWNLOAD);

    public DBHelper() {
        this(OkDownload.getInstance().getContext());
    }

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        downloadTableEntity
                .addColumn(new ColumnEntity(RequestConstant.TAG, "VARCHAR", true, true))//
                .addColumn(new ColumnEntity(RequestConstant.URL, "VARCHAR"))//
                .addColumn(new ColumnEntity(RequestConstant.FILE_NAME, "VARCHAR"))//
                .addColumn(new ColumnEntity(RequestConstant.FILE_DIR, "VARCHAR"))//
                .addColumn(new ColumnEntity(RequestConstant.CURRENT_SIZE, "INTEGER"))//
                .addColumn(new ColumnEntity(RequestConstant.TOTAL_SIZE, "INTEGER"))//
                .addColumn(new ColumnEntity(RequestConstant.SUPPORT_RANGE, "INTEGER"))//
                .addColumn(new ColumnEntity(RequestConstant.LAST_MODIFY, "VARCHAR"))
                .addColumn(new ColumnEntity(RequestConstant.STATUS, "INTEGER"));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(downloadTableEntity.buildTableString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (DBUtils.isNeedUpgradeTable(db, downloadTableEntity)) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOWNLOAD);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
