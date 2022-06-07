package com.example.pages.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationClickReceiver extends BroadcastReceiver {

    public static final String TAG = "NotificClickReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(TAG,"通知栏点击");

    }
}