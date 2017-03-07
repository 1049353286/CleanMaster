package com.apricot.cleanmaster.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.base.BaseSwipeBackActivity;
import com.apricot.cleanmaster.bean.AppTaskModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppTaskActivity extends BaseSwipeBackActivity implements SwipeRefreshLayout.OnRefreshListener {


    List<AppTaskModel> list = new ArrayList<>();
    String[] requestedPermissions;
    private static final String TAG = "AppTaskActivity";
    ListAdapter adapter;
    ListView listView;
    SwipeRefreshLayout swipeLayout;

    @Override
    protected int initLayout() {
        return R.layout.activity_app_task;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("应用权限信息");
        initViews();
//        initList();


    }

    private void initList(){
        final PackageManager pm = getPackageManager();
        final List<ApplicationInfo> installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo app : installedApps){
            AppTaskModel model = new AppTaskModel();

            //permissions
            StringBuffer permissions = new StringBuffer();

            try{
                PackageInfo packageInfo = pm.getPackageInfo(app.packageName,PackageManager.GET_PERMISSIONS);
                requestedPermissions = packageInfo.requestedPermissions;

// Do not add System Packages
                if ((packageInfo.requestedPermissions == null || packageInfo.packageName.equals("android")) ||
                        (packageInfo.applicationInfo != null && (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ||
                        (packageInfo.packageName.equals("com.gamesterz.antivirus"))
                        )
                    continue;

                if(requestedPermissions!=null){

                    model.setPkgName(app.packageName);
                    model.setAppName(app.loadLabel(getPackageManager()).toString());
//                    Log.d(TAG, "application name: " + app.loadLabel(getPackageManager()).toString() + " ( " + requestedPermissions.length + " )");
                    model.setPermissionsTotal(requestedPermissions.length);
                }
            }catch (PackageManager.NameNotFoundException e){
                e.printStackTrace();
            }
            list.add(model);
            adapter = new ListAdapter(this,list);
            adapter.notifyDataSetChanged();
            // stopping swipe refresh
            swipeLayout.setRefreshing(false);

        }
    }

    private void initViews(){
        listView = (ListView) findViewById(android.R.id.list);
        adapter = new ListAdapter(this,list);
        listView.setAdapter(adapter);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeLayout.setOnRefreshListener(this);
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeLayout.setRefreshing(true);

                                        initList();
                                    }
                                }
        );



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AppTaskActivity.this,AppDetailActivity.class);
                intent.putExtra("appName",list.get(position).getAppName());
                intent.putExtra("pkgName",list.get(position).getPkgName());
                intent.putExtra("totalPermissions",list.get(position).getPermissionsTotal());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        list.clear();
        initList();
//        listView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class ListAdapter extends ArrayAdapter<AppTaskModel>{
        private List<AppTaskModel> list = new ArrayList<>();
        private Context context;

        public ListAdapter(Context context, List<AppTaskModel> list){
            super(context,R.layout.item_app_task,list);

            this.context = context;
            this.list = list;

            Collections.sort(list, new Comparator<AppTaskModel>() {
                @Override
                public int compare(AppTaskModel l1, AppTaskModel l2) {
                    return l1.getAppName().compareTo(l2.getAppName());
                }
            });
        }


        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public AppTaskModel getItem(int position) {
            return list.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            AppTaskHolder holder;

            if(v==null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.item_app_task,parent,false);
                holder = new AppTaskHolder(v);
                v.setTag(holder);
            } else {
                holder = (AppTaskHolder) v.getTag();
            }


            AppTaskModel model = list.get(position);


            try {
                holder.ivIcon.setImageDrawable(context.getPackageManager().getApplicationIcon(model.getPkgName()));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            holder.txtApp.setText(model.getAppName()+" ("+model.getPermissionsTotal()+")");

            return v;
        }

        class AppTaskHolder{
            TextView txtApp;
            ImageView ivIcon;

            public AppTaskHolder(View v){
                txtApp = (TextView) v.findViewById(R.id.appName);
                ivIcon = (ImageView) v.findViewById(R.id.appIcon);
            }
        }


    }


}
