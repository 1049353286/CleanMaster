package com.apricot.cleanmaster.bean;

/**
 * Created by Boy Mustafa on 14/06/16.
 */
public class AppTaskModel {

    String pkgName,appName;
    int permissionsTotal;

    public AppTaskModel() {

    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getPermissionsTotal() {
        return permissionsTotal;
    }

    public void setPermissionsTotal(int permissionsTotal) {
        this.permissionsTotal = permissionsTotal;
    }
}
