package com.apricot.cleanmaster.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.bean.AppInfo;
import com.apricot.cleanmaster.utils.StorageUtil;

import java.util.List;

/**
 * Created by Apricot on 2016/9/20.
 */
public class SoftwareAdapter extends BaseAdapter{
    List<AppInfo> mAppInfoList;
    private Context mContext;

    public SoftwareAdapter(Context context,List<AppInfo> apps){
        mContext=context;
        mAppInfoList=apps;
    }

    @Override
    public int getCount() {
        return mAppInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAppInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.software_list_item,null);
            holder=new ViewHolder();
            holder.appIcon = (ImageView) convertView
                    .findViewById(R.id.app_icon);
            holder.appName = (TextView) convertView
                    .findViewById(R.id.app_name);
            holder.appSize = (TextView) convertView
                    .findViewById(R.id.app_size);
            holder.btnUninstall= (Button) convertView
                    .findViewById(R.id.btn_uninstall);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        final AppInfo item= (AppInfo) getItem(position);
        if(item!=null){
            holder.appIcon.setImageDrawable(item.getAppIcon());
            holder.appName.setText(item.getAppName());
            holder.appSize.setText(StorageUtil.convertStorage(item.getPkgSize()));
            holder.btnUninstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent();
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setAction("android.intent.action.VIEW");
                    intent.setAction("android.intent.action.DELETE");
                    intent.setData(Uri.parse("package:"+item.getPkgName()));
                    mContext.startActivity(intent);
                }
            });
        }
        return convertView;
    }

    public class ViewHolder{
        ImageView appIcon;
        TextView appName;
        TextView appSize;
        Button btnUninstall;
    }
}
