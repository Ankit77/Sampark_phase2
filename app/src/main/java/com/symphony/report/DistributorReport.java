package com.symphony.report;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.symphony.R;
import com.symphony.database.DB;
import com.symphony.pager.FragmentTitle;
import com.symphony.report.CheckStatusReport.UpdateContentObsever;
import com.symphony.sms.SyncManager;
import com.symphony.sms.SyncManager.CHECK_DATA;

public class DistributorReport  extends  Fragment   implements LoaderManager.LoaderCallbacks <Cursor> , FragmentTitle{
	
	
	
	private ListView reportList;
	private ReportAdapter reportAdapter;
	private Cursor cur ;
	private UpdateContentObsever updateContentObserver = new UpdateContentObsever(new Handler());

	private  static int[] RESOURCES = new int []{
		
		R.id.distributorReportName
		

	};
	
	
	@Override
	public View onCreateView(LayoutInflater inflater , ViewGroup container  , Bundle savedInstanceState){		
				super.onCreateView(inflater, container, savedInstanceState);
				
				
				View v = inflater.inflate(R.layout.distributor_report_list, null);
				return v;

	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
			super.onActivityCreated(savedInstanceState);
			
			reportList = (ListView) getActivity().findViewById(R.id.distributorReportList);
		
			reportList.setEmptyView(getActivity().findViewById(R.id.emptyViewDistributor));
			
			


			reportAdapter = new ReportAdapter(getActivity(),
					  R.layout.distributor_report_list , 
					  cur , 
					  SyncManager.DISTRIBUTER_META_DATA.PROJECTION,
					  RESOURCES,
					  0
						);


			
			reportList.setAdapter(reportAdapter);
			getActivity().getSupportLoaderManager().initLoader(SyncManager.DISTRIBUTER_META_VIEW.ID, null, DistributorReport.this);					

	}
	
	

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "IMAGE STATUS";
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
		// TODO Auto-generated method stub
		
		
		switch(id){
		
		case SyncManager.DISTRIBUTER_META_VIEW.ID:
			
							return new CursorLoader(getActivity(),
									  Uri.parse("content://com.symphony.database.DBProvider/getDistributerMetaData"),
									  SyncManager.DISTRIBUTER_META_DATA.PROJECTION,
										null, 
										
										null, 
										null);		
		
	}
	
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		// TODO Auto-generated method stub
		
		
		if(reportAdapter!=null){
			
			
			Log.e("DistributorReport " , cursor.getCount()+"");
			
			reportAdapter.swapCursor(cursor);
			
			
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		if(reportAdapter!=null)
			reportAdapter.swapCursor(null);
	}
	
class ReportAdapter extends SimpleCursorAdapter{
		
		private Cursor cursor;
		private Context context;
		private int layout;
		private LayoutInflater layoutInflater;

		public ReportAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);
			// TODO Auto-generated constructor stub
	
		
			this.context = context;
			this.cursor = c;
			this.layout = layout;
			layoutInflater = LayoutInflater.from(context);
			
		}
		
		public View newView(Context context , Cursor cursor, ViewGroup parent){
			
			
			return layoutInflater.inflate(R.layout.distributor_report_list_row,null);
			
			
			
			
		}
		
		@SuppressLint("NewApi")
		@Override
		public void bindView(View view , Context context , Cursor cursor ){
					super.bindView(view, context, cursor);
					
					TextView distributorReportName = (TextView) view.findViewById(R.id.distributorReportName);
					TextView distributorReportTime = (TextView) view.findViewById(R.id.distributorReportTime);
					TextView distributorReportStatus = (TextView) view.findViewById(R.id.distributorReportStatus);
					TextView distributorReportLatLng = (TextView) view.findViewById(R.id.distributorReportLatLng);


					View distributorReportBar = (View) view.findViewById(R.id.distributorReportBar);
					
					
					int flagStatus = cursor.getInt(cursor.getColumnIndex(DB.DIST_FLAG));
					
					if(flagStatus==1){
						
						distributorReportBar.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
						distributorReportStatus.setText("Pending");

					}else{

						distributorReportBar.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));

						distributorReportStatus.setText("Success");

						
					}							
					
					
					distributorReportTime.setText(cursor.getString(cursor.getColumnIndex(DB.DIST_TIME)));
					distributorReportName.setText(
							
							cursor.getString(cursor.getColumnIndex(DB.DIST_NAME))
					);
					
					distributorReportLatLng.setText(
							cursor.getString(cursor.getColumnIndex(DB.DIST_LAT))+","+
						    cursor.getString(cursor.getColumnIndex(DB.DIST_LNG))
					);
		}
		
		
		
	}


	@Override
	public void onResume(){
		super.onResume();
		
		Log.e("DistributorReport", "onResume");
		getActivity().getContentResolver().
		
		      registerContentObserver(
		
		    		  Uri.parse("content://com.symphony.database.DBProvider/updateFlagDistributerMetaData"),
		
		            false,
		
		            updateContentObserver);
		
	}
	class UpdateContentObsever extends ContentObserver{
	
		public UpdateContentObsever(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
			
		}
		
		
		
		
		@Override
	    public void onChange(boolean selfChange) {
	        super.onChange(selfChange);
	        
	        
	        Log.e("Distributor ->>>> 1" , ""+selfChange);
	        
	       getActivity().getSupportLoaderManager().restartLoader(SyncManager.DISTRIBUTER_META_VIEW.ID, null, DistributorReport.this);
	       
	    }
		
		
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		

		Log.e("CheckStatus", "onDestroy");

		getActivity().getContentResolver().unregisterContentObserver(updateContentObserver);
		
	}
}
