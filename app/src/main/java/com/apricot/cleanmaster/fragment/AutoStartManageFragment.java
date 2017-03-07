package com.apricot.cleanmaster.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.adapter.AutoStartAdapter;
import com.apricot.cleanmaster.base.BaseFragment;
import com.apricot.cleanmaster.bean.AutoStartInfo;
import com.apricot.cleanmaster.utils.BootStartUtil;
import com.apricot.cleanmaster.utils.ShellUtil;
import com.apricot.cleanmaster.utils.T;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Apricot on 2016/9/25.
 */
public class AutoStartManageFragment extends BaseFragment{
    private Context mContext;
    public static final int REFRESH_BT=1;
    public static final String ARG_POSITION="position";
    private int position;
    private int canDisableCount;
    @BindView(R.id.tv_autostart_fragment)
    TextView mTopText;
    @BindView(R.id.list_autostart_fragment)
    ListView mListView;
    @BindView(R.id.bottom_linear)
    View mBottomLinear;
    @BindView(R.id.disable_button)
    Button mButton;
    @BindView(R.id.progressBarText)
    TextView mProgressBarText;

    List<AutoStartInfo> isSystemAuto;
    List<AutoStartInfo> noSystemAuto;
    AutoStartAdapter mAutoStartAdapter;

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REFRESH_BT:
                    refreshBottom();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position=getArguments().getInt(ARG_POSITION);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_auto_start,container,false);
        mContext=getActivity();
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillData();
    }

    private void disableApp(){
        List<String> mString=new ArrayList<>();
        for(AutoStartInfo a:noSystemAuto){
            String pkgReceiverList[]=a.getPkgReceiver().split(";");
            for(int i=0;i<pkgReceiverList.length;i++){
                String cmd="pm disable"+pkgReceiverList[i];
                cmd=cmd.replace("$","\""+"$"+"\"");
                mString.add(cmd);
            }
        }

        ShellUtil.CommandResult mCommandResult= ShellUtil.execCommand(mString,true,true);
        if(mCommandResult.result!=-1){
            T.showLong(mContext,"应用已全部禁止");
            for(AutoStartInfo a:noSystemAuto){
                a.setEnable(false);
            }

        }else{
            T.showLong(mContext, "该功能需要获取系统root权限，请允许获取root权限");
        }

    }

    private void fillData(){
        if (position == 0) {
            mTopText.setText("禁止下列软件自启,可提升运行速度");
        } else {
            mTopText.setText("禁止系统核心软件自启,将会影响手机的正常使用,请谨慎操作");
        }

        List<AutoStartInfo> mAutoStartInfo= BootStartUtil.fetchAutoApps(mContext);
        noSystemAuto = new ArrayList<>();
        isSystemAuto = new ArrayList<>();

        for (AutoStartInfo a : mAutoStartInfo) {
            if (a.isSystem()) {
                isSystemAuto.add(a);
            } else {
                noSystemAuto.add(a);
            }
        }

        if(position==0){
            mAutoStartAdapter=new AutoStartAdapter(mContext,noSystemAuto,mHandler);
            mListView.setAdapter(mAutoStartAdapter);
            refreshBottom();
        }else{
            mAutoStartAdapter=new AutoStartAdapter(mContext,isSystemAuto,mHandler);
            mListView.setAdapter(mAutoStartAdapter);
        }
    }

    private void refreshBottom(){
        if(position==0){
            canDisableCount=0;
            for(AutoStartInfo a:noSystemAuto){
                if(a.isEnable){
                    canDisableCount++;
                }
            }
            if(canDisableCount>0){
                mBottomLinear.setVisibility(View.VISIBLE);
                mButton.setText("可优化"+canDisableCount+"款");
            }else{
                mBottomLinear.setVisibility(View.GONE);
            }
        }
    }
}
