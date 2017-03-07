package com.apricot.cleanmaster.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.apricot.cleanmaster.bean.AppInfo;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Apricot on 2017/2/1.
 */

public class WhiteListDao {
    private WhiteListDaoHelper daoHelper;
    private SQLiteDatabase db;
    Context mContext;

    public WhiteListDao(Context context){
        mContext=context;
        daoHelper=new WhiteListDaoHelper(context,WhiteListDaoHelper.DB_NAME,null,1,null);
        db=daoHelper.getWritableDatabase();
    }

    public void addWhiteApp(AppInfo appInfo){
        BitmapDrawable drawable= (BitmapDrawable) appInfo.getAppIcon();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Bitmap bm=drawable.getBitmap();
        bm.compress(Bitmap.CompressFormat.PNG, 100, os);
        ContentValues values = new ContentValues();
        values.put("appIcon",os.toByteArray());
        values.put("packageName",appInfo.getPkgName());
        values.put("appName",appInfo.getAppName());
        values.put("version",appInfo.getVersion());
        values.put("isUser",appInfo.isUserApp()?"true":"false");


        db.insert("whitelist",null,values);

//        String sql="insert into whitelist (packageName,appIcon，appName,version,isUser) values('"+appInfo.getPkgName()+"','"+os.toByteArray()+"','"+appInfo.getAppName()+"','"+appInfo.getVersion()+"','"+appInfo.isUserApp()+"')";
//        db.execSQL(sql);

    }

    public void deleteWhiteApp(AppInfo appInfo){
        String sql="delete from whitelist where packageName='"+appInfo.getPkgName()+"'";
        db.execSQL(sql);
    }

    public List<AppInfo> queryAllWhiteApp(){
        List<AppInfo> list=new ArrayList<>();
        Cursor cursor=db.query("whitelist",null,null,null,null,null,null,null);
        while (cursor.moveToNext()){

            AppInfo appInfo=new AppInfo();
            byte[] blob = cursor.getBlob(cursor.getColumnIndex("appIcon"));
            Bitmap bm=BitmapFactory.decodeByteArray(blob,0,blob.length);
            Drawable drawable=new BitmapDrawable(bm);
            appInfo.setAppIcon(drawable);
            appInfo.setAppName(cursor.getString(cursor.getColumnIndex("appName")));
            appInfo.setPkgName(cursor.getString(cursor.getColumnIndex("packageName")));
            appInfo.setVersion(cursor.getString(cursor.getColumnIndex("version")));
            appInfo.setUserApp(cursor.getString(cursor.getColumnIndex("isUser")).equals("true"));
            list.add(appInfo);
        }
        cursor.close();
        return list;
    }

}
