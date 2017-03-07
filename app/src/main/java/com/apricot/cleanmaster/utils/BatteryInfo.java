package com.apricot.cleanmaster.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


import com.apricot.cleanmaster.bean.BatterySipper;
import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;


public class BatteryInfo {
	
	private static final String TAG = "BatteryInfo";
	private static final boolean DEBUG = true;

	private double mMinPercentOfTotal = 0;
	private long mStatsPeriod = 0;

	private Context mContext;
	public int testType;

	public static enum DrainType {
		IDLE, CELL, PHONE, WIFI, BLUETOOTH, SCREEN, APP, KERNEL, MEDIASERVER;
	}

	public BatteryInfo(Context context) {
		testType = 1;
		mContext = context;
	}


	public void setMinPercentOfTotal(double minPercentOfTotal) {
		this.mMinPercentOfTotal = minPercentOfTotal;
	}


	/**
	 * ��ȡ��ص�ʹ��ʱ��
	 * 
	 * @return
	 */
	public String getStatsPeriod() {
		return Utils.formatElapsedTime(mContext, mStatsPeriod);
	}

	public List<BatterySipper> getBatteryStats() {


		return getAppListCpuTime();


//		mMaxPower = 0;
//		mTotalPower = 0;
//		mWifiPower = 0;
//		mBluetoothPower = 0;
//		mAppWifiRunning = 0;
//
//		mUsageList.clear();
//		mWifiSippers.clear();
//		mBluetoothSippers.clear();
//		processAppUsage();
////		processMiscUsage();
//
//		final List<BatterySipper> list = new ArrayList<BatterySipper>();
//
//		Collections.sort(mUsageList);
//		for (BatterySipper sipper : mUsageList) {
//			if (sipper.getValue() < MIN_POWER_THRESHOLD)
//				continue;
//			final double percentOfTotal = ((sipper.getValue() / mTotalPower) * 100);
//			sipper.setPercent(percentOfTotal);
//			if (percentOfTotal < mMinPercentOfTotal)
//				continue;
//			list.add(sipper);
//		}
//
//		if (list.size() <= 1) {
//			return getAppListCpuTime();
//		}
//
//		return list;
	}
	
	private long getAppProcessTime(int pid) {
		FileInputStream in = null;
		String ret = null;
		try {
			in = new FileInputStream("/proc/" + pid + "/stat");
			byte[] buffer = new byte[1024];
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int len = 0;
			while ((len = in.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
			ret = os.toString();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (ret == null) {
			return 0;
		}
		
		String[] s = ret.split(" ");
		if (s == null || s.length < 17) {
			return 0;
		}
		
		final long utime = string2Long(s[13]);
		final long stime = string2Long(s[14]);
		final long cutime = string2Long(s[15]);
		final long cstime = string2Long(s[16]);
		
		return utime + stime + cutime + cstime;
	}
	
	private long string2Long(String s) {
		try {
			return Long.parseLong(s);
		} catch (NumberFormatException e) {
		}
		return 0;
	}

	private List<BatterySipper> getAppListCpuTime() {
		testType = 2;
		final List<BatterySipper> list = new ArrayList<BatterySipper>();
		long totalTime = 0;

		PackageManager pm=mContext.getPackageManager();
		List<AndroidAppProcess> runningAppProcessInfoList = ProcessManager.getRunningAppProcesses();
		ApplicationInfo appInfo=null;

		HashMap<String, BatterySipper> templist = new HashMap<String, BatterySipper>();
		for (AndroidAppProcess info : runningAppProcessInfoList) {
			try {
				appInfo= pm.getApplicationInfo(info.getPackageName(),0);
//				if((appInfo.flags& ApplicationInfo.FLAG_SYSTEM)!=0){
//					continue;
//					//过滤系统进程
//				}
				final long time = getAppProcessTime(info.pid);
				String pkgName = info.getPackageName();
				if (pkgName == null) {
					if (templist.containsKey(info)) {
						BatterySipper sipper = templist.get(info.name);
						sipper.setValue(sipper.getValue() + time);
					} else {
						templist.put(info.name, new BatterySipper(mContext, info.name, time));
					}
					totalTime += time;
				} else {

					if (templist.containsKey(pkgName)) {
						BatterySipper sipper = templist.get(pkgName);
						sipper.setValue(sipper.getValue() + time);
					} else {
						templist.put(pkgName, new BatterySipper(mContext, pkgName, time));
					}
					totalTime += time;
				}
			} catch (PackageManager.NameNotFoundException e) {

				final long time = getAppProcessTime(info.pid);
				String serviceName = info.name;
				if (serviceName != null) {
					if (!templist.containsKey(serviceName)) {
						templist.put(serviceName, new BatterySipper(mContext, serviceName, time));
						totalTime += time;
					}
				}

			}


		}

		
		if (totalTime == 0) totalTime = 1;
		
		list.addAll(templist.values());
		for (int i = list.size() - 1; i >= 0; i--) {
			BatterySipper sipper = list.get(i);
			double percentOfTotal = sipper.getValue() * 100 / totalTime;
			if (percentOfTotal < mMinPercentOfTotal) {
				list.remove(i);
			} else {
				sipper.setPercent(percentOfTotal);
			}
		}
		
		Collections.sort(list, new Comparator<BatterySipper>() {
			@Override
			public int compare(BatterySipper object1, BatterySipper object2) {
				double d1 = object1.getPercentOfTotal();
				double d2 = object2.getPercentOfTotal();
				if(d1-d2 < 0){
					return 1;
				}else if(d1-d2 > 0){
					return -1;
				}else{
					return 0;
				}
			}
		});
		
		return list;
	}



}
