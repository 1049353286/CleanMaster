package com.apricot.cleanmaster.adapter;

import android.content.Context;

import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.bean.ApkFile;
import com.apricot.cleanmaster.bean.AppInfo;
import com.apricot.cleanmaster.utils.StorageUtil;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Apricot on 2016/9/20.
 */
public class SoftwareAdapter extends BaseAdapter implements View.OnClickListener{
    List<AppInfo> mAppInfoList;
    private Context mContext;
    private IClickPopupMenuItem mClickPopupMenuItem;

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
            convertView= LayoutInflater.from(mContext).inflate(R.layout.item_software_list,null);
            holder=new ViewHolder();
            holder.appIcon = (ImageView) convertView
                    .findViewById(R.id.app_icon);
            holder.appName = (TextView) convertView
                    .findViewById(R.id.app_name);
            holder.appSize = (TextView) convertView
                    .findViewById(R.id.app_size);
            holder.ivOverFlow= (ImageView) convertView.findViewById(R.id.iv_over_flow);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        final AppInfo item= mAppInfoList.get(position);
        if(item!=null){
            holder.appIcon.setImageDrawable(item.getAppIcon());
            holder.appName.setText(item.getAppName());
            holder.appSize.setText(StorageUtil.convertStorage(item.getPkgSize()));

            holder.ivOverFlow.setOnClickListener(this);
            holder.ivOverFlow.setTag(item);
//            holder.btnUninstall.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent=new Intent();
//                    intent.addCategory("android.intent.category.DEFAULT");
//                    intent.setAction("android.intent.action.VIEW");
//                    intent.setAction("android.intent.action.DELETE");
//                    intent.setData(Uri.parse("package:"+item.getPkgName()));
//                    mContext.startActivity(intent);
//                }
//            });
        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        AppInfo appInfo= (AppInfo) v.getTag();
        switch (v.getId()){
            case R.id.iv_over_flow:
                showPopMenu(appInfo,v);
                break;

        }
    }

    /**
     * 显示弹出式菜单
     * @param entity
     * @param ancho
     */
    private void showPopMenu(final AppInfo entity,View ancho) {
        PopupMenu popupMenu = new PopupMenu(mContext,ancho);
        popupMenu.getMenuInflater().inflate(R.menu.item_software_pop_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(mClickPopupMenuItem!=null){
                    mClickPopupMenuItem.onClickMenuItem(item.getItemId(),entity);
                }
                return false;
            }
        });

        makePopForceShowIcon(popupMenu);
        popupMenu.show();
    }

    //使用反射让popupMenu 显示菜单icon
    private void makePopForceShowIcon(PopupMenu popupMenu) {
        try {
            Field mFieldPopup=popupMenu.getClass().getDeclaredField("mPopup");
            mFieldPopup.setAccessible(true);
            MenuPopupHelper mPopup = (MenuPopupHelper) mFieldPopup.get(popupMenu);
            mPopup.setForceShowIcon(true);
        } catch (Exception e) {

        }
    }

    public interface IClickPopupMenuItem{
        void onClickMenuItem(int itemId,AppInfo appInfo);
    }

    public void setClickPopupMenuItem(IClickPopupMenuItem mClickPopupMenuItem) {
        this.mClickPopupMenuItem = mClickPopupMenuItem;
    }

    class ViewHolder{
        ImageView appIcon;
        TextView appName;
        TextView appSize;
        ImageView ivOverFlow;
    }
}
