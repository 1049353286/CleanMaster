package com.apricot.cleanmaster.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.apricot.cleanmaster.service.AppLockerService;

public class BootCompleteReceiver extends BroadcastReceiver {
    private static boolean DEBUG = true;
    private static final String TAG = "BootCompleteReceiver";

    public BootCompleteReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (DEBUG) Log.d(TAG, "Receive boot complete");
            context.startService(new Intent(context, AppLockerService.class));
        } catch (Exception e) {
            Log.e(TAG, "Failed to start app locker service");
        }
    }
}
