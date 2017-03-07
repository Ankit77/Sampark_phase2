package com.symphony.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.symphony.sms.SyncManager;


public class ConnectivityChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NetworkInfo networkInfo = intent
                .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (networkInfo != null) {

            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {

                //get the different network states
                if (networkInfo.getState() == NetworkInfo.State.CONNECTING || networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    Log.e(ConnectivityChangeReceiver.class.getSimpleName(), "Connect");
                    if (isNetworkAvailable(context)) {
                        Intent syncManager = new Intent(context, SyncManager.class);
                        syncManager.setAction(SyncManager.SYNC_CHECK_STATUS_DATA);
                        context.startService(syncManager);
                    }
                } else {
                    Log.e(ConnectivityChangeReceiver.class.getSimpleName(), "disconnect");
                }
            }
        }

    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
