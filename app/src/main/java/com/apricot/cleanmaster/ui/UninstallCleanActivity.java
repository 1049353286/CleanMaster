package com.apricot.cleanmaster.ui;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.adapter.UninstallCleanAdapter;
import com.apricot.cleanmaster.base.BaseSwipeBackActivity;
import com.apricot.cleanmaster.bean.ApkFile;
import com.apricot.cleanmaster.bean.AppInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Apricot on 2016/12/11.
 */

public class UninstallCleanActivity extends BaseSwipeBackActivity{
    private List<ApkFile> uninstallInfos;
    AsyncTask<Void,Integer,List<AppInfo>> mTask;
    List<AppInfo> userAppInfos=null;
    ListView mListVIew;
    private UninstallCleanAdapter mUninstallCleanAdapter;

    @Override
    protected int initLayout() {
        return R.layout.activity_uninstall_clean;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("卸载残留清理");

        mListVIew= (ListView) findViewById(R.id.lv_uninstall);


        mTask=new AsyncTask<Void, Integer, List<AppInfo>>() {

            @Override
            protected List<AppInfo> doInBackground(Void... params) {
                uninstallInfos=getApkUninstallInfos();

                PackageManager pm=getPackageManager();
                List<PackageInfo> packageInfos=pm.getInstalledPackages(0);
                List<AppInfo> appInfos=new ArrayList<>();
                for(PackageInfo packageInfo:packageInfos){
                    final AppInfo appInfo=new AppInfo();
                    appInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(pm));
                    appInfo.setAppName(packageInfo.applicationInfo.loadLabel(pm).toString());
                    String packageName=packageInfo.applicationInfo.packageName;
                    appInfo.setPkgName(packageName);
                    appInfo.setVersion(packageInfo.versionName);
                    appInfo.setUid(packageInfo.applicationInfo.uid);
                    int flags=packageInfo.applicationInfo.flags;
                    if((flags& ApplicationInfo.FLAG_SYSTEM)!=0){
                        appInfo.setUserApp(false);
                    }else{
                        appInfo.setUserApp(true);
                    }
                    if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)!=0){
                        appInfo.setInRom(false);
                    }else{
                        appInfo.setInRom(true);
                    }

                    appInfos.add(appInfo);

                }
                return appInfos;
            }

            @Override
            protected void onPostExecute(List<AppInfo> appInfos) {
                super.onPostExecute(appInfos);
                userAppInfos=new ArrayList<>();
                List<ApkFile> junkFile=new ArrayList<>();

                for(AppInfo appInfo:appInfos){
                    if(appInfo.isUserApp()){
                        userAppInfos.add(appInfo);
                        Log.d("MainActivity",appInfo.getAppName()+appInfo.getPkgName());
                    }
                }

                for(ApkFile uninstallInfo:uninstallInfos){
                    for(int i=0;i<userAppInfos.size();i++){
                        if(!uninstallInfo.getPackageName().equals(userAppInfos.get(i).getPkgName())&&(i==userAppInfos.size()-1)){
                            File file=new File(Environment.getExternalStorageDirectory()+uninstallInfo.getFilePath());
                            if(file.exists()&&file.length()>0){
                                Log.d("MainActivity",uninstallInfo.toString());
                                uninstallInfo.setApkIcon(getResources().getDrawable(R.mipmap.trash_bin,null));
                                junkFile.add(uninstallInfo);
                                break;
                            }
                        }
                    }
                }

                mUninstallCleanAdapter=new UninstallCleanAdapter(UninstallCleanActivity.this,junkFile);
                mListVIew.setAdapter(mUninstallCleanAdapter);
            }
        };
        mTask.execute();
//        paths = this.getPaths();
    }



//    public void clearData(View v){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
//                File[] files = file.listFiles();
//                if(files != null && files.length > 0){
//                    for(File f : files){
//                        String name = "/" + f.getName();
//                        //paths闆嗗悎涓寘鍚玭ame
//                        if(paths.contains(name)){
//                            delete(f);
//                        }
//                    }
//                }
//                Looper.prepare();
//                Toast.makeText(MainActivity.this, "SD鍗＄紦瀛樻竻鐞嗗畬鎴�", Toast.LENGTH_SHORT).show();
//                Looper.loop();
//            }
//        }).start();
//    }

    private void delete(File file){
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for(File f : files){
                delete(f);
            }
        }else{
            file.delete();
        }
    }


    private File copyDb(){

        File file = new File(getFilesDir(), "clearpath.db");
        if(!file.exists()){
            try {
                InputStream in = getAssets().open("clearpath.db");
                OutputStream out = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while((len = in.read(buffer)) != -1){
                    out.write(buffer, 0, len);
                }
                out.close();
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }


    private List<ApkFile> getApkUninstallInfos(){
        List<ApkFile> list = new ArrayList<>();
        File file = copyDb();
        if(file != null){
            SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
            if(db.isOpen()){
                Cursor c = db.query("softdetail", new String[]{"softChinesename","apkname","filepath"}, null, null, null, null, null);
                while(c.moveToNext()){
                    String apkname=c.getString(c.getColumnIndex("softChinesename"));
                    String path = c.getString(c.getColumnIndex("filepath"));
                    String pkgname=c.getString(c.getColumnIndex("apkname"));
                    ApkFile info=new ApkFile();
                    info.setApkName(apkname);
                    info.setPackageName(pkgname);
                    info.setFilePath(path);
//                    Log.d("MainActivity",info.toString());
                    list.add(info);
                }
                c.close();
                db.close();
            }
        }
        return list;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
