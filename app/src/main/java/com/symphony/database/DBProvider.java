package com.symphony.database;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteQueryBuilder;

public class DBProvider extends ContentProvider {


    private SymphonyDB mDBHelper;
    private SQLiteDatabase mDB;
    private ContentObserver contentObserver;
    private ContentObserver insertContentObserver;
    private static final UriMatcher MATCHER;


    static {
        MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

        MATCHER.addURI("com.symphony.database.DBProvider",
                "distributer", DB.LIST_DISTRIBUTOR);

        MATCHER.addURI("com.symphony.database.DBProvider",
                DB.ADD_NEW_DISTRIBUTER, DB.ADD_DISTRIBUTER);

        MATCHER.addURI("com.symphony.database.DBProvider",
                DB.SEARCH_DISTRIBUTER_NAME, DB.SEARCH_DISTRIBUTER);

        MATCHER.addURI("com.symphony.database.DBProvider",
                DB.ADD_NEW_CHECK_STATUS, DB.ADD_CHECK_STATUS);

        MATCHER.addURI("com.symphony.database.DBProvider",
                DB.DELETE_ALL_DISTRIBUTER, DB.DELETE_DISTRIBUTER);

        MATCHER.addURI("com.symphony.database.DBProvider",
                DB.DELETE_DISTRIBUTER_BY_ID, DB.DELETE_DISTRIBUTER_ID);
        MATCHER.addURI("com.symphony.database.DBProvider",
                DB.DELETE_CHECKINOUTBYID, DB.DELETE_CHEKINOUT_BY_ID);

        MATCHER.addURI("com.symphony.database.DBProvider",
                DB.ADD_NEW_DISTRIBUTER_META_DATA, DB.ADD_DISTRIBUTER_META_DATA);


        MATCHER.addURI("com.symphony.database.DBProvider",
                DB.GET_DISTRIBUTER_META_DATA, DB.LIST_DISTRIBUTER_META_DATA);


        MATCHER.addURI("com.symphony.database.DBProvider",
                DB.DELETE_ALL_DISTRIBUTER_METADATA, DB.DELETE_DISTRIBUTER_METADATA);


        MATCHER.addURI("com.symphony.database.DBProvider",
                DB.UPDATE_FLAG_DISTRIBUTER_METADATA, DB.UPDATE_DISTRIBUTER_METADATA);


        MATCHER.addURI("com.symphony.database.DBProvider",
                DB.UPDATE_FLAG_CHECK_STATUS, DB.UPDATE_CHECK_STATUS);


        MATCHER.addURI("com.symphony.database.DBProvider",
                DB.GET_CHECK_DATA, DB.LIST_CHECK_DATA);


        MATCHER.addURI("com.symphony.database.DBProvider",
                DB.GET_DISTRIBUTER_VIEW_DATA, DB.LIST_DISTRIBUTER_VIEW_DATA);

        MATCHER.addURI("com.symphony.database.DBProvider",
                DB.ADD_NEW_USER, DB.ADD_USER);


        MATCHER.addURI("com.symphony.database.DBProvider",
                DB.GET_NOTIFICATION_DATA, DB.LIST_NOTIFICATION);

        MATCHER.addURI("com.symphony.database.DBProvider",
                DB.ADD_NEW_NOTIFICATION, DB.ADD_NOTIFICATION);

        MATCHER.addURI("com.symphony.database.DBProvider",
                DB.UPDATE_DISTRIBUTER, DB.UPDATE_DISTRIBUTER_DATA);


        MATCHER.addURI("com.symphony.database.DBProvider",
                DB.DELETE_DISTRIBUTER_REPORT, DB.DELETE_DISTRIBUTER_REPORT_DATA);


        MATCHER.addURI("com.symphony.database.DBProvider",
                DB.DELETE_CHECK_REPORT, DB.DELETE_CHECK_REPORT_DATA);


        MATCHER.addURI("com.symphony.database.DBProvider",
                DB.DELETE_NOTIFICATION_REPORT, DB.DELETE_NOTIFICATION_REPORT_DATA);
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub

        int match = MATCHER.match(uri);
        int count = 0;
        switch (match) {

            case DB.DELETE_DISTRIBUTER:

                count = mDB.delete(DB.DISTRIBUTER, selection, selectionArgs);


                break;

            case DB.DELETE_DISTRIBUTER_ID:

                count = mDB.delete(DB.DISTRIBUTER, selection, selectionArgs);

                break;
            case DB.DELETE_CHEKINOUT_BY_ID:

                count = mDB.delete(DB.CHECK, selection, selectionArgs);

                break;
            case DB.DELETE_DISTRIBUTER_METADATA:
            case DB.DELETE_DISTRIBUTER_REPORT_DATA:

                count = mDB.delete(DB.DISTRIBUTER_META_DATA, selection, selectionArgs);

                break;

            case DB.DELETE_CHECK_REPORT_DATA:

                count = mDB.delete(DB.CHECK, selection, selectionArgs);

                break;

            case DB.DELETE_NOTIFICATION_REPORT_DATA:
                count = mDB.delete(DB.NOTIFICATION, selection, selectionArgs);

                break;

        }


        getContext().getContentResolver().notifyChange(uri, null);


        return count;
    }

    @Override
    public String getType(Uri arg0) {
        // TODO Auto-generated method stub
        return null;
    }


    public Uri insert(Uri uri, ContentValues values, ContentObserver cb) {

        insertContentObserver = cb;
        return insert(uri, values);

    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub


        int match = MATCHER.match(uri);

        switch (match) {


            // add distributor
            case DB.ADD_DISTRIBUTER: {

                long rowID = mDB.insert(DB.DISTRIBUTER, null, values);
                if (rowID > 0) {

                    Uri insertUri = ContentUris.withAppendedId(uri, rowID);
                    getContext().getContentResolver().notifyChange(insertUri, null);

                    return insertUri;

                }

            }

            case DB.ADD_CHECK_STATUS: {


                long rowID = mDB.insert(DB.CHECK, null, values);
                if (rowID > 0) {

                    Uri insertUri = ContentUris.withAppendedId(uri, rowID);
                    getContext().getContentResolver().notifyChange(insertUri, null);
                    Log.e("Insert  : insert ", rowID + "");

                    return insertUri;

                }


            }

            case DB.ADD_DISTRIBUTER_META_DATA: {


                long rowID = mDB.insert(DB.DISTRIBUTER_META_DATA, null, values);


                Log.e("DBProvider ->>>>>> ", rowID + "");
                if (rowID > 0) {


                    Uri insertUri = ContentUris.withAppendedId(uri, rowID);
                    getContext().getContentResolver().notifyChange(insertUri, null);

                    return insertUri;
                }

            }
            case DB.ADD_USER: {

                long rowID = mDB.insert(DB.USER, null, values);


                Log.e("DBProvider ->>>>>> ", rowID + "");
                if (rowID > 0) {


                    Uri insertUri = ContentUris.withAppendedId(uri, rowID);
                    getContext().getContentResolver().notifyChange(insertUri, null);

                    return insertUri;
                }

            }
            case DB.ADD_NOTIFICATION: {

                long rowID = mDB.insert(DB.NOTIFICATION, null, values);


                if (rowID > 0) {


                    Uri insertUri = ContentUris.withAppendedId(uri, rowID);

                    Log.e("DBProvider ->>>>>> ", rowID + " " + insertUri.toString());
                    Uri tempInsertUri = insertUri;


                    getContext().getContentResolver().notifyChange(uri, insertContentObserver);

                    return insertUri;
                }

            }


        }


        return null;
    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub

        //initialize db helper & ciper libraries
        SQLiteDatabase.loadLibs(getContext());
        mDBHelper = new SymphonyDB(getContext());
        mDB = mDBHelper.getWritableDatabase(DB.DATABASE_STRING);


        return (mDBHelper == null ? false : true);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // TODO Auto-generated method stub

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String orderBy;


        int match = MATCHER.match(uri);

        //Log.e("DBProvider " , "matched " + match);

        switch (match) {


            // fetch all distributor
            case DB.LIST_DISTRIBUTOR:


                queryBuilder.setTables(DB.DISTRIBUTER);
                break;


            case DB.SEARCH_DISTRIBUTER:

                queryBuilder.setTables(DB.DISTRIBUTER);


                break;

            case DB.LIST_DISTRIBUTER_META_DATA:

                queryBuilder.setTables(DB.DISTRIBUTER_META_DATA);
                sortOrder = DB.DIST_META_KEY + " DESC ";
                break;


            case DB.LIST_CHECK_DATA:

                queryBuilder.setTables(DB.CHECK);

                //sortOrder = "datetime("+DB.CHECK_TIMESTAMP+") DESC ";
                sortOrder = DB.CHECK_ID + " DESC ";
                break;

            case DB.LIST_DISTRIBUTER_VIEW_DATA:

                queryBuilder.setTables(DB.DISTRIBUTER_META_VIEW);
                //	sortOrder = "datetime("+DB.DIST_TIMESTAMP+") DESC ";
                //	sortOrder = DB.DIST_META_KEY + " DESC ";


                break;

            case DB.LIST_NOTIFICATION:

                queryBuilder.setTables(DB.NOTIFICATION);
                sortOrder = DB.NOTIFICATION_ID + " DESC ";

                break;


        }


        //    orderBy =  TextUtils.isEmpty(sortOrder) ?   DB.DIST_NAME :  sortOrder;

        orderBy = TextUtils.isEmpty(sortOrder) ? null : sortOrder;

        Cursor cursor = queryBuilder.query(mDB,
                projection,
                selection,
                selectionArgs, null, null, orderBy);


        // passing specific uri rather then generic
        cursor.setNotificationUri(getContext().getContentResolver(), uri);


        return cursor;
    }


    //	public int update(Uri uri, ContentValues values, String whereClause, String[] whereArgs, ContentObserver  cb){
//		
//		contentObserver = cb;
//		return update(uri,values,whereClause,whereArgs);
//		
//	}
    @Override
    public int update(Uri uri, ContentValues values, String whereClause, String[] whereArgs) {
        // TODO Auto-generated method stub


        int match = MATCHER.match(uri);

        switch (match) {

            case DB.UPDATE_DISTRIBUTER_METADATA: {

                int rowsUpdated = mDB.update(DB.DISTRIBUTER_META_DATA, values, whereClause, whereArgs);
                getContext().getContentResolver().notifyChange(uri, null);

                return rowsUpdated;
            }


            case DB.UPDATE_CHECK_STATUS: {

                int rowsUpdated = mDB.update(DB.CHECK, values, whereClause, whereArgs);
                getContext().getContentResolver().notifyChange(uri, contentObserver);
                Log.e("DBProvider ", "update " + uri);

                return rowsUpdated;
            }

            case DB.UPDATE_DISTRIBUTER_DATA: {


                int rowsUpdated = mDB.update(DB.DISTRIBUTER, values, whereClause, whereArgs);
                getContext().getContentResolver().notifyChange(uri, contentObserver);

                return rowsUpdated;
            }


        }


        return 0;
    }


}
