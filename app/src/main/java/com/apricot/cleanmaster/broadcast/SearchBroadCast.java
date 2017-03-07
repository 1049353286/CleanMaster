package com.apricot.cleanmaster.broadcast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.apricot.cleanmaster.ui.FileManageActivity;

public class SearchBroadCast extends BroadcastReceiver {
	public static  String mServiceKeyword = "";//接收搜索关键字的静态变量
    public static  String mServiceSearchPath = "";//接收搜索路径的静态变量
	@Override
	public void onReceive(Context context, Intent intent) {

		String mAction = intent.getAction();
		if(FileManageActivity.KEYWORD_BROADCAST.equals(mAction)){

			mServiceKeyword = intent.getStringExtra("keyword");
			mServiceSearchPath = intent.getStringExtra("searchpath");
		}
	}
}
