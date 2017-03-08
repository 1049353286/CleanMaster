package com.apricot.cleanmaster.ui;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.adapter.AppLockAdapter;
import com.apricot.cleanmaster.base.BaseSwipeBackActivity;
import com.apricot.cleanmaster.bean.AppLock;
import com.apricot.cleanmaster.service.AppLockerService;

import java.util.ArrayList;
import java.util.List;

import static com.apricot.cleanmaster.ui.AppLockSettingActivity.SHARED_PREF_LOCK_PASSWORD;

/**
 * Created by Apricot on 2017/1/13.
 */

public class AppLockActivity extends BaseSwipeBackActivity {
    private final static String TAG = "AppLockActivity";
    private final static boolean DEBUG  = true;

    private List<AppLock> mAppLockList = new ArrayList<>();

    @Override
    protected int initLayout() {
        return R.layout.activity_app_lock;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("应用锁");

        SharedPreferences pref=getSharedPreferences(SHARED_PREF_LOCK_PASSWORD,MODE_PRIVATE);
        String pwd=pref.getString("pwd",null);
        if(pwd==null){
            Intent intent=new Intent(this,AppLockSettingActivity.class);
            startActivity(intent);
        }
        
        initAppList();
        AppLockAdapter adapter = new AppLockAdapter(this,mAppLockList);

        final ListView appListView = (ListView) findViewById(R.id.lv_app_lock);
        appListView.setAdapter(adapter);
        appListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppLock app = mAppLockList.get(position);
                boolean lockStatus = app.isLocked();
                app.setLocked(!lockStatus);

                ImageView appLockStatus = (ImageView) view.findViewById(R.id.app_lock_status);
                appLockStatus.setImageDrawable(app.getLockStatusIcon());

                // notify AppLockerService
                Intent intent = new Intent(AppLockActivity.this, AppLockerService.class);
                intent.putExtra(app.isLocked() ? "ADD_APP" : "REMOVE_APP", app.getPackageName());
                startService(intent);
            }
        });

        if(!isNoSwitch()){
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }


    }
    private boolean isNoOption() {
        PackageManager packageManager = getApplicationContext()
                .getPackageManager();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private boolean isNoSwitch() {
        long ts = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext()
                .getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST, 0, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initAppList() {
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if(packageInfo.packageName.equals("com.apricot.cleanmaster")){
                continue;
            }
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) ==  0) {
                AppLock app = new AppLock(getApplicationContext(), packageInfo.packageName);
                mAppLockList.add(app);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
