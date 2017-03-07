package com.apricot.cleanmaster.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.bean.AppProcessInfo;
import com.apricot.cleanmaster.utils.StorageUtil;

import java.util.List;

/**
 * Created by Apricot on 2016/10/12.
 */
public class MemoryCleanAdapter extends BaseAdapter{
    private List<AppProcessInfo> processInfoList;
    private Context mContext;

    public MemoryCleanAdapter(Context context,List<AppProcessInfo> processInfos){
        mContext=context;
        processInfoList=processInfos;
    }

    @Override
    public int getCount() {
        return processInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return processInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.memory_list_item,null);
            holder=new ViewHolder();
            holder.appIcon = (ImageView) convertView
                    .findViewById(R.id.mem_clean_image);
            holder.appName = (TextView) convertView
                    .findViewById(R.id.mem_clean_name);
            holder.memory = (TextView) convertView
                    .findViewById(R.id.memory);
            holder.cb = (RadioButton) convertView
                    .findViewById(R.id.choice_radio);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        final AppProcessInfo processInfo=processInfoList.get(position);
        holder.appIcon.setImageDrawable(processInfo.icon);
        holder.appName.setText(processInfo.appName);
        holder.memory.setText(StorageUtil.convertStorage(processInfo.memory));
        if (processInfo.checked) {
            holder.cb.setChecked(true);
        } else {
            holder.cb.setChecked(false);
        }
        holder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (processInfo.checked) {
                    processInfo.checked = false;
                } else {
                    processInfo.checked = true;
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }


    class ViewHolder{
        ImageView appIcon;
        TextView appName;
        TextView memory;
        RadioButton cb;
    }
}
