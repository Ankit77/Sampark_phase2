package com.symphony.receiver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.symphony.sms.SMSService;

/**
 * Created by Ankit on 1/6/2016.
 */
public class MyService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                startJob();
            }
        });
        t.start();
        return START_STICKY;
    }

    private synchronized void startJob() {
        //do job here
        Log.e(MyService.class.getSimpleName(), "LocationReceiver Call");
        Intent intentLocationService = new Intent(MyService.this, SMSService.class);
        intentLocationService.setAction(SMSService.FETCH_LOCATION_INTENT);
        startService(intentLocationService);
        //job completed. Rest for 5 second before doing another one
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //do job again
        startJob();
    }


}
