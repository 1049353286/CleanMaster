package com.apricot.cleanmaster.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.adapter.MemoryCleanAdapter;
import com.apricot.cleanmaster.base.BaseSwipeBackActivity;
import com.apricot.cleanmaster.bean.AppInfo;
import com.apricot.cleanmaster.bean.AppProcessInfo;
import com.apricot.cleanmaster.bean.StorageSize;
import com.apricot.cleanmaster.dao.WhiteListDao;
import com.apricot.cleanmaster.service.CoreService;
import com.apricot.cleanmaster.utils.StorageUtil;
import com.apricot.cleanmaster.utils.T;
import com.etiennelawlor.quickreturn.library.enums.QuickReturnViewType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;
import com.github.premnirmal.textcounter.CounterView;
import com.github.premnirmal.textcounter.DecimalFormatter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Apricot on 2016/10/8.
 */
public class MemoryCleanActivity extends BaseSwipeBackActivity implements CoreService.OnProcessActionListener{
    @BindView(R.id.memory_clean_header)
    View mHeader;
    @BindView(R.id.memory_clean_listview)
    ListView mList;
    @BindView(R.id.memory_clean_bottom_lin)
    View mBottomLinear;
    @BindView(R.id.memory_clean_textCounter)
    CounterView mTextCounter;
    @BindView(R.id.sufix)
    TextView mSuffix;
    @BindView(R.id.progressBar)
    View mProgressBar;
    @BindView(R.id.progressBarText)
    TextView mProgressBarText;
    private MemoryCleanAdapter mMemoryCleanAdapter;
    private List<AppProcessInfo> mProcessInfoList=new ArrayList<>();
    private long allMemory;
    private WhiteListDao mWhiteListDao;
    private CoreService mCoreService;

    @Override
    protected int initLayout() {
        return R.layout.activity_memory_clean;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("内存清理");

        bindService(new Intent(this,CoreService.class),mServiceConnection,BIND_AUTO_CREATE);
        mWhiteListDao=new WhiteListDao(this);
        mMemoryCleanAdapter=new MemoryCleanAdapter(mContext,mProcessInfoList);
        mList.setAdapter(mMemoryCleanAdapter);
        int footHeight=mContext.getResources().getDimensionPixelSize(R.dimen.footer_height);
        QuickReturnListViewOnScrollListener.Builder builder=new QuickReturnListViewOnScrollListener.Builder(QuickReturnViewType.FOOTER);
        builder.footer(mBottomLinear)
                .minFooterTranslation(footHeight);
        mList.setOnScrollListener(builder.build());

        mTextCounter.setAutoFormat(false);
        mTextCounter.setFormatter(new DecimalFormatter());
        mTextCounter.setAutoStart(false);
        mTextCounter.setIncrement(5f); // the amount the number increments at each time interval
        mTextCounter.setTimeInterval(50); // the time interval (ms) at which the text changes


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ServiceConnection mServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCoreService= ((CoreService.ProcessServiceBinder) service).getService();
            mCoreService.setOnActionListener(MemoryCleanActivity.this);
            mCoreService.ScanRunningProcess();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCoreService.setOnActionListener(null);
            mCoreService=null;
        }
    };

    @Override
    public void onScanStarted(Context context) {
        mProgressBarText.setText(R.string.scanning);
        showProgressBar(true);
    }

    @Override
    public void onScanProgressUpdated(Context context, int current, int max) {
        mProgressBarText.setText(getString(R.string.scanning_m_of_n, current, max));
    }

    @Override
    public void onScanCompleted(Context context, List<AppProcessInfo> apps) {
        mProcessInfoList.clear();
        allMemory=0;

        List<AppInfo> whitelist=mWhiteListDao.queryAllWhiteApp();

        for(AppProcessInfo processInfo:apps){
            if(processInfo.processName.equals("com.apricot.cleanmaster")){
                continue;
            }
            if(!processInfo.isSystem){
                if(whitelist.size()>0){
                    for(int i=0;i<whitelist.size();i++){
                        if(!processInfo.appName.equals(whitelist.get(i).getAppName())&&i==(whitelist.size()-1)){
                            mProcessInfoList.add(processInfo);
                            allMemory+=processInfo.memory;
                        }
                    }
                }else{
                    mProcessInfoList.add(processInfo);
                    allMemory+=processInfo.memory;
                }
            }
        }

        mMemoryCleanAdapter.notifyDataSetChanged();
        showProgressBar(false);
        refreshTextCounter();

        if(mProcessInfoList.size()>0){
            mHeader.setVisibility(View.VISIBLE);
            mBottomLinear.setVisibility(View.VISIBLE);
        }else{
            mHeader.setVisibility(View.GONE);
            mBottomLinear.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCleanStarted(Context context) {

    }

    @Override
    public void onCleanCompleted(Context context, long cacheSize) {

    }

    @OnClick(R.id.clear_button)
    public void onClickClear(){
        long killAppMem=0;
        for (int i=0;i<mProcessInfoList.size();i++){
            if(mProcessInfoList.get(i).checked){
                killAppMem+=mProcessInfoList.get(i).memory;
                mCoreService.killBackgroundProcesses(mProcessInfoList.get(i).processName);
                mProcessInfoList.remove(mProcessInfoList.get(i));
                mMemoryCleanAdapter.notifyDataSetChanged();
            }
        }
        allMemory=allMemory-killAppMem;
        T.showLong(mContext,"共清理"+StorageUtil.convertStorage(killAppMem)+"内存");
        if(allMemory>0){
            refreshTextCounter();
        }
    }


    private void showProgressBar(boolean show){
        if(show){
            mProgressBar.setVisibility(View.VISIBLE);
        }else{
            mProgressBar.startAnimation(AnimationUtils.loadAnimation(mContext,android.R.anim.fade_out));
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void refreshTextCounter(){
        StorageSize storageSize= StorageUtil.convertStorageSize(allMemory);
        mTextCounter.setStartValue(0);
        mTextCounter.setEndValue(storageSize.value);
        mSuffix.setText(storageSize.suffix);
        mTextCounter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }
}
