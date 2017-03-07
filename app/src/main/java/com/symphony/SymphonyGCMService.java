package com.symphony;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.symphony.database.DB;
import com.symphony.distributer.DistributerActivity;
import com.symphony.utils.SymphonyUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SymphonyGCMService extends Service {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    private String masterIP;
    private String masterPort;

    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;

    public static boolean isIpAddress(String ipAddress) {
        String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }

    public static boolean valisDomain(String domainName) {

        final String DOMAIN_NAME_PATTERN = "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$";
        final Pattern pDomainNameOnly;

        pDomainNameOnly = Pattern.compile(DOMAIN_NAME_PATTERN);

        return pDomainNameOnly.matcher(domainName).find();

    }

    public static final String TAG = "SymphonyGCMService";

    @Override
    public void onCreate() {

        Log.e("GCM", "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("GCM", "onStartCommand");
        E_Sampark e_sampark = (E_Sampark) getApplicationContext();
        prefs = e_sampark.getSharedPreferences();

        if (intent != null) {
            Bundle extras = intent.getExtras();
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

            String messageType = gcm.getMessageType(intent);

            if (extras != null) {
                if (!extras.isEmpty()) {


                    if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                            .equals(messageType)) {
                        sendNotification("Send error: " + extras.toString());
                    } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                            .equals(messageType)) {
                        sendNotification("Deleted messages on server: "
                                + extras.toString());
                    } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                            .equals(messageType)) {

				/*for (int i = 0; i < 3; i++) {
                    Log.i(TAG,
							"Working... " + (i + 1) + "/5 @ "
									+ SystemClock.elapsedRealtime());
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}

				}*/
                        Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());


                        sendNotification(
                                extras.get(SymphonyUtils.MESSAGE_KEY) + "");
                        Log.i(TAG, "Received: " + extras.toString());
                    }
                }
            }
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
        return Service.START_STICKY;
    }


    private void sendNotification(String msg) {
        Log.d(TAG, "Preparing to send notification...: " + msg);

        if (msg.contains("sip")) {

            try {

                String ipAddress[] = msg.split("#");
                if (ipAddress != null) {

                    if (ipAddress.length == 2) {


                        if (ipAddress[1].contains(":")) {

                            String[] ip = ipAddress[1].split(":");

                            if (ip != null) {
                                if (ip.length == 2) {

                                    masterIP = ip[0];
                                    masterPort = ip[1];
                                } else {

                                    masterIP = null;
                                    masterPort = null;

                                }
                            }


                        } else {


                            masterIP = ipAddress[1];
                            masterPort = null;
                        }

                        if (masterIP != null) {


                            if (isIpAddress(masterIP)) {


                                //set port here

                                setMasterIP();

                                msg = "Endpoint changed";

                            } else if (valisDomain(masterIP)) {

                                setMasterIP();

                                msg = "Endpoint changed";


                            } else {

                                msg = "Endpoint is not valid";
                            }

                        }


                    }
                }


            } catch (Exception e) {

                masterIP = null;
                masterPort = null;

                Toast.makeText(this,
                        "Not able to change ip", Toast.LENGTH_SHORT)
                        .show();
            }

        }
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, DistributerActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("eSampark Notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Log.d(TAG, "Notification sent successfully.");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss a");
        String currentDateandTime = sdf.format(new Date()).replace(" ", "");
        currentDateandTime = currentDateandTime.replace(".", "");


        // insert into database
        ContentValues value = new ContentValues();
        value.put(DB.NOTIFICATION_MESSAGE, msg);
        value.put(DB.NOTIFICATION_TIMESTAMP, currentDateandTime);
        value.put(DB.NOTIFICATION_TYPE, 0);

        this.getContentResolver().insert(Uri.parse("content://com.symphony.database.DBProvider/addNewNotification"), value);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.e("GCM", "destroy");


    }

    private void setMasterIP() {

        edit = prefs.edit();
        edit.putString("masterIP", masterIP);
        edit.putString("masterPort", masterPort);


        edit.commit();


    }
}
