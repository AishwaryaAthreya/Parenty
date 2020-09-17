package com.brogrammers.the.parenty;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Sandesh on 27-08-2017.
 */


public class StartTrackingAtBoot extends BroadcastReceiver {

    private PendingIntent pendingIntent;
    private AlarmManager manager;
    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context,"StartTrackingAtBoot",Toast.LENGTH_SHORT).show();
        System.out.print("hahaha");
        Intent locationintent=new Intent(context,GeoListener.class);
        pendingIntent= PendingIntent.getBroadcast(context,0,locationintent,0);
        manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        int interval = 20000;

        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);



        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show();



    }
}
