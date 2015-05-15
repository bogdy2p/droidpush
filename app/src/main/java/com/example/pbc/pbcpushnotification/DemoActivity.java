package com.example.pbc.pbcpushnotification;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Array;
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
    Context context;
    String regid;
    String ACCOUNT_ID, ACCOUNT_NAME, FIRST_NAME, LAST_NAME, LOCATION, LANGUAGE;
    String USER_REWARDS = "norewards";


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        mDisplay = (TextView) findViewById(R.id.display);
        context = getApplicationContext();


        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }

            SharedPreferences googleUserData = getSharedPreferences(PREFS_NAME, 0);
            ACCOUNT_ID = googleUserData.getString("ACCOUNT_ID", "null");
            getUserCoinsInformation();



        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    private void getUserCoinsInformation() {

        class GetUserCoinsAsync extends AsyncTask<Void, Integer, String> {

            protected String doInBackground(Void... arg0) {
                String msg;

                GetMethodExample apiCaller = new GetMethodExample();
                try {
                    USER_REWARDS = apiCaller.getUserGameInfo(ACCOUNT_ID, "2", "1");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                msg = USER_REWARDS;
//                Log.e(TAG, "INTRAT IN DO IN BG");
//                Log.e("MSG este", msg);

                String value = "0";
                try {
                    JSONObject json = new JSONObject(msg);
                    JSONObject message;

                    String success = json.getString("success");
//                    Log.w(TAG, success);
                    if (success.equals("true")) {
//                        Log.e(TAG,"SUCCESS WAS TRUE ENTERED IF");
                        message = new JSONObject(json.getString("message"));
                        value = message.getString("value");
//                        Log.e("THE VALUE HERE IS", value);
                    }else{
                        value = "01";
                    }
//                    Log.w("JsoNObject : message", message.toString());
//                    Log.w("JsoNObject : value", value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SharedPreferences googleUserData = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = googleUserData.edit();
                editor.putString("CURRENT_COINS", value);
                editor.apply();
                return value;
            }
            protected void onProgressUpdate(Integer... a) {
                Log.e(TAG, "You are in progress update ... ");
            }
            protected void onPostExecute(String value) {
                Log.e(TAG, "This user currently has  " + value + " coins in the database for this app.");
                mDisplay.setText(value);
            }
        }
        new GetUserCoinsAsync().execute();
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


//    private void refreshUserCoinsInformation() {
//        new AsyncTask() {
//            String msg = "ERRORMESSAGE";
//
//            @Override
//            protected Object doInBackground(Object[] params) {
//                try {
//                    GetMethodExample apiCaller = new GetMethodExample();
//                    USER_REWARDS = apiCaller.getUserGameInfo(ACCOUNT_ID, "2", "1");
//                    msg = USER_REWARDS;
//                    Log.e(TAG, "INTRAT IN DO IN BG");
//                    SharedPreferences googleUserData = getSharedPreferences(PREFS_NAME, 0);
//                    SharedPreferences.Editor editor = googleUserData.edit();
//                    editor.putString("CURRENT_COINS", msg);
//                    editor.apply();
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return msg;
//            }
//
//
//            protected void onPostExecute(String msg) {
//                TextView testtextview = (TextView) findViewById(R.id.display);
//                testtextview.setText(msg);
//            }
//
////            protected onPostExecute(Void a) {
////
////                Log.e(TAG,"Entered ZZZZZZZZZZZZZZZZZZz");
////                mDisplay.setText(msg);
////                mDisplay.invalidate();
////                Log.w(TAG,msg);
////            }
//        }.execute();
//    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
//        Log.i(TAG, "Entered RegisterInBackgroundFunction");
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

//                    Log.i(TAG, regid.toString());
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
        ACCOUNT_NAME = googleUserData.getString("ACCOUNT_NAME", "noname");
        ACCOUNT_ID = googleUserData.getString("ACCOUNT_ID", "noid");
        FIRST_NAME = googleUserData.getString("FIRST_NAME", "null");
        LAST_NAME = googleUserData.getString("LAST_NAME", "null");
        LOCATION = googleUserData.getString("LOCATION", "null");
        LANGUAGE = googleUserData.getString("LANGUAGE", "null");

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

        if (userexists.contains("User not found.")) {
            String addUserToDatabase;
            try {
                addUserToDatabase = test.postNewuser(ACCOUNT_NAME, ACCOUNT_ID, FIRST_NAME, LAST_NAME, LOCATION, LANGUAGE, "birthday");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        Log.i(TAG, "Get UserGameInfo ========================================================");
        Log.i(TAG, "END Get UserGameInfo ========================================================");
        //TODO : CHECK IF A REGISTRATION FOR THIS CURRENT USER AND THIS VERSION OF THE APP ALREADY EXISTS

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
