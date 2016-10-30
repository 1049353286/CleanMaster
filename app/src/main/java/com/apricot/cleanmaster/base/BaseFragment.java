package com.apricot.cleanmaster.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.apricot.cleanmaster.utils.T;

/**
 * Created by Apricot on 2016/9/15.
 */
public class BaseFragment extends Fragment{
    /** 通过Class跳转界面 **/
    protected void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /** 含有Bundle通过Class跳转界面 **/
    protected void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /** 通过Action跳转界面 **/
    protected void startActivity(String action) {
        startActivity(action, null);
    }

    /** 含有Bundle通过Action跳转界面 **/
    protected void startActivity(String action, Bundle bundle) {
        Intent intent = new Intent();
        intent.setAction(action);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 吐司
     *
     * @param message
     */
    protected void showShort(String message) {
        T.showShort(getActivity(), message);
    }

    protected void showLong(String message) {
        T.showLong(getActivity(), message);
    }
}
