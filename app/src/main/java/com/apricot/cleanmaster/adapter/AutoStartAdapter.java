package com.apricot.cleanmaster.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.bean.AutoStartInfo;
import com.apricot.cleanmaster.fragment.AutoStartManageFragment;
import com.apricot.cleanmaster.utils.ShellUtil;
import com.apricot.cleanmaster.utils.T;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Apricot on 2016/9/28.
 */
public class AutoStartAdapter extends BaseAdapter{
    List<AutoStartInfo> autoStartInfoList;
    Context mContext;
    Handler mHandler;

    public AutoStartAdapter(Context context,List<AutoStartInfo> autoStartInfoList,Handler handler){
        mContext=context;
        this.autoStartInfoList=autoStartInfoList;
        mHandler=handler;
    }

    @Override
    public int getCount() {
        return autoStartInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return autoStartInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.autostart_list_item,null);
            holder=new ViewHolder();
            holder.appIcon = (ImageView) convertView
                    .findViewById(R.id.auto_app_icon);
            holder.appName = (TextView) convertView
                    .findViewById(R.id.auto_app_name);
            holder.appSize = (TextView) convertView
                    .findViewById(R.id.auto_app_size);
            holder.disableSwitch= (TextView) convertView.findViewById(R.id.disable_switch);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }

        final AutoStartInfo item=autoStartInfoList.get(position);
        if(item!=null){
            holder.appIcon.setImageDrawable(item.getIcon());
            holder.appName.setText(item.getLabel());
            if (item.isEnable()) {
                holder.disableSwitch.setBackgroundResource(R.drawable.switch_on);
                holder.disableSwitch.setText("已开启");
            } else {
                holder.disableSwitch.setBackgroundResource(R.drawable.switch_off);
                holder.disableSwitch.setText("已禁止");
            }
            holder.disableSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ShellUtil.checkRootPermission()) {
                        if (item.isEnable()) {
                            disableApp(item);
                        } else {
                            enableApp(item);
                        }
                    } else {
                        T.showLong(mContext, "该功能需要获取系统root权限，点击允许获取root权限");
                    }
                }
            });
        }
        return convertView;
    }

    private void disableApp(AutoStartInfo item) {
        String packageReceiverList[] = item.getPkgReceiver().toString().split(";");

        List<String> mSring = new ArrayList<>();
        for (int j = 0; j < packageReceiverList.length; j++) {
            String cmd = "pm disable " + packageReceiverList[j];
            //部分receiver包含$符号，需要做进一步处理，用"$"替换掉$
            cmd = cmd.replace("$", "\"" + "$" + "\"");
            //执行命令
            mSring.add(cmd);

        }
        ShellUtil.CommandResult mCommandResult = ShellUtil.execCommand(mSring, true, true);

        if (mCommandResult.result == 0) {
            T.showLong(mContext, item.getLabel() + "已禁止");
            item.setEnable(false);
            notifyDataSetChanged();
            if (mHandler != null) {
                mHandler.sendEmptyMessage(AutoStartManageFragment.REFRESH_BT);
            }
        } else {
            T.showLong(mContext, item.getLabel() + "禁止失败");
        }

        // T.showLong(mContext, mCommandResult.result + "" + mCommandResult.errorMsg + mCommandResult.successMsg);
    }

    private void enableApp(AutoStartInfo item) {
        String packageReceiverList[] = item.getPkgReceiver().toString().split(";");

        List<String> mSring = new ArrayList<>();
        for (int j = 0; j < packageReceiverList.length; j++) {
            String cmd = "pm enable " + packageReceiverList[j];
            //部分receiver包含$符号，需要做进一步处理，用"$"替换掉$
            cmd = cmd.replace("$", "\"" + "$" + "\"");
            //执行命令
            mSring.add(cmd);

        }
        ShellUtil.CommandResult mCommandResult = ShellUtil.execCommand(mSring, true, true);

        if (mCommandResult.result == 0) {
            T.showLong(mContext, item.getLabel() + "已开启");
            item.setEnable(true);
            notifyDataSetChanged();
            if (mHandler != null) {
                mHandler.sendEmptyMessage(AutoStartManageFragment.REFRESH_BT);
            }
        } else {
            T.showLong(mContext, item.getLabel() + "开启失败");
        }
        //   T.showLong(mContext, mCommandResult.result + "" + mCommandResult.errorMsg + mCommandResult.successMsg);
    }

    class ViewHolder{
        ImageView appIcon;
        TextView appName;
        TextView appSize;
        TextView disableSwitch;
    }
}
