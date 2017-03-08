package com.apricot.cleanmaster.dao;

import android.test.AndroidTestCase;

import com.apricot.cleanmaster.bean.AppInfo;

import java.util.List;


/**
 * Created by Apricot on 2017/3/8.
 */
public class WhiteListDaoTest extends AndroidTestCase{

    public void testWhiteListDao(){
        List<AppInfo> appInfoList=new WhiteListDao(getContext()).queryAllWhiteApp();
        assertEquals(appInfoList.size(),2);
    }

}