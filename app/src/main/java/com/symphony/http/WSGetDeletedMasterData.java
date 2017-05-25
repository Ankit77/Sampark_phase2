package com.symphony.http;

import android.content.Context;

import com.symphony.model.MasterDataModel;
import com.symphony.utils.WriteLog;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by indianic on 23/05/17.
 */

public class WSGetDeletedMasterData {
    public ArrayList<String> executeTown(String lastdattime, Context context) {
        String murl = "http://61.12.85.74:800/eSampark_Delete_Record.asp?NM=track_new&PASS=track123&xMNO=9374146578&lastdate=" + lastdattime;
        WriteLog.E("url", murl);
        URL url = null;
        try {
            url = new URL(murl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            InputStream stream = conn.getInputStream();
            return getDeletedMasterDataIds(stream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (ProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getDeletedMasterDataIds(InputStream stream) {
        ArrayList<String> masterList = new ArrayList<>();
        MasterDataModel masterDataModel = null;
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
                        if (tagname.equalsIgnoreCase("DealerLETLONGID")) {
                            masterList.add(text);
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
        return masterList;
    }

}
