package com.symphony;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.symphony.sms.SMSService;
import com.symphony.sms.SyncAlaram;
import com.symphony.utils.Const;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {


    private Context mcontext;

    private SharedPreferences prefs;
    private E_Sampark e_sampark;


    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context, Intent intent) {

        this.mcontext = context;
        e_sampark = (E_Sampark) context.getApplicationContext();
        // TODO Auto-generated method stub
        prefs = e_sampark.getSharedPreferences();
        if ((intent.getAction().equals("android.intent.action.BOOT_COMPLETED")
                || intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON")
        )) {

            startLocationService();
            e_sampark.getSharedPreferences().edit().putBoolean(Const.PREF_ISSYNCDATA, true).commit();
            setSyncCheckStatusAlarm(context);
            //get time diff when alarm was setup
            long timediff = Calendar.getInstance().getTimeInMillis() - e_sampark.getSharedPreferences().getLong(Const.PREF_WIPEOUT_TIME, 0);
            timediff = Const.WIPEDATA_INTERVAL - timediff;

            Log.e(BootReceiver.class.getSimpleName(), "Time is" + timediff);
            if (timediff > 0) {
                setWipeDataAlarm(mcontext, (int) timediff);
            } else {

                setWipeDataAlarm(mcontext, (int) 10000);
            }

        }
    }


    private void startLocationService() {

        Intent intentLocationService = new Intent(mcontext, SMSService.class);
        intentLocationService.setAction(SMSService.FETCH_LOCATION_INTENT);
        mcontext.startService(intentLocationService);
    }


    private void setSyncCheckStatusAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alramReceiverIntent = new Intent(context, SyncAlaram.class);
        alramReceiverIntent.setAction(SyncAlaram.DB_CHECK_FOR_DIST_PHOTO);
        PendingIntent alramPendingIntent = PendingIntent.getBroadcast(context, 0, alramReceiverIntent, 0);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                calendar.getTimeInMillis(),
                Const.SYNCDATA_INTERVAL, alramPendingIntent);
    }

    private void setWipeDataAlarm(Context context, int remainingTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, remainingTime);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alramReceiverIntent = new Intent(context, SyncAlaram.class);
        alramReceiverIntent.setAction(SyncAlaram.WIPE_REPORT_DATA);
        PendingIntent alramPendingIntent = PendingIntent.getBroadcast(context, 1, alramReceiverIntent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                Const.WIPEDATA_INTERVAL, alramPendingIntent);
    }

}
