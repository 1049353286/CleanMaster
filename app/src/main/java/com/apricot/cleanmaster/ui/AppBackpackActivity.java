package com.apricot.cleanmaster.ui;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.widget.ListView;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.adapter.AppBackpackAdapter;
import com.apricot.cleanmaster.base.BaseSwipeBackActivity;
import com.apricot.cleanmaster.bean.ApkFile;
import com.apricot.cleanmaster.utils.ActionUtil;
import com.apricot.cleanmaster.utils.L;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Apricot on 2016/12/14.
 */

public class AppBackpackActivity extends BaseSwipeBackActivity implements AppBackpackAdapter.IClickPopupMenuItem{
    private static final String TAG="AppBackpackActivity";

    @BindView(R.id.lv_app_backpack)
    ListView mListView;
    AsyncTask<Void,Integer,List<ApkFile>> mTask;

    @Override
    protected int initLayout() {
        return R.layout.activity_app_backpack;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("应用备份");

        mTask=new AsyncTask<Void, Integer, List<ApkFile>>(){

            @Override
            protected List<ApkFile> doInBackground(Void... params) {
                PackageManager pm=getPackageManager();
                List<PackageInfo> packageInfos=pm.getInstalledPackages(PackageManager.GET_META_DATA);
                List<ApkFile> apkFileList=new ArrayList<>();

                for(PackageInfo packageInfo:packageInfos) {
                    final ApkFile apkFile=new ApkFile();
                    apkFile.setApkIcon(packageInfo.applicationInfo.loadIcon(pm));
                    apkFile.setApkName(packageInfo.applicationInfo.loadLabel(pm).toString());
                    apkFile.setVersionName(packageInfo.versionName);
                    apkFile.setFilePath(packageInfo.applicationInfo.sourceDir);
                    int flags=packageInfo.applicationInfo.flags;
                    if((flags& ApplicationInfo.FLAG_SYSTEM)==0){
                        //只显示添加用户应用
                        apkFileList.add(apkFile);
                    }
                }

                return apkFileList;
            }

            @Override
            protected void onPostExecute(List<ApkFile> apkFiles) {
                super.onPostExecute(apkFiles);

                for(ApkFile apkFile:apkFiles){
                    L.d(TAG,apkFile.getFilePath());
                }

                AppBackpackAdapter mAppBackpackAdapter=new AppBackpackAdapter(AppBackpackActivity.this,apkFiles);
                mAppBackpackAdapter.setClickPopupMenuItem(AppBackpackActivity.this);
                mListView.setAdapter(mAppBackpackAdapter);
            }
        }.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickMenuItem(int itemId, ApkFile apkFile) {
        switch (itemId) {
            case R.id.pop_share:
                ActionUtil.shareApk(AppBackpackActivity.this, apkFile);
                break;
            case R.id.pop_export:
                ActionUtil.exportApk(AppBackpackActivity.this, apkFile);
                break;
        }
    }
}
