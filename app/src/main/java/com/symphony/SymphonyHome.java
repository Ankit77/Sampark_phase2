package com.symphony;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.symphony.distributer.DistributerActivity;
import com.symphony.register.RegisterFragment;
import com.symphony.sms.SMSService;
import com.symphony.utils.SymphonyUtils;
import com.symphony.utils.WriteLog;

import org.apache.commons.net.io.Util;

import java.io.IOException;


public class SymphonyHome extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {

//extends ActionBarActivity{

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;


    private SharedPreferences prefs;
    private LocationManager mLocationManager;
    private GoogleApiClient googleApiClient;
    private E_Sampark e_sampark;
    private AsyncRegisterGCM asyncRegisterGCM;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.symphony_home);
        e_sampark = (E_Sampark) getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        //this.getSupportActionBar().setCustomView(R.layout.home_actionbar);
        this.getSupportActionBar().setDisplayShowHomeEnabled(false);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.getSupportActionBar().setDisplayUseLogoEnabled(false);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        this.getSupportActionBar().setIcon(android.R.color.transparent);
        prefs = e_sampark.getSharedPreferences();
        asyncRegisterGCM = new AsyncRegisterGCM();
        asyncRegisterGCM.execute();

    }

    private void loadData() {
        Intent intentLocationService = new Intent(this, SMSService.class);
        intentLocationService.setAction(SMSService.FETCH_LOCATION_INTENT);
        startService(intentLocationService);
        final String regId = SymphonyGCMHome.getGCMRegistrationId(SymphonyHome.this);
        if (!TextUtils.isEmpty(regId)) {
            boolean isRegister = prefs.getBoolean("isregister", false);
            if (!isRegister) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.homeFragment, new RegisterFragment()).commitAllowingStateLoss();
            } else {
                Intent intent = new Intent(this, DistributerActivity.class);
                startActivity(intent);
                finish();
            }
            FragmentManager fm = getSupportFragmentManager();
            fm.removeOnBackStackChangedListener(new OnBackStackChangedListener() {

                @Override
                public void onBackStackChanged() {
                    // TODO Auto-generated method stub
                    Log.e("REMOVED ", getFragmentManager().getBackStackEntryCount() + "");
                }


            });
            fm.addOnBackStackChangedListener(new OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    // if(getFragmentManager().getBackStackEntryCount() == 0) finish();
                    Log.e("ADD ", getFragmentManager().getBackStackEntryCount() + "");
                }
            });

        } else {
            SymphonyUtils.displayDialog(SymphonyHome.this, getString(R.string.app_name), "Something went wrong.Please try again");
        }
    }

    @Override
    public void onBackPressed() {
        boolean isRegister = prefs.getBoolean("isregister", false);
        if (isRegister) {
            getSupportFragmentManager().popBackStack();
            finish();
            System.exit(1);
        } else {
            super.onBackPressed();
        }
    }


    public void locationChecker(GoogleApiClient mGoogleApiClient, final Activity activity) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(this);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(SymphonyHome.class.getSimpleName(), "All location settings are satisfied.");
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(SymphonyHome.class.getSimpleName(), "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(SymphonyHome.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(SymphonyHome.class.getSimpleName(), "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(SymphonyHome.class.getSimpleName(), "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                loadData();
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(SymphonyHome.class.getSimpleName(), "User agreed to make required location settings changes.");
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(SymphonyHome.class.getSimpleName(), "User chose not to make required location settings changes.");
                        break;

                }
                break;
        }
    }

    private class AsyncRegisterGCM extends AsyncTask<Void, Void, String> {
        private String regId = "";
        private GoogleCloudMessaging gcm;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = SymphonyUtils.displayProgressDialog(SymphonyHome.this);
        }

        @Override
        protected String doInBackground(Void... params) {
            if (TextUtils.isEmpty(SymphonyGCMHome.getRegistrationId(SymphonyHome.this))) {
                gcm = GoogleCloudMessaging.getInstance(SymphonyHome.this);
                try {
                    regId = gcm.register(SymphonyUtils.GCM_KEY);
                    SymphonyGCMHome.setRegistrationId(SymphonyHome.this, regId);
                } catch (IOException e) {
                    e.printStackTrace();
                    regId = "";
                }
            }
            Log.e(SymphonyGCMHome.class.getSimpleName(), "REGID" + regId);
            return regId;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            SymphonyUtils.dismissProgressDialog(progressDialog);
            if (!mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                googleApiClient = new GoogleApiClient
                        .Builder(SymphonyHome.this)
                        .enableAutoManage(SymphonyHome.this, 34992, SymphonyHome.this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(SymphonyHome.this)
                        .addOnConnectionFailedListener(SymphonyHome.this)
                        .build();
                locationChecker(googleApiClient, SymphonyHome.this);
            } else {
                loadData();
            }
        }
    }
}
