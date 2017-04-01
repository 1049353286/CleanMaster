package com.apricot.cleanmaster.utils;

/**
 * Created by Apricot on 2016/11/11.
 */

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;


import com.apricot.cleanmaster.bean.ApkFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取手机上apk文件信息类，主要是判断是否安装再手机上了，安装的版本比较现有apk版本信息
 * @author  Dylan
 */
public class ApkSearchUtils {
    private static int INSTALLED = 0; // 表示已经安装，且跟现在这个apk文件是一个版本
    private static int UNINSTALLED = 1; // 表示未安装
    private static int INSTALLED_UPDATE =2; // 表示已经安装，版本比现在这个版本要低，可以点击按钮更新

    public static final int COLUMN_ID = 0;

    public static final int COLUMN_PATH = 1;

    public static final int COLUMN_SIZE = 2;

    public static final int COLUMN_DATE = 3;

    private Context context;
    private List<ApkFile> apkFiles = new ArrayList<ApkFile>();

    public List<ApkFile> getApkFiles() {
        Log.d("ApkSearch","getFiles");
        return apkFiles;
    }

    public void setApkFiles(List<ApkFile> apkFiles) {
        this.apkFiles = apkFiles;
    }

    public ApkSearchUtils(Context context) {
        super();
        this.context = context;
    }

    /**
     * @param file
     *            运用递归的思想，递归去找每个目录下面的apk文件
     */
    public void FindAllAPKFile(File file) {

        // 手机上的文件,目前只判断SD卡上的APK文件
        // file = Environment.getDataDirectory();
        // SD卡上的文件目录
        if (file.isFile()) {
            String name_s = file.getName();
            ApkFile apkFile = new ApkFile();
            String apk_path = null;
            // MimeTypeMap.getSingleton()
            if(name_s.equals("Android")){
                return;
            }
            if (name_s.toLowerCase().endsWith(".apk")) {
                Log.d("ApkSearch","findApk");
                apk_path = file.getAbsolutePath();// apk文件的绝对路劲
                // System.out.println("----" + file.getAbsolutePath() + "" +
                // name_s);
                PackageManager pm = context.getPackageManager();
                PackageInfo packageInfo = pm.getPackageArchiveInfo(apk_path, PackageManager.GET_ACTIVITIES);
                if(packageInfo==null){
                    return;
                }
                ApplicationInfo appInfo = packageInfo.applicationInfo;


                /**获取apk的图标 */
                appInfo.sourceDir = apk_path;
                appInfo.publicSourceDir = apk_path;
                Drawable apk_icon = appInfo.loadIcon(pm);
                apkFile.setApkIcon(apk_icon);
                /** 得到包名 */
                String packageName = packageInfo.packageName;
                apkFile.setPackageName(packageName);
                /** apk的绝对路劲 */
                apkFile.setFilePath(file.getAbsolutePath());
                /** apk的版本名称 String */
                String versionName = packageInfo.versionName;
                apkFile.setVersionName(versionName);
                /** apk的版本号码 int */
                int versionCode = packageInfo.versionCode;
                apkFile.setVersionCode(versionCode);
                /**安装处理类型*/
                int type = doType(pm, packageName, versionCode);
                apkFile.setInstalled(type);

                Log.i("ok", "处理类型:"+String.valueOf(type)+"\n" + "------------------我是纯洁的分割线-------------------");
                apkFiles.add(apkFile);
            }
            // String apk_app = name_s.substring(name_s.lastIndexOf("."));
        } else {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File file_str : files) {
                    FindAllAPKFile(file_str);
                }
            }
        }
    }

	/*
	 * 判断该应用是否在手机上已经安装过，有以下集中情况出现
	 * 1.未安装，这个时候按钮应该是“安装”点击按钮进行安装
	 * 2.已安装，按钮显示“已安装” 可以卸载该应用
	 * 3.已安装，但是版本有更新，按钮显示“更新” 点击按钮就安装应用
	 */

    /**
     * 判断该应用在手机中的安装情况
     * @param pm                   PackageManager
     * @param packageName  要判断应用的包名
     * @param versionCode     要判断应用的版本号
     */
    private int doType(PackageManager pm, String packageName, int versionCode) {
        List<PackageInfo> pakageinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo pi : pakageinfos) {
            String pi_packageName = pi.packageName;
            int pi_versionCode = pi.versionCode;
            //如果这个包名在系统已经安装过的应用中存在
            if(packageName.endsWith(pi_packageName)){
                //Log.i("test","此应用安装过了");
                if(versionCode==pi_versionCode){
                    Log.i("test","已经安装，不用更新，可以卸载该应用");
                    return INSTALLED;
                }else if(versionCode>pi_versionCode){
                    Log.i("test","已经安装，有更新");
                    return INSTALLED_UPDATE;
                }
            }
        }
        Log.i("test","未安装该应用，可以安装");
        return UNINSTALLED;
    }


    public Cursor query() {

        String volumeName = "external";
        Uri uri = MediaStore.Files.getContentUri(volumeName);
        String selection = MediaStore.Files.FileColumns.DATA + " LIKE '%.apk'";

        if (uri == null) {
            Log.e("ApkSearch", "invalid uri");
            return null;
        }

        String[] columns = new String[] {
                MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATE_MODIFIED
        };

        if(context.getContentResolver().query(uri, columns, selection, null, null)!=null){
            Cursor cursor=context.getContentResolver().query(uri, columns, selection, null, null);
            return cursor;
        }else{
            return null;
        }

    }

    public void queryApkFile(){
        Cursor cursor=query();
        if(cursor==null){
            return;
        }
        while(cursor.moveToNext()){
            ApkFile file=new ApkFile();
            String apk_path=cursor.getString(COLUMN_PATH);
            file.setFilePath(apk_path);
            file.setApkName(getNameFromFilepath(apk_path));
            file.setFileSize(cursor.getLong(COLUMN_SIZE));
            file.setModifiedDate(cursor.getLong(COLUMN_DATE));

            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageArchiveInfo(apk_path, PackageManager.GET_ACTIVITIES);
            if(packageInfo==null){
                continue;
            }
            ApplicationInfo appInfo = packageInfo.applicationInfo;


            /**获取apk的图标 */
            appInfo.sourceDir = apk_path;
            appInfo.publicSourceDir = apk_path;
            Drawable apk_icon = appInfo.loadIcon(pm);
            file.setApkIcon(apk_icon);
            /** 得到包名 */
            String packageName = packageInfo.packageName;
            file.setPackageName(packageName);
            /** apk的版本名称 String */
            String versionName = packageInfo.versionName;
            file.setVersionName(versionName);
            /** apk的版本号码 int */
            int versionCode = packageInfo.versionCode;
            file.setVersionCode(versionCode);
            /**安装处理类型*/
            int type = doType(pm, packageName, versionCode);
            file.setInstalled(type);

            apkFiles.add(file);
        }
    }

    public static String getNameFromFilepath(String filepath) {
        int pos = filepath.lastIndexOf('/');
        if (pos != -1) {
            return filepath.substring(pos + 1);
        }
        return "";
    }

}