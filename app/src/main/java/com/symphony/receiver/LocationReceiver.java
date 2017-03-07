package com.symphony.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.symphony.sms.SMSService;

/**
 * Created by Ankit on 1/6/2016.
 */
public class LocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(LocationReceiver.class.getSimpleName(), "LocationReceiver Call");
        Intent intentLocationService = new Intent(context, SMSService.class);
        intentLocationService.setAction(SMSService.FETCH_LOCATION_INTENT);
        context.startService(intentLocationService);
    }
}
