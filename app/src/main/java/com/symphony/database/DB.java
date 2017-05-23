package com.symphony.database;

import android.net.Uri;

public class DB {

	
	//database 
	public static final String DATABASE_NAME = "salesapp";
	public static final String CREATE_DATABASE ="CREATE DATABASE "+ DATABASE_NAME;
	public static final int DATABASE_VERSION =1;
	public static final String DATABASE_STRING = "symphonysales";
	
	
	
	//url matcher constant
	//fetch all distributer from database
	public static final int LIST_DISTRIBUTOR = 		1;
	public static final int ADD_DISTRIBUTER = 		2;
	public static final int SEARCH_DISTRIBUTER =3;
	public static final int ADD_CHECK_STATUS = 4;
	public static final int DELETE_DISTRIBUTER = 5;
	public static final int DELETE_DISTRIBUTER_ID = 6;
	public static final int ADD_DISTRIBUTER_META_DATA = 8;
	public static final int LIST_DISTRIBUTER_META_DATA = 9;
	public static final int DELETE_DISTRIBUTER_METADATA = 10;
	
	
	public static final int UPDATE_DISTRIBUTER_METADATA = 11;
	public static final int UPDATE_CHECK_STATUS = 12;
	
	public static final int LIST_CHECK_DATA = 14;

	public static final int LIST_DISTRIBUTER_VIEW_DATA = 15;
	public static final int ADD_USER = 		16;
	public static final int ADD_NOTIFICATION = 17;
	public static final int LIST_NOTIFICATION = 18;
	public static final int UPDATE_DISTRIBUTER_DATA= 19;
	
	public static final int DELETE_DISTRIBUTER_REPORT_DATA= 20;
	public static final int DELETE_CHECK_REPORT_DATA= 21;
	public static final int DELETE_NOTIFICATION_REPORT_DATA=22;
	public static final int DELETE_CHEKINOUT_BY_ID = 23;



	public static final String ADD_NEW_DISTRIBUTER = "addDistributer";
	public static final String SEARCH_DISTRIBUTER_NAME = "getDistributerByName";
	public static final String ADD_NEW_CHECK_STATUS = "addCheckStatus";
	public static final String DELETE_ALL_DISTRIBUTER = "deleteAllDistributer";
	public static final String DELETE_DISTRIBUTER_BY_ID = "deleteDistributerById";
	public static final String ADD_NEW_DISTRIBUTER_META_DATA = "addDistributerMetaData";
	public static final String GET_DISTRIBUTER_META_DATA = "getDistributerMetaData";
	public static final String DELETE_ALL_DISTRIBUTER_METADATA = "deleteAllDistributerData";
	public static final String UPDATE_FLAG_DISTRIBUTER_METADATA = "updateFlagDistributerMetaData";
	public static final String UPDATE_FLAG_CHECK_STATUS = "updateCheckFlagStatus";
	public static final String UPDATE_DISTRIBUTER= "updateDistributer";
	public static final String DELETE_DISTRIBUTER_REPORT= "deleteDistributerReport";
	public static final String DELETE_CHECK_REPORT= "deleteCheckReport";
	public static final String DELETE_NOTIFICATION_REPORT="deleteNotificationReport";

	
	public static final String GET_CHECK_DATA ="getCheckData";
	public static final String GET_DISTRIBUTER_VIEW_DATA="getDistributerViewData";
	
	public static final String ADD_NEW_USER = "addUser";
	public static final String ADD_NEW_NOTIFICATION = "addNewNotification";
	
	public static final String GET_NOTIFICATION_DATA = "getNotificationData";
	public static final String DELETE_CHECKINOUTBYID = "deletecheckinoutById";



	
	
	public static final String DISTRIBUTER_META_DATA ="distributer_meta_data";
	public static final String DIST_META_KEY = "_id";
	public static final String DIST_META_ID = "dist_meta_id";
	public static final String DIST_LAT = "dist_lat";
	public static final String DIST_LNG = "dist_lng";
	public static final String DIST_IMG = "dist_img";
	public static final String DIST_IMG_URL = "dist_img_url";
	public static final String DIST_TIME = "dist_time";
	public static final String DIST_FLAG = "dist_flag";
	
	
	

	
	
	//table name
	public static final String DISTRIBUTER ="distributer_info";	
	public static final String DISTRIBUTER_META_VIEW ="distributer_meta_view";	

	//column names for distributor's list
	public static final String DIST_KEY = "_id";
	public static final String DIST_ID = "dist_id";
	public static final String DIST_NAME = "dist_name";
	public static final String DIST_CONTACT_PERSON ="dist_contact_name";
	public static final String DIST_ADDRESS = "dist_address";
	public static final String DIST_AREA = "dist_area";
	public static final String DIST_TIMESTAMP = "dist_timestamp";



	//Customer
	public static final String MASTER_TABLE = "latlongtable";
	public static final String COLUME_MASTER_DEALERLETLONG_ID = "dealerletlongid";
	public static final String COLUME_MASTER_DEALERENROLMENT_ID = "dealerenrolmentid";
	public static final String COLUME_MASTER_LAT = "lat";
	public static final String COLUME_MASTER_LANG = "lang";
	public static final String COLUME_MASTER_CREATED_ON = "created_on";
	public static final String COLUME_MASTER_NAME = "name";
	public static final String COLUME_MASTER_ADDR = "addr";





	static final String CREATE_MASTER_TABLE = "CREATE TABLE IF NOT EXISTS " + MASTER_TABLE + " (" + COLUME_MASTER_DEALERLETLONG_ID + " TEXT, " + COLUME_MASTER_DEALERENROLMENT_ID
			+ " TEXT," + COLUME_MASTER_LAT + " TEXT," + COLUME_MASTER_LANG + " TEXT," + COLUME_MASTER_CREATED_ON + " TEXT," + COLUME_MASTER_NAME + " TEXT," + COLUME_MASTER_ADDR + " TEXT)";


	// create statement
	public static final String CREATE_DISTRIBUTER_TABLE=
						
							"CREATE TABLE IF NOT EXISTS "+DISTRIBUTER + " ( "+
							
									DIST_KEY +" INTEGER primary key autoincrement , "+
									DIST_ID + " TEXT , "+
									DIST_NAME + " TEXT , "+
									DIST_CONTACT_PERSON + " TEXT , "+
									DIST_AREA + " TEXT ,  " +
									DIST_ADDRESS + " TEXT "+

							" ); ";
	
	
	
	
	// create statement
	public static final String CREATE_DISTRIBUTER_META_DATA_TABLE=
						
							"CREATE TABLE IF NOT EXISTS "+DISTRIBUTER_META_DATA + " ( "+
							
									DIST_META_KEY +" INTEGER primary key autoincrement , "+
									DIST_META_ID + " INTEGER , "+
									DIST_NAME + " TEXT , "+
									DIST_TIME + " TEXT , "+
									DIST_LAT + " TEXT , "+
									DIST_LNG + " TEXT , "+
									DIST_IMG + " BLOB ,  " +
									DIST_IMG_URL + " TEXT ,"+
									DIST_FLAG + " BOOLEAN , "+
									DIST_TIMESTAMP + " TEXT  " +
									
									/*"FOREIGN KEY ( "+DIST_META_ID+" ) REFERENCES "+
									DISTRIBUTER + " ( "+DIST_KEY+" ) " +*/
							" ); ";
	
	
	
	public static final String CREATE_DISTRIBUTER_VIEW=
			
			"CREATE VIEW IF NOT EXISTS "+DISTRIBUTER_META_VIEW + " AS "+
			" SELECT  "+
			DISTRIBUTER+"."+DIST_KEY + "  as _id , "+
			DIST_META_ID + " , "+
			DIST_TIME + " , "+
			DIST_FLAG + " , "+
			DIST_NAME + " , "+
			DIST_ID + " , "+
			DB.DIST_TIMESTAMP+
				    			

			
			
			
			" FROM "+DISTRIBUTER_META_DATA +  " , " + DISTRIBUTER +" "+
			 " WHERE "+DIST_META_ID + " =  " + DISTRIBUTER+"."+DIST_ID +";";
	
	
	//table name
	public static final String CHECK ="check_info";
	
	//column names for check in & check out
	public static final String CHECK_ID="_id";
	public static final String CHECK_STATUS ="check_status";
	public static final String CHECK_SMS ="check_sms";
	public static final String DIST_CHECK_KEY = "dist_check_id";
	public static final String CHECK_FLAG="check_flag";
	public static final String CHECK_LAT ="check_lat";
	public static final String CHECK_LNG ="check_lng";
	public static final String DIST_CHECK_NAME = "dist_check_name";
	public static final String CHECK_TIMESTAMP = "check_timestamp";

	

	
	
	

	// create statement
	public static final String CREATE_CHECK_TABLE=
						
							"CREATE TABLE IF NOT EXISTS "+CHECK + " ( "+
							
									CHECK_ID +" INTEGER primary key autoincrement , "+
									CHECK_STATUS + " INTEGER , "+
									CHECK_SMS + " TEXT , "+
									DIST_CHECK_KEY +" INTEGER  , "+
									DIST_CHECK_NAME + " TEXT , "+
									CHECK_FLAG + " BOOLEAN , "+
									CHECK_LAT + " TEXT , "+
									CHECK_LNG + " TEXT , "+
									CHECK_TIMESTAMP+ " TEXT ,"+
									
									"FOREIGN KEY ( "+DIST_CHECK_KEY+" ) REFERENCES "+
									DISTRIBUTER + " ( "+DIST_KEY+" ) " +
									
							" );";
	
	
	
	//table name
		public static final String USER ="user_info";
		
		//column names for check in & check out
		public static final String USER_ID="_id";
		public static final String USER_NAME ="user_name";
		public static final String USER_MOBILE ="user_mobile";
		public static final String USER_DEVICE_ID = "user_device_id";
		public static final String USER_TIMESTAMP = "user_timestamp";

		
		
	// create statement
		public static final String CREATE_USER_TABLE=
							
								"CREATE TABLE IF NOT EXISTS "+ USER + " ( "+
								
										USER_ID +" INTEGER primary key autoincrement , "+
										USER_NAME + " TEXT , "+
										USER_MOBILE + " TEXT , "+
										USER_DEVICE_ID +" TEXT,  "+
										USER_TIMESTAMP + " TEXT "+
										
										
								" );";
	
	public static final Uri  SALES_URI =
			Uri.parse("content://com.symphony.database.DBProvider/distributer");
	
	
	
	
	
	public static final String NOTIFICATION ="notification";

							
	public static final String NOTIFICATION_ID="_id";
	public static final String NOTIFICATION_MESSAGE ="not_message";
	public static final String NOTIFICATION_TIMESTAMP ="not_timestamp";	
	public static final String NOTIFICATION_TYPE ="not_type";	

	
	public static final String CREATE_NOTIFICATION_TABLE=
			
			"CREATE TABLE IF NOT EXISTS "+ NOTIFICATION + " ( "+
			
					NOTIFICATION_ID +" INTEGER primary key autoincrement , "+
					NOTIFICATION_MESSAGE + " TEXT , "+
					NOTIFICATION_TIMESTAMP + " TEXT , "+
					NOTIFICATION_TYPE +" INTEGER"+
					
					
			" );";
}
