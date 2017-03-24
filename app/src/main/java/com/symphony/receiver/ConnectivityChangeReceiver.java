package com.symphony.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.symphony.E_Sampark;
import com.symphony.service.TimeTickService;
import com.symphony.sms.SyncManager;
import com.symphony.utils.SymphonyUtils;


public class ConnectivityChangeReceiver extends BroadcastReceiver {
    private E_Sampark e_sampark;

    @Override
    public void onReceive(Context context, Intent intent) {
        e_sampark = (E_Sampark) context.getApplicationContext();

        NetworkInfo networkInfo = intent
                .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (networkInfo != null) {
            if (e_sampark.getSharedPreferences().getBoolean("isregister", false)) {
                if (!SymphonyUtils.isMyServiceRunning(TimeTickService.class, context)) {
                    Intent service_intent = new Intent(context, TimeTickService.class);
                    context.startService(service_intent);
                }
            }
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
