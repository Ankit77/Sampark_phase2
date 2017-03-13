package com.symphony.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 27-Jun-16.
 */
public class WSAddCustomer {

    private HttpEntity httpEntity;
    public boolean executeAddCustomer(String murl, String mobileNumber, String empid, String customername, String address, String townid, String contactname, String designation, String mobile, String email, String pincode) {

        HttpClient httpClient = new DefaultHttpClient();
        // replace with your url
        HttpPost httpPost = new HttpPost(murl);
        httpPost.setHeader(HTTP.CONTENT_TYPE,
                "application/x-www-form-urlencoded;charset=UTF-8");

        //Post Data
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        nameValuePair.add(new BasicNameValuePair("user", "track_new"));
        nameValuePair.add(new BasicNameValuePair("pass", "track123"));
        nameValuePair.add(new BasicNameValuePair("MNO", mobileNumber));
        nameValuePair.add(new BasicNameValuePair("EMPID", empid));
        nameValuePair.add(new BasicNameValuePair("CUSTOMERNAME", customername));
        nameValuePair.add(new BasicNameValuePair("Address", address));
        nameValuePair.add(new BasicNameValuePair("TownID", townid));
        nameValuePair.add(new BasicNameValuePair("ContactName", contactname));
        nameValuePair.add(new BasicNameValuePair("Designation", designation));
        nameValuePair.add(new BasicNameValuePair("Mobile", mobile));
        nameValuePair.add(new BasicNameValuePair("Email", email));


        //Encoding POST data
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            // log exception
            e.printStackTrace();
        }

        //making POST request.
        try {
            HttpResponse response = httpClient.execute(httpPost);
            httpEntity = response.getEntity();

            String responseXml = EntityUtils.toString(response.getEntity());
            // write response to log

            InputStream stream = new ByteArrayInputStream(responseXml.toString().getBytes());
            return parseResponse(stream);
        } catch (ClientProtocolException e) {
            // Log exception
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // Log exception
            e.printStackTrace();
            return false;
        }
    }

    private boolean parseResponse(InputStream stream) {

        boolean isSuccess = false;
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

                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("sucess")) {
                            // add employee object to list
                            if (text.equalsIgnoreCase("True")) {
                                isSuccess = true;
                            } else {
                                isSuccess = false;
                            }

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

        return isSuccess;
    }

}
