package com.apricot.cleanmaster.bean;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by Apricot on 2016/9/26.
 */
public class AutoStartInfo {

    private String label;
    private Drawable icon;
    private String pkgName;
    private String pkgReceiver;
    public boolean isSystem;
    public boolean isEnable;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getPkgReceiver() {
        return pkgReceiver;
    }

    public void setPkgReceiver(String pkgReceiver) {
        this.pkgReceiver = pkgReceiver;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

}
