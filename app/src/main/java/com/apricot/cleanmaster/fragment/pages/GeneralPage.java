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
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;


import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.utils.Utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hhhung on September 4, 2015.
 */
public class GeneralPage extends BasePage {
    private ArrayList<Map.Entry<String, BroadcastReceiver>> mBroadcastReceivers;

    private TextView mTvIp;
    private TextView mTvOperatorCode;
    private TextView mTvOperatorName;

    public GeneralPage() {
        super();

        mBroadcastReceivers = new ArrayList<Map.Entry<String, BroadcastReceiver>>();

        // create receivers
        BroadcastReceiver connectivity = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mPhoneInfo.getDataState() == TelephonyManager.DATA_CONNECTED) {
                    mTvIp.setLines(2);
                    mTvIp.setText(mPhoneInfo.getIPAddress(true) + "\n" + mPhoneInfo.getIPAddress(false));
                } else {
                    int ip = mPhoneInfo.getWifiIp();
                    mTvIp.setLines(1);
                    mTvIp.setText(ip != 0 ? Utils.toIpv4(ip) : null);
                }
            }
        };

        mBroadcastReceivers.add(new AbstractMap.SimpleEntry<String, BroadcastReceiver>("android.net.conn.CONNECTIVITY_CHANGE", connectivity));

//        BroadcastReceiver simState = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                mTvOperatorCode.setText(mPhoneInfo.getOperatorCode());
//                mTvOperatorName.setText(mPhoneInfo.getOperatorName() + " (" + mPhoneInfo.getNetworkCountryIso().toUpperCase() + ")");
//            }
//        };
//
//        mBroadcastReceivers.add(new AbstractMap.SimpleEntry<String, BroadcastReceiver>("android.intent.action.SIM_STATE_CHANGED", simState));
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
        mPanels = new ArrayList<View>();

        View panel;

        // panel label
        panel = createPanel(getString(R.string.lb_panel_device_label));
        addRow(panel, getString(R.string.lb_manufacturer), Build.MANUFACTURER, true);
        addRow(panel, getString(R.string.lb_model), Build.MODEL, true);
        addRow(panel, getString(R.string.lb_codename), Build.DEVICE, false);

        mPanels.add(panel);

        // panel identity
        panel = createPanel(getString(R.string.lb_panel_identity));
        addRow(panel, getString(R.string.lb_imei), mPhoneInfo.getImei(), true);
        addRow(panel, getString(R.string.lb_imsi), mPhoneInfo.getImsi(), true);
        addRow(panel, getString(R.string.lb_serial_number), Build.SERIAL, true);
        addRow(panel, getString(R.string.lb_phone_number), mPhoneInfo.getPhoneNumber(), true);
        mTvIp = addRow(panel, getString(R.string.lb_ip), mPhoneInfo.getIPAddress(true) + "\n" + mPhoneInfo.getIPAddress(false), true);
        mTvIp.setLines(2);
        addRow(panel, getString(R.string.lb_wifi_mac), mPhoneInfo.getWifiMac(), false);

        mPanels.add(panel);

        // panel carrier
//        panel = createPanel(getString(R.string.lb_panel_network_operator));
//        mTvOperatorCode = addRow(panel, getString(R.string.lb_network_operator_code), mPhoneInfo.getOperatorCode(), true);
//        mTvOperatorName = addRow(panel, getString(R.string.lb_network_operator_name), mPhoneInfo.getOperatorName() + " (" + mPhoneInfo.getNetworkCountryIso().toUpperCase() + ")", true);
//
//        mPanels.add(panel);
    }
}
