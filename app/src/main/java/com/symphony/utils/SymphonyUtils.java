package com.symphony.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

import com.inforoeste.mocklocationdetector.MockLocationDetector;
import com.symphony.E_Sampark;
import com.symphony.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String getCurrentDataTime() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat(Const.DEFAULT_DATETIME_FORMAT);
        String formattedDate = df.format(c.getTime());
        return formattedDate;
// Now formattedDate have current date/time
    }

    public static void copyDatabase(Context c, String DATABASE_NAME) {
        String databasePath = c.getDatabasePath(DATABASE_NAME).getPath();
        File f = new File(databasePath);
        OutputStream myOutput = null;
        InputStream myInput = null;
//        Log.d("testing", " testing db path " + databasePath);
//        Log.d("testing", " testing db exist " + f.exists());

        if (f.exists()) {
            try {

                File directory = new File("/mnt/sdcard/DB_DEBUG");
                if (!directory.exists())
                    directory.mkdir();

                myOutput = new FileOutputStream(directory.getAbsolutePath()
                        + "/" + DATABASE_NAME);
                myInput = new FileInputStream(databasePath);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
            } catch (Exception e) {
            } finally {
                try {
                    if (myOutput != null) {
                        myOutput.close();
                        myOutput = null;
                    }
                    if (myInput != null) {
                        myInput.close();
                        myInput = null;
                    }
                } catch (Exception e) {
                }
            }
        }
    }
}
