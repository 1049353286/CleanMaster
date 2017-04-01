package com.apricot.cleanmaster.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.apricot.cleanmaster.ui.AppLockerActivity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AppLockerService extends Service {
    private final static String TAG = "AppLockerService";
    private final static boolean DEBUG = false;

    private final int MSG_CHECK_FG_APP = 1;
    private final int MONITOR_INTERVAL = 300;
    private String SHARED_PREF_LOCK_STATUS = "lock_status";

    private HandlerThread mMonitorThread;
    private Handler mHandler;

    private HashMap<String, Boolean> mAppUnlockStatus;
    private KeyguardManager mKeyguardManager;

    public class Connection extends Binder {
        public AppLockerService getService() {
            return AppLockerService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (DEBUG) Log.d(TAG, "onBind");
        return new Connection();
    }

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "onCreate");
        super.onCreate();

        initLockList();

        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        mMonitorThread = new HandlerThread("AppLockerMonitorThread");
        mMonitorThread.start();

        mHandler = new Handler(mMonitorThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_CHECK_FG_APP:
                        if (mKeyguardManager.inKeyguardRestrictedInputMode()) {
                            if (DEBUG) Log.d(TAG, "Screen locked");
                            resetUnlockStatus();
                            break;
                        }

                        String foregroundApp = getForegroundApp();

                        if (shouldLock(foregroundApp)) {
                            Intent intent = new Intent(AppLockerService.this, AppLockerActivity.class);
                            intent.putExtra("APP_NAME", foregroundApp);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        break;
                }

                mHandler.sendEmptyMessageDelayed(MSG_CHECK_FG_APP, MONITOR_INTERVAL);
            }
        };

        mHandler.sendEmptyMessage(MSG_CHECK_FG_APP);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getStringExtra("ADD_APP") != null) {
            String packageName = intent.getStringExtra("ADD_APP");
            mAppUnlockStatus.put(packageName, false);
            if (DEBUG) Log.d(TAG, "add app into list: " + packageName);
        } else if (intent.getStringExtra("REMOVE_APP") != null) {
            String packageName = intent.getStringExtra("REMOVE_APP");
            mAppUnlockStatus.remove(packageName);
            if (DEBUG) Log.d(TAG, "remove app from list: " + packageName);
        } else if (intent.getStringExtra("UNLOCK_APP") != null) {
            String packageName = intent.getStringExtra("UNLOCK_APP");
            if (mAppUnlockStatus.get(packageName) != null) {
                mAppUnlockStatus.put(packageName, true);
                if (DEBUG) Log.d(TAG, "unlock app: " + packageName);
            }
        }

//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    private void resetUnlockStatus() {
        Iterator iter = mAppUnlockStatus.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            entry.setValue(false);
        }
    }

    private void initLockList() {
        mAppUnlockStatus = new HashMap<>();

        SharedPreferences pref = getSharedPreferences(SHARED_PREF_LOCK_STATUS, Context.MODE_PRIVATE);
        Map<String, ?> lockedApps = pref.getAll();
        Iterator iter = lockedApps.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String packageName = (String) entry.getKey();
            Boolean isLocked = (Boolean) entry.getValue();
            if (isLocked) {
                mAppUnlockStatus.put(packageName, false);
            }
        }
    }

    private boolean shouldLock(String pkgName) {
        if (mAppUnlockStatus.get(pkgName) != null
                && mAppUnlockStatus.get(pkgName).equals(false)) {
            return true;
        }

        return false;
    }

    private String getForegroundApp() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext()
                                                 .getSystemService(Context.USAGE_STATS_SERVICE);
        long ts = System.currentTimeMillis();
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return null;
        }

        UsageStats recentStats = null;
        for (UsageStats usageStats : queryUsageStats) {
            if(recentStats == null || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()){
                if (DEBUG) Log.d(TAG, usageStats.getPackageName() + ", " + usageStats.getLastTimeStamp());
                recentStats = usageStats;
            }
        }

        if (DEBUG) Log.d(TAG, "getForegroundApp: " + recentStats.getPackageName());
        return recentStats.getPackageName();
    }
}
