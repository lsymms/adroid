package com.example.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutostartTrackerAlarm extends BroadcastReceiver {
    TrackerAlarm trackerAlarm = new TrackerAlarm();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            trackerAlarm.SetAlarm(context);
        }
    }
}