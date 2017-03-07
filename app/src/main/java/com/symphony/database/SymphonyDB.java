package com.symphony.database;

import android.content.Context;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

public class SymphonyDB extends SQLiteOpenHelper {

	public SymphonyDB(Context context) {
		super(context, DB.DATABASE_NAME, null, DB.DATABASE_VERSION);
		// TODO Auto-generated constructor stub
		
		
		
	}

	@Override
	public void onCreate(SQLiteDatabase sqldb) {
		
		// TODO Auto-generated method stub
		
		sqldb.execSQL("PRAGMA foreign_keys = ON");
	//	sqldb.execSQL(DB.CREATE_DATABASE);
		sqldb.execSQL(DB.CREATE_USER_TABLE);

		sqldb.execSQL(DB.CREATE_DISTRIBUTER_TABLE);
		sqldb.execSQL(DB.CREATE_CHECK_TABLE);
		sqldb.execSQL(DB.CREATE_DISTRIBUTER_META_DATA_TABLE);
	//	sqldb.execSQL(DB.CREATE_DISTRIBUTER_VIEW);
		sqldb.execSQL(DB.CREATE_NOTIFICATION_TABLE);

		
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqldb, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
		onCreate(sqldb);
		
	}
	
	

}
