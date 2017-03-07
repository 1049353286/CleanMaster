package com.apricot.cleanmaster.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import com.apricot.cleanmaster.bean.AutoStartInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Apricot on 2016/9/26.
 */
public class BootStartUtil {
    private static final String BOOT_START_PERMISSION="android.permission.RECEIVE_BOOT_CPMPLETED";

    public static List<AutoStartInfo> fetchInstalledApps(Context context){
        PackageManager pm=context.getPackageManager();
        List<ApplicationInfo> appList=pm.getInstalledApplications(0);
        Iterator<ApplicationInfo> appInfoIterator=appList.iterator();
        List<AutoStartInfo> autoStartInfos=new ArrayList<>();
        while (appInfoIterator.hasNext()){
            ApplicationInfo app=appInfoIterator.next();
            int flag=pm.checkPermission(BOOT_START_PERMISSION,app.packageName);
            if(flag==PackageManager.PERMISSION_GRANTED){
                AutoStartInfo autoStartApp=new AutoStartInfo();
                autoStartApp.setLabel(pm.getApplicationLabel(app).toString());
                autoStartApp.setIcon(pm.getApplicationIcon(app));
                autoStartApp.setPkgName(app.packageName);
                if((app.flags&ApplicationInfo.FLAG_SYSTEM)!=0){
                    autoStartApp.setSystem(true);
                }else{
                    autoStartApp.setSystem(false);
                }
                autoStartInfos.add(autoStartApp);
            }
        }
        return autoStartInfos;
    }


    public static List<AutoStartInfo> fetchAutoApps(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);
        List<ResolveInfo> resolveInfoList = pm.queryBroadcastReceivers(intent, PackageManager.MATCH_DISABLED_COMPONENTS);
        List<AutoStartInfo> appList = new ArrayList<>();
        String appName = null;
        String packageReceiver = null;
        Drawable icon = null;
        boolean isSystem = false;
        boolean isenable = true;
        if (resolveInfoList.size() > 0) {

            appName = resolveInfoList.get(0).loadLabel(pm).toString();
            packageReceiver = resolveInfoList.get(0).activityInfo.packageName + "/" + resolveInfoList.get(0).activityInfo.name;
            icon = resolveInfoList.get(0).loadIcon(pm);
            ComponentName mComponentName1 = new ComponentName(resolveInfoList.get(0).activityInfo.packageName, resolveInfoList.get(0).activityInfo.name);

            if (pm.getComponentEnabledSetting(mComponentName1) == 2) {

                isenable = false;
            } else {
                isenable = true;
            }
            if ((resolveInfoList.get(0).activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                isSystem = true;
            } else {
                isSystem = false;
            }
            for (int i = 1; i < resolveInfoList.size(); i++) {
                AutoStartInfo mAutoStartInfo = new AutoStartInfo();
                if (appName.equals(resolveInfoList.get(i).loadLabel(pm).toString())) {
                    packageReceiver = packageReceiver + ";" + resolveInfoList.get(i).activityInfo.packageName + "/" + resolveInfoList.get(i).activityInfo.name;
                } else {
                    mAutoStartInfo.setLabel(appName);
                    mAutoStartInfo.setSystem(isSystem);
                    mAutoStartInfo.setEnable(isenable);
                    mAutoStartInfo.setIcon(icon);
                    mAutoStartInfo.setPkgReceiver(packageReceiver);

                    appList.add(mAutoStartInfo);
                    appName = resolveInfoList.get(i).loadLabel(pm).toString();
                    packageReceiver = resolveInfoList.get(i).activityInfo.packageName + "/" + resolveInfoList.get(i).activityInfo.name;
                    icon = resolveInfoList.get(i).loadIcon(pm);
                    ComponentName mComponentName2 = new ComponentName(resolveInfoList.get(i).activityInfo.packageName, resolveInfoList.get(i).activityInfo.name);
                    if (pm.getComponentEnabledSetting(mComponentName2) == 2) {

                        isenable = false;
                    } else {
                        isenable = true;
                    }

                    if ((resolveInfoList.get(i).activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        isSystem = true;
                    } else {
                        isSystem = false;
                    }

                }

            }
            AutoStartInfo mAutoStartInfo = new AutoStartInfo();
            mAutoStartInfo.setLabel(appName);
            mAutoStartInfo.setSystem(isSystem);
            mAutoStartInfo.setEnable(isenable);
            mAutoStartInfo.setIcon(icon);
            mAutoStartInfo.setPkgReceiver(packageReceiver);
            appList.add(mAutoStartInfo);
        }
        return appList;
    }

}
