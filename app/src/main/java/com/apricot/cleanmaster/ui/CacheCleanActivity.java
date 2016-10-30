package com.apricot.cleanmaster.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.adapter.CacheCleanAdapter;
import com.apricot.cleanmaster.base.BaseActivity;
import com.apricot.cleanmaster.bean.CacheListItem;
import com.apricot.cleanmaster.bean.StorageSize;
import com.apricot.cleanmaster.service.CleanService;
import com.apricot.cleanmaster.utils.StorageUtil;
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
 * Created by Apricot on 2016/10/12.
 */
public class CacheCleanActivity extends BaseActivity implements CleanService.OnCleanActionListener{
    @BindView(R.id.cache_clean_header)
    View mHeader;
    @BindView(R.id.cache_clean_textCounter)
    CounterView mTextCounter;
    @BindView(R.id.suffix)
    TextView mSuffix;
    @BindView(R.id.cache_clean_bottom_lin)
    View mBottom_lin;
    @BindView(R.id.cache_clean_listview)
    ListView mCacheList;
    @BindView(R.id.empty)
    TextView mEmptyView;
    @BindView(R.id.progressBar)
    View mProgressBar;
    @BindView(R.id.progressBarText)
    TextView mProgressBarText;

    private List<CacheListItem> mCacheListItems=new ArrayList<>();
    private CacheCleanAdapter mCacheCleanAdapter;
    private CleanService mCleanService;
    private long cache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clean);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);
        bindService(new Intent(this,CleanService.class),mServiceConnection,BIND_AUTO_CREATE);

        mCacheList.setEmptyView(mEmptyView);
        mCacheCleanAdapter=new CacheCleanAdapter(this,mCacheListItems);
        mCacheList.setAdapter(mCacheCleanAdapter);
        mCacheList.setOnItemClickListener(mCacheCleanAdapter);
        int footHeight=mContext.getResources().getDimensionPixelSize(R.dimen.footer_height);
        QuickReturnListViewOnScrollListener.Builder builder=new QuickReturnListViewOnScrollListener.Builder(QuickReturnViewType.FOOTER);
        builder.footer(mBottom_lin)
                .minFooterTranslation(footHeight);
        mCacheList.setOnScrollListener(builder.build());

        mTextCounter.setAutoFormat(false);
        mTextCounter.setFormatter(new DecimalFormatter());
        mTextCounter.setAutoStart(false);
        mTextCounter.setIncrement(5f); // the amount the number increments at each time interval
        mTextCounter.setTimeInterval(50); // the time interval (ms) at which the text changes
    }



    private ServiceConnection mServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCleanService=((CleanService.CleanServiceBinder)service).getService();
            mCleanService.setOnCleanActionListener(CacheCleanActivity.this);
            mCleanService.scanCache();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCleanService.setOnCleanActionListener(null);
            mCleanService=null;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScanStarted(Context context) {
        mProgressBarText.setText(R.string.scanning);
        showProgressBar(true);
    }

    @Override
    public void onScanProgressUpdated(Context context, int current, int max) {
        mProgressBarText.setText(getString(R.string.scanning_m_of_n,current,max));
    }

    @Override
    public void onScanCompleted(Context context, List<CacheListItem> apps) {
        showProgressBar(false);
        cache=0;
        mCacheListItems.clear();
        mCacheListItems.addAll(apps);
        mCacheCleanAdapter.notifyDataSetChanged();

        for(CacheListItem app:apps){
            if(app.getCacheSize()>0){
                cache+=app.getCacheSize();
            }
        }
        refreshTextCounter();

        if(apps.size()>0){
            mHeader.setVisibility(View.VISIBLE);
            mBottom_lin.setVisibility(View.VISIBLE);
        }else{
            mHeader.setVisibility(View.GONE);
            mBottom_lin.setVisibility(View.GONE);
        }

    }

    @Override
    public void onCleanStarted(Context context) {

    }

    @Override
    public void onCleanCompleted(Context context, long cacheSize) {
        Toast.makeText(context, context.getString(R.string.cleaned, Formatter.formatShortFileSize(
                mContext, cacheSize)), Toast.LENGTH_LONG).show();
        mHeader.setVisibility(View.GONE);
        mBottom_lin.setVisibility(View.GONE);
        mCacheListItems.clear();
        mCacheCleanAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.clear_button)
    public void onClickClear() {

        if (mCleanService != null&& mCleanService.getCacheSize() > 0) {
            mCleanService.cleanCache();
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
        StorageSize storageSize= StorageUtil.convertStorageSize(cache);
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
