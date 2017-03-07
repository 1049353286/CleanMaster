package com.apricot.cleanmaster.adapter;

/**
 * Created by Apricot on 2016/11/20.
 */

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
import java.util.List;

/**
 * Created by Apricot on 2016/9/20.
 */
public class UninstallCleanAdapter extends BaseAdapter {
    List<ApkFile> mUninstallInfos;
    private Context mContext;

    public UninstallCleanAdapter(Context context, List<ApkFile> infos){
        mContext=context;
        mUninstallInfos=infos;
    }

    @Override
    public int getCount() {
        return mUninstallInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mUninstallInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.item_uninstall,null);
            holder=new ViewHolder();
            holder.appIcon = (ImageView) convertView
                    .findViewById(R.id.app_uninstall_icon);
            holder.appName = (TextView) convertView
                    .findViewById(R.id.app_uninstall_name);
            holder.path= (TextView) convertView.findViewById(R.id.tv_uninstall_path);
            holder.delete= (Button) convertView.findViewById(R.id.bt_uninstall);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        final ApkFile item= (ApkFile) getItem(position);
        if(item!=null){
            holder.appIcon.setImageDrawable(item.getApkIcon());
            holder.appName.setText(item.getApkName());
            holder.path.setText(item.getFilePath());
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("是否删除文件夹")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    File file =new File(item.getFilePath().replace("/",""));
                                    if(file.exists()){
                                        delete(file);
                                        mUninstallInfos.remove(position);
                                        notifyDataSetChanged();
                                        T.show(mContext,"文件夹删除成功", Toast.LENGTH_SHORT);
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
        }
        return convertView;
    }

    private void delete(File file){
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for(File f : files){
                delete(f);
            }
        }else{
            file.delete();
        }
    }

    public class ViewHolder{
        ImageView appIcon;
        TextView appName;
        TextView path;
        Button delete;

    }
}
