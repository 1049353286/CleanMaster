package com.apricot.cleanmaster.adapter;

import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.bean.ApkFile;
import com.apricot.cleanmaster.utils.T;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Apricot on 2016/11/11.
 */
public class ApkSearchAdapter extends BaseAdapter{

    private List<ApkFile> files=new ArrayList<>();
    private Context mContext;

    public ApkSearchAdapter(Context context, List<ApkFile> files){
        mContext=context;
        this.files=files;
    }


    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.item_apk_search,null);
            holder=new ViewHolder();
            holder.apkIcon = (ImageView) convertView
                    .findViewById(R.id.apk_icon);
            holder.apkName = (TextView) convertView
                    .findViewById(R.id.apk_name);
            holder.apkPath= (TextView) convertView.findViewById(R.id.apk_path);
            holder.apkDelete= (Button) convertView.findViewById(R.id.apk_delete);

            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        final ApkFile apkFile=files.get(position);
        holder.apkName.setText(apkFile.getApkName());
        holder.apkIcon.setImageDrawable(apkFile.getApkIcon());
        holder.apkPath.setText(apkFile.getFilePath());
        holder.apkDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle("是否删除安装包")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File apk =new File(apkFile.getFilePath());
                                if(apk.exists()){
                                    apk.delete();
                                    files.remove(position);
                                    notifyDataSetChanged();
                                    T.show(mContext,"安装包删除成功", Toast.LENGTH_SHORT);
                                }
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();

            }
        });

        return convertView;

    }

    public static class ViewHolder{
        public ImageView apkIcon;
        public TextView apkName;
        public TextView apkPath;
        public Button apkDelete;

    }
}
