package com.symphony.sms;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.symphony.E_Sampark;
import com.symphony.database.DB;
import com.symphony.sms.SyncManager.DISTRIBUTER_META_DATA;
import com.symphony.utils.Const;
import com.symphony.utils.SymphonyUtils;

import java.util.Calendar;

public class SyncAlaram extends BroadcastReceiver {
    public static final String DB_CHECK_FOR_DIST_PHOTO = "com.symphony.sms.DB_CHECK_FOR_DIST_PHOTO";
    public static final String WIPE_REPORT_DATA = "com.symphony.sms.WIPE_REPORT_DATA";
    private E_Sampark e_sampark;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (intent.getAction().equals(DB_CHECK_FOR_DIST_PHOTO)) {
                Log.e(SyncAlaram.class.getSimpleName(), "DB_CHECK_FOR_DIST_PHOTO");
                Cursor cur = context.getContentResolver().
                        query(Uri.parse("content://com.symphony.database.DBProvider/getDistributerMetaData"),
                                DISTRIBUTER_META_DATA.PROJECTION,
                                DB.DIST_FLAG + " = 1",
                                null,
                                null);
                Cursor curCheck = context.getContentResolver().
                        query(Uri.parse("content://com.symphony.database.DBProvider/getCheckData"),
                                SyncManager.CHECK_DATA.PROJECTION,
                                DB.CHECK_FLAG + " = 1",
                                null,
                                null);
                Intent intentLocationService = new Intent(context, SMSService.class);
                intentLocationService.setAction(SMSService.FETCH_LOCATION_INTENT);
                context.startService(intentLocationService);

                if (cur != null) {
                    if (cur.getCount() != 0) {
                        //start service
                        if (isNetworkAvailable(context)) {
                            Intent syncManager = new Intent(context, SyncManager.class);
                            syncManager.setAction(SyncManager.SYNC_DISTRIBUTER_DATA);
                            context.startService(syncManager);
                        }
                    }
                    cur.close();
                }
                if (curCheck != null) {
                    if (curCheck.getCount() != 0) {
                        if (isNetworkAvailable(context)) {
                            Intent syncManager = new Intent(context, SyncManager.class);
                            syncManager.setAction(SyncManager.SYNC_CHECK_STATUS_DATA);
                            context.startService(syncManager);
                        }
                    }
                    curCheck.close();
                }
            } else if (intent.getAction().equals(WIPE_REPORT_DATA)) {
                //sendNotification("Wipe out all data");
                Log.e(SyncAlaram.class.getSimpleName(), "WIPE_REPORT_DATA");
                e_sampark = (E_Sampark) context.getApplicationContext();
                e_sampark.getSharedPreferences().edit().putLong(Const.PREF_WIPEOUT_TIME, Calendar.getInstance().getTimeInMillis()).commit();
                int delDistributerReport = context.getContentResolver()
                        .delete(Uri.parse("content://com.symphony.database.DBProvider/deleteDistributerReport"),
                                null,
                                null);

                int delCheckReport = context.getContentResolver()
                        .delete(Uri.parse("content://com.symphony.database.DBProvider/deleteCheckReport"),
                                DB.CHECK_FLAG + " = 0",
                                null);

                int delNotificationReport = context.getContentResolver()
                        .delete(Uri.parse("content://com.symphony.database.DBProvider/deleteNotificationReport"),
                                null,
                                null);

                Log.e("Symphony ", "Wipe out all data " + delDistributerReport + " " + delCheckReport + " " + delNotificationReport);
            }
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
