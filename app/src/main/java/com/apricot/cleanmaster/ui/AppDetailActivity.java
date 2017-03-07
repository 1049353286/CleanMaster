package com.apricot.cleanmaster.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.base.BaseSwipeBackActivity;

public class AppDetailActivity extends BaseSwipeBackActivity {

    String appName, pkgName;
    int totalPermsssions;
    TextView txtAppname,txtPkgName,txtTotalPermissions;
    ImageView iconApp;
    String[] requestedPermissions,concatPermissions;
    StringBuffer permissions;
    private static final String TAG = "AppDetailActivity";
    ListView permissionsList;

    @Override
    protected int initLayout() {
        return R.layout.activity_app_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent!=null){
            appName = intent.getExtras().getString("appName");
            pkgName = intent.getExtras().getString("pkgName");
            totalPermsssions = intent.getIntExtra("totalPermissions",0);
        }

        initViews();

    }



    private void initViews(){
        txtAppname = (TextView) findViewById(R.id.txtAppName);
        txtPkgName = (TextView) findViewById(R.id.txtPkgName);
        txtTotalPermissions = (TextView) findViewById(R.id.txtPermissions);
        iconApp = (ImageView) findViewById(R.id.ivIcon);


        txtAppname.setText(appName);
        txtPkgName.setText(pkgName);
        txtTotalPermissions.setText(String.valueOf(totalPermsssions));
        try {
            iconApp.setImageDrawable(getPackageManager().getApplicationIcon(pkgName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }



        //permissions
        final PackageManager pm = getPackageManager();
         permissions = new StringBuffer();

        try{
            PackageInfo packageInfo = pm.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS);
            requestedPermissions = packageInfo.requestedPermissions;
            if(requestedPermissions!=null){
                concatPermissions = new String[requestedPermissions.length];
                for (int i = 0; i < requestedPermissions.length ; i++){
                    permissions.append(requestedPermissions[i] + "\n");
                    concatPermissions[i] = requestedPermissions[i].substring(requestedPermissions[i].indexOf("permission.") +11,requestedPermissions[i].length());
                    Log.d(TAG," permssions: "+ requestedPermissions[i].substring(requestedPermissions[i].indexOf("permission.") +11,requestedPermissions[i].length()));
                }

                Log.d(TAG,"Permissions: "+permissions);
            }
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }

        permissionsList = (ListView) findViewById(R.id.permissionsList);

        if(requestedPermissions!=null){
            for (int i=0;i<requestedPermissions.length;i++){

            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, concatPermissions);

        permissionsList.setAdapter(adapter);


    }
}
