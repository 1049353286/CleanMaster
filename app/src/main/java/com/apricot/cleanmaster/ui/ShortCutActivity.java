package com.apricot.cleanmaster.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.apricot.cleanmaster.R;
import com.apricot.cleanmaster.bean.AppProcessInfo;
import com.apricot.cleanmaster.service.CoreService;
import com.apricot.cleanmaster.utils.StorageUtil;
import com.apricot.cleanmaster.utils.T;

import java.lang.reflect.Field;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ShortCutActivity extends Activity implements CoreService.OnProcessActionListener {

    @BindView(R.id.layout_anim)
    RelativeLayout layoutAnim;

    @BindView(R.id.mRelativeLayout)
    RelativeLayout mRelativeLayout;

    private Rect rect;
    @BindView(R.id.clean_light_img)
    ImageView cleanLightImg;


    private CoreService mCoreService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCoreService = ((CoreService.ProcessServiceBinder) service).getService();
            mCoreService.setOnActionListener(ShortCutActivity.this);
            mCoreService.cleanAllProcess();
            //  updateStorageUsage();


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCoreService.setOnActionListener(null);
            mCoreService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_cut);
        ButterKnife.bind(this);
        rect = getIntent().getSourceBounds();
        if (rect == null) {
            finish();
            return;
        }

        if (rect != null) {

            Class<?> c = null;
            Object obj = null;
            Field field = null;
            int x = 0, statusBarHeight = 0;
            try {
                c = Class.forName("com.android.internal.R$dimen");
                obj = c.newInstance();
                field = c.getField("status_bar_height");
                x = Integer.parseInt(field.get(obj).toString());
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {

                e1.printStackTrace();
            }

            layoutAnim.measure(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            int height = layoutAnim.getMeasuredHeight();
            int width = layoutAnim.getMeasuredWidth();

            RelativeLayout.LayoutParams layoutparams = (RelativeLayout.LayoutParams) layoutAnim
                    .getLayoutParams();

            layoutparams.leftMargin = rect.left + rect.width() / 2 - width / 2;

            layoutparams.topMargin = rect.top + rect.height() / 2 - height / 2;

//            mRelativeLayout.updateViewLayout(layoutAnim, layoutparams);
        }
        cleanLightImg.startAnimation(AnimationUtils.loadAnimation(this,
                R.anim.rotate_anim));
        bindService(new Intent(ShortCutActivity.this, CoreService.class),
                mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onScanStarted(Context context) {

    }

    @Override
    public void onScanProgressUpdated(Context context, int current, int max) {

    }

    @Override
    public void onScanCompleted(Context context, List<AppProcessInfo> apps) {

    }

    @Override
    public void onCleanStarted(Context context) {

    }

    @Override
    public void onCleanCompleted(Context context, long cacheSize) {
        if (cacheSize > 0) {
            T.showLong(ShortCutActivity.this, "应用管理助手,为您释放" + StorageUtil.convertStorage(cacheSize) + "内存");
        } else {
            T.showLong(ShortCutActivity.this, "您刚刚清理过内存,请稍后再来~");
        }

        finish();
    }


    private void killProcess() {
        // TODO Auto-generated method stub

        ActivityManager am = (ActivityManager) getBaseContext()
                .getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        // 获得正在运行的所有进程
        List<ActivityManager.RunningAppProcessInfo> processes = am
                .getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo info : processes) {
            if (info != null && info.processName != null
                    && info.processName.length() > 0) {
                String pkgName = info.processName;
                if (!("system".equals(pkgName) || "launcher".equals(pkgName)
                        || "android.process.media".equals(pkgName)
                        || "android.process.acore".equals(pkgName)
                        || "com.android.phone".equals(pkgName)
                        || "com.fb.FileBrower".equals(pkgName)// 浏览器
                        || "com.ott_pro.launcher".equals(pkgName)// 桌面
                        || "com.ott_pro.upgrade".equals(pkgName)// 升级
                        || "com.example.airplay".equals(pkgName)// 媒体分享
                        || "com.amlogic.mediacenter".equals(pkgName)// 媒体分享
                        || "com.android.dreams.phototable".equals(pkgName)// 屏保
                        || "com.amlogic.inputmethod.remote".equals(pkgName)// 输入法
                        || pkgName.startsWith("com.lefter"))) {
                    am.killBackgroundProcesses(pkgName);// 杀进程
                }
            }
        }


    }




    @Override
    public void onDestroy() {
        unbindService(mServiceConnection);
        super.onDestroy();
    }
}
