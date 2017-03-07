package com.apricot.cleanmaster.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.bean.AppLock;

import java.util.List;

/**
 * Created by Apricot on 2017/1/13.
 */

public class AppLockAdapter extends BaseAdapter{
    private List<AppLock> appLockList;
    private Context mContext;

    public AppLockAdapter(Context context,List<AppLock> appLockList){
        mContext=context;
        this.appLockList=appLockList;
    }

    @Override
    public int getCount() {
        return appLockList.size();
    }

    @Override
    public Object getItem(int position) {
        return appLockList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.item_app_lock,null);
            holder=new ViewHolder();
            holder.apkName= (TextView) convertView.findViewById(R.id.app_lock_name);
            holder.apkIcon= (ImageView) convertView.findViewById(R.id.app_lock_icon);
            holder.lockStatu= (ImageView) convertView.findViewById(R.id.app_lock_status);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }

        AppLock appLock=appLockList.get(position);
        holder.apkName.setText(appLock.getAppName());
        holder.apkIcon.setImageDrawable(appLock.getIcon());
        holder.lockStatu.setImageDrawable(appLock.getLockStatusIcon());

        return convertView;
    }

    class ViewHolder{
        public ImageView apkIcon;
        public TextView apkName;
        public ImageView lockStatu;
    }




}
