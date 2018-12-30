package com.zpx.contact;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "contacts.db";
    private static int DB_VERSION = 1;

    //创建数据库
    public DBHelper (Context context) {
        //1.上下文 2.数据库名称 3.一般都为null，用于创建一个Cursor对象 4.版本号
        super(context, DB_NAME, null, DB_VERSION);
    }

    //用于建表，在数据库第一次被创建的时候会调用  用于建表，在数据库第一次被创建的时候会调用
    public void onCreate (SQLiteDatabase db) {
        String sql = "CREATE TABLE contact (_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(10), phonenumber VARCHAR(12))";
        db.execSQL(sql);
    }

    //当数据库版本号增加时调用
    public void onUpgrade(SQLiteDatabase db , int oldVersion, int newVersion){

    }
}
