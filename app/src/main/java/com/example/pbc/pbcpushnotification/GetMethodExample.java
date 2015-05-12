package com.example.pbc.pbcpushnotification;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pbc on 12.05.2015.
 */
public class GetMethodExample {

    static final String TAG = "LOOOOGG";
    public String getInternetData() throws Exception{

        BufferedReader in = null;
        String data = null;
        try {
            HttpClient client = new DefaultHttpClient();
            URI website = new URI("http://www.mybringback.com");
            HttpGet request = new HttpGet();
            request.setURI(website);
            HttpResponse response = client.execute(request);

            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";
            String newLine = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + newLine);
            }
            in.close();

            data = sb.toString();
            return data;

        } finally {
            if (in != null){
                try {
                    in.close();
                    return data;
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }

    public String postRedeemItems() throws Exception{

        BufferedReader in = null;
        String data = null;
        try {
            HttpClient client = new DefaultHttpClient();
            URI postUrl = new URI("http://timedudeapi.cust21.reea.net/timedudeapi/web/api/v1/redeemItems");
            HttpPost httpPost = new HttpPost();
            httpPost.setURI(postUrl);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
            httpPost.addHeader("x-wsse", "ApiKey=\"bf4bff30-4664-48f9-87d5-fb78520df136\"");
            httpPost.getParams().setParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
            nameValuePairs.add(new BasicNameValuePair("googleUid", "1"));
            nameValuePairs.add(new BasicNameValuePair("gameId", "1"));
            nameValuePairs.add(new BasicNameValuePair("itemId", "1"));
            nameValuePairs.add(new BasicNameValuePair("ammount", "1"));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            Log.w(TAG, nameValuePairs.toString());
            Log.w(TAG, httpPost.toString());

            HttpResponse response = client.execute(httpPost);

            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";
            String newLine = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + newLine);
            }
            in.close();

            data = sb.toString();
            return data;
        } finally {
            if (in != null){
                try {
                    in.close();
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String putRegistration(String googleUid, String registrationKey, String gameId, String deviceId , String game_version) throws Exception{


        String GOOGLE_UID = googleUid;
        String REGISTRATION_KEY = registrationKey;
        String GAME_ID = gameId;
        String DEVICEID = deviceId;
        String GAME_VERSION = game_version;


        BufferedReader in = null;
        String data = null;
        try {
            HttpClient client = new DefaultHttpClient();
            URI postUrl = new URI("http://timedudeapi.cust21.reea.net/timedudeapi/web/api/v1/registration");
            HttpPut httpPut = new HttpPut();
            httpPut.setURI(postUrl);


            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            httpPut.addHeader("x-wsse", "ApiKey=\"bf4bff30-4664-48f9-87d5-fb78520df136\"");
            httpPut.getParams().setParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
            nameValuePairs.add(new BasicNameValuePair("googleUid", GOOGLE_UID));
            nameValuePairs.add(new BasicNameValuePair("registrationKey", REGISTRATION_KEY));
            nameValuePairs.add(new BasicNameValuePair("gameId", GAME_ID));
            nameValuePairs.add(new BasicNameValuePair("deviceId", DEVICEID));
            nameValuePairs.add(new BasicNameValuePair("game_version", GAME_VERSION));


            httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            Log.w(TAG, nameValuePairs.toString());
            Log.w(TAG, httpPut.toString());

            HttpResponse response = client.execute(httpPut);

            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";
            String newLine = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + newLine);
            }
            in.close();

            data = sb.toString();
            return data;
        } finally {
            if (in != null){
                try {
                    in.close();
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
