package com.symphony;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.symphony.utils.SymphonyUtils;

import java.io.IOException;

public class SymphonyGCMHome {


    private static String regId;
    private static GoogleCloudMessaging gcm;


    public static String getGCMRegistrationId(final Context context) {

        try {


            gcm = GoogleCloudMessaging.getInstance(context);
            regId = getRegistrationId(context);

            // if not available in preference then get id from GCM
            if (regId == null) {


                new AsyncTask<Void, Void, Boolean>() {

                    @Override
                    protected Boolean doInBackground(Void... params) {
                        // TODO Auto-generated method stub


                        try {

                            regId = gcm.register(SymphonyUtils.GCM_KEY);
                            Log.e(SymphonyGCMHome.class.getSimpleName(), "REGID" + regId);
                            setRegistrationId(context, regId);
                            //final HttpManager httpManager	= new HttpManager(context);
                            //httpManager.registerDeviceId(userName,emailId, regId);


                        } catch (IOException e) {


                            e.printStackTrace();
                            return false;

                        }


                        return true;
                    }

                    @Override
                    public void onPostExecute(Boolean res) {

                        if (!res) {

                            Toast.makeText(context,
                                    "Not able to get device id ", Toast.LENGTH_LONG).show();


                        }

                    }


                }.execute(null, null, null);

				
				/*new Thread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						try {
							regId = gcm.register(SymphonyUtils.GCM_KEY);
							
							setRegistrationId(context,regId);

						} catch (IOException e) {
							// TODO Auto-generated catch block
							
							
							e.printStackTrace();
						}
						
						
						
					}
					
					
					
				}).start(); */


            }


        } catch (UnsupportedOperationException e) {


        }


        return regId;

    }


    public static String getRegistrationId(Context context) {
        SharedPreferences prefs;
        E_Sampark e_sampark = (E_Sampark) context.getApplicationContext();
        prefs = e_sampark.getSharedPreferences();
        return prefs.getString("registrationId", null);


    }

    private static void setRegistrationId(Context context, String registrationId) {
        SharedPreferences prefs;
        E_Sampark e_sampark = (E_Sampark) context.getApplicationContext();
        prefs = e_sampark.getSharedPreferences();
        SharedPreferences.Editor editor;
        editor = prefs.edit();
        editor.putString("registrationId", registrationId);
        editor.commit();


    }
}
