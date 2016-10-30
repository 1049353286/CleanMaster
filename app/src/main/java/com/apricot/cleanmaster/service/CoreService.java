package com.apricot.cleanmaster.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.bean.AppProcessInfo;

import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Apricot on 2016/10/10.
 */
public class CoreService extends Service{

    public static final String ACTION_CLEAN_AND_EXIT = "com.yzy.service.cleaner.CLEAN_AND_EXIT";

    private static final String TAG = "CleanerService";

    private OnProcessActionListener mOnActionListener;
    private boolean mIsScanning = false;
    private boolean mIsCleaning = false;

    ActivityManager activityManager = null;
    List<AppProcessInfo> list = null;
    PackageManager packageManager = null;
    Context mContext;

    public static interface OnProcessActionListener {
        public void onScanStarted(Context context);

        public void onScanProgressUpdated(Context context, int current, int max);

        public void onScanCompleted(Context context, List<AppProcessInfo> apps);

        public void onCleanStarted(Context context);

        public void onCleanCompleted(Context context, long cacheSize);
    }

    public class ProcessServiceBinder extends Binder{
        public CoreService getService(){
            return CoreService.this;
        }
    }

    ProcessServiceBinder mBinder=new ProcessServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=getApplicationContext();

        activityManager= (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        packageManager=getPackageManager();
    }

    private class TaskScan extends AsyncTask<Void,Integer,List<AppProcessInfo>>{
        private int AppCount=0;

        @Override
        protected void onPreExecute() {
            if(mOnActionListener!=null){
                mOnActionListener.onScanStarted(mContext);
            }
        }

        @Override
        protected List<AppProcessInfo> doInBackground(Void... params) {
            list=new ArrayList<>();
//            google为了安全考虑，在5.0后调用activitymanager.getRunningAppProcesses（)方法只能返回你自己应用的进程
//            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList=activityManager.getRunningAppProcesses();

            List<AndroidAppProcess> runningAppProcessInfoList = ProcessManager.getRunningAppProcesses();
            publishProgress(0,runningAppProcessInfoList.size());
            AppProcessInfo mAppProcessInfo=null;
            ApplicationInfo appInfo=null;

            for(AndroidAppProcess appProcessInfo:runningAppProcessInfoList){
                publishProgress(AppCount++,runningAppProcessInfoList.size());
                mAppProcessInfo=new AppProcessInfo(appProcessInfo.name,appProcessInfo.pid,appProcessInfo.uid);
                try {
                    appInfo=packageManager.getApplicationInfo(mAppProcessInfo.processName,0);
                    if((appInfo.flags&ApplicationInfo.FLAG_SYSTEM)!=0){
                        mAppProcessInfo.isSystem=true;
                    }else{
                        mAppProcessInfo.isSystem=false;
                    }
                    mAppProcessInfo.icon=appInfo.loadIcon(packageManager);
                    mAppProcessInfo.appName=appInfo.loadLabel(packageManager).toString();
                } catch (PackageManager.NameNotFoundException e) {
                    //   e.printStackTrace();

                    // :服务的命名

                    if (appProcessInfo.name.indexOf(":") != -1) {
                        appInfo = getApplicationInfo(appProcessInfo.name.split(":")[0]);
                        if (appInfo != null) {
                            Drawable icon = appInfo.loadIcon(packageManager);
                            mAppProcessInfo.icon = icon;
                        }else{
                            mAppProcessInfo.icon = mContext.getResources().getDrawable(R.drawable.ic_launcher);
                        }

                    }else{
                        mAppProcessInfo.icon = mContext.getResources().getDrawable(R.drawable.ic_launcher);
                    }
                    mAppProcessInfo.isSystem = true;
                    mAppProcessInfo.appName = appProcessInfo.name;
                }

                long memSize=activityManager.getProcessMemoryInfo(new int[]{mAppProcessInfo.pid})[0].getTotalPrivateDirty()*1024;
                mAppProcessInfo.memory=memSize;
                list.add(mAppProcessInfo);

            }

            return list;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if(mOnActionListener!=null){
                mOnActionListener.onScanProgressUpdated(mContext,values[0],values[1]);
            }
        }

        @Override
        protected void onPostExecute(List<AppProcessInfo> appProcessInfos) {
            if(mOnActionListener!=null){
                mOnActionListener.onScanCompleted(mContext,appProcessInfos);
            }
        }
    }

    public void ScanRunningProcess(){
        new TaskScan().execute();
    }

    public void killBackgroundProcesses(String processName) {
        // mIsScanning = true;

        String packageName = null;
        try {
            if (processName.indexOf(":") == -1) {
                packageName = processName;
            } else {
                packageName = processName.split(":")[0];
            }

            activityManager.killBackgroundProcesses(packageName);

            //
            Method forceStopPackage = activityManager.getClass()
                    .getDeclaredMethod("forceStopPackage", String.class);
            forceStopPackage.setAccessible(true);
            forceStopPackage.invoke(activityManager, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private class TaskClean extends AsyncTask<Void,Void,Long>{

        @Override
        protected void onPreExecute() {
            if(mOnActionListener!=null){
                mOnActionListener.onCleanStarted(mContext);
            }
        }

        @Override
        protected Long doInBackground(Void... params) {
            long beforeMem=0;
            long afterMem=0;
            ActivityManager.MemoryInfo memoryInfo=new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            beforeMem=memoryInfo.availMem;
            List<ActivityManager.RunningAppProcessInfo> appProcessList = activityManager
                    .getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo info : appProcessList) {
                killBackgroundProcesses(info.processName);
            }
            activityManager.getMemoryInfo(memoryInfo);
            afterMem=memoryInfo.availMem;
            return beforeMem=afterMem;
        }

        @Override
        protected void onPostExecute(Long result) {
            if(mOnActionListener!=null){
                mOnActionListener.onCleanCompleted(mContext,result);
            }
        }
    }

    public void CleanAllProcess(){
        new TaskClean().execute();
    }

    public void setOnActionListener(OnProcessActionListener listener){
        mOnActionListener=listener;
    }

    public ApplicationInfo getApplicationInfo( String processName) {
        if (processName == null) {
            return null;
        }
        List<ApplicationInfo> appList = packageManager
                .getInstalledApplications(0);
        for (ApplicationInfo appInfo : appList) {
            if (processName.equals(appInfo.processName)) {
                return appInfo;
            }
        }
        return null;
    }



}
