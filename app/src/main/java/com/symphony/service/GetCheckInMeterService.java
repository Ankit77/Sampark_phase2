package com.symphony.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.symphony.http.WSGetMeter;

/**
 * Created by ANKIT on 5/22/2017.
 */

public class GetCheckInMeterService extends IntentService {
    private String url="http://61.12.85.74:800/eSampark_GetMeter.asp?UNM=track_new&PASS=track123";

    public GetCheckInMeterService() {
        super(GetCheckInMeterService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        WSGetMeter wsGetMeter = new WSGetMeter();
        wsGetMeter.executeTown(url, GetCheckInMeterService.this);
    }
}
