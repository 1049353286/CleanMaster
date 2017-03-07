package com.apricot.cleanmaster.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.bean.AppInfo;
import com.apricot.cleanmaster.dao.WhiteListDao;
import com.apricot.cleanmaster.utils.T;

import java.util.List;

/**
 * Created by Apricot on 2017/2/1.
 */

public class WhiteListAppAdapter extends BaseAdapter{
    private List<AppInfo> mAppInfoList;
    private WhiteListDao whiteListDao;
    private Context mContext;

    public WhiteListAppAdapter(Context context,List<AppInfo> apps){
        mContext=context;
        mAppInfoList=apps;
        whiteListDao=new WhiteListDao(context);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.item_white_list,null);
            holder=new ViewHolder();
            holder.appIcon = (ImageView) convertView
                    .findViewById(R.id.app_icon);
            holder.appName = (TextView) convertView
                    .findViewById(R.id.app_name);
            holder.appVersion = (TextView) convertView
                    .findViewById(R.id.app_version);
            holder.appRemove = (Button) convertView
                    .findViewById(R.id.remove);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }

        final AppInfo item=mAppInfoList.get(position);

        holder.appIcon.setImageDrawable(item.getAppIcon());
        holder.appName.setText(item.getAppName());
        holder.appVersion.setText(item.getVersion());
        holder.appRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whiteListDao.deleteWhiteApp(item);
                mAppInfoList.remove(position);
                notifyDataSetChanged();
                T.show(mContext,"应用移除成功", Toast.LENGTH_SHORT);
            }
        });

        return convertView;
    }


    private class ViewHolder{
        ImageView appIcon;
        TextView appName;
        TextView appVersion;
        Button appRemove;
    }
}
