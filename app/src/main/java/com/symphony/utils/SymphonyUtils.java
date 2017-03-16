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

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
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

    public static boolean isNetworkAvailable(Context context) {

        boolean isNetAvailable = false;
        if (context != null) {
            final ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (mConnectivityManager != null) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    final Network[] allNetworks = mConnectivityManager.getAllNetworks();

                    for (Network network : allNetworks) {
                        final NetworkInfo networkInfo = mConnectivityManager.getNetworkInfo(network);
                        if (networkInfo != null && networkInfo.isConnected()) {
                            isNetAvailable = true;
                            break;
                        }
                    }

                } else {
                    boolean wifiNetworkConnected = false;
                    boolean mobileNetworkConnected = false;

                    final NetworkInfo mobileInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                    final NetworkInfo wifiInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                    if (mobileInfo != null) {
                        mobileNetworkConnected = mobileInfo.isConnected();
                    }
                    if (wifiInfo != null) {
                        wifiNetworkConnected = wifiInfo.isConnected();
                    }
                    isNetAvailable = (mobileNetworkConnected || wifiNetworkConnected);
                }
            }
        }
        return isNetAvailable;
    }

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

    public static ProgressDialog displayProgressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Loading....");
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

    /**
     * Dismiss current progress dialog
     *
     * @param dialog dialog
     */
    public static void dismissProgressDialog(ProgressDialog dialog) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    /**
     * Alert dialog to show common messages.
     *
     * @param title   title
     * @param message message
     * @param context context
     */
    public static void displayDialog(final Context context, final String title, final String message) {

        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        if (title == null)
            alertDialog.setTitle(context.getString(R.string.app_name));
        else
            alertDialog.setTitle(title);
        alertDialog.setCancelable(false);

        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ok", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });
        if (!((Activity) context).isFinishing()) {

            alertDialog.show();
        }
    }
}