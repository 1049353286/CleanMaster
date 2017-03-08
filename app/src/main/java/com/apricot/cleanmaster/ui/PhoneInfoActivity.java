package com.apricot.cleanmaster.ui;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import android.view.MenuItem;
import android.widget.Toast;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.base.BaseSwipeBackActivity;
import com.apricot.cleanmaster.fragment.pages.BasePage;
import com.apricot.cleanmaster.fragment.pages.GeneralPage;
import com.apricot.cleanmaster.fragment.pages.HardwarePage;
import com.apricot.cleanmaster.fragment.pages.SoftwarePage;
import com.apricot.cleanmaster.fragment.pages.StatusPage;
import com.apricot.cleanmaster.utils.Utils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import butterknife.BindView;

/**
 * Created by Apricot on 2016/12/16.
 */

public class PhoneInfoActivity extends BaseSwipeBackActivity{
    @Override
    protected int initLayout() {
        return R.layout.activity_phone_info;
    }

    @BindView(R.id.tab_phone_info)
    TabLayout mTabLayout;

    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("手机信息");

        // check permission first
        boolean readPhoneStatePermissionGranted = Utils.checkPermissionGranted(getBaseContext(), "android.permission.READ_PHONE_STATE");
//        boolean cameraPermissionGranted = Utils.checkPermissionGranted(getBaseContext(), "android.permission.CAMERA");
        if (!readPhoneStatePermissionGranted) {
            Toast.makeText(getApplicationContext(), "Please grant all permission required (Phone)", Toast.LENGTH_LONG).show();
            finish();

            return;
        }



        // init tabs
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);


    }



    public class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public int getPageId(int position) {
            // TODO for debug
            //position = 1;
            switch (position) {
                case 0:
                    return R.string.tab_id_general;
                case 1:
                    return R.string.tab_id_status;
                case 2:
                    return R.string.tab_id_software;
                case 3:
                    return R.string.tab_id_hardware;
            }

            return R.string.tab_id_general;
        }

        @Override
        public Fragment getItem(int position) {
            int tabId = getPageId(position);

            BasePage page = null;

            switch (tabId) {
                case R.string.tab_id_general:
                    page = (BasePage) Fragment.instantiate(getApplicationContext(), GeneralPage.class.getName());
                    break;
                case R.string.tab_id_status:
                    page = (BasePage) Fragment.instantiate(getApplicationContext(), StatusPage.class.getName());
                    break;
                case R.string.tab_id_software:
                    page = (BasePage) Fragment.instantiate(getApplicationContext(), SoftwarePage.class.getName());
                    break;
                case R.string.tab_id_hardware:
                    page = (BasePage) Fragment.instantiate(getApplicationContext(), HardwarePage.class.getName());
                    break;
            }

            return page;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            int tabId = getPageId(position);

            switch (tabId) {
                case R.string.tab_id_general:
                    return getString(R.string.tab_title_general);
                case R.string.tab_id_status:
                    return getString(R.string.tab_title_status);
                case R.string.tab_id_software:
                    return getString(R.string.tab_title_software);
                case R.string.tab_id_hardware:
                    return getString(R.string.tab_title_hardware);
            }

            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
