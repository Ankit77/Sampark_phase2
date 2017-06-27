package com.symphony.distributer;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.symphony.E_Sampark;
import com.symphony.R;
import com.symphony.database.DB;
import com.symphony.sms.SMSService;
import com.symphony.sms.SyncManager.DISTRIBUTER_META_DATA;

import org.apache.http.util.TextUtils;

import java.io.ByteArrayOutputStream;

public class DistributerInfo extends Fragment {

    private TextView distInfoName;
    private TextView distInfoContactName;
    private TextView distInfoAddr;
    private Button getGeocodeBtn;
    private LocationManager mLocationManager;
    private DistributerListListener mDistributerListListener;
    private DistributerActivityListener mDistributerListener;
    private LocationFailedReceiver locationFailedReceiver;
    public static boolean isDeleted = false;
    public ImageView imageWindow;

    public Button previewBtn;
    public Button deleteBtn;
    private E_Sampark e_sampark;

    public class LocationFailedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            if (intent != null) {

                if (intent.getAction() == SMSService.GEO_LOCATION_FAILED) {

                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {

                        boolean status = bundle.getBoolean("locationstatus");

                        if (status)
                            Toast.makeText(getActivity(), "Not able to get the geocode , please try after a while " + status, Toast.LENGTH_LONG).show();
                    }


                }
            }

        }


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        e_sampark = (E_Sampark) getActivity().getApplicationContext();
        final Bundle bundle = this.getArguments();
        isDeleted = false;
        mDistributerListener = (DistributerActivityListener) getActivity();

        locationFailedReceiver = new LocationFailedReceiver();


        if (bundle != null) {


            distInfoName = (TextView) getActivity().findViewById(R.id.distInfoName);
            distInfoName.setText((String) bundle.get("distname"));


            distInfoAddr = (TextView) getActivity().findViewById(R.id.distInfoAddr);
            distInfoAddr.setText(((String) bundle.get("distaddr")).toLowerCase());

            Log.e("Button set", checkDistStatus((String) bundle.get("distkey")) + " " + distInfoAddr.getText().toString());


            deleteBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    new Thread(new Runnable() {

                        @Override
                        public void run() {


                            int deletedRows = getActivity().getBaseContext().getContentResolver().
                                    delete(Uri.parse("content://com.symphony.database.DBProvider/getDistributerMetaData"),
                                            null,
                                            null
                                    );
                            //	Toast.makeText(getActivity(), "Rows deleted "+ deletedRows, Toast.LENGTH_LONG).show();
                        }
                    }).start();


                }


            });
            previewBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub


                    Cursor cur = getActivity().getBaseContext().getContentResolver().
                            query(Uri.parse("content://com.symphony.database.DBProvider/getDistributerMetaData"),
                                    DISTRIBUTER_META_DATA.PROJECTION,
                                    null,
                                    null,
                                    null);


                    //	Toast.makeText(getActivity(), "Rows get "+ cur.getCount(), Toast.LENGTH_LONG).show();

                    if (cur.getCount() != 0) {


                        cur.moveToLast();


                        byte[] imgByteArry = cur.getBlob(cur.getColumnIndexOrThrow(DB.DIST_IMG));
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;

                        Bitmap bm = BitmapFactory.decodeByteArray(imgByteArry, 0, imgByteArry.length, options);

                        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);


                        imageWindow.setImageBitmap(bm);

									
									/*
                                     BitmapFactory.Options options = new BitmapFactory.Options();
								 	 
							            
							            options.inSampleSize = 8;
							 
							            final Bitmap bitmap = BitmapFactory.decodeFile(cur.getString(cur.getColumnIndex(DB.DIST_IMG_URL)),
							                    options);
							 
							            imageWindow.setImageBitmap(bitmap);*/


                        cur.close();
                    }


                }

            });

            getGeocodeBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                        mDistributerListener.onGPSDialogOpen("Can not get GEO LOCATION , because GPS is disabled");

                    } else {
//				            Log.e("DISTRIBUTER'S CO-ORDINATES", DistributerActivity.currentLocation+"");
//		                    Log.e("BG  CO-ORDINATES", SMSService.addressLatLng+"");

                        if (SMSService.addressLatLng != null && !TextUtils.isEmpty(SMSService.addressLatLng)) {

                            setGeoLocaitonBtnEnable(false);
                            mDistributerListener.onCameraImage((String) bundle.get("distid"), (String) bundle.get("distkey"), (String) bundle.get("distname"));
								

								/*Intent intentService = new Intent(getActivity(),SMSService.class);
								intentService.setAction(SMSService.SEND_GEO_SMS_INTENT);
								intentService.putExtra("distid" ,(String)bundle.get("distid"));
								intentService.putExtra("distkey" ,(String)bundle.get("distkey"));
								getActivity().startService(intentService);
								*/


                            isDeleted = true;

                        } else {


                            Toast.makeText(getActivity(), "Not able to get the geocode , please try after a while", Toast.LENGTH_LONG).show();
                        }


                    }


                    //	mDistributerListListener.onListItemRemoved((String)bundle.get("distkey") ,(String)bundle.get("distid") );

                }


            });
        }

        //		Log.e("INFO NAME " , bundle.get("distname")+"");


    }


    public boolean checkDistStatus(String distKey) {

        SharedPreferences prefs = e_sampark.getSharedPreferences();


        boolean currentStatus = prefs.getBoolean("curr_status", false);
        String currentDistKey = prefs.getString("curr_distkey", "");


        Log.e("matched key", distKey + " " + currentDistKey);


        if (currentDistKey.equals(distKey))

            return !currentStatus;

        else
            return true;

    }

    public boolean setCheckInStatus(boolean status, String distName, String distKey, String distId) {


        SharedPreferences prefs = e_sampark.getSharedPreferences();

        boolean currentStatus = prefs.getBoolean("curr_status", false);
        String currentDistKey = prefs.getString("curr_distkey", "");


        if (currentDistKey.equals(distKey) && status) {


            Log.e("matched key", distKey + " " + currentDistKey + " " + status);

            Editor edit = prefs.edit();
            edit.putBoolean("curr_status", false); // lock is free
            edit.putString("curr_distname", "");
            edit.putString("curr_distid", "");
            edit.putString("curr_distkey", null);

            edit.commit();

            return true;

        } else if (currentStatus == false) { // create a lock


            Editor edit = prefs.edit();
            edit.putBoolean("curr_status", true);
            edit.putString("curr_distname", distName);
            edit.putString("curr_distid", "");
            edit.putString("curr_distkey", distKey);

            edit.commit();
            Log.e("not matched key", distKey + " " + currentDistKey + " " + status);


            return true;

        } else {


            Toast.makeText(this.getActivity(), "Please checkout from " + prefs.getString("curr_distname", ""), Toast.LENGTH_SHORT).show();

        }

        return false;
		/*Log.e("STATUS - > " , status+"");
		// true means its trying to check in 
		if(status) {
			
			if(currentStatus){
				
				
				StringBuilder currentDist = new StringBuilder();
				
				currentDist.append(String.valueOf(prefs.getBoolean("curr_status",false)));
				currentDist.append(prefs.getString("curr_distname",""));
				currentDist.append(prefs.getString("curr_distid",""));
				currentDist.append(prefs.getString("curr_distkey",""));
				
			
				Toast.makeText(this.getActivity(),"Please checkout from " + currentDist.toString() ,Toast.LENGTH_SHORT).show();
				
				
				return false;
			
				
				
			}else{
				
				Editor edit  = prefs.edit();
				
				
				edit.putBoolean("curr_status",status);
				edit.putString("curr_distname",distName);
				edit.putString("curr_distid",distId);
				edit.putString("curr_distkey",distKey);
				
				edit.commit();
			
				return true;
				
				
			}
			
			
			
		}*/


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case android.R.id.home:

                getFragmentManager().popBackStack();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.distributer_info, null);


        mDistributerListListener = (DistributerListListener) getActivity().getSupportFragmentManager().findFragmentByTag("distlist");
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        imageWindow = (ImageView) v.findViewById(R.id.imgPreview);
        getGeocodeBtn = (Button) v.findViewById(R.id.getGeocodeBtn);
        previewBtn = (Button) v.findViewById(R.id.previewImage);
        deleteBtn = (Button) v.findViewById(R.id.deleteImage);
        v.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return true;
            }

        });
        return v;


    }


    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {


        menu.findItem(R.id.distributer_refresh).setVisible(false);
        menu.findItem(R.id.distributer_search).setVisible(false);
        menu.findItem(R.id.distributer_listview).setVisible(false);
        menu.findItem(R.id.symphony_settings).setVisible(false);


    }


    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();


        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Customer Information");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true); // disable the button
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true); // remove the left caret
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.getActivity().registerReceiver(locationFailedReceiver, new IntentFilter(SMSService.GEO_LOCATION_FAILED));


    }

    @Override
    public void onPause() {
        super.onPause();


        Log.e("TILE ", ((AppCompatActivity) getActivity()).getSupportActionBar().getTitle() + "");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("CHECK IN / CHECK OUT");
        this.getActivity().unregisterReceiver(locationFailedReceiver);


    }

/*	@Override
	public void onGPSCancel() {
		// TODO Auto-generated method stub
		
		Toast.makeText(getActivity(), "Can not get geolocation , because GPS is disabled", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onGPSOK() {
		// TODO Auto-generated method stub
        Toast.makeText(getActivity(), " GPS is enabled", Toast.LENGTH_LONG+Toast.LENGTH_LONG).show();

	}*/


    private byte[] readCameraImage(String path) {


        Log.e("path ", path + "");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;

        Bitmap bm = BitmapFactory.decodeFile(path, options);

        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);


        imageWindow.setImageBitmap(bm);


        return bos.toByteArray();


    }


    public void setGeoLocaitonBtnEnable(boolean state) {


        getGeocodeBtn.setEnabled(state);


    }
}
