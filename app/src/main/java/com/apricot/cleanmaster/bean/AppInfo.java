package com.apricot.cleanmaster.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Apricot on 2016/9/20.
 */
public class AppInfo {
    private Drawable appIcon;
    private String appName;
    private String pkgName;
    private String version;
    private long pkgSize;
    private int uid;
    private boolean userApp;
    private boolean isInRom;

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getPkgSize() {
        return pkgSize;
    }

    public void setPkgSize(long pkgSize) {
        this.pkgSize = pkgSize;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public boolean isInRom() {
        return isInRom;
    }

    public void setInRom(boolean inRom) {
        isInRom = inRom;
    }
}
