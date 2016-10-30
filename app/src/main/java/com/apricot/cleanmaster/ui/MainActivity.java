package com.apricot.cleanmaster.ui;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.base.BaseActivity;
import com.apricot.cleanmaster.fragment.MainFragment;
import com.apricot.cleanmaster.fragment.NavigationDrawerFragment;
import com.apricot.cleanmaster.fragment.RelaxFragment;
import com.apricot.cleanmaster.fragment.SettingsFragment;
import com.apricot.cleanmaster.utils.T;
import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements NavigationDrawerFragment.NavigationDrawerCallBacks{
    public static final long TWO_SECOND = 2 * 1000;
    @BindView(R.id.container)
    FrameLayout container;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.navigation_drawer)
    View mFragmentContainerView;
    ActionBar ab;
    long preTime;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerArrowDrawable mDrawerArrowDrawable;

    private MainFragment mMainFragment;
    private RelaxFragment mRelaxFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initDrawer();
        onNavigationDrawerItemSelected(0);

    }

    private void initDrawer(){
        ab=getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);


        mDrawerArrowDrawable=new DrawerArrowDrawable(this){
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };
        mActionBarDrawerToggle=new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mDrawerArrowDrawable,
                R.string.drawer_open,
                R.string.drawer_close
                ){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            if(mDrawerLayout.isDrawerOpen(mFragmentContainerView)){
                mDrawerLayout.closeDrawer(mFragmentContainerView);
            }else{
                mDrawerLayout.openDrawer(mFragmentContainerView);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        hideFragment(transaction);

        switch (position){
            case 0:
                closeDrawer();
                if(mMainFragment==null){
                    mMainFragment=new MainFragment();
                    transaction.add(R.id.container,mMainFragment);
                }else{
                    transaction.show(mMainFragment);
                }
                transaction.commit();
                break;
            case 1:
                closeDrawer();
                if(mRelaxFragment==null){
                    mRelaxFragment=new RelaxFragment();
                    transaction.add(R.id.container,mRelaxFragment);
                }else{
                    transaction.show(mRelaxFragment);
                }
                transaction.commit();
                break;
            case 2:
                SettingsFragment.launch(this);
                break;

        }

    }

    private void hideFragment(FragmentTransaction transaction){
        if(mMainFragment!=null){
            transaction.hide(mMainFragment);
        }
        if(mRelaxFragment!=null){
            transaction.hide(mRelaxFragment);
        }
    }

    private void closeDrawer(){
        mDrawerLayout.closeDrawers();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 截获后退键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long currentTime = new Date().getTime();
            // 如果时间间隔大于2秒, 不处理
            if ((currentTime - preTime) > TWO_SECOND) {
                // 显示消息
                T.showShort(mContext, "再按一次退出应用程序");

                // 更新时间
                preTime = currentTime;

                // 截获事件,不再处理
                return true;
            } else {
                this.finish();
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
