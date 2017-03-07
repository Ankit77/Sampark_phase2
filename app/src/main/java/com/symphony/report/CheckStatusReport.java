package com.symphony.report;

import com.symphony.R;
import com.symphony.database.DB;
import com.symphony.distributer.DistributerList;
import com.symphony.distributer.DistributerList.DISTRIBUTER_INFO;
import com.symphony.pager.FragmentTitle;
import com.symphony.sms.SyncManager;
import com.symphony.sms.SyncManager.CHECK_DATA;
import com.symphony.utils.Const;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CheckStatusReport extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, FragmentTitle {


    private ListView reportList;
    private ReportAdapter reportAdapter;
    private Cursor cur;
    private static int[] RESOURCES = new int[]{

            R.id.checkStatusType,
            R.id.checkStatus,
            R.id.checkTime

    };
    private UpdateContentObsever updateContentObserver = new UpdateContentObsever(new Handler());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.checkstatus_report_list, null);
        return v;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reportList = (ListView) getActivity().findViewById(R.id.checkStatusList);
        reportList.setEmptyView(getActivity().findViewById(R.id.emptyViewCheckStatus));
        reportAdapter = new ReportAdapter(getActivity(),
                R.layout.checkstatus_report_list,
                cur,
                SyncManager.CHECK_DATA.PROJECTION,
                RESOURCES,
                0
        );

        reportList.setAdapter(reportAdapter);
        getActivity().getSupportLoaderManager().initLoader(CHECK_DATA.ID, null, CheckStatusReport.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("CheckStatus", "onResume");
        getActivity().getContentResolver().
                registerContentObserver(
                        Uri.parse("content://com.symphony.database.DBProvider/updateCheckFlagStatus"),
                        false,
                        updateContentObserver);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.UPDATE_SYNC_BROADCAST);
        getActivity().registerReceiver(updateCheckStatusBroadcast, intentFilter);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(updateCheckStatusBroadcast);
    }

    class ReportAdapter extends SimpleCursorAdapter {

        private LayoutInflater layoutInflater;

        public ReportAdapter(Context context, int layout, Cursor c,
                             String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            // TODO Auto-generated constructor stub
            layoutInflater = LayoutInflater.from(context);
        }

        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return layoutInflater.inflate(R.layout.checkstatus_report_list_row, null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);

            // CHECK IN OR CHECK OUT
            TextView checkStatusType = (TextView) view.findViewById(R.id.checkStatusType);
            // STATUS RESPONSE FROM SERVER
            TextView checkServerStatus = (TextView) view.findViewById(R.id.checkServerStatus);
            // WHETHER ITS BEEN SYNCD OR NOT
            TextView checkSync = (TextView) view.findViewById(R.id.checkSync);
            // SEND TIME
            TextView checkTime = (TextView) view.findViewById(R.id.checkTime);

            TextView checkLatLng = (TextView) view.findViewById(R.id.checklatLng);
            TextView checkDist = (TextView) view.findViewById(R.id.checkDist);

            View checkStatusReportBar = (View) view.findViewById(R.id.checkStatusReportBar);

            String checkSMS = cursor.getString(cursor.getColumnIndex(DB.CHECK_SMS));
            int checkFlag = cursor.getInt(cursor.getColumnIndex(DB.CHECK_FLAG));
            int checkStatus = cursor.getInt(cursor.getColumnIndex(DB.CHECK_STATUS));
            String checkLatLnt = cursor.getString(cursor.getColumnIndex(DB.CHECK_LAT)) +
                    "," +
                    cursor.getString(cursor.getColumnIndex(DB.CHECK_LNG));

            String checkDistData =
                    cursor.getString(cursor.getColumnIndex(DB.DIST_CHECK_NAME));

					
				/*	if(checkLatLng!=null)
					checkLatLng.setText(checkLatLnt);*/

            if (checkDist != null)
                checkDist.setText(checkDistData);


            if (checkSMS != null) {

                String checkString[] = checkSMS.split(",");

                if (checkLatLng != null)
                    checkLatLng.setText(checkString[2] + "," + checkString[3]);

                if (checkStatusType != null) {

                    if (checkString[0].equals("TRACK")) checkString[0] = "LOGIN";
                    checkStatusType.setText(checkString[0]);
                }

                if (checkServerStatus != null) {


                    if (checkStatus == 1 && checkFlag == 0) {

                        checkServerStatus.setText("Success");
                        checkStatusReportBar.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                    } else if (checkStatus == 0 && checkFlag == 0) {


                        checkStatusReportBar.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                        checkServerStatus.setText("Fail");
                    } else {

                        checkStatusReportBar.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                        checkServerStatus.setText("Sync Pending");
                    }


                }
					
				/*	if(checkSync!=null)
						checkSync.setText( (checkFlag == 0 ? "Synced " : " Sync Pending"));
				*/


                try {

                    if (checkTime != null) {
                        if (checkString[0].equals("GEOCODE")) {

                            checkTime.setText(checkString[5]);
                        } else {

                            checkTime.setText(checkString[4]);
                        }

                    }


                } catch (ArrayIndexOutOfBoundsException e) {


                }
            }

        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        // TODO Auto-generated method stub

        switch (id) {

            case CHECK_DATA.ID:

                return new CursorLoader(getActivity(),
                        Uri.parse("content://com.symphony.database.DBProvider/getCheckData"),
                        SyncManager.CHECK_DATA.PROJECTION,
                        null,
                        null,
                        null);

        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        // TODO Auto-generated method stub

        if (reportAdapter != null) {


            Log.e("CheckStatusReport  ", cursor.getCount() + "");

            reportAdapter.swapCursor(cursor);


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
        if (reportAdapter != null)
            reportAdapter.swapCursor(null);

    }

    @Override
    public String getTitle() {
        // TODO Auto-generated method stub
        return "VISIT STATUS";
    }


    class UpdateContentObsever extends ContentObserver {

        public UpdateContentObsever(Handler handler) {
            super(handler);
            // TODO Auto-generated constructor stub

        }
		
		
		/*@Override
		public boolean deliverSelfNotifications (){
			
			return true;
		}*/


        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);


            getActivity().getSupportLoaderManager().restartLoader(CHECK_DATA.ID, null, CheckStatusReport.this);

        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        Log.e("CheckStatus", "onDestroy");

        getActivity().getContentResolver().unregisterContentObserver(updateContentObserver);

    }

    BroadcastReceiver updateCheckStatusBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(Const.UPDATE_SYNC_BROADCAST)) {
                reportAdapter = new ReportAdapter(getActivity(),
                        R.layout.checkstatus_report_list,
                        cur,
                        SyncManager.CHECK_DATA.PROJECTION,
                        RESOURCES,
                        0
                );
                reportList.setAdapter(reportAdapter);
            }
        }
    };
}
