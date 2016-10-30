package com.apricot.cleanmaster.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;


import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.base.BaseSwipeBackActivity;
import com.apricot.cleanmaster.fragment.SoftwareManageFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * Created by Apricot on 2016/9/20.
 */
public class SoftwareManageActivity extends BaseSwipeBackActivity{
    @BindView(R.id.tab_software_manage)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager_software_manage)
    ViewPager mViewPager;
    private MyPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_software_manage);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews(){
        mViewPagerAdapter=new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(2);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter{

        private final String[] TITLES={"用户软件","预装软件"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            SoftwareManageFragment softwareManageFragment=new SoftwareManageFragment();
            Bundle bundle=new Bundle();
            bundle.putInt("position",position);
            softwareManageFragment.setArguments(bundle);

            return softwareManageFragment;
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }
}
