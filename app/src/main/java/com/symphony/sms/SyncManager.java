package com.symphony.sms;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.symphony.E_Sampark;
import com.symphony.database.CheckData;
import com.symphony.database.DB;
import com.symphony.distributer.DistributerActivity;
import com.symphony.distributer.DistributerList.DISTRIBUTER_INFO;
import com.symphony.http.HttpManager;
import com.symphony.http.HttpStatusListener;
import com.symphony.utils.SymphonyUtils;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

public class SyncManager extends IntentService {

    public SyncManager() {
        super("SyncManager");
        // TODO Auto-generated constructor stub
    }

    private HttpClient httpClient;
    private HttpResponse httpResponse;
    private HttpEntity httpEntity;
    private boolean isTimeout = false;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String USER_MOBILE_NUMBER;

    public static final String SYNC_CHECK_STATUS_DATA = "SYNC_CHECK_STATUS_DATA";
    public static final String SYNC_DISTRIBUTER_DATA = "SYNC_DISTRIBUTER_DATA";

    private static final String HTTP_SERVER = "61.12.85.74";
    private static final String HTTP_PORT = "800";
    private static final String HTTP_PROTOCOL = "http://";
    private String HTTP_ENDPOINT = HTTP_PROTOCOL + HTTP_SERVER + ":" + HTTP_PORT;
    private E_Sampark e_sampark;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    private class ImageSync extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void v) {
            editor = prefs.edit();
            editor.putBoolean("isServiceOn", false);
            editor.commit();
        }

        @Override
        protected void onCancelled() {

            if (isTimeout) {
                //Time out occurs and image wont get uploaded
                Toast.makeText(SyncManager.this, "Request Timeout occurs for offline sync , please check your net connection", Toast.LENGTH_LONG).show();

            } else {
                // no network connection
                Toast.makeText(SyncManager.this, "Network not available at this moment", Toast.LENGTH_SHORT).show();
            }

        }


        @Override
        protected Void doInBackground(Void... params) {

            if (!isNetworkAvailable()) {
                cancel(false);
                return null;
            }
            Cursor cur = getBaseContext().getContentResolver().
                    query(Uri.parse("content://com.symphony.database.DBProvider/getDistributerMetaData"),
                            DISTRIBUTER_META_DATA.PROJECTION,
                            DB.DIST_FLAG + " = 1",
                            null,
                            null);

            String versionNumber = SymphonyUtils.getAppVersion(SyncManager.this);
            if (cur.getCount() != 0) {
                while (cur.moveToNext()) {
                    byte[] bitmapdata = cur.getBlob(cur.getColumnIndexOrThrow(DB.DIST_IMG));

                    String currentUrl = HTTP_ENDPOINT + "/MobilePicv1.asp?user=track_new&pass=track123&" +

                            "Lat=" + cur.getString(cur.getColumnIndex(DB.DIST_LAT)) +
                            "&Long=" + cur.getString(cur.getColumnIndex(DB.DIST_LNG)) +
                            "&Custid=" + cur.getString(cur.getColumnIndex(DB.DIST_META_ID)) +
                            "&Mno=" + USER_MOBILE_NUMBER + "&Timestamp=" + cur.getString(cur.getColumnIndex(DB.DIST_TIME)) +
                            "&name=" + cur.getString(cur.getColumnIndex(DB.DIST_IMG_URL))
                            + "&v=v" + versionNumber;


                    HttpPost httppost = new HttpPost(currentUrl);


                    Log.e("URL HIT ->>", currentUrl + "");

                    MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
                    multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                    String fileName = cur.getString(cur.getColumnIndex(DB.DIST_IMG_URL));

                    if (fileName != null) {
                        multipartEntity.addBinaryBody("file", bitmapdata, ContentType.create("image/jpeg"), fileName);


                    }


                    final HttpEntity httpEntity = multipartEntity.build();
                    httppost.setEntity(httpEntity);

                    try {


                        httpResponse = httpClient.execute(httppost);

                        if (getUploadStatus()) {

                            updateImageFlag(0,
                                    cur.getInt(cur.getColumnIndex(DB.DIST_META_KEY)),
                                    cur.getString(cur.getColumnIndex(DB.DIST_META_ID)));

                        } else {

                            updateImageFlag(1,
                                    cur.getInt(cur.getColumnIndex(DB.DIST_META_KEY)),
                                    cur.getString(cur.getColumnIndex(DB.DIST_META_ID)));
                        }

                    } catch (SocketTimeoutException e) {


                        isTimeout = true;
                        cancel(false);

                        e.printStackTrace();

                        return null;

                    } catch (ConnectTimeoutException e) {


                        isTimeout = true;
                        cancel(false);

                        e.printStackTrace();

                        return null;


                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }

            }


            cur.close();

            // work is done

            return null;


        }


    }


    public interface DISTRIBUTER_META_VIEW {


        public static final int ID = 3;

        public static final String[] PROJECTION = {

                DB.DIST_META_ID,
                DB.DIST_TIME,
                DB.DIST_FLAG,
                DB.DIST_ID,
                DB.DIST_NAME,
                DB.DIST_KEY

        };

    }

    public interface DISTRIBUTER_META_DATA {


        public static final int ID = 1;

        public static final String[] PROJECTION = {

                DB.DIST_META_KEY,
                DB.DIST_META_ID,
                DB.DIST_LAT,
                DB.DIST_LNG,
                DB.DIST_IMG,
                DB.DIST_IMG_URL,
                DB.DIST_TIME,
                DB.DIST_FLAG,
                DB.DIST_TIMESTAMP,
                DB.DIST_FLAG,
                DB.DIST_TIME,
                DB.DIST_NAME


        };

    }

    public interface CHECK_DATA {


        public static final int ID = 2;

        public static final String[] PROJECTION = {

                DB.CHECK_ID,
                DB.CHECK_STATUS,
                DB.CHECK_STATUS,
                DB.DIST_CHECK_KEY,
                DB.CHECK_FLAG,
                DB.CHECK_SMS,
                DB.CHECK_LAT,
                DB.CHECK_LNG,
                DB.DIST_CHECK_NAME,
                DB.CHECK_TIMESTAMP

        };

    }

    public interface NOTIFICATION {


        public static final int ID = 4;

        public static final String[] PROJECTION = {

                DB.NOTIFICATION_ID,
                DB.NOTIFICATION_MESSAGE,
                DB.NOTIFICATION_TIMESTAMP,
                DB.NOTIFICATION_TYPE


        };

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean getUploadStatus() throws ParseException, IOException {

        httpEntity = httpResponse.getEntity();

        String responseXml = EntityUtils.toString(httpEntity);
        //	Log.e("RESPONSE ->>> " , responseXml+"");

        StringReader strReader = new StringReader(responseXml);


        XmlPullParser parser = Xml.newPullParser();
        try {

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(strReader);
            parser.nextTag();

            return parseUploadStatus(parser);


        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;

    }

    private boolean parseUploadStatus(XmlPullParser parser) throws XmlPullParserException, IOException {


        parser.require(XmlPullParser.START_TAG, null, "data");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();


            if (name.equals("status")) {


                String status = readStatus(parser);
                //Log.e("UPLOAD CONFIRMATION " , status+"");
                if (status.equals("success")) {


                    return true;

                } else {


                    return false;
                }


            }
        }
        return false;

    }

    private String readStatus(XmlPullParser parser) throws XmlPullParserException, IOException {


        parser.require(XmlPullParser.START_TAG, null, "status");

        //String title = readText(parser);
        String status = null;
        if (parser.next() == XmlPullParser.TEXT) {
            status = parser.getText();


            parser.nextTag();
        }

        parser.require(XmlPullParser.END_TAG, null, "status");
        return status;

    }


    private void updateImageFlag(int flag, int distKey, String distId) {


        ContentValues values = new ContentValues();
        values.put(DB.DIST_FLAG, flag);


        int updateRes = getBaseContext().getContentResolver().
                update(
                        Uri.parse("content://com.symphony.database.DBProvider/updateFlagDistributerMetaData"),
                        values,
                        DB.DIST_META_ID + " = '" + distId + "' AND " + DB.DIST_META_KEY + " = " + distKey,
                        null

                );


        //Log.e("UPDATE DB "  , updateRes+" "+distId);


    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub

        e_sampark = (E_Sampark) getApplicationContext();
        prefs = e_sampark.getSharedPreferences();
        //service status is on
        editor = prefs.edit();
        editor.putBoolean("isServiceOn", true);
        editor.commit();


        USER_MOBILE_NUMBER = prefs.getString("usermobilenumber", null);


        if (intent != null) {

            if (intent.getAction().equals(SYNC_DISTRIBUTER_DATA)) {

                getMasterIP();

                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, 1000 * 120);
                HttpConnectionParams.setSoTimeout(httpParams, 1000 * 120);

                httpClient = new DefaultHttpClient(httpParams);


                ImageSync imgSync = new ImageSync();
                imgSync.execute();


            } else if (intent.getAction().equals(SYNC_CHECK_STATUS_DATA)) {


                syncCheckStatusData();

            }


        }


    }


    private class CheckStatusSync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {


            Cursor curCheck = getBaseContext().getContentResolver().
                    query(Uri.parse("content://com.symphony.database.DBProvider/getCheckData"),
                            SyncManager.CHECK_DATA.PROJECTION,
                            DB.CHECK_FLAG + " = 1",
                            null,
                            null);


            if (curCheck.getCount() != 0) {

                while (curCheck.moveToNext()) {


                    String smsBody = curCheck.getString(curCheck.getColumnIndex(DB.CHECK_SMS));
                    final int checkId = curCheck.getInt(curCheck.getColumnIndex(DB.CHECK_ID));

                    sendCheckStatusData(smsBody, String.valueOf(checkId));

                }


            }

            curCheck.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {


            editor = prefs.edit();
            editor.putBoolean("isServiceOn", false);
            editor.commit();


        }

        @Override
        protected void onCancelled() {

            if (isTimeout) {

                //Time out occurs and image wont get uploaded

                Toast.makeText(SyncManager.this, "Request Timeout occurs for offline sync , please check your net connection", Toast.LENGTH_LONG).show();

            } else {

                // no network connection
                Toast.makeText(SyncManager.this, "Network not available at this moment", Toast.LENGTH_SHORT).show();


            }

        }


    }


    private void sendCheckStatusData(String smsBody, final String checkId) {

        HttpManager httpManger = new HttpManager(SyncManager.this);
        httpManger.sendCheckStatus(smsBody, checkId, new HttpStatusListener() {

            CheckData checkData = new CheckData();

            @Override
            public void onVerifyStatus(Boolean status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDistributerListLoad(Boolean status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onVerifyMobileStatus(Boolean status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onCheckStatus(CheckData checkData) {
                // TODO Auto-generated method stub

                this.checkData = checkData;
                //	Log.e("data value >>",checkData.getCheckDistKey() + " "+checkData.getCheckLat()+ " "+checkData.getCheckLng());


                checkData.setCheckId(checkId);
                checkData.setCheckStatus(checkData.isCheckStatus());
                checkData.setCheckFlag(false);


                //insertCheckFlag(checkData);

                // check in or check out sent status
                int updateRes = updateCheckFlag(checkData);


                //	Log.e("SyncManager : Update Res on status  ",checkId+" "+updateRes + " Synced" );
            }

            @Override
            public void onTimeOut() {
                // TODO Auto-generated method stub

                //	Log.e("data value >>",checkData.getCheckLat()+ " "+checkData.getCheckLng());

                checkData.setCheckId(checkId);
                checkData.setCheckStatus(false);
                checkData.setCheckFlag(true);
                //insertCheckFlag(checkData);


                int updateRes = updateCheckFlag(checkData);


                //	Log.e("SyncManager : Update Res in timeout ",updateRes+"");

            }

            @Override
            public void onNetworkDisconnect() {
                // TODO Auto-generated method stub


                checkData.setCheckId(checkId);
                checkData.setCheckStatus(false);
                checkData.setCheckFlag(true);
                //insertCheckFlag(checkData);

                int updateRes = updateCheckFlag(checkData);

                //Log.e("SyncManager : Update Res in onNetworkDisconnect ",updateRes+"");

            }


        });
    }

    private void syncCheckStatusData() {


        //	Log.e("SycManager :: syncCheckStatusData", "called");
        new CheckStatusSync().execute();


    }

    private CheckData insertCheckFlag(CheckData checkData) {

        ContentValues values = new ContentValues();


        values.put(DB.CHECK_STATUS, checkData.isCheckStatus() == true ? 1 : 0);
        values.put(DB.CHECK_FLAG, checkData.isCheckFlag() == true ? 1 : 0);
        values.put(DB.CHECK_LAT, checkData.getCheckLat());
        values.put(DB.CHECK_LAT, checkData.getCheckLng());
        values.put(DB.DIST_CHECK_KEY, checkData.getCheckDistKey());
        values.put(DB.DIST_CHECK_NAME, checkData.getCheckDistName());


        getBaseContext().getContentResolver().insert(Uri.parse("content://com.symphony.database.DBProvider/addCheckStatus"),

                values);


        return checkData;
    }


    private int updateCheckFlag(CheckData checkData) {


        ContentValues values = new ContentValues();
        values.put(DB.CHECK_STATUS, checkData.isCheckStatus() == true ? 1 : 0); // 0 -> 0 success ,  		1 -> failed
        values.put(DB.CHECK_FLAG, checkData.isCheckFlag() == true ? 1 : 0); // 0 -> successfully synced , 1 -> not synced
        values.put(DB.CHECK_LAT, checkData.getCheckLat());
        values.put(DB.CHECK_LNG, checkData.getCheckLng());
        values.put(DB.DIST_CHECK_KEY, checkData.getCheckDistKey());
        values.put(DB.DIST_CHECK_NAME, checkData.getCheckDistName());
        values.put(DB.CHECK_ID, checkData.getCheckId());


        int updateRes = getBaseContext().getContentResolver().
                update(
                        Uri.parse("content://com.symphony.database.DBProvider/updateCheckFlagStatus"),
                        values,
                        DB.CHECK_ID + " = " + checkData.getCheckId(),
                        null

                );

        //Log.e("SyncManager :: Check Key " , checkData.getCheckId()+"");

        return updateRes;
    }

    public void getMasterIP() {


        String ip = prefs.getString("masterIP", null);
        String port = prefs.getString("masterPort", null);

        if (ip != null) {

            HTTP_ENDPOINT = ip;

            if (port != null)
                HTTP_ENDPOINT = HTTP_PROTOCOL + HTTP_ENDPOINT + ":" + port;
            else
                HTTP_ENDPOINT = HTTP_PROTOCOL + HTTP_ENDPOINT;
        }

        //	Log.e("Notification ip change " , HTTP_ENDPOINT+"");

    }
}
