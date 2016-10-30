package com.apricot.cleanmaster.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.bean.CacheListItem;

import java.util.List;


/**
 * Created by Apricot on 2016/10/17.
 */
public class CacheCleanAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{
    private List<CacheListItem> cacheListItems;
    private Context mContext;

    public CacheCleanAdapter(Context context,List<CacheListItem> cacheListItems){
        mContext=context;
        this.cacheListItems=cacheListItems;
    }

    @Override
    public int getCount() {
        return cacheListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return cacheListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.cache_list_item,null);
            holder=new ViewHolder();
            holder.appIcon= (ImageView) convertView.findViewById(R.id.app_icon);
            holder.appName= (TextView) convertView.findViewById(R.id.app_name);
            holder.size= (TextView) convertView.findViewById(R.id.app_size);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        final CacheListItem cacheListItem=cacheListItems.get(position);
        holder.appIcon.setImageDrawable(cacheListItem.getApplicationIcon());
        holder.appName.setText(cacheListItem.getApplicationName());
        holder.size.setText(Formatter.formatShortFileSize(mContext,cacheListItem.getCacheSize()));
        holder.packageName=cacheListItem.getPackageName();
        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        if (viewHolder != null && viewHolder.packageName != null) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + viewHolder.packageName));

            mContext.startActivity(intent);
        }
    }


    class ViewHolder{
        ImageView appIcon;
        TextView appName;
        TextView size;

        String packageName;
    }
}
