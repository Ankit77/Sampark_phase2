package com.symphony.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.symphony.model.MasterDataModel;
import com.symphony.utils.WriteLog;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.ArrayList;

public class SymphonyDB extends SQLiteOpenHelper {
    private SQLiteDatabase sqLiteDatabase;

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
        sqldb.execSQL(DB.CREATE_MASTER_TABLE);
        sqLiteDatabase = sqldb;

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqldb, int arg1, int arg2) {
        // TODO Auto-generated method stub
        if (arg1 > arg2) {
            sqldb.execSQL("ALTER TABLE " + DB.CHECK + " ADD COLUMN " + DB.CHECK_DEALERLETLONGID);
        }
        onCreate(sqldb);

    }

    public void openDataBase() throws SQLException {
        if (sqLiteDatabase != null && sqLiteDatabase.isOpen()) {
            sqLiteDatabase.close();
        }
        sqLiteDatabase = this.getWritableDatabase(DB.DATABASE_STRING);

    }
    //insert customer

    public void insertMasterData(ArrayList<MasterDataModel> masterList) {

        try {
            openDataBase();
            sqLiteDatabase.beginTransaction();

            for (int i = 0; i < masterList.size(); i++) {
                MasterDataModel masterDataModel = masterList.get(i);
                String query = "Select * from " + DB.MASTER_TABLE + " where " + DB.COLUME_MASTER_DEALERENROLMENT_ID + " = " + "'" + masterDataModel.getDealerenrolmentid() + "'";
                Cursor cursor = sqLiteDatabase.rawQuery(query, null);
                ContentValues values = new ContentValues();
                values.put(DB.COLUME_MASTER_DEALERLETLONG_ID, masterDataModel.getDealerletlongid());
                values.put(DB.COLUME_MASTER_DEALERENROLMENT_ID, masterDataModel.getDealerenrolmentid());
                values.put(DB.COLUME_MASTER_NAME, masterDataModel.getName());
                values.put(DB.COLUME_MASTER_ADDR, masterDataModel.getAddr());
                values.put(DB.COLUME_MASTER_LAT, masterDataModel.getLat());
                values.put(DB.COLUME_MASTER_LANG, masterDataModel.getLang());
                values.put(DB.COLUME_MASTER_CREATED_ON, masterDataModel.getCreated_on());
                if (cursor.getCount() > 0) {
                    long j = sqLiteDatabase.update(DB.MASTER_TABLE, values, DB.COLUME_MASTER_DEALERENROLMENT_ID + "=" + masterDataModel.getDealerenrolmentid(), null);
                    WriteLog.E("UPDATE", "" + i);
                } else {
                    long k = sqLiteDatabase.insert(DB.MASTER_TABLE, null, values);
                    WriteLog.E("INSERT", "" + i);
                }
                cursor.close();

            }
            sqLiteDatabase.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            sqLiteDatabase.endTransaction();
            sqLiteDatabase.close();
            android.database.sqlite.SQLiteDatabase.releaseMemory();
        }
    }

    public void deleteMasterData(String dealerletlong_id) {

        try {
            sqLiteDatabase.delete(DB.MASTER_TABLE, DB.COLUME_MASTER_DEALERLETLONG_ID + "=?", new String[]{dealerletlong_id});
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            sqLiteDatabase.close();
            android.database.sqlite.SQLiteDatabase.releaseMemory();
        }
    }


    public ArrayList<MasterDataModel> getMasterDataList() {
        final ArrayList<MasterDataModel> visitList = new ArrayList<MasterDataModel>();
        openDataBase();
        Cursor cursor = null;
        try {
            String query = "Select * from " + DB.MASTER_TABLE;
            cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                MasterDataModel model = null;
                for (int i = 0; i < cursor.getCount(); i++) {
                    model = new MasterDataModel();
                    model.setDealerenrolmentid(cursor.getString(cursor.getColumnIndex(DB.COLUME_MASTER_DEALERENROLMENT_ID)));
                    model.setDealerletlongid(cursor.getString(cursor.getColumnIndex(DB.COLUME_MASTER_DEALERLETLONG_ID)));
                    model.setName(cursor.getString(cursor.getColumnIndex(DB.COLUME_MASTER_NAME)));
                    model.setAddr(cursor.getString(cursor.getColumnIndex(DB.COLUME_MASTER_ADDR)));
                    model.setLat(cursor.getString(cursor.getColumnIndex(DB.COLUME_MASTER_LAT)));
                    model.setLang(cursor.getString(cursor.getColumnIndex(DB.COLUME_MASTER_LANG)));
                    model.setCreated_on(cursor.getString(cursor.getColumnIndex(DB.COLUME_MASTER_CREATED_ON)));
                    visitList.add(model);
                    cursor.moveToNext();
                }
                visitList.trimToSize();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            close();
            if (cursor != null) {
                cursor.close();
                sqLiteDatabase.close();
                android.database.sqlite.SQLiteDatabase.releaseMemory();
            }
        }
        return visitList;
    }

}
