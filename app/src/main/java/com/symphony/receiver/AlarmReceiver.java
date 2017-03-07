package com.symphony.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.symphony.E_Sampark;


public class AlarmReceiver extends BroadcastReceiver {
    private E_Sampark e_sampark;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(AlarmReceiver.class.getSimpleName(), "Call");
        e_sampark = (E_Sampark) context.getApplicationContext();
        SharedPreferences.Editor editor = e_sampark.getSharedPreferences().edit();
        editor.putBoolean("ISENABLE", true);
        editor.commit();
        Intent myIntent = new Intent();
        myIntent.setAction("ENABLE_BUTTON");
        context.sendBroadcast(myIntent);

    }
}
