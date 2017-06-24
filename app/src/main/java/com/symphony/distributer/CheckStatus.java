package com.symphony.distributer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.symphony.E_Sampark;
import com.symphony.R;
import com.symphony.dealerList.DealerListActivity;
import com.symphony.http.WSGetDeletedMasterData;
import com.symphony.http.WSGetMasterData;
import com.symphony.model.MasterDataModel;
import com.symphony.receiver.AlarmReceiver;
import com.symphony.sms.SMSService;
import com.symphony.utils.Const;
import com.symphony.utils.SymphonyUtils;
import com.symphony.utils.WriteLog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

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
    //private static final long TIME_DIFFERENCE = 1000 * 60 * 10;
    private static final long TIME_DIFFERENCE = 1000 * 60 * 1;
    private E_Sampark e_sampark;
    private ProgressDialog progressDialog;
    private FloatingActionButton fbSyncMasterData;
    private FloatingActionButton fbDealerList;
    private FloatingActionButton fbAdd;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private AsyncLoadMasterData asyncLoadMasterData;
    private AsyncGetDeletedMasterData asyncGetDeletedMasterData;
    private Boolean isFabOpen = false;

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
        fbAdd = (FloatingActionButton) v.findViewById(R.id.fbadd);
        fbDealerList = (FloatingActionButton) v.findViewById(R.id.fabDealerlist);
        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_backward);


        prefs = e_sampark.getSharedPreferences();
        editor = prefs.edit();
        checkStatus.setOnClickListener(this);
        fbSyncMasterData.setOnClickListener(this);
        fbAdd.setOnClickListener(this);
        fbDealerList.setOnClickListener(this);
        return v;
    }

    public void animateFAB() {

        if (isFabOpen) {

            fbAdd.startAnimation(rotate_backward);
            fbSyncMasterData.startAnimation(fab_close);
            fbDealerList.startAnimation(fab_close);
            fbSyncMasterData.setClickable(false);
            fbDealerList.setClickable(false);
            isFabOpen = false;
            Log.d("Raj", "close");

        } else {

            fbAdd.startAnimation(rotate_forward);
            fbSyncMasterData.startAnimation(fab_open);
            fbDealerList.startAnimation(fab_open);
            fbSyncMasterData.setClickable(true);
            fbDealerList.setClickable(true);
            isFabOpen = true;
            Log.d("Raj", "open");

        }
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
                                AsyncGetNearbyDealer asyncGetNearbyDealer = new AsyncGetNearbyDealer();
                                asyncGetNearbyDealer.execute();

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
        } else if (view == fbAdd) {
            animateFAB();
        } else if (view == fbDealerList) {
            Intent intent = new Intent(getActivity(), DealerListActivity.class);
            startActivity(intent);
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
            ArrayList<MasterDataModel> masterDataModels = wsGetMasterData.executeTown(SymphonyUtils.getDateTime(e_sampark.getSharedPreferences_masterdata().getString(Const.PREF_LAST_DATETIME, "")), prefs.getString("usermobilenumber", ""), getActivity());
            if (masterDataModels != null && masterDataModels.size() > 0) {
                e_sampark.getSymphonyDB().insertMasterData(masterDataModels);
                e_sampark.getSharedPreferences_masterdata().edit().putBoolean(Const.PREF_IS_LOAD_MASTER_DATA_FIRSTTIME, false).commit();
            }
            return masterDataModels;
        }

        @Override
        protected void onPostExecute(ArrayList<MasterDataModel> masterDataModels) {
            super.onPostExecute(masterDataModels);
            new AsyncGetDeletedMasterData().execute();
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
            ArrayList<String> masterIds = wsGetDeletedMasterData.executeTown(SymphonyUtils.getDateTime(e_sampark.getSharedPreferences_masterdata().getString(Const.PREF_LAST_DATETIME, "")), prefs.getString("usermobilenumber", ""), getActivity());
            if (masterIds != null && masterIds.size() > 0) {
                for (int i = 0; i < masterIds.size(); i++) {
                    e_sampark.getSymphonyDB().deleteMasterData(masterIds.get(i));
                }
            }
            return masterIds;
        }

        @Override
        protected void onPostExecute(ArrayList<String> masterIds) {
            super.onPostExecute(masterIds);
            SymphonyUtils.dismissProgressDialog(progressDialog);
        }
    }

    private class AsyncGetNearbyDealer extends AsyncTask<Void, Void, Void> {
        private ArrayList<Double> closestDistanceList;
        private ArrayList<MasterDataModel> masterDataList;
        private HashMap<Double, MasterDataModel> hasmapList;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = SymphonyUtils.displayProgressDialog(getActivity(), "Loading....");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            /**
             * Algoridham for finding closest branch from check in location
             */
            masterDataList = new ArrayList<>();
            closestDistanceList = new ArrayList<>();
            hasmapList = new HashMap<>();
            masterDataList = e_sampark.getSymphonyDB().getMasterDataList();
            Location currentLocation = SMSService.location;
            if (masterDataList != null && masterDataList.size() > 0) {

                for (int i = 0; i < masterDataList.size(); i++) {
                    if (!TextUtils.isEmpty(masterDataList.get(i).getLat()) && !TextUtils.isEmpty(masterDataList.get(i).getLang())) {
                        Location destination = new Location("");
                        destination.setLatitude(Double.parseDouble(masterDataList.get(i).getLat()));
                        destination.setLongitude(Double.parseDouble(masterDataList.get(i).getLang()));
//                        float distanceInMeters = currentLocation.distanceTo(destination);

                        LatLng latLng_src = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        LatLng latLng_dest = new LatLng(Double.parseDouble(masterDataList.get(i).getLat()), Double.parseDouble(masterDataList.get(i).getLang()));
                        double distanceInMeters = SymphonyUtils.calculationByDistance(latLng_src, latLng_dest);
                        if (distanceInMeters < e_sampark.getSharedPreferences().getInt(Const.PREF_CHECKIN_METER, Const.DEFAULT_CHECKIN_METER)) {
                            hasmapList.put(distanceInMeters, masterDataList.get(i));
                            closestDistanceList.add(distanceInMeters);
                        }
                        WriteLog.E(SMSService.class.getSimpleName(), "Distance2 = " + currentLocation.getLongitude() + "," + currentLocation.getLatitude());
                        WriteLog.E(SMSService.class.getSimpleName(), "Distance1 = " + masterDataList.get(i).getLang() + "," + masterDataList.get(i).getLat());
                        WriteLog.E(SMSService.class.getSimpleName(), "Distance = " + distanceInMeters + " - " + masterDataList.get(i).getName());

                    }
                }

                //sorting array for getting close distance
                Collections.sort(closestDistanceList);


            }
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            SymphonyUtils.dismissProgressDialog(progressDialog);
            if (closestDistanceList != null && closestDistanceList.size() > 0) {
                ArrayList<String> dealerlatlongIds = new ArrayList<>();
                String[] dealername = new String[closestDistanceList.size()];
                for (int i = 0; i < closestDistanceList.size(); i++) {
                    dealername[i] = hasmapList.get(closestDistanceList.get(i)).getName();
                    dealerlatlongIds.add(hasmapList.get(closestDistanceList.get(i)).getDealerletlongid());
                }
                if (checkStatus.getTag().toString().equalsIgnoreCase(Const.CHECKOUT)) {

                    if (e_sampark.getSharedPreferences().getString(Const.PREF_VISIT_CHECKIN_DATE, "").equalsIgnoreCase(SymphonyUtils.getCurrentDate())) {
                        if (!dealerlatlongIds.contains(e_sampark.getSharedPreferences().getString(Const.PREF_CHECKIN_DEALERLATLONGID, ""))) {
                            showAlertForCheckout(getActivity(), "You are not checkout from check-In dealer,Do you want to go back or cancel visit");
                            return;
                        } else {
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
                            editor.putString("TAG", Const.CHECKIN);
                            Intent intentService = new Intent(getActivity(), SMSService.class);
                            intentService.setAction(SMSService.SEND_CHECK_SMS_INTENT);
                            intentService.putExtra("checkstatus", false);
                            intentService.putExtra("dealerlatlongid", e_sampark.getSharedPreferences().getString(Const.PREF_CHECKIN_DEALERLATLONGID, ""));
                            getActivity().startService(intentService);
                            setCheckIn();
                            editor.commit();
                        }
                    } else {
                        showAlertForVisitCancel(getActivity(), "Your visit is cancelled due to not checkout on Same Day.");
                    }


                } else {
                    new AlertDialog.Builder(getActivity())
                            .setSingleChoiceItems(dealername, 0, null)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                    int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                    if (checkStatus.getTag().toString().equalsIgnoreCase(Const.CHECKIN)) {
                                        MasterDataModel masterDataModel = hasmapList.get(closestDistanceList.get(selectedPosition));
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
                                            intentService.putExtra("dealerlatlongid", masterDataModel.getDealerletlongid());
                                            getActivity().startService(intentService);
                                            setCheckOut();
                                            e_sampark.getSharedPreferences().edit().putString(Const.PREF_CHECKIN_DEALERLATLONGID, masterDataModel.getDealerletlongid()).commit();
                                            e_sampark.getSharedPreferences().edit().putString(Const.PREF_VISIT_UNIQKEY, "" + System.currentTimeMillis()).commit();
                                            e_sampark.getSharedPreferences().edit().putString(Const.PREF_VISIT_CHECKIN_DATE, SymphonyUtils.getCurrentDate()).commit();
                                        }
                                        editor.commit();
                                    }
                                    // Do something useful withe the position of the selected radio button
                                }
                            })
                            .show();
                }
            } else {
                Toast.makeText(getActivity(), "No nearby dealer available", Toast.LENGTH_LONG).show();
                if (checkStatus.getTag().toString().equalsIgnoreCase(Const.CHECKIN)) {
                    Intent intentService = new Intent(getActivity(), SMSService.class);
                    intentService.setAction(SMSService.SEND_CHECK_SMS_INTENT);
                    intentService.putExtra("checkstatus", true);
                    intentService.putExtra("dealerlatlongid", "");
                    getActivity().startService(intentService);
                } else {
                    Intent intentService = new Intent(getActivity(), SMSService.class);
                    intentService.setAction(SMSService.SEND_CHECK_SMS_INTENT);
                    intentService.putExtra("checkstatus", false);
                    intentService.putExtra("dealerlatlongid", "");
                    getActivity().startService(intentService);
                }
            }
        }
    }

    public void showAlertForCheckout(Context context, final String message) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.app_name))
                .setCancelable(false)
                .setIcon(R.drawable.ic_launcher)
                .setMessage(message)
                .setPositiveButton("GoBack", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel Visit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                e_sampark.getSharedPreferences().edit().putLong("TIME", 0).commit();
                if (e_sampark.getSharedPreferences().getString("TAG", Const.CHECKIN).equalsIgnoreCase(Const.CHECKIN)) {
                    e_sampark.getSharedPreferences().edit().putString("TAG", Const.CHECKOUT).commit();
                } else {
                    e_sampark.getSharedPreferences().edit().putString("TAG", Const.CHECKIN).commit();
                }
                setCheckIn();
                dialog.dismiss();
            }
        })
                .setIcon(R.drawable.ic_launcher)
                .show();
    }

    //Show alert for cancel visit once date is change
    public void showAlertForVisitCancel(Context context, final String message) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.app_name))
                .setCancelable(false)
                .setIcon(R.drawable.ic_launcher)
                .setMessage(message)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        e_sampark.getSharedPreferences().edit().putLong("TIME", 0).commit();
                        if (e_sampark.getSharedPreferences().getString("TAG", Const.CHECKIN).equalsIgnoreCase(Const.CHECKIN)) {
                            e_sampark.getSharedPreferences().edit().putString("TAG", Const.CHECKOUT).commit();
                        } else {
                            e_sampark.getSharedPreferences().edit().putString("TAG", Const.CHECKIN).commit();
                        }
                        setCheckIn();
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_launcher)
                .show();
    }

}
