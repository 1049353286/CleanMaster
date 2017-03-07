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

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.utils.PhoneInfoUtil;
import com.apricot.cleanmaster.utils.Utils;


import java.util.ArrayList;

/**
 * Created by hhhung on September 4, 2015.
 */
public abstract class BasePage extends Fragment {
    protected boolean mVisible;
    protected PhoneInfoUtil mPhoneInfo;
    protected ArrayList<View> mPanels;

    protected abstract void createPanels();

    @Override
    public void onResume() {
        super.onResume();

        mVisible = true;
    }

    @Override
    public void onPause() {
        super.onPause();

        mVisible = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_phone_info, container, false);
        LinearLayout layout = (LinearLayout) rootView.findViewWithTag("container");

        if (mPanels == null) {
            mPhoneInfo = new PhoneInfoUtil(getActivity());

            createPanels();
        }

        for (int i = 0; i < mPanels.size(); i++) {
            View panel = mPanels.get(i);

            if (panel.getParent() != null) {
                ((ViewGroup) panel.getParent()).removeView(panel);
            }

            layout.addView(panel);

            if (i < mPanels.size() - 1) {
                View divider = new View(getActivity());

                divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dp2Pixel(10, getActivity())));

                layout.addView(divider);
            }
        }

        return rootView;
    }

    protected View createPanel(String title) {
        LinearLayout panel = new LinearLayout(getActivity());
        TextView heading = new TextView(getActivity());
        TableLayout table = new TableLayout(getActivity());

        // initialize panel
        panel.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setBackgroundDrawable(getResources().getDrawable(R.drawable.panel_bg));

        // heading text
        heading.setTag("heading");
        heading.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        heading.setBackgroundDrawable(getResources().getDrawable(R.drawable.panel_head_bg));
        heading.setTextAppearance(getActivity().getBaseContext(), R.style.PanelHeading);
        heading.setText(title);

        // table
        table.setTag("table");
        table.setColumnShrinkable(1, true);
        table.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        table.setBackgroundDrawable(getResources().getDrawable(R.drawable.panel_table_bg));

        panel.addView(heading);
        panel.addView(table);

        return panel;
    }

    protected TextView addRow(View view, String headerText, String contentText, boolean withDivider) {
        return addRow(view, headerText, contentText, null, withDivider);
    }

    /**
     * @param view
     * @param headerText
     * @param contentText
     * @param withDivider
     * @return the content TextView
     */
    protected TextView addRow(View view, String headerText, String contentText, String foreground, boolean withDivider) {
        TableLayout table = (TableLayout) view.findViewWithTag("table");
        TableRow tr = new TableRow(view.getContext());
        TextView th = new TextView(view.getContext());
        TextView td = new TextView(view.getContext());

        table.addView(tr);
        tr.addView(th);
        tr.addView(td);

        th.setWidth((int) view.getResources().getDimension(R.dimen.panel_row_header_size));
        th.setBackgroundDrawable(view.getResources().getDrawable(R.drawable.panel_table_row_head_bg));
        th.setTextAppearance(getActivity().getBaseContext(), R.style.PanelRowHeader);
        th.setText(headerText);

        td.setBackgroundDrawable(view.getResources().getDrawable(R.drawable.panel_table_row_content_bg));
        td.setTextAppearance(getActivity().getBaseContext(), R.style.PanelRowContent);
        if (foreground != null) {
            td.setTextColor(Color.parseColor(foreground));
        }
        td.setTextIsSelectable(true);
        td.setFocusable(false);
        td.setText(contentText);

        if (withDivider) {
            View divider = new View(view.getContext());

            divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
            divider.setBackgroundColor(Color.parseColor("#ffe5e5e5"));

            table.addView(divider);
        }

        return td;
    }

    public Button addButton(View view, String text) {
        Button button = new Button(view.getContext());

        ((ViewGroup) view).addView(button);

        button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setText(text);

        return button;
    }
}