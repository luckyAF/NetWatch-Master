package com.luckyaf.netwatch.db;

/**
 * 类描述：列的属性
 *
 * @author Created by luckyAF on 2018/8/20
 */
public class ColumnEntity {

    public String columnName;               //列的名字
    public String columnType;               //列的类型
    public String[] compositePrimaryKey;    //复合主键
    public boolean isPrimary;               //是否是主键
    public boolean isNotNull;               //是否不能为空
    public boolean isAutoincrement;         //AUTOINCREMENT 是否自增

    /**
     * @param compositePrimaryKey 复合主键
     */
    public ColumnEntity(String... compositePrimaryKey) {
        this.compositePrimaryKey = compositePrimaryKey;
    }

    /**
     * @param columnName 列名
     * @param columnType 列的数据类型
     */
    public ColumnEntity(String columnName, String columnType) {
        this(columnName, columnType, false, false, false);
    }

    /**
     * @param columnName 列名
     * @param columnType 列的数据类型
     * @param isPrimary  是否为主键
     * @param isNotNull  是否不能为空
     */
    public ColumnEntity(String columnName, String columnType, boolean isPrimary, boolean isNotNull) {
        this(columnName, columnType, isPrimary, isNotNull, false);
    }

    /**
     * @param columnName      列名
     * @param columnType      列的数据类型
     * @param isPrimary       是否为主键
     * @param isNotNull       是否不能为空
     * @param isAutoincrement 是否自增
     */
    public ColumnEntity(String columnName, String columnType, boolean isPrimary, boolean isNotNull, boolean isAutoincrement) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.isPrimary = isPrimary;
        this.isNotNull = isNotNull;
        this.isAutoincrement = isAutoincrement;
    }
}
