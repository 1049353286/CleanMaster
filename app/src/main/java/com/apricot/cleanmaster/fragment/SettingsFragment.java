package com.apricot.cleanmaster.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.base.FragmentContainerActivity;
import com.apricot.cleanmaster.ui.AppLockSettingActivity;
import com.apricot.cleanmaster.utils.AppUtil;
import com.apricot.cleanmaster.utils.FragmentArgs;
import com.apricot.cleanmaster.utils.T;
import com.apricot.cleanmaster.utils.Utils;

/**
 * Created by Apricot on 2016/10/14.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{

    private Preference createShortCut;
    private Preference whiteList;
    private Preference applock;
    private Preference pVersion;
    private Preference pVersionDetail;
    private Preference pGrade;
    private Preference pShare;
    private Preference pAbout;

    public static void launch(Activity from){
        FragmentArgs args=new FragmentArgs();
        args.add("title","设置");
        FragmentContainerActivity.launch(from,SettingsFragment.class,args);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addPreferencesFromResource(R.xml.ui_settings);

        createShortCut = findPreference("createShortCut");
        createShortCut.setOnPreferenceClickListener(this);
        whiteList=findPreference("whiteList");
        whiteList.setOnPreferenceClickListener(this);
        applock=findPreference("applock");
        applock.setOnPreferenceClickListener(this);
        pVersion = findPreference("pVersion");
        pVersion.setOnPreferenceClickListener(this);
        pVersionDetail = findPreference("pVersionDetail");
        pVersionDetail.setSummary("当前版本：" + AppUtil.getVersion(getActivity()));
        pVersionDetail.setOnPreferenceClickListener(this);

        pGrade = findPreference("pGrade");
        pGrade.setOnPreferenceClickListener(this);
        pShare = findPreference("pShare");
        pShare.setOnPreferenceClickListener(this);
        pAbout = findPreference("pAbout");
        pAbout.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if ("createShortCut".equals(preference.getKey())) {
            createShortCut();
        }else if("whiteList".equals(preference.getKey())) {
            WhiteListFragment.launch(getActivity());
        }else if("applock".equals(preference.getKey())) {
            Intent intent=new Intent(getActivity(), AppLockSettingActivity.class);
            startActivity(intent);
        } else if ("pVersion".equals(preference.getKey())) {

        } else if ("pVersionDetail".equals(preference.getKey())) {
            VersionFragment.launch(getActivity());
        }else if ("pGrade".equals(preference.getKey())) {
            startMarket();
        }else if ("pShare".equals(preference.getKey())) {
            shareMyApp();
        }
        else if ("pAbout".equals(preference.getKey())) {
//            getActivity().startActivity(new Intent(getActivity(), AboutActivity.class));
        }
        return false;
    }

    private void createShortCut(){
        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "一键加速");
        intent.putExtra("duplicate", false);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.short_cut_icon));
        Intent i = new Intent();
        i.setAction("com.apricot.shortcut");
        i.addCategory("android.intent.category.DEFAULT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);
        getActivity().sendBroadcast(intent);
        T.showLong(getActivity(), "“一键加速”快捷图标已创建");
    }

    private void shareMyApp(){

    }

    public  void startMarket() {
        Uri uri = Uri.parse(String.format("market://details?id=%s", AppUtil.getPackageInfo(getActivity()).packageName));
        if (Utils.isIntentSafe(getActivity(), uri)) {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().startActivity(intent);
        }
        // 没有安装市场
        else {
            T.showLong(getActivity(),"无法打开应用市场");

        }
    }


}
