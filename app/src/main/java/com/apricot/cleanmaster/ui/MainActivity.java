package com.apricot.cleanmaster.ui;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.base.BaseActivity;
import com.apricot.cleanmaster.fragment.AppCleanFragment;
import com.apricot.cleanmaster.fragment.AppInfoFragment;
import com.apricot.cleanmaster.fragment.MainFragment;
import com.apricot.cleanmaster.fragment.RelaxFragment;

import com.apricot.cleanmaster.fragment.SettingsFragment;
import com.apricot.cleanmaster.service.AppLockerService;
import com.apricot.cleanmaster.utils.SharedPreferencesUtils;
import com.apricot.cleanmaster.utils.T;
import com.umeng.analytics.MobclickAgent;


import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends BaseActivity{
    public static final long TWO_SECOND = 2 * 1000;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.navigation_drawer)
    NavigationView mNavigationView;
    long preTime;
    private MainFragment mMainFragment;
    private RelaxFragment mRelaxFragment;
    private AppInfoFragment mAppInfoFragment;
    private AppCleanFragment mAppCleanFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupDrawerContent(mNavigationView);
        updatePosition(mNavigationView.getMenu().findItem(R.id.nav_main));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        Intent lockIntent=new Intent(this, AppLockerService.class);
        startService(lockIntent);

        if (!SharedPreferencesUtils.isShortCut(mContext)) {
            createShortCut();
            SharedPreferencesUtils.setIsShortCut(this,true);
        }

    }

    private void createShortCut() {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "一键加速");
        intent.putExtra("duplicate", false);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.short_cut_icon));
        Intent i = new Intent();
        i.setAction("com.apricot.shortcut");
        i.addCategory("android.intent.category.DEFAULT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);
        sendBroadcast(intent);
        SharedPreferencesUtils.setIsShortCut(mContext, true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            if(mDrawerLayout.isDrawerOpen(mNavigationView)){
                mDrawerLayout.closeDrawer(mNavigationView);
            }else{
                mDrawerLayout.openDrawer(mNavigationView);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        updatePosition(menuItem);
                        return true;
                    }
                });
    }

    private void updatePosition(final MenuItem menuItem) {
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        hideFragment(transaction);
        Fragment fragment = null;
        switch (menuItem.getItemId()) {
            case R.id.nav_main:
                if(mMainFragment==null){
                    mMainFragment=new MainFragment();
                    transaction.add(R.id.container,mMainFragment);
                }
                fragment=mMainFragment;
                break;
//            case R.id.nav_relax:
//                if(mRelaxFragment==null){
//                    mRelaxFragment=new RelaxFragment();
//                    transaction.add(R.id.container,mRelaxFragment);
//                }
//                fragment = mRelaxFragment;
//                break;
            case R.id.nav_appinfo:
                if(mAppInfoFragment==null){
                    mAppInfoFragment=new AppInfoFragment();
                    transaction.add(R.id.container,mAppInfoFragment);
                }
                fragment = mAppInfoFragment;
                break;
            case R.id.nav_app_clean:
                if(mAppCleanFragment==null){
                    mAppCleanFragment=new AppCleanFragment();
                    transaction.add(R.id.container,mAppCleanFragment);
                }
                fragment = mAppCleanFragment;
                break;

            case R.id.nav_app_backpack:
                startActivity(AppBackpackActivity.class);
                break;

            case R.id.nav_file_manage:
                startActivity(FileManageActivity.class);
                break;

            case R.id.nav_app_lock:
                startActivity(AppLockActivity.class);
                break;

            case R.id.nav_settings:
                SettingsFragment.launch(this);
                break;
        }

        if (fragment != null) {
            menuItem.setChecked(true);
            closeDrawer();
            transaction.show(fragment);
            transaction.commit();
        }
    }

//    @Override
//    public void onNavigationDrawerItemSelected(int position) {
//        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
//        hideFragment(transaction);
//
//        switch (position){
//            case 0:
//                closeDrawer();
//                if(mMainFragment==null){
//                    mMainFragment=new MainFragment();
//                    transaction.add(R.id.container,mMainFragment);
//                }else{
//                    transaction.show(mMainFragment);
//                }
//                transaction.commit();
//                break;
//            case 1:
//                closeDrawer();
//                if(mRelaxFragment==null){
//                    mRelaxFragment=new RelaxFragment();
//                    transaction.add(R.id.container,mRelaxFragment);
//                }else{
//                    transaction.show(mRelaxFragment);
//                }
//                transaction.commit();
//                break;
//            case 2:
//                SettingsFragment.launch(this);
//                break;
//
//        }
//
//    }

    private void hideFragment(FragmentTransaction transaction){
        if(mMainFragment!=null){
            transaction.hide(mMainFragment);
        }
        if(mRelaxFragment!=null){
            transaction.hide(mRelaxFragment);
        }
        if(mAppInfoFragment!=null){
            transaction.hide(mAppInfoFragment);
        }
        if(mAppCleanFragment!=null){
            transaction.hide(mAppCleanFragment);
        }
    }

    private void closeDrawer(){
        mDrawerLayout.closeDrawers();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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
