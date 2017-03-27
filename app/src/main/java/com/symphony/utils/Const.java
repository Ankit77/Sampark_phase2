package com.symphony.utils;

/**
 * Created by indianic on 19/12/15.
 */
public class Const {

//  test number -  9426332929

    public static final String HTTP_SERVER = "61.12.85.74";
    public static final String HTTP_PORT = "800";
    public static final String HTTP_PROTOCOL = "http://";
    public static final String HTTP_ENDPOINT = HTTP_PROTOCOL + HTTP_SERVER + ":" + HTTP_PORT;

    public static final String EMPID = "EMPID";
    public static final String USERTYPE = "USERTYPE";
    public static final String MESSAGE = "MESSAGE";
    public static final String PREF_MOBILE = "MOBILE";

    public static final String CHECKIN = "CHECKIN";
    public static final String CHECKOUT = "CHECKOUT";
    public static final String UPDATE_SYNC_BROADCAST = "UPDATE_SYNC_BROADCAST";
    public static final String PREF_STRAT_TIME = "PREF_STRAT_TIME";
    public static final String PREF_ISSYNCDATA = "PREF_ISSYNC";
    public static final String PREF_WIPEOUT_TIME = "PREF_WIPEOUT_TIME";
    //public static final long WIPEDATA_INTERVAL = 1000 * 60 * 1;

    // public static final long WIPEDATA_INTERVAL = 1000 * 60 * 15;
    public static final long WIPEDATA_INTERVAL = 1000 * 60 * 60 * 48;
    public static final long SYNCDATA_INTERVAL = 1000 * 60 * 2;

    public static final String PREF_WIPEDATA = "WIPEDATA";
    public static final String PREF_SYNC = "SYNC";
}
