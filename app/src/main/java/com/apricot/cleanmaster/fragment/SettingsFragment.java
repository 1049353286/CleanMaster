package com.apricot.cleanmaster.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.base.FragmentContainerActivity;
import com.apricot.cleanmaster.utils.AppUtil;
import com.apricot.cleanmaster.utils.T;
import com.apricot.cleanmaster.utils.Utils;

/**
 * Created by Apricot on 2016/10/14.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{

    private Preference createShortCut;
    private Preference pVersion;
    private Preference pVersionDetail;
    private Preference pGrade;
    private Preference pShare;
    private Preference pAbout;

    public static void launch(Activity from){
        FragmentContainerActivity.launch(from,SettingsFragment.class,null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addPreferencesFromResource(R.xml.ui_settings);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getActionBar().setDisplayShowHomeEnabled(false);
        getActivity().getActionBar().setTitle(R.string.title_settings);

        createShortCut = findPreference("createShortCut");
        createShortCut.setOnPreferenceClickListener(this);
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
