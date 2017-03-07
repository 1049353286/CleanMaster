package com.apricot.cleanmaster.bean;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.apricot.cleanmaster.R;

/**
 * Created by Apricot on 2017/1/13.
 */

public class AppLock {
    private Context mContext;
    private String mPackageName;
    private boolean mLocked;
    private SharedPreferences mLockStatus;

    private String SHARED_PREF_LOCK_STATUS = "lock_status";

    public AppLock(Context context, String packageName) {
        mContext = context;
        mPackageName = packageName;

        mLockStatus = mContext.getSharedPreferences(SHARED_PREF_LOCK_STATUS, Context.MODE_PRIVATE);
        mLocked = mLockStatus.getBoolean(packageName, false);
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        this.mPackageName = packageName;
    }

    public boolean isLocked() {
        return mLocked;
    }

    public void setLocked(boolean locked) {
        this.mLocked = locked;

        SharedPreferences.Editor editor = mLockStatus.edit();
        editor.putBoolean(mPackageName, mLocked);
        editor.apply();
    }

    public Drawable getIcon() {
        Drawable icon = null;

        try {
            icon = mContext.getPackageManager().getApplicationIcon(mPackageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return icon;
    }

    public String getAppName(){
        String appName=null;

        try {
            ApplicationInfo appInfo=null;
            PackageManager pm=mContext.getPackageManager();
            appInfo=pm.getApplicationInfo(mPackageName,0);
            appName=appInfo.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;

    }

    public Drawable getLockStatusIcon() {
        int resId = (mLocked ? R.mipmap.lock_locked : R.mipmap.lock_unlocked);
        return mContext.getResources().getDrawable(resId, null);
    }
}
