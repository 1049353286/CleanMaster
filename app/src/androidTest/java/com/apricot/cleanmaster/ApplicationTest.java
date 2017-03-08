package com.apricot.cleanmaster;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.apricot.cleanmaster.bean.AppInfo;
import com.apricot.cleanmaster.dao.WhiteListDao;

import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testWhiteListDao(){
        List<AppInfo> appInfoList=new WhiteListDao(getContext()).queryAllWhiteApp();
        assertEquals(appInfoList.size(),2);
    }
}