/*
 * Copyright (C) 2012 www.amsoft.cn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.apricot.cleanmaster.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Looper;
import android.os.RemoteException;
import android.util.TypedValue;

import com.apricot.cleanmaster.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.List;

/**
 *
 */
public class Utils {
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = 60 * 60;
    private static final int SECONDS_PER_DAY = 24 * 60 * 60;

    public static boolean isSystemApp(PackageInfo pInfo) {
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public static boolean isSystemUpdateApp(PackageInfo pInfo) {
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
    }

    public static boolean isUserApp(PackageInfo pInfo) {
        return (!isSystemApp(pInfo) && !isSystemUpdateApp(pInfo));
    }

    public static long getPkgSize(final Context context, String pkgName) throws NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException {
       long pkgSize=0;
        // getPackageSizeInfo是PackageManager中的一个private方法，所以需要通过反射的机制来调用
        Method method = PackageManager.class.getMethod("getPackageSizeInfo",
                new Class[]{String.class, IPackageStatsObserver.class});
        // 调用 getPackageSizeInfo 方法，需要两个参数：1、需要检测的应用包名；2、回调
        method.invoke(context.getPackageManager(), new Object[]{
                pkgName,
                new IPackageStatsObserver.Stub() {
                    @Override
                    public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                        // 子线程中默认无法处理消息循环，自然也就不能显示Toast，所以需要手动Looper一下
                        Looper.prepare();
                        // 从pStats中提取各个所需数据


                      //  pkgSize= pStats.cacheSize+pStats.dataSize+pStats.codeSize;
//                        Toast.makeText(context,
//                                "缓存大小=" + Formatter.formatFileSize(context, pStats.cacheSize) +
//                                        "\n数据大小=" + Formatter.formatFileSize(context, pStats.dataSize) +
//                                        "\n程序大小=" + Formatter.formatFileSize(context, pStats.codeSize),
//                                Toast.LENGTH_LONG).show();
                        // 遍历一次消息队列，弹出Toast
                        Looper.loop();
                    }
                }
        });

        return pkgSize;
    }
    public static void launchBrowser(Activity from, String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        from.startActivity(intent);
    }

    public static boolean isIntentSafe(Activity activity, Uri uri) {
        Intent mapCall = new Intent(Intent.ACTION_VIEW, uri);
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(mapCall, 0);
        return activities.size() > 0;
    }


    /**
     * Returns elapsed time for the given millis, in the following format: 2d 5h 40m 29s
     *
     * @param context
     *            the application context
     * @param millis
     *            the elapsed time in milli seconds
     * @return the formatted elapsed time
     */
    public static String formatElapsedTime(Context context, double millis) {
        StringBuilder sb = new StringBuilder();
        int seconds = (int) Math.floor(millis / 1000);

        int days = 0, hours = 0, minutes = 0;
        if (seconds > SECONDS_PER_DAY) {
            days = seconds / SECONDS_PER_DAY;
            seconds -= days * SECONDS_PER_DAY;
        }
        if (seconds > SECONDS_PER_HOUR) {
            hours = seconds / SECONDS_PER_HOUR;
            seconds -= hours * SECONDS_PER_HOUR;
        }
        if (seconds > SECONDS_PER_MINUTE) {
            minutes = seconds / SECONDS_PER_MINUTE;
            seconds -= minutes * SECONDS_PER_MINUTE;
        }
        if (days > 0) {
            sb.append(context.getString(R.string.battery_history_days, days, hours, minutes, seconds));
        } else if (hours > 0) {
            sb.append(context.getString(R.string.battery_history_hours, hours, minutes, seconds));
        } else if (minutes > 0) {
            sb.append(context.getString(R.string.battery_history_minutes, minutes, seconds));
        } else {
            sb.append(context.getString(R.string.battery_history_seconds, seconds));
        }
        return sb.toString();
    }

    /**
     * Formats data size in KB, MB, from the given bytes.
     *
     * @param context
     *            the application context
     * @param bytes
     *            data size in bytes
     * @return the formatted size such as 4.52 MB or 245 KB or 332 bytes
     */
    public static String formatBytes(Context context, double bytes) {
        // TODO: I18N
        if (bytes > 1000 * 1000) {
            return String.format("%.2f MB", ((int) (bytes / 1000)) / 1000f);
        } else if (bytes > 1024) {
            return String.format("%.2f KB", ((int) (bytes / 10)) / 100f);
        } else {
            return String.format("%d bytes", (int) bytes);
        }
    }

    public static String getBatteryPercentage(Intent batteryChangedIntent) {
        int level = batteryChangedIntent.getIntExtra("level", 0);
        int scale = batteryChangedIntent.getIntExtra("scale", 100);
        return String.valueOf(level * 100 / scale) + "%";
    }

    public static String getBatteryStatus(Resources res, Intent batteryChangedIntent) {
        final Intent intent = batteryChangedIntent;

        int plugType = intent.getIntExtra("plugged", 0);
        int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
        String statusString;
        if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
            statusString = res.getString(R.string.battery_info_status_charging);
            if (plugType > 0) {
                statusString = statusString
                        + " "
                        + res.getString((plugType == BatteryManager.BATTERY_PLUGGED_AC) ? R.string.battery_info_status_charging_ac
                        : R.string.battery_info_status_charging_usb);
            }
        } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
            statusString = res.getString(R.string.battery_info_status_discharging);
        } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
            statusString = res.getString(R.string.battery_info_status_not_charging);
        } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
            statusString = res.getString(R.string.battery_info_status_full);
        } else {
            statusString = res.getString(R.string.battery_info_status_unknown);
        }

        return statusString;
    }

    public static boolean isSystemApp(Context pContext, String pPackageName){
        PackageInfo info;
        try {
            info = pContext.getPackageManager().getPackageInfo(pPackageName, PackageManager.GET_ACTIVITIES);
			/*if(info.applicationInfo.uid > 10000){
				return false;
			}else */if ((info.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                return false;
            } else if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                return false;
            }else{
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Convert dp to pixel
     *
     * @param dp
     * @param context
     * @return pixels number
     */
    public static int dp2Pixel(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * Read file and return content of the file as string
     *
     * @param filename
     * @return file content
     */
    public static String readFile(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            StringBuilder sb = new StringBuilder();


            while ((line = br.readLine()) != null) {

                sb.append(line + "\n");

            }

            if (br != null) {
                br.close();
            }

            return sb.toString().trim();
        } catch (IOException e) {
        }

        return "";
    }

    /**
     * Format bytes to human readable
     *
     * @param bytes
     * @param decimals
     * @return human-readable string
     */
    public static String formatByte(long bytes, int decimals) {
        DecimalFormat df;
        if (decimals == 0) {
            df = new DecimalFormat("0");
        } else {
            StringBuilder sb = new StringBuilder(decimals);
            sb.append("0.");

            for (int i = 0; i < decimals; i++) {
                sb.append("0");
            }

            df = new DecimalFormat(sb.toString());
        }

        if (bytes >= 1073741824) {
            return df.format((float) bytes / 1073741824) + " GB";
        } else if (bytes >= 1048576) {
            return df.format((float) bytes / 1048576) + " MB";
        } else if (bytes >= 1024) {
            return df.format((float) bytes / 1024) + " KB";
        } else {
            return "" + bytes;
        }
    }

    /**
     * Convert ip int to string
     *
     * @param ip
     * @return string represent ip address
     */
    public static String toIpv4(int ip) {
        return String.format("%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));
    }

    /**
     * Check if app has specific permission granted
     *
     * @param context
     * @param name
     * @return
     */
    public static boolean checkPermissionGranted(Context context, String name) {

        int res = context.checkCallingOrSelfPermission(name);
        return res == PackageManager.PERMISSION_GRANTED;
    }
}
