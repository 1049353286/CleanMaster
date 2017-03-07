package com.apricot.cleanmaster.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.views.gesturelock.GestureLockViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Apricot on 2017/3/5.
 */

public class AppLockSettingActivity extends AppCompatActivity{
    public static final String SHARED_PREF_LOCK_PASSWORD="lock_pwd";
    public static final Integer RESET_PWD=1;
    private List<Integer> newpassword=new ArrayList<>();
    private int action;

    private TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applock_setting);
        mTextView= (TextView) findViewById(R.id.tv_notice);
        final GestureLockViewGroup gestureLock = (GestureLockViewGroup) findViewById(R.id.gestureLockViewGroup);

        action=getIntent().getIntExtra("reset_pwd",0);

        SharedPreferences pref=getSharedPreferences(SHARED_PREF_LOCK_PASSWORD,MODE_PRIVATE);
        String pwd=pref.getString("pwd",null);

        if(pwd==null){
            action=RESET_PWD;
            mTextView.setText("请设置手势");
        }else{
            int[] password=new int[pwd.length()];
            for(int i=0;i<pwd.length();i++){
                password[i]=Integer.parseInt(String.valueOf(pwd.charAt(i)));
            }
            gestureLock.setAnswer(password);
            if(action==RESET_PWD){
                mTextView.setText("请设置新密码");
            }else{
                mTextView.setText("请输入原密码");
            }

        }


        gestureLock.setOnGestureLockViewListener(new GestureLockViewGroup.OnGestureLockViewListener() {
            @Override
            public void onBlockSelected(int cId) {

                if(action==RESET_PWD){
                    newpassword.add(cId);
                }

            }

            @Override
            public void onGestureEvent(boolean matched) {
                if(matched){
                    mTextView.setText("密码正确，请设置新密码");
                    Intent intent=new Intent(AppLockSettingActivity.this,AppLockSettingActivity.class);
                    intent.putExtra("reset_pwd",RESET_PWD);
                    startActivity(intent);
                    finish();
                }else{
                    if(action==RESET_PWD){
                        mTextView.setText("密码设置成功");
                        SharedPreferences.Editor editor=getSharedPreferences(SHARED_PREF_LOCK_PASSWORD,MODE_PRIVATE).edit();
                        StringBuilder sb=new StringBuilder();
                        for(int i=0;i<newpassword.size();i++){
                            sb.append(newpassword.get(i));
                        }
                        editor.putString("pwd",sb.toString());
                        editor.commit();
                        finish();
                    }else{
                        mTextView.setText("密码错误,请重新输入");
                    }
                }




            }

            @Override
            public void onUnmatchedExceedBoundary() {

            }
        });
    }
}
