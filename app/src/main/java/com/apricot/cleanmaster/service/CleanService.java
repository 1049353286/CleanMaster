package com.apricot.cleanmaster.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StatFs;
import android.support.annotation.Nullable;

import com.apricot.cleanmaster.bean.CacheListItem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Apricot on 2016/10/16.
 */
public class CleanService extends Service{

    public static final String ACTION_CLEAN_AND_EXIT = "com.apricot.cache.cleaner.CLEAN_AND_EXIT";

    private static final String TAG = "CleanService";

    private Method mGetPackageSizeInfoMethod, mFreeStorageAndNotifyMethod;
    private OnCleanActionListener mOnActionListener;
    private boolean mIsScanning = false;
    private boolean mIsCleaning = false;
    private long mCacheSize = 0;

    public  interface OnCleanActionListener {
        void onScanStarted(Context context);

        void onScanProgressUpdated(Context context, int current, int max);

        void onScanCompleted(Context context, List<CacheListItem> apps);

        void onCleanStarted(Context context);

        void onCleanCompleted(Context context, long cacheSize);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new CleanServiceBinder();
    }

    public class CleanServiceBinder extends Binder{
        public CleanService getService(){
            return CleanService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mGetPackageSizeInfoMethod = getPackageManager().getClass().getMethod(
                    "getPackageSizeInfo", String.class, IPackageStatsObserver.class);

            mFreeStorageAndNotifyMethod = getPackageManager().getClass().getMethod(
                    "freeStorageAndNotify", long.class, IPackageDataObserver.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private class TaskScan extends AsyncTask<Void,Integer,List<CacheListItem>>{
        private int mAppCount=0;
        @Override
        protected void onPreExecute() {
            if (mOnActionListener != null) {
                mOnActionListener.onScanStarted(CleanService.this);
            }
        }

        @Override
        protected List<CacheListItem> doInBackground(Void... params) {
            mCacheSize=0;
            final List<ApplicationInfo> appinfos=getPackageManager().getInstalledApplications(0);
            final List<CacheListItem> cacheListItems=new ArrayList<>();
            final CountDownLatch countDownLatch=new CountDownLatch(appinfos.size());
            publishProgress(0,appinfos.size());
            try {
                for(ApplicationInfo appinfo:appinfos) {
                    mGetPackageSizeInfoMethod.invoke(getPackageManager(), appinfo.packageName, new IPackageStatsObserver.Stub() {

                        @Override
                        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                            synchronized (cacheListItems) {
                                publishProgress(++mAppCount, appinfos.size());

                                if (succeeded && (pStats.externalCacheSize > 0||pStats.cacheSize>0)) {
                                    try {
                                        cacheListItems.add(new CacheListItem(pStats.packageName,
                                                getPackageManager().getApplicationLabel(
                                                        getPackageManager().getApplicationInfo(
                                                                pStats.packageName,
                                                                PackageManager.GET_META_DATA)
                                                ).toString(),
                                                getPackageManager().getApplicationIcon(
                                                        pStats.packageName),
                                                pStats.externalCacheSize+pStats.cacheSize
                                        ));

                                        mCacheSize += (pStats.externalCacheSize+pStats.cacheSize);
                                    } catch (PackageManager.NameNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            synchronized (countDownLatch) {
                                countDownLatch.countDown();
                            }
                        }
                    });
                }

                countDownLatch.await();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return new ArrayList<>(cacheListItems);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (mOnActionListener != null) {
                mOnActionListener.onScanProgressUpdated(CleanService.this, values[0], values[1]);
            }
        }

        @Override
        protected void onPostExecute(List<CacheListItem> cacheListItems) {
            if (mOnActionListener != null) {
                mOnActionListener.onScanCompleted(CleanService.this, cacheListItems);
            }
            mIsScanning=false;
        }
    }

    private class TaskClean extends AsyncTask<Void,Void,Long>{

        @Override
        protected void onPreExecute() {
            if (mOnActionListener != null) {
                mOnActionListener.onCleanStarted(CleanService.this);
            }
        }

        @Override
        protected Long doInBackground(Void... params) {
            final CountDownLatch countDownLatch=new CountDownLatch(1);
            StatFs statFs=new StatFs(Environment.getDataDirectory().getAbsolutePath());
            try{
                mFreeStorageAndNotifyMethod.invoke(getPackageManager(), (long)(statFs.getBlockCount() * statFs.getBlockSize()), new IPackageDataObserver.Stub() {
                    @Override
                    public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                        countDownLatch.countDown();
                    }
                });
                countDownLatch.await();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return mCacheSize;
        }

        @Override
        protected void onPostExecute(Long result) {
            mCacheSize = 0;

            if (mOnActionListener != null) {
                mOnActionListener.onCleanCompleted(CleanService.this, result);
            }

            mIsCleaning = false;
        }
    }

    public void scanCache() {
        mIsScanning = true;

        new TaskScan().execute();
    }

    public void cleanCache(){
        mIsCleaning=true;
        new TaskClean().execute();
    }



    public void setOnCleanActionListener(OnCleanActionListener listener){
        mOnActionListener=listener;
    }

    public boolean isScanning() {
        return mIsScanning;
    }

    public boolean isCleaning() {
        return mIsCleaning;
    }

    public long getCacheSize() {
        return mCacheSize;
    }
}
