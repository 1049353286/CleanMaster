/*
 *     Copyright (c) 2015 GuDong
 *
 *     Permission is hereby granted, free of charge, to any person obtaining a copy
 *     of this software and associated documentation files (the "Software"), to deal
 *     in the Software without restriction, including without limitation the rights
 *     to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *     copies of the Software, and to permit persons to whom the Software is
 *     furnished to do so, subject to the following conditions:
 *
 *     The above copyright notice and this permission notice shall be included in all
 *     copies or substantial portions of the Software.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *     SOFTWARE.
 */

package com.apricot.cleanmaster.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.base.BaseFragment;
import com.apricot.cleanmaster.ui.AppTaskActivity;
import com.apricot.cleanmaster.ui.BatteryUsageInfoActivity;
import com.apricot.cleanmaster.ui.PhoneInfoActivity;
import com.apricot.cleanmaster.utils.L;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class AppInfoFragment extends BaseFragment{

    public static final String TAG= "AppInfoFragment";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_app_info,container,false);
        ButterKnife.bind(this,v);
        L.d(TAG,"on create view");
        return v;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("AppInfoFragment"); //统计页面，"MainScreen"为页面名称，可自定义
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AppInfoFragment");
    }

    @OnClick(R.id.tv_battery)
    void onClickBatteryInfo(){
        L.d(TAG,"click battery info");
        startActivity(BatteryUsageInfoActivity.class);
    }

    @OnClick(R.id.tv_permission)
    void onClickPermissionInfo(){
        L.d(TAG,"click permission info");
        startActivity(AppTaskActivity.class);
    }

    @OnClick(R.id.tv_phone_info)
    void onClickPhoneInfo(){
        startActivity(PhoneInfoActivity.class);
    }




}
