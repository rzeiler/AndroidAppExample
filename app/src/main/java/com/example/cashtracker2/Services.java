package com.example.cashtracker2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.cashtracker2.cash.Cash;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Ralf on 09.06.2017.
 */

public class Services extends BroadcastReceiver {

    private SimpleDateFormat DeDateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
    private static AlarmManager alarmMgr;
    private static PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {

        Calendar c = Calendar.getInstance();
        DatabaseHandler db = new DatabaseHandler(context);

        for (int i = 2; i <= 4; i++ ){
            Log.w("Index", String.valueOf(i));
            List<Cash> lc = db.getCash(" iscloned IS NULL AND repeat="+ i +" AND int_create_date <= date('now') ");
            for (Cash cash : lc) {
                Log.w("asd", String.valueOf(cash.getRepeat()));
            }

        }




        Log.w("onReceive", "Service call " + DeDateFormat.format(c.getTime()));
        Toast.makeText(context, "onReceive Service call " + DeDateFormat.format(c.getTime()), Toast.LENGTH_LONG).show();


    }

    public static void setReceiver(Context context) {
        Intent receiverIntent = new Intent(context, Services.class);
        alarmIntent = PendingIntent.getBroadcast(context, 123456789,
                receiverIntent, 0);
        alarmMgr = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        // Set the alarm to start at 8:30 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 5, alarmIntent);


        Toast.makeText(context, "Service call", Toast.LENGTH_LONG).show();
    }

    public static void cancelReceiver(Context context) {
        Intent receiverIntent = new Intent(context, Services.class);
        alarmIntent = PendingIntent.getBroadcast(context, 123456789,
                receiverIntent, 0);
        alarmMgr = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(alarmIntent);
    }
}
