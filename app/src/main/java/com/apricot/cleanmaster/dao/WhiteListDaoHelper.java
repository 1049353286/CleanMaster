package com.apricot.cleanmaster.dao;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.apricot.cleanmaster.utils.L;

/**
 * Created by Apricot on 2017/2/1.
 */

public class WhiteListDaoHelper extends SQLiteOpenHelper{

    private static final String TAG="WhiteListDaoHelper";

    public static final String DB_NAME = "whitelist.db";

    private static final String DB_CREATE_TABLE="create table whitelist (" +
            "_id integer primary key autoincrement," +
            "packageName text," +
            "appIcon blob,"+
            "appName text," +
            "version text," +
            "isUser text)";

    public WhiteListDaoHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public WhiteListDaoHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE_TABLE);

        L.d(TAG,"table create success");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists whitelist");
        onCreate(db);

    }
}
