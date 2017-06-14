package com.symphony.http;

import android.content.Context;
import android.util.Log;

import com.symphony.E_Sampark;
import com.symphony.utils.Const;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by ANKIT on 5/21/2017.
 */

public class WSGetMeter {

    private String message;
    private E_Sampark e_sampark;
    private Context context;

    public String getMessage() {
        return message;
    }

    public boolean executeTown(String murl, Context context) {
        URL url = null;
        this.context = context;
        e_sampark = (E_Sampark) context.getApplicationContext();
        Log.e("STRAT TIME", "" + System.currentTimeMillis());
        try {
            url = new URL(murl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            InputStream stream = conn.getInputStream();
            Log.e("END TIME", "" + System.currentTimeMillis());
            return isLogout(stream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (ProtocolException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isLogout(InputStream stream) {
        boolean isLogout = false;
        String text = "";
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(stream, null);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("Meter")) {
                            e_sampark.getSharedPreferences().edit().putInt(Const.PREF_CHECKIN_METER, Integer.parseInt(text)).commit();
                           // e_sampark.getSharedPreferences().edit().putInt(Const.PREF_CHECKIN_METER, 400).commit();
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("RESPONSE TIME", "" + System.currentTimeMillis());
        return isLogout;
    }

}
