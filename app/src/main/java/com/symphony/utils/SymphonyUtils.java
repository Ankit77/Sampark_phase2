package com.symphony.utils;

import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.inforoeste.mocklocationdetector.MockLocationDetector;
import com.symphony.BootReceiver;
import com.symphony.E_Sampark;
import com.symphony.R;
import com.symphony.sms.SyncAlaram;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

public class SymphonyUtils {

    public static final String TIME_SERVER = "time-a.nist.gov";
    public static final String GCM_KEY = "36615211224";//Live
    //public static final String GCM_KEY = " 915617718405";//Test
    public static final String MESSAGE_KEY = "message";
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor edit;


    public static String getAppVersion(Context context) {

        PackageInfo pInfo;
        try {

            if (context != null) {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                return pInfo.versionName;
            } else
                return "0.0";

        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;

    }

    public static void setMasterIp(Context context, String masterIP, String masterPort) {

        if (context != null) {

            E_Sampark e_sampark = (E_Sampark) context.getApplicationContext();
            prefs = e_sampark.getSharedPreferences();
            edit = prefs.edit();
            edit.putString("masterIP", masterIP);
            edit.putString("masterPort", masterPort);


            edit.commit();
        }


    }

    public static boolean isAutomaticDateTime(Context context) {
        int i = 0;
        if (android.os.Build.VERSION.SDK_INT < 17) {
            i = android.provider.Settings.System.getInt(context.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0);
        } else {
            i = android.provider.Settings.Global.getInt(context.getContentResolver(), android.provider.Settings.Global.AUTO_TIME, 0);
        }
        if (i == 1) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean isFackLocation(Context context, Location location) {
        if (location != null) {
            boolean isMock = MockLocationDetector.isLocationFromMockProvider(context, location);
            return isMock;
        } else {
            return false;
        }
    }
}
