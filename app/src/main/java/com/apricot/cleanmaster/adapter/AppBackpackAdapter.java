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

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Apricot on 2016/12/14.
 */

public class AppBackpackAdapter extends BaseAdapter implements View.OnClickListener{
    List<ApkFile> appInfoList;
    private Context mContext;
    private IClickPopupMenuItem mClickPopupMenuItem;

    public AppBackpackAdapter(Context context,List<ApkFile> appInfoList){
        mContext=context;
        this.appInfoList=appInfoList;
    }

    @Override
    public int getCount() {
        return appInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return appInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoler holder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.item_app_backpack,null);
            holder=new ViewHoler();
            holder.ivIcon= (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tvName= (TextView) convertView.findViewById(R.id.tv_backpack_name);
            holder.ivOverFlow= (ImageView) convertView.findViewById(R.id.iv_over_flow);
            convertView.setTag(holder);
        }else{
            holder= (ViewHoler) convertView.getTag();
        }
        ApkFile apkFile=appInfoList.get(position);
        holder.ivIcon.setImageDrawable(apkFile.getApkIcon());
        holder.tvName.setText(apkFile.getApkName());

        holder.ivOverFlow.setOnClickListener(this);
        holder.ivOverFlow.setTag(apkFile);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        ApkFile apkFile= (ApkFile) v.getTag();

        switch (v.getId()){
            case R.id.iv_over_flow:
                showPopMenu(apkFile, v);
                break;
        }
    }

    /**
     * 显示弹出式菜单
     * @param entity
     * @param ancho
     */
    private void showPopMenu(final ApkFile entity,View ancho) {
        PopupMenu popupMenu = new PopupMenu(mContext,ancho);
        popupMenu.getMenuInflater().inflate(R.menu.item_pop_menu,popupMenu.getMenu());
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
        void onClickMenuItem(int itemId,ApkFile apkFile);
    }

    public void setClickPopupMenuItem(IClickPopupMenuItem mClickPopupMenuItem) {
        this.mClickPopupMenuItem = mClickPopupMenuItem;
    }



    class ViewHoler{
        public ImageView ivOverFlow;
        private TextView tvName;
        public ImageView ivIcon;
    }
}
