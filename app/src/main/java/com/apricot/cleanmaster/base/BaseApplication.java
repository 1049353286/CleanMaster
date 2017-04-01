package com.apricot.cleanmaster.base;

import android.app.Application;
import android.content.Context;

import com.umeng.analytics.MobclickAgent;

public class BaseApplication extends Application {
    private static BaseApplication mInstance;


    private static BaseApplication mContext;


    public static BaseApplication getAppInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext=this;

        MobclickAgent.setScenarioType(mContext, MobclickAgent.EScenarioType.E_UM_NORMAL);

        MyCrashHandler myCrashHandler = MyCrashHandler.getInstance();
        myCrashHandler.init(getApplicationContext());
        Thread.currentThread().setUncaughtExceptionHandler(myCrashHandler);
    }


    @Override
    public void onLowMemory() {
        // TODO Auto-generated method stub
        MobclickAgent.onKillProcess(mContext);
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onLowMemory();

    }

}
