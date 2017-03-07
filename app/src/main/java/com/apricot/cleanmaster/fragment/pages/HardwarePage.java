/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.apricot.cleanmaster.fragment.pages;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.view.View;
import android.widget.TextView;


import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.ui.MainActivity;
import com.apricot.cleanmaster.ui.PhoneInfoActivity;
import com.apricot.cleanmaster.utils.Utils;

import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hhhung on September 4, 2015.
 */
public class HardwarePage extends BasePage {
    private ArrayList<Map.Entry<String, BroadcastReceiver>> mBroadcastReceivers;

    private TextView mTvGpuRenderer;
    private TextView mTvGpuVendor;

    private TextView mTvBatteryPresent;
    private TextView mTvBatteryTechnology;
    private TextView mTvBatteryMaxLevel;
    private TextView mTvBatteryHealth;

    public HardwarePage() {
        super();

        mBroadcastReceivers = new ArrayList<Map.Entry<String, BroadcastReceiver>>();

        // create receivers
        BroadcastReceiver battery = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT)) {
                    mTvBatteryPresent.setTextColor(Color.parseColor("#00ff00"));
                    mTvBatteryPresent.setText("YES");
                } else {
                    mTvBatteryPresent.setTextColor(Color.parseColor("#ff0000"));
                    mTvBatteryPresent.setText("NO");
                }
                mTvBatteryTechnology.setText(intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY));
                mTvBatteryMaxLevel.setText("" + intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1));

                switch (intent.getExtras().getInt(BatteryManager.EXTRA_HEALTH)) {
                    case BatteryManager.BATTERY_HEALTH_COLD:
                        mTvBatteryHealth.setTextColor(Color.parseColor("#ff0000"));
                        mTvBatteryHealth.setText("Cold");
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        mTvBatteryHealth.setTextColor(Color.parseColor("#ff0000"));
                        mTvBatteryHealth.setText("Dead");
                        break;
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        mTvBatteryHealth.setTextColor(Color.parseColor("#00ff00"));
                        mTvBatteryHealth.setText("Good");
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        mTvBatteryHealth.setTextColor(Color.parseColor("#ff0000"));
                        mTvBatteryHealth.setText("Over Voltage");
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        mTvBatteryHealth.setTextColor(Color.parseColor("#ff0000"));
                        mTvBatteryHealth.setText("Over Heat");
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                        mTvBatteryHealth.setText("Unknown");
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                        mTvBatteryHealth.setTextColor(Color.parseColor("#ff0000"));
                        mTvBatteryHealth.setText("Failure");
                        break;
                    default:
                        mTvBatteryHealth.setText("");
                        break;
                }
            }
        };

        mBroadcastReceivers.add(new AbstractMap.SimpleEntry<String, BroadcastReceiver>(Intent.ACTION_BATTERY_CHANGED, battery));
    }

    @Override
    public void onResume() {
        super.onResume();

        Activity activity = getActivity();
        for (Map.Entry<String, BroadcastReceiver> en : mBroadcastReceivers) {
            IntentFilter iff = new IntentFilter();

            iff.addAction(en.getKey());

            activity.registerReceiver(en.getValue(), iff);
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        Activity activity = getActivity();
        for (Map.Entry<String, BroadcastReceiver> en : mBroadcastReceivers) {
            activity.unregisterReceiver(en.getValue());
        }
    }

    @Override
    protected void createPanels() {
        mPanels = new ArrayList<>();

        View panel;

        // panel cpu
        panel = createPanel(getString(R.string.lb_panel_cpu));
        addRow(panel, getString(R.string.lb_cpu_processor), mPhoneInfo.getCpuProcessor(), true);
        addRow(panel, getString(R.string.lb_cpu_implementer), mPhoneInfo.getCpuImplementer(), true);
        addRow(panel, getString(R.string.lb_cpu_part), mPhoneInfo.getCpuPart(), true);
        addRow(panel, getString(R.string.lb_cpu_cores), "" + mPhoneInfo.getCpuCores(), true);
        addRow(panel, getString(R.string.lb_cpu_frequency), mPhoneInfo.getCpuMinFrequency() / 1000 + " MHz" + " - " + mPhoneInfo.getCpuMaxFrequency() / 1000 + " MHz", false);

        mPanels.add(panel);

        // panel display
        panel = createPanel(getString(R.string.lb_panel_display));
        addRow(panel, getString(R.string.lb_dimension), mPhoneInfo.getDimensions(), true);
        addRow(panel, getString(R.string.lb_resolution), mPhoneInfo.getResolution(), true);
        addRow(panel, getString(R.string.lb_density), mPhoneInfo.getDensity(), true);
//        mTvGpuRenderer = addRow(panel, getString(R.string.lb_gpu_renderer), "", true);
//        mTvGpuVendor = addRow(panel, getString(R.string.lb_gpu_vendor), "", true);
//        addRow(panel, getString(R.string.lb_gl_version), mPhoneInfo.getGlVersion(), false);

        mPanels.add(panel);

        // panel battery
        panel = createPanel(getString(R.string.lb_panel_battery));
        mTvBatteryPresent = addRow(panel, getString(R.string.lb_battery_present), "", true);
        mTvBatteryTechnology = addRow(panel, getString(R.string.lb_battery_technology), "", true);
        mTvBatteryMaxLevel = addRow(panel, getString(R.string.lb_battery_max_level), "", true);
        mTvBatteryHealth = addRow(panel, getString(R.string.lb_battery_health), "", false);

        mPanels.add(panel);

        // panel memory
//        panel = createPanel(getString(R.string.lb_panel_memory));
//        addRow(panel, getString(R.string.lb_ram), Utils.formatByte(mPhoneInfo.getRamTotal(), 2), true);
//        addRow(panel, getString(R.string.lb_internal_storage), Utils.formatByte(mPhoneInfo.getInternalStorageTotal(), 2), false);
//
//        mPanels.add(panel);

        // panel camera
    }
}
