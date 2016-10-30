package com.apricot.cleanmaster.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.base.BaseFragment;
import com.apricot.cleanmaster.bean.SDCardInfo;
import com.apricot.cleanmaster.ui.AutoStartManageActivity;
import com.apricot.cleanmaster.ui.CacheCleanActivity;
import com.apricot.cleanmaster.ui.MemoryCleanActivity;
import com.apricot.cleanmaster.ui.SoftwareManageActivity;
import com.apricot.cleanmaster.utils.AppUtil;
import com.apricot.cleanmaster.utils.StorageUtil;
import com.github.lzyzsd.circleprogress.ArcProgress;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Apricot on 2016/9/18.
 */
public class MainFragment extends BaseFragment{
    @BindView(R.id.arc_store)
    ArcProgress arcStore;
    @BindView(R.id.arc_process)
    ArcProgress arcProgress;
    @BindView(R.id.capacity)
    TextView capacity;

    private Context mContext;
    private Timer timer1;
    private Timer timer2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_main,container,false);
        ButterKnife.bind(this,v);
        mContext=getActivity();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        fillData();
    }

    private void fillData(){
        timer1=new Timer(true);
        timer2=new Timer(true);

        long avail= AppUtil.getAvailMemory(mContext);
        long total=AppUtil.getTotalMemory(mContext);
        final double used=((total-avail)/(double)total*100);
        arcProgress.setProgress(0);
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(arcProgress.getProgress()>=(int)used){
                            timer1.cancel();
                        }else{
                            arcProgress.setProgress(arcProgress.getProgress()+1);
                        }
                    }
                });
            }
        },50,20);

        SDCardInfo mSDCardInfo = StorageUtil.getSDCardInfo();
        SDCardInfo mSystemInfo = StorageUtil.getSystemSpaceInfo(mContext);

        long mAvailBlocks;
        long mTotalBlocks;
        if (mSDCardInfo != null) {
            mAvailBlocks = mSDCardInfo.free + mSystemInfo.free;
            mTotalBlocks = mSDCardInfo.total + mSystemInfo.total;
        } else {
            mAvailBlocks = mSystemInfo.free;
            mTotalBlocks = mSystemInfo.total;
        }

        final double percentStore=((mTotalBlocks-mAvailBlocks)/(double)mTotalBlocks*100);
        capacity.setText(StorageUtil.convertStorage(mTotalBlocks-mAvailBlocks)+"/"+StorageUtil.convertStorage(mTotalBlocks));
        arcStore.setProgress(0);
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(arcStore.getProgress()>(int)percentStore){
                            timer2.cancel();
                        }else{
                            arcStore.setProgress(arcStore.getProgress()+1);
                        }
                    }
                });
            }
        },50,20);
    }

    @OnClick(R.id.card1)
    void memoryClean(){
        startActivity(MemoryCleanActivity.class);
    }

    @OnClick(R.id.card2)
    void cacheClean(){
        startActivity(CacheCleanActivity.class);
    }

    @OnClick(R.id.card3)
    void autoStartManage(){
        startActivity(AutoStartManageActivity.class);
    }

    @OnClick(R.id.card4)
    void softwareManage(){
        startActivity(SoftwareManageActivity.class);
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer1.cancel();
        timer2.cancel();
    }
}
