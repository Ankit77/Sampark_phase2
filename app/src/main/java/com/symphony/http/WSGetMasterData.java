package com.symphony.http;

import android.content.Context;

import com.symphony.model.MasterDataModel;

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
 * Created by indianic on 22/05/17.
 */

public class WSGetMasterData {


    public ArrayList<MasterDataModel> executeTown(String lastdattime, Context context) {
        String murl = "http://61.12.85.74:800/eSampark_Masterdata.asp?NM=track_new&PASS=track123&xMNO=9374146578&lastdate=" + lastdattime;
        URL url = null;
        try {
            url = new URL(murl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            InputStream stream = conn.getInputStream();
            return getMasterDataLIst(stream);
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

    public ArrayList<MasterDataModel> getMasterDataLIst(InputStream stream) {
        ArrayList<MasterDataModel> masterList = new ArrayList<>();
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
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("Record")) {
                            masterDataModel = new MasterDataModel();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("DealerLETLONGID")) {
                            masterDataModel.setDealerletlongid(text);
                        } else if (tagname.equalsIgnoreCase("DealerEnrolmentID")) {
                            masterDataModel.setDealerenrolmentid(text);
                        } else if (tagname.equalsIgnoreCase("LET")) {
                            masterDataModel.setLat(text);
                        } else if (tagname.equalsIgnoreCase("LONG")) {
                            masterDataModel.setLang(text);
                        } else if (tagname.equalsIgnoreCase("Createdon")) {
                            masterDataModel.setCreated_on(text);
                        } else if (tagname.equalsIgnoreCase("name")) {
                            masterDataModel.setName(text);
                        } else if (tagname.equalsIgnoreCase("addr")) {
                            masterDataModel.setAddr(text);
                        } else if (tagname.equalsIgnoreCase("Record")) {
                            masterList.add(masterDataModel);
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
