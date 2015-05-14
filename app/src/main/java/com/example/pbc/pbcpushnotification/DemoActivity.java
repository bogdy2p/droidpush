package com.example.pbc.pbcpushnotification;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by pbc on 06.05.2015.
 */
public class DemoActivity extends Activity {


    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "1";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String USER_AGENT = "";
    public static final String PREFS_NAME = "GoogleUserData";


    String SENDER_ID = "338252548778";

    static final String TAG = "GCMDemo";

    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    String regid;
    String ACCOUNT_ID, ACCOUNT_NAME ,FIRST_NAME , LAST_NAME, LOCATION , LANGUAGE;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo);
        mDisplay = (TextView) findViewById(R.id.display);

        context = getApplicationContext();

        if (checkPlayServices()) {

            Log.i(TAG, "Entered checkPlayServices");



            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);


            Log.i(TAG, "REG ID IS " + regid);
            mDisplay.setTextColor(3);
            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");

        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(DemoActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }


    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        Log.i(TAG, "Entered RegisterInBackgroundFunction");
        new AsyncTask() {

            @Override
            protected String doInBackground(Object[] params) {
                String msg = "";
                try {
                    Log.i(TAG, "Entered RIBF->NewASYNCTASK TRY");
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }

                    regid = gcm.register(SENDER_ID);

                    Log.i(TAG, regid.toString());
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend(regid);

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }


            protected void onPostExecute(String msg) {
                mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend(String regid) {



        SharedPreferences googleUserData = getSharedPreferences(PREFS_NAME, 0);
        ACCOUNT_NAME = googleUserData.getString("ACCOUNT_NAME","noname");
        ACCOUNT_ID = googleUserData.getString("ACCOUNT_ID","noid");
        FIRST_NAME = googleUserData.getString("FIRST_NAME","null");
        LAST_NAME = googleUserData.getString("LAST_NAME","null");
        LOCATION = googleUserData.getString("LOCATION","null");
        LANGUAGE = googleUserData.getString("LANGUAGE","null");

        Log.i(TAG, ACCOUNT_NAME);
        Log.i(TAG, ACCOUNT_ID);
        Log.i(TAG, FIRST_NAME);
        Log.i(TAG, LAST_NAME);
        Log.i(TAG, LOCATION);
        Log.i(TAG, LANGUAGE);


        GetMethodExample test = new GetMethodExample();

        String googleUid = ACCOUNT_ID;
        String gameId = "2";
        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String appVersion = String.valueOf(getAppVersion(context));


        Log.i(TAG, "Get UserExists ========================================================");
        String userexists = null;
        try {
            userexists = test.getUserexists(googleUid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, userexists);
        Log.i(TAG, "END Get UserExists =====================================================");

        if (userexists.contains("User not found.")){
            String addUserToDatabase;
            try {
                addUserToDatabase = test.postNewuser(ACCOUNT_NAME,ACCOUNT_ID,FIRST_NAME,LAST_NAME,LOCATION,LANGUAGE,"birthday");

            }catch (Exception e){
                e.printStackTrace();
            }


        }




        Log.i(TAG, "Get UserGameInfo ========================================================");


        Log.i(TAG, "END Get UserGameInfo ========================================================");
        String returned = null;
        try {
            returned = test.putRegistration(googleUid, regid, gameId, deviceID, appVersion);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, returned);
    }


    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
