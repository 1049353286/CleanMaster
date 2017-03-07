package com.apricot.cleanmaster.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Apricot on 2016/11/11.
 */
public class ApkFile {
    private Drawable apkIcon;
    private String apkName;
    private String packageName;
    private String filePath;
    private String versionName;
    private long fileSize;
    public long ModifiedDate;
    private int versionCode;
    private int installed;

    public Drawable getApkIcon() {
        return apkIcon;
    }

    public void setApkIcon(Drawable apk_icon) {
        this.apkIcon = apk_icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getInstalled() {
        return installed;
    }

    public void setInstalled(int installed) {
        this.installed = installed;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getModifiedDate() {
        return ModifiedDate;
    }

    public void setModifiedDate(long modifiedDate) {
        ModifiedDate = modifiedDate;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }
}
