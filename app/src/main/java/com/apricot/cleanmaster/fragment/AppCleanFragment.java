package com.apricot.cleanmaster.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.base.BaseFragment;
import com.apricot.cleanmaster.ui.CacheCleanActivity;
import com.apricot.cleanmaster.ui.UninstallCleanActivity;
import com.apricot.cleanmaster.utils.L;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Apricot on 2016/12/11.
 */

public class AppCleanFragment extends BaseFragment{
    public static final String TAG= "AppCleanFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_app_clean,container,false);
        ButterKnife.bind(this,v);
        L.d(TAG,"on create view");
        return v;
    }

    @OnClick(R.id.tv_cache_clean)
    void onClickCacheClean(){
        startActivity(CacheCleanActivity.class);
    }

    @OnClick(R.id.tv_uninstall_clean)
    void onClickUninstallClean(){
        startActivity(UninstallCleanActivity.class);
    }
}
