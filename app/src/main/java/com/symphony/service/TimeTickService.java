package com.symphony.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.symphony.E_Sampark;
import com.symphony.sms.SyncAlaram;
import com.symphony.utils.Const;

import java.util.Calendar;


public class TimeTickService extends Service {
    private E_Sampark e_sampark;
    private String TAG = TimeTickService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        e_sampark = (E_Sampark) getApplicationContext();
        registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(tickReceiver);
    }

    BroadcastReceiver tickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                if (e_sampark.getSharedPreferences().getBoolean("isregister", false)) {
                    Log.e(TimeTickService.class.getSimpleName(), "Time  Tick Call");

                    long diff_wipedata = Calendar.getInstance().getTimeInMillis() - e_sampark.getSharedPreferences().getLong(Const.PREF_WIPEDATA, 0);
                    if (diff_wipedata >= Const.WIPEDATA_INTERVAL) {
                        Log.e(TAG, "WIPE IS CALL");
                        setWipeDataAlarm(TimeTickService.this);
                        SharedPreferences.Editor editor = e_sampark.getSharedPreferences().edit();
                        editor.putLong(Const.PREF_WIPEDATA, Calendar.getInstance().getTimeInMillis());
                        editor.commit();
                    }

                    long diff_syncdata = Calendar.getInstance().getTimeInMillis() - e_sampark.getSharedPreferences().getLong(Const.PREF_SYNC, 0);
                    if (diff_syncdata >= Const.SYNCDATA_INTERVAL) {
                        Log.e(TAG, "SYNC IS CALL");
                        setSyncCheckStatusAlarm(TimeTickService.this);
                        SharedPreferences.Editor editor = e_sampark.getSharedPreferences().edit();
                        editor.putLong(Const.PREF_SYNC, Calendar.getInstance().getTimeInMillis());
                        editor.commit();
                    }

                }
            }
        }
    };

    private void setSyncCheckStatusAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alramReceiverIntent = new Intent(context, SyncAlaram.class);
        alramReceiverIntent.setAction(SyncAlaram.DB_CHECK_FOR_DIST_PHOTO);
        PendingIntent alramPendingIntent = PendingIntent.getBroadcast(context, 0, alramReceiverIntent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), alramPendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), alramPendingIntent);
        }
    }

    private void setWipeDataAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alramReceiverIntent = new Intent(context, SyncAlaram.class);
        alramReceiverIntent.setAction(SyncAlaram.WIPE_REPORT_DATA);
        PendingIntent alramPendingIntent = PendingIntent.getBroadcast(context, 1, alramReceiverIntent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), alramPendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), alramPendingIntent);
        }
    }

}
