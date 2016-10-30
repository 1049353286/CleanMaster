package com.apricot.cleanmaster.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.base.BaseFragment;

/**
 * Created by Apricot on 2016/9/16.
 */
public class NavigationDrawerFragment extends BaseFragment{
    private NavigationDrawerCallBacks mCallBacks;
    final int radioIds[]={R.id.radio0,R.id.radio1,R.id.radio2};
    RadioButton radios[]=new RadioButton[radioIds.length];

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of
        // actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_navigation_drawer,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initButton();
    }

    private void initButton(){
        for(int i=0;i<radioIds.length;i++){
            radios[i]= (RadioButton) getView().findViewById(radioIds[i]);
            radios[i].setOnClickListener(clickListener);
        }
    }

    View.OnClickListener clickListener=new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            for(int i=0;i<radios.length;i++){
                if(v.equals(radios[i])){
                    mCallBacks.onNavigationDrawerItemSelected(i);
                }else{
                    radios[i].setChecked(false);
                }
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallBacks= (NavigationDrawerCallBacks) context;
    }


//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        mCallBacks= (NavigationDrawerCallBacks) activity;
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBacks=null;
    }

    public interface NavigationDrawerCallBacks{
        void onNavigationDrawerItemSelected(int position);
    }
}
