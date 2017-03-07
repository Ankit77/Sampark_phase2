package com.symphony;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.symphony.receiver.MyService;
import com.symphony.sms.SyncAlaram;
import com.symphony.utils.Const;

import java.util.Calendar;

/**
 * Created by indianic on 19/12/15.
 */
public class E_Sampark extends Application {

    private SharedPreferences sharedPreferences;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        Intent intentLocationService = new Intent(getApplicationContext(), MyService.class);
        startService(intentLocationService);
        if (!sharedPreferences.getBoolean(Const.PREF_ISSYNCDATA, false)) {
            setSyncCheckStatusAlarm();
            setWipeDataAlarm();
            sharedPreferences.edit().putBoolean(Const.PREF_ISSYNCDATA, true).commit();
        }

    }

//    private void reStartAlarm() {
//        calendar = Calendar.getInstance();
//        long diff = sharedPreferences.getLong("TIME", 0) - Calendar.getInstance().getTimeInMillis();
//
//        Intent intent = new Intent(MyApplication.this, AlarmReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(MyApplication.this,
//                1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, diff,
//                pendingIntent);
//    }


    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
//    BroadcastReceiver tickReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
//                long time = Calendar.getInstance().getTimeInMillis() - sharedPreferences.getLong(Const.PREF_STRAT_TIME, 0);
//                if (time > Const.WIPETIME) {
//                    symphonyDB.deleteAllVisit();
//                    getContentResolver()
//                            .delete(Uri.parse("content://com.symphony_ecrm.database.DBProvider/deleteNotificationReport"),
//                                    null,
//                                    null);
//                    sharedPreferences.edit().putLong(Const.PREF_STRAT_TIME, Calendar.getInstance().getTimeInMillis()).commit();
//
//                }
//                Log.e(CheckStatus.class.getSimpleName(), "Time  Tick Call");
//            }
//        }
//    };

    private void setSyncCheckStatusAlarm() {
        Calendar calendar = Calendar.getInstance();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alramReceiverIntent = new Intent(this, SyncAlaram.class);
        alramReceiverIntent.setAction(SyncAlaram.DB_CHECK_FOR_DIST_PHOTO);
        PendingIntent alramPendingIntent = PendingIntent.getBroadcast(this, 0, alramReceiverIntent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                Const.SYNCDATA_INTERVAL, alramPendingIntent);
    }

    private void setWipeDataAlarm() {
        Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.HOUR_OF_DAY, 48);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alramReceiverIntent = new Intent(this, SyncAlaram.class);
        alramReceiverIntent.setAction(SyncAlaram.WIPE_REPORT_DATA);
        PendingIntent alramPendingIntent = PendingIntent.getBroadcast(this, 1, alramReceiverIntent, 0);
//        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY * 2, alramPendingIntent);
        getSharedPreferences().edit().putLong(Const.PREF_WIPEOUT_TIME, calendar.getTimeInMillis()).commit();
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                Const.WIPEDATA_INTERVAL, alramPendingIntent);
    }

}
