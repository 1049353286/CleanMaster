package com.apricot.cleanmaster.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.apricot.cleanmaster.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

/**
 * Created by Apricot on 2016/9/15.
 */
public abstract class BaseSwipeBackActivity extends BaseActivity implements SwipeBackActivityBase{
    protected abstract int initLayout();
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private SwipeBackActivityHelper mHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper=new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
        if(initLayout()>0){
            setContentView(initLayout());
        }
        ButterKnife.bind(this);
        initToolBar();
    }

    private void initToolBar(){
        if (mToolbar!=null){
            setSupportActionBar(mToolbar);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    public View findViewById(int id){
        View v=super.findViewById(id);
        if(v==null&&mHelper!=null){
            return mHelper.findViewById(id);
        }
        return v;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }
}
