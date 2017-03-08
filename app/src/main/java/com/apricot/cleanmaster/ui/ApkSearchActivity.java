package com.apricot.cleanmaster.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.adapter.ApkSearchAdapter;
import com.apricot.cleanmaster.base.BaseSwipeBackActivity;
import com.apricot.cleanmaster.bean.ApkFile;
import com.apricot.cleanmaster.utils.ApkSearchUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Apricot on 2016/12/11.
 */

public class ApkSearchActivity extends BaseSwipeBackActivity{
    @Override
    protected int initLayout() {
        return R.layout.activity_apk_search;
    }
    private ListView mListView;
    private ApkSearchAdapter mAdapter;
    private View mEmptyView;

    List<ApkFile> files=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("安装包管理");

        mEmptyView=findViewById(R.id.empty);
        ApkSearchUtils apkSearch=new ApkSearchUtils(this);
        apkSearch.queryApkFile();
        files.addAll(apkSearch.getApkFiles());
        Log.d("MainActivity","add files");
        initViews();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews(){
        mListView= (ListView) findViewById(R.id.lv_apk_search);
        mListView.setEmptyView(mEmptyView);
        mAdapter = new ApkSearchAdapter(this,files);
        mListView.setAdapter(mAdapter);
        Log.d("MainActivity","initViews");
    }
}
