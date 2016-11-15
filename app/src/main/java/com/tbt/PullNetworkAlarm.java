package com.tbt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

/**
 * Created by bradley on 04-09-2016.
 */
public class PullNetworkAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        PendingIntent pendingIntent = null;
        if(manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isConnected()) {
            Intent networkIntent = new Intent(context, PullNetworkForNewData.class);
            pendingIntent = PendingIntent.getBroadcast(context, 0, networkIntent, 0);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR, pendingIntent);
        } else {
            if(pendingIntent != null) {
                alarmManager.cancel(pendingIntent);
            }
        }
    }
}