package app.psiteportal.com.psiteportal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.gcm.IntentServiceGCM;
import app.psiteportal.com.test.MainActivity;
import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.Config;
import app.psiteportal.com.utils.JSONParser;

public class LoginActivity extends Activity {

    ProgressDialog pDialog;
    private boolean toast;
    private EditText username_et, password_et;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_ID = "id";
    private static final String TAG_USERTYPE = "usertype";

    public static final String MyPREFERENCES = "MyPrefsPSITE" ;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;


    JSONParser jsonParser = new JSONParser();
    // Asyntask
    AsyncTask<Void, Void, Void> mRegisterTask;
     String regId;
    // Alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();

    // Connection detector
    ConnectionDetector cd;

    public static String name;
    public static String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username_et = (EditText) findViewById(R.id.input_name);
        password_et = (EditText) findViewById(R.id.input_password);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String user_pid = sharedpreferences.getString("pid", "");
        if(!user_pid.isEmpty()){
            String user_usertype = sharedpreferences.getString("usertype", "");
            String has_nominated = sharedpreferences.getString("has_nominated", "");
            String has_voted = sharedpreferences.getString("has_voted", "");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("pid", user_pid);
            intent.putExtra("usertype", user_usertype);
            intent.putExtra("has_voted", has_voted);
            intent.putExtra("has_nominated", has_nominated);
            startActivity(intent);
        }

        cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) { 
            // Internet Connection is not present
            alert.showAlertDialog(LoginActivity.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }
        registerGCM();
    }

        // Getting name, email from intent
//        Intent i = getIntent();
//        if(i.getStringExtra("name")!=null && i.getStringExtra("email")!=null);
//        {
//            name = i.getStringExtra("name");
//            email = i.getStringExtra("email");
//
//            // Make sure the device has the proper dependencies.
//            GCMRegistrar.checkDevice(this);
//
//            // Make sure the manifest was properly set - comment out this line
//            // while developing the app, then uncomment it when it's ready.
//            GCMRegistrar.checkManifest(this);
//
////        lblMessage = (TextView) findViewById(R.id.lblMessage);
//
//            // Get GCM registration id
//            final String regId = GCMRegistrar.getRegistrationId(this);
//
//            Log.e("reg reg reg reg", regId);
//
//            // Check if regid already presents
//            if (regId.equals("")) {
//                // Registration is not present, register now with GCM
//                GCMRegistrar.register(this, SENDER_ID);
//            } else {
//                // Device is already registered on GCM
//                if (GCMRegistrar.isRegisteredOnServer(this)) {
//                    // Skips registration.
////                    Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
//                } else {
//                    // Try to register again, but not in the UI thread.
//                    // It's also necessary to cancel the thread onDestroy(),
//                    // hence the use of AsyncTask instead of a raw thread.
//                    final Context context = this;
//                    mRegisterTask = new AsyncTask<Void, Void, Void>() {
//
//                        @Override
//                        protected Void doInBackground(Void... params) {
//                            // Register on our server
//                            // On server creates a new user
//                            ServerUtilities.register(context, name, email, regId);
//                            return null;
//                        }
//
//                        @Override
//                        protected void onPostExecute(Void result) {
//                            mRegisterTask = null;
//                        }
//
//                    };
//                    mRegisterTask.execute(null, null, null);
//                }
//            }
//        }
//    }
//
//    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
//            // Waking up mobile if it is sleeping
//            WakeLocker.acquire(getApplicationContext());
//
//            /**
//             * Take appropriate action on this message
//             * depending upon your app requirement
//             * For now i am just displaying it on the screen
//             * */
//
//            // Showing received message
//
//            Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
//
//            // Releasing wake lock
//            WakeLocker.release();
//        }
//    };
//
//    @Override
//    protected void onDestroy() {
//        if (mRegisterTask != null) {
//            mRegisterTask.cancel(true);
//        }
//        try {
//            unregisterReceiver(mHandleMessageReceiver);
//            GCMRegistrar.onDestroy(this);
//        } catch (Exception e) {
//
//        }
//        super.onDestroy();
//    }
    public void registerUser(View v) {

        startActivity(new Intent(this, RegisterActivity.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void signinUser(View v) {

        new LoginTask().execute();

    }


    private class LoginTask extends AsyncTask<String, String, String> {

        int success;
        String username = username_et.getText().toString();
        String password = password_et.getText().toString();
        Resources res = getResources();
        String LOGIN_URL = Config.ROOT_URL + Config.WEB_SERVICES + "mobilelogin.php";
//        String url = "http://psite7.org/portal/psite7/mobilelogin";
        String url = Config.ROOT_URL + "mobilelogin";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setTitle("Logging in...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));
                params.add(new BasicNameValuePair("gcm_reg_id", AppController.getInstance().getGCM_registration_id()));

                JSONObject json = jsonParser.makeHttpRequest(
                        url, "POST", params);

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Login successfull!", json.toString());
                    String first_time_login;
                    Intent i = null;
                    first_time_login = json.getString("first_time_login");

                    Bundle bundle = new Bundle();
                    String username, mPid, mUsertype, mHas_voted, mHas_nominated;
                    mPid = json.getString(TAG_ID);
                    mUsertype = json.getString(TAG_USERTYPE);
                    mHas_voted = json.getString("has_voted");
                    mHas_nominated = json.getString("has_nominated");
                    username = json.getString("username");

                    if(first_time_login.equals("0")) {
                        i = new Intent(LoginActivity.this, MainActivity.class);
                        editor = sharedpreferences.edit();
                        editor.putString("pid", mPid);
                        editor.putString("usertype", mUsertype);
                        editor.putString("has_voted", mHas_voted);
                        editor.putString("has_nominated", mHas_nominated);
                        editor.putString("username", username);
                        editor.commit();

                    }else if(first_time_login.equals("1")){
                        i = new Intent(LoginActivity.this, FirstTimeLogin.class);
                    }


                    i.putExtra("pid", mPid);
                    i.putExtra("usertype", mUsertype);
                    i.putExtra("has_voted", mHas_voted);
                    i.putExtra("has_nominated", mHas_nominated);
                    i.putExtra("username", username);

                    i.putExtras(bundle);
                    startActivity(i);
                    finish();

                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Login failure", json.getString(TAG_MESSAGE));

                    return json.getString(TAG_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.wtf("error on log in", e.getMessage());
                toast = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            if (file_url != null) {
                Toast.makeText(LoginActivity.this, file_url, Toast.LENGTH_SHORT).show();

                Log.d("Login result", file_url);

            }
            if(toast){
                Toast.makeText(getApplicationContext(), "Can't connect right now. Try again later.", Toast.LENGTH_SHORT).show();
                toast = false;
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Back?
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Back
            finish();
         //   moveTaskToBack(true);
        }
            return super.onKeyDown(keyCode, event);
//            return true;
//        }
//        else {
//            // Return
//            return super.onKeyDown(keyCode, event);
//        }
    }

    private void registerGCM() {
        Log.wtf("CALLED","service called");
        Intent intent = new Intent(this, IntentServiceGCM.class);
        startService(intent);
    }


}
