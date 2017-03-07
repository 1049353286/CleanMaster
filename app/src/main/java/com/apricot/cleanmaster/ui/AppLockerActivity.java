package com.apricot.cleanmaster.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.service.AppLockerService;
import com.apricot.cleanmaster.views.gesturelock.GestureLockViewGroup;

public class AppLockerActivity extends AppCompatActivity {
    private final static String TAG = "AppLockerActivity";
    private final static boolean DEBUG  = true;

    private String mAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_locker);

        // initialize app icon
        ImageView appIcon = (ImageView) findViewById(R.id.img_appicon);
        mAppName = getIntent().getStringExtra("APP_NAME");
        if (mAppName != null) {
            try {
                Drawable icon = getPackageManager().getApplicationIcon(mAppName);
                appIcon.setImageDrawable(icon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        SharedPreferences pref=getSharedPreferences(AppLockSettingActivity.SHARED_PREF_LOCK_PASSWORD,MODE_PRIVATE);
        String pwd=pref.getString("pwd",null);

        int[] password=new int[pwd.length()];
        for(int i=0;i<pwd.length();i++){
            password[i]=Integer.parseInt(String.valueOf(pwd.charAt(i)));
        }

        // initialize gesture lock
        GestureLockViewGroup gestureLock = (GestureLockViewGroup) findViewById(R.id.gestureLockViewGroup);
        gestureLock.setAnswer(password); //just for demo
        gestureLock.setOnGestureLockViewListener(new GestureLockViewGroup.OnGestureLockViewListener() {
            @Override
            public void onBlockSelected(int cId) {

            }

            @Override
            public void onGestureEvent(boolean matched) {
                if (matched) {
                    Intent intent = new Intent(AppLockerActivity.this, AppLockerService.class);
                    intent.putExtra("UNLOCK_APP", mAppName);
                    startService(intent);
                    finish();
                }
            }

            @Override
            public void onUnmatchedExceedBoundary() {

            }
        });
    }
}
