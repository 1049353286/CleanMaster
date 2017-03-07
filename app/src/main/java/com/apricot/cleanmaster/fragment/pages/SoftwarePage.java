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

import android.os.Build;
import android.view.View;


import com.apricot.cleanmaster.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by hhhung on September 4, 2015.
 */
public class SoftwarePage extends BasePage {
    @Override
    protected void createPanels() {
        mPanels = new ArrayList<View>();

        View panel;

        // panel android
        panel = createPanel(getString(R.string.lb_panel_android));
        addRow(panel, getString(R.string.lb_android_version), Build.VERSION.RELEASE, true);
        addRow(panel, getString(R.string.lb_android_build), Build.DISPLAY + " (" + Build.VERSION.INCREMENTAL + ")", true);
//        addRow(panel, getString(R.string.lb_android_build_fingerprint), Build.FINGERPRINT, true);
        addRow(panel, getString(R.string.lb_android_build_time), new SimpleDateFormat("yyyy-MM-dd").format(new Date(Build.TIME)), true);
        addRow(panel, getString(R.string.lb_android_sdk_int), "" + Build.VERSION.SDK_INT, false);

        mPanels.add(panel);

        // panel more
        panel = createPanel(getString(R.string.lb_panel_more));
        addRow(panel, getString(R.string.lb_kernel_version), mPhoneInfo.getKernelVersion(), true);
        addRow(panel, getString(R.string.lb_baseband), Build.getRadioVersion(), true);
//        addRow(panel, getString(R.string.lb_board), Build.BOARD, true);
        addRow(panel, getString(R.string.lb_bootloader), Build.BOOTLOADER, false);

        mPanels.add(panel);
    }
}
