package com.symphony.distributer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.symphony.E_Sampark;
import com.symphony.R;
import com.symphony.http.WSGetDeletedMasterData;
import com.symphony.http.WSGetMasterData;
import com.symphony.model.MasterDataModel;
import com.symphony.receiver.AlarmReceiver;
import com.symphony.sms.SMSService;
import com.symphony.utils.Const;
import com.symphony.utils.SymphonyUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class CheckStatus extends Fragment implements CheckStatusListener, LocationListener, SMSService.OnStatusFailedListner, OnClickListener {

    private Button checkStatus;
    private TextView txtMessage;
    private TextView txtCheckINOUTLabel;
    private DistributerActivityListener mDistributerListener;
    private LocationManager mLocationManager;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    //    private LocationFailedReceiver locationFailedReceiver;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long TIME_DIFFERENCE = 1000 * 60 * 10;
    // private static final long TIME_DIFFERENCE = 1000 * 60 * 1;
    private E_Sampark e_sampark;
    private ProgressDialog progressDialog;
    private FloatingActionButton fbSyncMasterData;
    private AsyncLoadMasterData asyncLoadMasterData;
    private AsyncGetDeletedMasterData asyncGetDeletedMasterData;

    @Override
    public void onStatusFailed() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDistributerListener = (DistributerActivityListener) getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        e_sampark = (E_Sampark) getActivity().getApplicationContext();
        setHasOptionsMenu(true);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        View v = inflater.inflate(R.layout.checkstatus_fragment, null);
        checkStatus = (Button) v.findViewById(R.id.checkStatus);
        txtMessage = (TextView) v.findViewById(R.id.txtStatusLabel);
        txtCheckINOUTLabel = (TextView) v.findViewById(R.id.checkStatusText);
        fbSyncMasterData = (FloatingActionButton) v.findViewById(R.id.fabSynCMasterData);
        prefs = e_sampark.getSharedPreferences();
        editor = prefs.edit();
        checkStatus.setOnClickListener(this);
        fbSyncMasterData.setOnClickListener(this);
        return v;
    }


    public void setFirstTime() {

        if (editor != null) {
            editor.putBoolean("isFirstTime", false);
            editor.commit();
        }
    }

    public void setDistStatus(boolean status) {
        Log.e("SETTING STATUS >>>>", status + "");
        if (editor != null) {
            editor.putBoolean("curr_status", status);
            editor.commit();
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {

        menu.findItem(R.id.distributer_refresh).setVisible(false);
        menu.findItem(R.id.distributer_search).setVisible(false);
    }


    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        getActivity().registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("ENABLE_BUTTON"));
        getActivity().registerReceiver(checkinoutFailedReceiver, new IntentFilter("com.symphony.CHECKINOUTFAIL"));

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("CHECK IN/OUT");

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false); // disable the button
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false); // remove the left caret
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);

        IntentFilter intentFilter = new IntentFilter(SMSService.GEO_LOCATION_FAILED);
        intentFilter.addAction(SMSService.GEO_LOCATION_FAILED);
        intentFilter.addAction(DistributerActivity.LOCATION_RECEIVER);

//        this.getActivity().registerReceiver(locationFailedReceiver, intentFilter);
        if (!e_sampark.getSharedPreferences().getBoolean("ISENABLE", false)) {
            long diff = Calendar.getInstance().getTimeInMillis() - e_sampark.getSharedPreferences().getLong("TIME", 0);
            if (diff > 0 && diff > TIME_DIFFERENCE) {
                checkStatus.setEnabled(true);
                checkStatus.setVisibility(View.VISIBLE);
                txtMessage.setVisibility(View.GONE);
                txtCheckINOUTLabel.setVisibility(View.VISIBLE);
            } else {
                checkStatus.setEnabled(false);
                checkStatus.setVisibility(View.GONE);
                txtMessage.setVisibility(View.VISIBLE);
                txtCheckINOUTLabel.setVisibility(View.GONE);
            }
        } else {
            checkStatus.setEnabled(true);
            checkStatus.setVisibility(View.VISIBLE);
            txtMessage.setVisibility(View.GONE);
            txtCheckINOUTLabel.setVisibility(View.VISIBLE);
        }

        if (e_sampark.getSharedPreferences().getString("TAG", Const.CHECKIN).equalsIgnoreCase(Const.CHECKIN)) {
            setCheckIn();
        } else {
            setCheckOut();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("CHECK IN/OUT");
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(broadcastReceiver);
        getActivity().unregisterReceiver(tickReceiver);
        getActivity().unregisterReceiver(checkinoutFailedReceiver);
    }

    @Override
    public void onGPSCancel(String messageText) {
        // TODO Auto-generated method stub
        Toast.makeText(getActivity(), messageText, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onGPSOK() {
        // TODO Auto-generated method stub
        Toast.makeText(getActivity(), " GPS is enabled", Toast.LENGTH_SHORT).show();
    }


    private void setCheckOut() {
        checkStatus.setBackgroundResource(R.drawable.button_red);
        checkStatus.setText("CHECK OUT");
        checkStatus.setTag(Const.CHECKOUT);
        checkStatus.setTextColor(Color.WHITE);
        setDistStatus(false);
        setFirstTime();
    }

    private void setCheckIn() {
        checkStatus.setBackgroundResource(R.drawable.button_green);
        checkStatus.setText("CHECK IN");
        checkStatus.setTag(Const.CHECKIN);

        setDistStatus(true);
        setFirstTime();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(AlarmReceiver.class.getSimpleName(), "Call1");
            if (intent.getAction().equalsIgnoreCase("ENABLE_BUTTON")) {
                Log.e(AlarmReceiver.class.getSimpleName(), "Call2");
                checkStatus.setEnabled(true);
                checkStatus.setVisibility(View.VISIBLE);
                txtMessage.setVisibility(View.GONE);
                txtCheckINOUTLabel.setVisibility(View.VISIBLE);
                if (e_sampark.getSharedPreferences().getString("TAG", Const.CHECKIN).equalsIgnoreCase(Const.CHECKIN)) {
                    //btnClick.setText("CheckIN");
                    checkStatus.setTag(Const.CHECKIN);
                } else {
                    //btnClick.setText("CheckOUT");
                    checkStatus.setTag(Const.CHECKOUT);
                }
            }

        }
    };

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            SMSService.addressLatLng = location.getLatitude() + "," + location.getLongitude();
            Toast.makeText(getActivity(), "CHECK STATUS - " + SMSService.addressLatLng, Toast.LENGTH_LONG).show();
            mLocationManager.removeUpdates(this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    BroadcastReceiver tickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                Log.e(CheckStatus.class.getSimpleName(), "Time  Tick Call");
                if (!e_sampark.getSharedPreferences().getBoolean("ISENABLE", true)) {
                    long diff = Calendar.getInstance().getTimeInMillis() - e_sampark.getSharedPreferences().getLong("TIME", 0);
                    if (diff > 0 && diff > TIME_DIFFERENCE) {
                        checkStatus.setEnabled(true);
                        checkStatus.setVisibility(View.VISIBLE);
                        txtMessage.setVisibility(View.GONE);
                        txtCheckINOUTLabel.setVisibility(View.VISIBLE);
                    } else {
                        checkStatus.setEnabled(false);
                        checkStatus.setVisibility(View.GONE);
                        txtMessage.setVisibility(View.VISIBLE);
                        txtCheckINOUTLabel.setVisibility(View.GONE);
                    }
                } else {
                    checkStatus.setEnabled(true);
                    checkStatus.setVisibility(View.VISIBLE);
                    txtMessage.setVisibility(View.GONE);
                    txtCheckINOUTLabel.setVisibility(View.VISIBLE);
                }


                if (e_sampark.getSharedPreferences().getString("TAG", Const.CHECKIN).equalsIgnoreCase(Const.CHECKIN)) {
                    setCheckIn();
                } else {
                    setCheckOut();
                }
            }
        }
    };


    BroadcastReceiver checkinoutFailedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equalsIgnoreCase("com.symphony.CHECKINOUTFAIL")) {
                    checkStatus.setVisibility(View.VISIBLE);
                    checkStatus.setEnabled(true);

                    if (e_sampark.getSharedPreferences().getString("TAG", Const.CHECKIN).equalsIgnoreCase(Const.CHECKIN)) {
                        setCheckIn();
                    } else {
                        setCheckOut();
                    }
                }

            }
        }
    };

    @Override
    public void onClick(View view) {
        if (view == fbSyncMasterData) {
            if (SymphonyUtils.isNetworkAvailable(getActivity())) {
                asyncLoadMasterData = new AsyncLoadMasterData();
                asyncLoadMasterData.execute();
            } else {
                SymphonyUtils.displayDialog(getActivity(), getString(R.string.app_name), "Please Check Internet Connection");
            }

        } else if (view == checkStatus) {
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                mDistributerListener.onGPSDialogOpen("Can not CHECK IN/OUT , because GPS is disabled");
            } else {
                if (SymphonyUtils.isAutomaticDateTime(getActivity())) {
                    if (SMSService.addressLatLng == null || TextUtils.isEmpty(SMSService.addressLatLng)) {
                        Toast.makeText(getActivity(), "Not able to get the geocode , please try after a while", Toast.LENGTH_SHORT).show();
                        return;
                    } else {

                        if (!SymphonyUtils.isFackLocation(getActivity(), SMSService.location)) {
                            Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {

                                Calendar calendar = Calendar.getInstance();
                                checkStatus.setEnabled(false);
                                checkStatus.setVisibility(View.GONE);
                                txtMessage.setVisibility(View.VISIBLE);
                                txtCheckINOUTLabel.setVisibility(View.GONE);
                                SharedPreferences.Editor editor = e_sampark.getSharedPreferences().edit();
                                editor.putBoolean("ISENABLE", false);
                                editor.putLong("TIME", calendar.getTimeInMillis());
                                editor.putLong("COUNTDOWNTIMER", TIME_DIFFERENCE);
                                editor.commit();


                                if (checkStatus.getTag().toString().equalsIgnoreCase(Const.CHECKIN)) {
                                    editor.putString("TAG", Const.CHECKOUT);
                                    Intent intentService = new Intent(getActivity(), SMSService.class);
                                    intentService.setAction(SMSService.SEND_CHECK_SMS_INTENT);
                                    intentService.putExtra("checkstatus", true);
                                    getActivity().startService(intentService);
                                    setCheckOut();
                                } else {
                                    editor.putString("TAG", Const.CHECKIN);
                                    Intent intentService = new Intent(getActivity(), SMSService.class);
                                    intentService.setAction(SMSService.SEND_CHECK_SMS_INTENT);
                                    intentService.putExtra("checkstatus", false);
                                    getActivity().startService(intentService);
                                    setCheckIn();
                                }
                                editor.commit();
                            } else {
                                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, MIN_DISTANCE_CHANGE_FOR_UPDATES, CheckStatus.this);
                                Toast.makeText(getActivity(), "Not able to get the geocode , please try after a while", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Please Disable Fake Location", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(getActivity(), "Please Make date & Time setting to automatic mode", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class AsyncLoadMasterData extends AsyncTask<String, Void, ArrayList<MasterDataModel>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = SymphonyUtils.displayProgressDialog(getActivity(), "Loading...");
        }

        @Override
        protected ArrayList<MasterDataModel> doInBackground(String... strings) {
            WSGetMasterData wsGetMasterData = new WSGetMasterData();
            return wsGetMasterData.executeTown(SymphonyUtils.getDateTime(e_sampark.getSharedPreferences_masterdata().getString(Const.PREF_LAST_DATETIME, "")), getActivity());
        }

        @Override
        protected void onPostExecute(ArrayList<MasterDataModel> masterDataModels) {
            super.onPostExecute(masterDataModels);

            if (masterDataModels != null && masterDataModels.size() > 0) {
                e_sampark.getSymphonyDB().insertMasterData(masterDataModels);
            }
            asyncGetDeletedMasterData = new AsyncGetDeletedMasterData();
            asyncGetDeletedMasterData.execute();
        }
    }

    private class AsyncGetDeletedMasterData extends AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            WSGetDeletedMasterData wsGetDeletedMasterData = new WSGetDeletedMasterData();
            return wsGetDeletedMasterData.executeTown(SymphonyUtils.getDateTime(e_sampark.getSharedPreferences_masterdata().getString(Const.PREF_LAST_DATETIME, "")), getActivity());
        }

        @Override
        protected void onPostExecute(ArrayList<String> masterIds) {
            super.onPostExecute(masterIds);

            if (masterIds != null && masterIds.size() > 0) {
                for (int i = 0; i < masterIds.size(); i++) {
                    e_sampark.getSymphonyDB().deleteMasterData(masterIds.get(i));
                }
            }
            SymphonyUtils.dismissProgressDialog(progressDialog);
        }
    }

}
