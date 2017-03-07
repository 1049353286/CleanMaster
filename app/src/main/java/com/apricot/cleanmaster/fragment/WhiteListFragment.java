package com.apricot.cleanmaster.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.adapter.WhiteListAppAdapter;
import com.apricot.cleanmaster.base.BaseFragment;
import com.apricot.cleanmaster.base.FragmentContainerActivity;
import com.apricot.cleanmaster.bean.AppInfo;
import com.apricot.cleanmaster.dao.WhiteListDao;
import com.apricot.cleanmaster.utils.FragmentArgs;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Apricot on 2017/2/1.
 */

public class WhiteListFragment extends Fragment {
    @BindView(R.id.lv_white_list)
    ListView mListView;

    WhiteListDao mWhiteListDao;
    List<AppInfo> mAppInfoList;
    WhiteListAppAdapter mWhiteListAppAdapter;

    public static void launch(Activity from){
        FragmentArgs args=new FragmentArgs();
        args.add("title","白名单");
        FragmentContainerActivity.launch(from,WhiteListFragment.class,args);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_white_list,null);
        ButterKnife.bind(this,v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWhiteListDao=new WhiteListDao(getActivity());
        mAppInfoList=mWhiteListDao.queryAllWhiteApp();

        mWhiteListAppAdapter=new WhiteListAppAdapter(getActivity(),mAppInfoList);
        mListView.setAdapter(mWhiteListAppAdapter);
    }
}
