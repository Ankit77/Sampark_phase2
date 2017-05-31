package com.symphony.distributer;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.symphony.E_Sampark;
import com.symphony.R;
import com.symphony.SymphonyHome;
import com.symphony.database.DB;
import com.symphony.http.WSLogout;
import com.symphony.report.SymphonyReport;
import com.symphony.sms.SMSService;
import com.symphony.sms.SyncAlaram;
import com.symphony.sms.SyncManager;
import com.symphony.utils.Const;
import com.symphony.utils.SymphonyUtils;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DistributerActivity extends AppCompatActivity implements DistributerActivityListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    //    private LocationReceiver mLocationReceiver;
    public static final int GPS_RESULT = 100;
    public String messageText;
    private int GET_DISTRIBUTER_IMAGE_REQUEST = 120;
    private NotificationManager mNotificationManager;
    public static String currentLocation;
    public static final String LOCATION_RECEIVER = "com.symphony.locationreceiver";
    private LocationManager mLocationManager;
    private CheckStatusListener mCheckStatusListener;
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;
    public static final String IMAGE_DIR = "Sampark";
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static String currentDistId;
    private static String currentDistKey;
    private static String currentDistName;
    private Uri imageFileUri;
    private AlarmManager alramManager;
    private PendingIntent alramPendingIntent;
    private DistributerInfo distInfoFrag;
    private E_Sampark e_sampark;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private AsyncLogout asyncLogout;
    private static final String HTTP_SERVER = "61.12.85.74";
    private static final String HTTP_PORT = "800";
    private static final String HTTP_PROTOCOL = "http://";
    private String HTTP_ENDPOINT = HTTP_PROTOCOL + HTTP_SERVER + ":" + HTTP_PORT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distributer_home);
        e_sampark = (E_Sampark) getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        prefs = e_sampark.getSharedPreferences();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        getSupportActionBar().setIcon(android.R.color.transparent);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Bundle bundle = new Bundle();
        bundle.putString("checkin_location", currentLocation);
        mCheckStatusListener = (CheckStatusListener) getSupportFragmentManager().findFragmentById(R.id.checkStatusFragment);
//        mLocationReceiver = new LocationReceiver();
//        startSyncAlram();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
//        this.unregisterReceiver(mLocationReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
//        this.registerReceiver(mLocationReceiver, new IntentFilter(LOCATION_RECEIVER));
        getSupportActionBar().setTitle("CHECK IN/OUT");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.symphony_exit:
                if (e_sampark.getSharedPreferences().getString("TAG", Const.CHECKIN).equalsIgnoreCase(Const.CHECKOUT)) {
                    SymphonyUtils.displayDialog(DistributerActivity.this, getString(R.string.app_name), getString(R.string.alert_logout));
                } else {

                    Cursor cur_disdata = getBaseContext().getContentResolver().
                            query(Uri.parse("content://com.symphony.database.DBProvider/getDistributerMetaData"),
                                    SyncManager.DISTRIBUTER_META_DATA.PROJECTION,
                                    DB.DIST_FLAG + " = 1",
                                    null,
                                    null);
                    Cursor cur_CheckData = getBaseContext().getContentResolver().
                            query(Uri.parse("content://com.symphony.database.DBProvider/getCheckData"),
                                    SyncManager.CHECK_DATA.PROJECTION,
                                    DB.CHECK_FLAG + " = 1",
                                    null,
                                    null);


                    if (cur_disdata.getCount() > 0 || cur_CheckData.getCount() > 0) {
                        cur_CheckData.close();
                        cur_disdata.close();
                        displayDialog(DistributerActivity.this, getString(R.string.app_name), "Please Sync Pending data to Server first");
                    } else {

                        if (SymphonyUtils.isNetworkAvailable(DistributerActivity.this)) {
                            String url = HTTP_ENDPOINT + "/eSampark_Logout.asp?user=android&pass=xand123&MNO=" + e_sampark.getSharedPreferences().getString("usermobilenumber", "") + "&empid=" + e_sampark.getSharedPreferences().getString(Const.EMPID, "");
                            asyncLogout = new AsyncLogout();
                            asyncLogout.execute(url);
                        } else {
                            SymphonyUtils.displayDialog(DistributerActivity.this, getString(R.string.app_name), "Please Check Internet Connection");
                        }
                    }

                }
//                System.exit(1);
                return true;
            case R.id.distributer_listview:
                onDistributerListSelect();
                return true;
            case R.id.symphony_settings:
                onSettingsSelect();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut() {
        e_sampark.getSharedPreferences().edit().clear().commit();
        getContentResolver()
                .delete(Uri.parse("content://com.symphony.database.DBProvider/deleteDistributerReport"),
                        null,
                        null);

        getContentResolver()
                .delete(Uri.parse("content://com.symphony.database.DBProvider/deleteCheckReport"),
                        DB.CHECK_FLAG + " = 0",
                        null);

        getContentResolver()
                .delete(Uri.parse("content://com.symphony.database.DBProvider/deleteNotificationReport"),
                        null,
                        null);
        Intent intent = new Intent(this, SymphonyHome.class);
        intent.putExtra("finish", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
        startActivity(intent);
        finish();
    }

    @Override
    public void onDistributerListSelect() {
        Log.e("MENU ITEM  ", "distributer list selected");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        DistributerList distList = new DistributerList();
        ft.replace(R.id.distHome, distList).addToBackStack("distlist").commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_RESULT) {
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                mCheckStatusListener.onGPSOK();

                Log.e("gps", resultCode + " " + requestCode + " " +
                        mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                );

            }


        } else if (requestCode == GET_DISTRIBUTER_IMAGE_REQUEST) {


            if (resultCode == RESULT_OK) {


                if (data != null) {


                    if (distInfoFrag != null)
                        distInfoFrag.setGeoLocaitonBtnEnable(true);


                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap bitmap = extras.getParcelable("data");
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte imageInByte[] = stream.toByteArray();
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                                Locale.getDefault()).format(new Date());
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss a");
                        String currentDateandTime = sdf.format(new Date()).replace(" ", "");
                        currentDateandTime = currentDateandTime.replace(".", "");
                        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        String timeStampSort = timeStampFormat.format(new Date());


                        if (prefs != null) {

                            currentDistId = prefs.getString("tdistid", null);
                            currentDistKey = prefs.getString("tdistkey", null);
                            currentDistName = prefs.getString("tdistname", null);

                        }
                        ContentValues valueOne = new ContentValues();
                        valueOne.put(DB.DIST_META_ID, currentDistId);
                        valueOne.put(DB.DIST_NAME, currentDistName);

                        valueOne.put(DB.DIST_IMG, imageInByte);
                        valueOne.put(DB.DIST_IMG_URL, currentDistId + "_" + timeStamp + ".jpg");
                        valueOne.put(DB.DIST_FLAG, 1);
                        valueOne.put(DB.DIST_TIME, currentDateandTime);
                        valueOne.put(DB.DIST_TIMESTAMP, timeStampSort);


                        if (!TextUtils.isEmpty(SMSService.addressLatLng)) {

                            String[] latlng = SMSService.addressLatLng.split(",");

                            if (latlng.length == 2) {
                                valueOne.put(DB.DIST_LAT, latlng[0]);
                                valueOne.put(DB.DIST_LNG, latlng[1]);
                            }
                        }


                        getBaseContext().getContentResolver().insert
                                (Uri.parse("content://com.symphony.database.DBProvider/addDistributerMetaData"),
                                        valueOne);


                        Toast.makeText(this,
                                "Image Saved Successfully", Toast.LENGTH_SHORT)
                                .show();


                        // delete record here


                        int count = getBaseContext().getContentResolver()
                                .delete(Uri.parse("content://com.symphony.database.DBProvider/deleteDistributerById"),
                                        DB.DIST_KEY + " = " + currentDistKey + " AND " + DB.DIST_ID + " = '" + currentDistId + "'",
                                        null);


                        Intent intent = new Intent(DistributerActivity.this, SyncManager.class);
                        intent.setAction(SyncManager.SYNC_DISTRIBUTER_DATA);
                        startService(intent);
//                        super.onBackPressed();
                        //getSupportFragmentManager().popBackStack();


                    }


                }


                // store it into database


            } else if (resultCode == RESULT_CANCELED) {
                if (distInfoFrag != null)
                    distInfoFrag.setGeoLocaitonBtnEnable(true);
                // user cancelled Image capture
                Toast.makeText(this,
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                if (distInfoFrag != null)
                    distInfoFrag.setGeoLocaitonBtnEnable(true);
                Toast.makeText(this,
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        }


    }


    @Override
    public void onOKPressed() {
        // TODO Auto-generated method stub

        Intent callGPSSettingIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(callGPSSettingIntent, GPS_RESULT);


    }

    @Override
    public void onCanclePressed() {
        // TODO Auto-generated method stub

        mCheckStatusListener.onGPSCancel(messageText);

    }

    @Override
    public void onDistributerListItemSelect(Bundle bundle) {
        // TODO Auto-generated method stub
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        distInfoFrag = new DistributerInfo();
        distInfoFrag.setArguments(bundle);
        ft.replace(R.id.distHome, distInfoFrag).addToBackStack("distinfo").commit();

    }

    @Override
    public void onGPSDialogOpen(String msgText) {
        // TODO Auto-generated method stub
        messageText = msgText;
        showDialog(msgText);
    }

    private void showDialog(String msgText) {
        DialogFragment newFragment = DialogAlert.newInstance(R.string.gps_dialog_text);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onSettingsSelect() {
        // TODO Auto-generated method stub
        Intent intent = new Intent(DistributerActivity.this, SymphonyReport.class);
        startActivity(intent);
    }

    @Override
    public void onCameraImage(String distId, String distKey, String distName) {
        // TODO Auto-generated method stub


        currentDistId = distId;
        currentDistName = distName;
        if (prefs != null) {

            edit = prefs.edit();
            edit.putString("tdistid", distId);
            edit.putString("tdistkey", distKey);
            edit.putString("tdistname", distName);

            edit.commit();

        }

        //Toast.makeText(this, currentDistId, Toast.LENGTH_LONG).show();
        Log.e("IDS", distId + " " + distKey);
        getCameraImage();

    }

    public void getCameraImage() {


        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, GET_DISTRIBUTER_IMAGE_REQUEST);

        } else {
            Toast.makeText(this, "Camera is not supported on this device", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        createLocationRequest();
        startLocationUpdates();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null)
            SMSService.location = location;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * Asynctask for logout
     */
    private class AsyncLogout extends AsyncTask<String, Void, Boolean> {

        private WSLogout wsLogout;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = SymphonyUtils.displayProgressDialog(DistributerActivity.this,"Loading...");
        }

        @Override
        protected Boolean doInBackground(String... params) {
            wsLogout = new WSLogout();
            return wsLogout.executeTown(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            SymphonyUtils.dismissProgressDialog(progressDialog);
            if (aBoolean) {
                signOut();
            } else {
                SymphonyUtils.displayDialog(DistributerActivity.this, getString(R.string.app_name), wsLogout.getMessage());
            }
        }
    }

    private void displayDialog(final Context context, final String title, final String message) {

        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        if (title == null)
            alertDialog.setTitle(context.getString(R.string.app_name));
        else
            alertDialog.setTitle(title);
        alertDialog.setCancelable(false);

        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sync Now", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                setSyncCheckStatusAlarm(DistributerActivity.this);
                dialog.dismiss();

            }
        });
        if (!((Activity) context).isFinishing()) {

            alertDialog.show();
        }
    }

    private void setSyncCheckStatusAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alramReceiverIntent = new Intent(context, SyncAlaram.class);
        alramReceiverIntent.setAction(SyncAlaram.DB_CHECK_FOR_DIST_PHOTO);
        PendingIntent alramPendingIntent = PendingIntent.getBroadcast(context, 0, alramReceiverIntent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    calendar.getTimeInMillis(), alramPendingIntent);
        } else {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    calendar.getTimeInMillis(), alramPendingIntent);
        }
    }

}
