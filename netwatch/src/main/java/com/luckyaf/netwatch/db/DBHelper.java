package com.luckyaf.netwatch.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.luckyaf.netwatch.NetWatch;
import com.luckyaf.netwatch.constant.ProgressConstant;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 类描述：
 *
 * @author Created by luckyAF on 2018/8/20
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "netwatch.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE_DOWNLOAD = "download";

    static final Lock lock = new ReentrantLock();

    private TableEntity downloadTableEntity = new TableEntity(TABLE_DOWNLOAD);

    public DBHelper() {
        this(NetWatch.getInstance().getContext());
    }

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        downloadTableEntity
                .addColumn(new ColumnEntity(ProgressConstant.TAG, "VARCHAR", true, true))//
                .addColumn(new ColumnEntity(ProgressConstant.URL, "VARCHAR"))//
                .addColumn(new ColumnEntity(ProgressConstant.FILE_DIR, "VARCHAR"))//
                .addColumn(new ColumnEntity(ProgressConstant.FILE_NAME, "VARCHAR"))//
                .addColumn(new ColumnEntity(ProgressConstant.FRACTION, "VARCHAR"))//
                .addColumn(new ColumnEntity(ProgressConstant.TOTAL_SIZE, "INTEGER"))//
                .addColumn(new ColumnEntity(ProgressConstant.CURRENT_SIZE, "INTEGER"))//
                .addColumn(new ColumnEntity(ProgressConstant.STATUS, "INTEGER"))//
                .addColumn(new ColumnEntity(ProgressConstant.NOTIFY_INTERVAL,"INTEGER"))
                .addColumn(new ColumnEntity(ProgressConstant.SPEED_LIMIT, "INTEGER"))//
                .addColumn(new ColumnEntity(ProgressConstant.SUPPORT_RANGE, "INTEGER"))//
                .addColumn(new ColumnEntity(ProgressConstant.LAST_MODIFY, "VARCHAR"));


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
