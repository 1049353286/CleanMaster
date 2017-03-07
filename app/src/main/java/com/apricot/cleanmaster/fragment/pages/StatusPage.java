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
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.utils.Utils;

import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hhhung on September 4, 2015.
 */
public class StatusPage extends BasePage {
    private ArrayList<Map.Entry<String, BroadcastReceiver>> mBroadcastReceivers;

    // cpu
    private TextView mTvRootCpuLoad;
    private Map<Integer, TextView> mTvCpuLoads;
    private TextView mTvProcesses;

    // memory
    private TextView mTvRam;
    private TextView mTvInternalStorage;

    // battery
    private TextView mTvBatteryLevel;
    private TextView mTvBatteryChargeStatus;
    private TextView mTvBatteryPowerSource;
    private TextView mTvBatteryTemperature;
    private TextView mTvBatteryVoltage;
    private TextView mTvBatteryHealth;

    // wifi
    private TextView mTvWifiStatus;
    private TextView mTvWifiNetworkName;
    private TextView mTvWifiIp;
    private TextView mTvWifiLinkSpeed;

    // mobile
    private TextView mTvSimStatus;
    private TextView mTvOperatorCode;
    private TextView mTvOperatorName;
    private TextView mTvPhoneType;
    private TextView mTvDataStatus;
    private TextView mTvDataType;
    private TextView mTvDataRoaming;
    private TextView mTvDataIp;

    private Thread mUpdater;


    public StatusPage() {
        mTvCpuLoads = new HashMap<Integer, TextView>();
        mBroadcastReceivers = new ArrayList<Map.Entry<String, BroadcastReceiver>>();

        BroadcastReceiver battery = new BroadcastReceiver() {
            private DecimalFormat mDfTemperature = new DecimalFormat("0.0");

            @Override
            public void onReceive(Context context, Intent intent) {
                mTvBatteryLevel.setText(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) + " %");

                switch (intent.getExtras().getInt(BatteryManager.EXTRA_STATUS)) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        mTvBatteryChargeStatus.setText("Charging");
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        mTvBatteryChargeStatus.setText("Discharging");
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        mTvBatteryChargeStatus.setText("Full");
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        mTvBatteryChargeStatus.setText("Not charging");
                        break;
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        mTvBatteryChargeStatus.setText("Unknown");
                        break;
                    default:
                        mTvBatteryChargeStatus.setText("");
                        break;
                }

                switch (intent.getExtras().getInt(BatteryManager.EXTRA_PLUGGED)) {
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        mTvBatteryPowerSource.setText("AC");
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        mTvBatteryPowerSource.setText("USB");
                        break;
                    default:
                        mTvBatteryPowerSource.setText("Battery");
                        break;
                }

                mTvBatteryTemperature.setText(mDfTemperature.format((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10) + " \u00B0 C");
                mTvBatteryVoltage.setText((float) intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / 1000 + " V");

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

//        BroadcastReceiver simState = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                switch (mPhoneInfo.getSimState()) {
//                    case TelephonyManager.SIM_STATE_ABSENT:
//                        mTvSimStatus.setText("No SIM");
//                        break;
//                    case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
//                        mTvSimStatus.setText("Network Locked");
//                        break;
//                    case TelephonyManager.SIM_STATE_PIN_REQUIRED:
//                        mTvSimStatus.setText("PIN Required");
//                        break;
//                    case TelephonyManager.SIM_STATE_PUK_REQUIRED:
//                        mTvSimStatus.setText("PUK Required");
//                        break;
//                    case TelephonyManager.SIM_STATE_READY:
//                        mTvSimStatus.setText("Ready");
//                        break;
//                    default:
//                        mTvSimStatus.setText("");
//                        break;
//                }
//
//                mTvOperatorCode.setText(mPhoneInfo.getOperatorCode());
//                mTvOperatorName.setText(mPhoneInfo.getOperatorName() + " (" + mPhoneInfo.getNetworkCountryIso().toUpperCase() + ")");
//                mTvPhoneType.setText(mPhoneInfo.getPhoneTypeString());
//            }
//        };
//
//        mBroadcastReceivers.add(new AbstractMap.SimpleEntry<String, BroadcastReceiver>("android.intent.action.SIM_STATE_CHANGED", simState));

//        BroadcastReceiver connectivity = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                mTvDataStatus.setText(mPhoneInfo.getDataStateString());
//
//                if (mPhoneInfo.getDataState() == TelephonyManager.DATA_CONNECTED) {
//                    mTvDataType.setText(mPhoneInfo.getNetworkTypeString());
//                    mTvDataRoaming.setText(mPhoneInfo.isNetworkRoaming() ? "Roaming" : "Not Roaming");
//                    mTvDataIp.setText(mPhoneInfo.getIPAddress(true) + "\n" + mPhoneInfo.getIPAddress(false));
//                } else {
//                    mTvDataType.setText(null);
//                    mTvDataRoaming.setText(null);
//                    mTvDataIp.setText(null);
//                }
//            }
//        };
//
//        mBroadcastReceivers.add(new AbstractMap.SimpleEntry<String, BroadcastReceiver>("android.net.conn.CONNECTIVITY_CHANGE", connectivity));
    }

    @Override
    public void onResume() {
        super.onResume();

        final Activity activity = getActivity();
        for (Map.Entry<String, BroadcastReceiver> en : mBroadcastReceivers) {
            IntentFilter iff = new IntentFilter();

            iff.addAction(en.getKey());

            activity.registerReceiver(en.getValue(), iff);
        }

        mUpdater = new Thread() {
            @Override
            public void run() {
                final ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                final WifiManager wifiMan = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }

                while (mVisible && activity != null) {
                    final Map<Integer, Float> cpuStats = mPhoneInfo.getCpuLoad();

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // cpu
                                mTvRootCpuLoad.setText((int) (cpuStats.get(0) * 100) + " %");

                                for (Map.Entry<Integer, TextView> en : mTvCpuLoads.entrySet()) {
                                    Float stat = cpuStats.get(en.getKey() + 1);

                                    if (stat == null) {
                                        //en.getValue().setTextColor(Color.parseColor("#999999"));
                                        en.getValue().setText("Idle");
                                    } else {
                                        //en.getValue().setTextColor(Color.parseColor("#000000"));
                                        en.getValue().setText((int) (stat * 100) + " %");
                                    }
                                }

                                mTvProcesses.setText("" + mPhoneInfo.getRunningProcesses());

                                // memory
                                mTvRam.setText((int) ((1 - (float) mPhoneInfo.getRamFree() / mPhoneInfo.getRamTotal()) * 100) + " % - Available: " + Utils.formatByte(mPhoneInfo.getRamFree(), 0));
                                mTvInternalStorage.setText((int) ((1 - (float) mPhoneInfo.getInternalStorageFree() / mPhoneInfo.getInternalStorageTotal()) * 100) + " % - Available: " + Utils.formatByte(mPhoneInfo.getInternalStorageFree(), 1));

                                // wifi
                                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                                WifiInfo wifiInf = wifiMan.getConnectionInfo();

                                if (mWifi.isConnected()) {
                                    mTvWifiStatus.setText("Connected");
                                    mTvWifiNetworkName.setText(wifiInf.getSSID());
                                    mTvWifiLinkSpeed.setText(wifiInf.getLinkSpeed() + " Mbps");
                                    mTvWifiIp.setText(Utils.toIpv4(wifiInf.getIpAddress()));
                                } else {
                                    mTvWifiStatus.setText("Disconnected");
                                    mTvWifiNetworkName.setText("");
                                    mTvWifiLinkSpeed.setText("");
                                    mTvWifiIp.setText("");
                                }

                                // mobile
                                if (mPhoneInfo.getDataState() == TelephonyManager.DATA_CONNECTED) {
                                    mTvDataType.setText(mPhoneInfo.getNetworkTypeString());
                                    mTvDataRoaming.setText(mPhoneInfo.isNetworkRoaming() ? "Roaming" : "Not Roaming");
                                }
                            } catch (Exception e) {
                            }
                        }
                    });

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };

        mUpdater.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        Activity activity = getActivity();
        for (Map.Entry<String, BroadcastReceiver> en : mBroadcastReceivers) {
            activity.unregisterReceiver(en.getValue());
        }

        mUpdater.interrupt();
    }

    @Override
    protected void createPanels() {
        mPanels = new ArrayList<View>();

        View panel;
        Button button;

        // panel cpu
        panel = createPanel(getString(R.string.lb_panel_cpu_load));
        mTvRootCpuLoad = addRow(panel, getString(R.string.lb_cpu_load_total), "", true);

        for (int i = 0; i < mPhoneInfo.getCpuCores(); i++) {
            mTvCpuLoads.put(i, addRow(panel, "CPU " + i, "", true));
        }

        mTvProcesses = addRow(panel, getString(R.string.lb_cpu_procs), "", false);

//        button = addButton(panel, getString(R.string.btn_apps_manager));
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    getActivity().startActivity(new Intent(Settings.ACTION_APN_SETTINGS));
//                } catch (ActivityNotFoundException e) {
//                    Toast.makeText(getActivity(), "Cannot open Application Settings", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        mPanels.add(panel);

        panel = createPanel(getString(R.string.lb_panel_memory_usage));
        mTvRam = addRow(panel, getString(R.string.lb_ram), "", true);
        mTvInternalStorage = addRow(panel, getString(R.string.lb_internal_storage), "", false);

        mPanels.add(panel);

        panel = createPanel(getString(R.string.lb_panel_battery_status));
        mTvBatteryLevel = addRow(panel, getString(R.string.lb_battery_level), "", true);
        mTvBatteryChargeStatus = addRow(panel, getString(R.string.lb_battery_charge_status), "", true);
        mTvBatteryPowerSource = addRow(panel, getString(R.string.lb_battery_power_source), "", true);
        mTvBatteryTemperature = addRow(panel, getString(R.string.lb_battery_temperature), "", true);
        mTvBatteryVoltage = addRow(panel, getString(R.string.lb_battery_voltage), "", true);
        mTvBatteryHealth = addRow(panel, getString(R.string.lb_battery_health), "", false);

//        button = addButton(panel, getString(R.string.btn_battery_usage));
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    getActivity().startActivity(new Intent(Intent.ACTION_POWER_USAGE_SUMMARY));
//                } catch (ActivityNotFoundException e) {
//                    Toast.makeText(getActivity(), "Cannot open Battery Usage", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        mPanels.add(panel);

        panel = createPanel(getString(R.string.lb_panel_wifi));
        mTvWifiStatus = addRow(panel, getString(R.string.lb_wifi_status), "", true);
        mTvWifiNetworkName = addRow(panel, getString(R.string.lb_wifi_network), "", true);
        mTvWifiIp = addRow(panel, getString(R.string.lb_wifi_ip), "", true);
        mTvWifiLinkSpeed = addRow(panel, getString(R.string.lb_wifi_link_speed), "", false);

        mPanels.add(panel);

//        panel = createPanel(getString(R.string.lb_panel_network_operator));
//        mTvSimStatus = addRow(panel, getString(R.string.lb_sim_status), "", true);
//        mTvOperatorCode = addRow(panel, getString(R.string.lb_network_operator_code), mPhoneInfo.getOperatorCode(), true);
//        mTvOperatorName = addRow(panel, getString(R.string.lb_network_operator_name), mPhoneInfo.getOperatorName() + " (" + mPhoneInfo.getNetworkCountryIso().toUpperCase() + ")", true);
//        mTvPhoneType = addRow(panel, getString(R.string.lb_mobile_data_status), mPhoneInfo.getPhoneTypeString(), true);
//        mTvDataStatus = addRow(panel, getString(R.string.lb_mobile_data_status), mPhoneInfo.getDataStateString(), true);
//        mTvDataType = addRow(panel, getString(R.string.lb_mobile_data_type), mPhoneInfo.getNetworkTypeString(), true);
//        mTvDataRoaming = addRow(panel, getString(R.string.lb_mobile_data_roaming), mPhoneInfo.isNetworkRoaming() ? "Roaming" : "Not Roaming", true);
//        mTvDataIp = addRow(panel, getString(R.string.lb_ip), mPhoneInfo.getIPAddress(true) + "\n" + mPhoneInfo.getIPAddress(false), false);
//        mTvDataIp.setLines(2);
//
//        mPanels.add(panel);
    }
}
