package com.apricot.cleanmaster.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.base.BaseSwipeBackActivity;
import com.apricot.cleanmaster.bean.BatterySipper;
import com.apricot.cleanmaster.utils.BatteryInfo;
import com.apricot.cleanmaster.utils.Utils;

import java.util.List;

/**
 * Created by Apricot on 2016/12/8.
 */

public class BatteryUsageInfoActivity extends BaseSwipeBackActivity{

    private TextView batterySummary;
    private ListView listView;
    private customAdapter adapter;
    private BatteryInfo info;
    private List<BatterySipper> mList;
    private String mBatterySummary;

    @Override
    protected int initLayout() {
        return R.layout.activity_battery;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("应用耗电信息");

        batterySummary = (TextView) findViewById(R.id.batterySummary);
        listView = (ListView) findViewById(R.id.listview);
        adapter = new customAdapter();
        listView.setAdapter(adapter);

        info = new BatteryInfo(this);
        info.setMinPercentOfTotal(0.001);

        registerReceiver(mBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        getBatteryStats();
    }

    private void getBatteryStats() {
        new Thread() {
            public void run() {
                mList = info.getBatteryStats();
                mHandler.sendEmptyMessage(1);
            }
        }.start();
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    if(isFinishing())
                        return;
                    batterySummary.setText("根据前台CPU使用时间");
                    adapter.setData(mList);
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    class customAdapter extends BaseAdapter {
        private List<BatterySipper> list;
        private LayoutInflater inflater;

        public customAdapter() {
            inflater = LayoutInflater.from(BatteryUsageInfoActivity.this);
        }


        public void setData(List<BatterySipper> list) {
            this.list = list;
            notifyDataSetInvalidated();
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public BatterySipper getItem(int position) {
            return list == null ? null : list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                holder = new Holder();
                convertView = inflater.inflate(R.layout.item_battery_usage, null);
                holder.appIcon = (ImageView) convertView.findViewById(R.id.appIcon);
                holder.appName = (TextView) convertView.findViewById(R.id.appName);
                holder.txtProgress = (TextView) convertView.findViewById(R.id.txtProgress);
                holder.progress = (ProgressBar) convertView.findViewById(R.id.progress);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            BatterySipper sipper = getItem(position);
            holder.appName.setText(sipper.getName());
            holder.appIcon.setImageDrawable(sipper.getIcon());

            double percentOfTotal = sipper.getPercentOfTotal();
            holder.txtProgress.setText(format(percentOfTotal));
            holder.progress.setProgress((int) percentOfTotal);

            return convertView;
        }
    }

    class Holder {
        ImageView appIcon;
        TextView appName;
        TextView txtProgress;
        ProgressBar progress;
    }

    private String format(double size) {
        return String.format("%1$.2f%%", size);
        // return new BigDecimal("" + size).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBatteryInfoReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                String batteryLevel = Utils.getBatteryPercentage(intent);
                String batteryStatus = Utils.getBatteryStatus(BatteryUsageInfoActivity.this.getResources(), intent);
                mBatterySummary = context.getResources().getString(R.string.power_usage_level_and_status, batteryLevel, batteryStatus);
            }
        }
    };
}
