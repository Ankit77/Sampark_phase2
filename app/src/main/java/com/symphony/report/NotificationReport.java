package com.symphony.report;

import com.symphony.R;
import com.symphony.database.DB;
import com.symphony.pager.FragmentTitle;
import com.symphony.sms.SyncManager;
import com.symphony.sms.SyncManager.CHECK_DATA;

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
import android.widget.Toast;

public class NotificationReport  extends Fragment  implements LoaderManager.LoaderCallbacks <Cursor> , FragmentTitle{

	private ListView reportList;
	private ReportAdapter reportAdapter;
	private Cursor cur ;
	
	private  static int[] RESOURCES = new int []{
		
		R.id.notificationMessage,
		R.id.notificationReportTime
		

	};
	private InsertContentObsever insertContentObserver = new InsertContentObsever(new Handler());


	@Override
	public View onCreateView(LayoutInflater inflater , ViewGroup container  , Bundle savedInstanceState){		
				super.onCreateView(inflater, container, savedInstanceState);
				
				View v = inflater.inflate(R.layout.notification_report_list, null);
				
				return v;

	}
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
			super.onActivityCreated(savedInstanceState);
			
			reportList = (ListView) getActivity().findViewById(R.id.notificationReportList);
		
			reportList.setEmptyView(getActivity().findViewById(R.id.emptyViewNotification));
			
			


			reportAdapter = new ReportAdapter(getActivity(),
					  R.layout.notification_report_list , 
					  cur , 
					  SyncManager.NOTIFICATION.PROJECTION,
					  RESOURCES,
					  0
			);


			
			reportList.setAdapter(reportAdapter);
			getActivity().getSupportLoaderManager().initLoader(SyncManager.NOTIFICATION.ID, null, NotificationReport.this);					

	}
	
	
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "NOTIFICATIONS";
	}
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		// TODO Auto-generated method stub
		
		
		if(reportAdapter!=null){
			
			
			Log.e("Notification Report " , cursor.getCount()+"");
			
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
			
			
			return layoutInflater.inflate(R.layout.notification_report_list_row,null);
			
			
			
			
		}
		
		
		
		@SuppressLint("NewApi")
		@Override
		public void bindView(View view , Context context , Cursor cursor ){
				super.bindView(view, context, cursor);
				
				TextView notificationMessage = (TextView) view.findViewById(R.id.notificationMessage);
				TextView notificationTime = (TextView) view.findViewById(R.id.notificationReportTime);
				
				notificationMessage.setText(cursor.getString(cursor.getColumnIndex(DB.NOTIFICATION_MESSAGE)));
				notificationTime.setText(cursor.getString(cursor.getColumnIndex(DB.NOTIFICATION_TIMESTAMP)));
						
		}
		
		
		
	}

	

	@Override
	public void onResume(){
		super.onResume();
		
		Log.e("notification", "onResume");
		getActivity().getContentResolver().
		
		      registerContentObserver(
		
		    		  Uri.parse("content://com.symphony.database.DBProvider/addNewNotification"),
		
		            false,
		
		           insertContentObserver);
		
	}
	
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
		// TODO Auto-generated method stub
		
		switch(id){
			
			case SyncManager.NOTIFICATION.ID:
				
								return new CursorLoader(getActivity(),
										
										  Uri.parse("content://com.symphony.database.DBProvider/getNotificationData"),
										  SyncManager.NOTIFICATION.PROJECTION,
											null, 
											null, 
											null);		
			
		}
	
		return null;
	}
	
	
	class InsertContentObsever extends ContentObserver{

		public InsertContentObsever(Handler handler) {
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
	        
	        
	        
	        
	        getActivity().getSupportLoaderManager().restartLoader(SyncManager.NOTIFICATION.ID, null,NotificationReport.this);
	       
	    }

		
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		


		getActivity().getContentResolver().unregisterContentObserver(insertContentObserver);
		
	}
	
}
