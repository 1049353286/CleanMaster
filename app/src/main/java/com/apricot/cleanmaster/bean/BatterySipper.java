package com.apricot.cleanmaster.bean;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.utils.BatteryInfo;
import com.apricot.cleanmaster.utils.BatteryInfo.DrainType;


public class BatterySipper implements Comparable<BatterySipper> {

	private final Context mContext;
	private String name;
	private Drawable icon;
	private double value;
	private double percent;



	public BatterySipper(Context context, String pkgName, double time) {
		mContext = context;
		value = time;
		getQuickNameIcon(pkgName);
	}



	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public double getPercentOfTotal() {
		return percent;
	}

	@Override
	public int compareTo(BatterySipper other) {
		return (int) (other.getValue() - getValue());
	}

	private void getQuickNameIcon(String pkgName) {
		PackageManager pm = mContext.getPackageManager();
		try {
			ApplicationInfo appInfo = pm.getApplicationInfo(pkgName, 0);
			icon = appInfo.loadIcon(pm);// pm.getApplicationIcon(appInfo);
			name = appInfo.loadLabel(pm).toString();// pm.getApplicationLabel(appInfo).toString();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			icon=mContext.getResources().getDrawable(R.mipmap.ic_launcher);
			name=pkgName;
		}
	}

}