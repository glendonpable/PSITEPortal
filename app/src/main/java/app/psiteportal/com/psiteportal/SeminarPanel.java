package app.psiteportal.com.psiteportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.psiteportal.com.utils.Config;
import app.psiteportal.com.utils.JSONParser;

import static com.android.volley.Request.Method;

/**
 * Created by Lawrence on 10/6/2015.
 */
public class SeminarPanel extends AppCompatActivity {

    String user_id;
    String is_active;
    String out_activated;
    String sem_id;
    Switch out_switch;
    String activate_out_seminar;
    ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    int success;
    public final String TAG_MESSAGE = "message";
    public final String TAG_SUCCESS = "success";
    public final String TAG_OUT_ACTIVATED = "1";
    public final String TAG_OUT_DEACTIVATED = "0";
    public final String TAG_DEACTIVATE_SEMINAR = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seminar_panel_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Configurations");

        out_switch = (Switch) findViewById(R.id.attendance_switch);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user_id = extras.getString("user_id");
            sem_id = extras.getString("seminar_id");
        }



        getSeminar(sem_id);

        out_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    activate_out_seminar = TAG_OUT_ACTIVATED;
                    new ActivateAttendance().execute();

                }else{
                    activate_out_seminar = TAG_OUT_DEACTIVATED;
                    new ActivateAttendance().execute();
                }
            }
        });


    }

    class UpdateConfig extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        String UPDATE_CONFIG_URL = Config.ROOT_URL + Config.WEB_SERVICES + "update_seminar_config.php";
        String message;

//        String dateValue =  deactivate_seminar.getText().toString();
//        String amountValue = amount.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SeminarPanel.this);
            pDialog.setMessage("Updating configurations. . . ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("sid", sem_id));
                params.add(new BasicNameValuePair("isActive", TAG_DEACTIVATE_SEMINAR));

                Log.e("params to be passed", params.toString());

                JSONObject json = jsonParser.makeHttpRequest(UPDATE_CONFIG_URL, "POST", params);

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    message = json.getString(TAG_MESSAGE);
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();

            if (success == 1) {
                Toast.makeText(SeminarPanel.this, message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SeminarPanel.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }



    class CancelSeminar extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        String UPDATE_CONFIG_URL = Config.ROOT_URL + Config.WEB_SERVICES + "cancel_seminar_config.php";
        String message;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SeminarPanel.this);
            pDialog.setMessage("Updating configurations. . . ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("sid", sem_id));

                Log.e("params to be passed", params.toString());

                JSONObject json = jsonParser.makeHttpRequest(UPDATE_CONFIG_URL, "POST", params);

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    message = json.getString(TAG_MESSAGE);
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();

            if (success == 1) {
                Toast.makeText(SeminarPanel.this, message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SeminarPanel.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }










    public void getSeminar(String id){

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        String url = Config.ROOT_URL + Config.WEB_SERVICES + "get_seminar.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("sid", id);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        hidePDialog();
                        try{
                            is_active = jsonObject.getString("isActive");
                            out_activated = jsonObject.getString("out_activated");

                            if(out_activated.equals("0")){
                                out_switch.setChecked(false);
                            }else{
                                out_switch.setChecked(true);
                            }

                            Log.wtf("is active", is_active);
                            Log.wtf("out_activated", out_activated);

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
        queue.add(jsonObjectRequest);

    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


    class ActivateAttendance extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        String UPDATE_ATTENDANCE = Config.ROOT_URL + Config.WEB_SERVICES + "enable_attendance.php";
        String message;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SeminarPanel.this);
            pDialog.setMessage("Updating configurations. . . ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("sid", sem_id));
                params.add(new BasicNameValuePair("out_activated", activate_out_seminar));

                JSONObject json = jsonParser.makeHttpRequest(UPDATE_ATTENDANCE, "POST", params);

                Log.wtf("testestest", activate_out_seminar);

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    message = json.getString(TAG_MESSAGE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();

            if (success == 1) {
                Toast.makeText(SeminarPanel.this, message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SeminarPanel.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void editSeminar(View v){
        Intent i = new Intent(this, EditSeminar.class);
        i.putExtra("sid", sem_id);
        startActivity(i);
    }

    public void cancelSeminar(View v){
//        Toast.makeText(this, "Deactivate clicked", Toast.LENGTH_SHORT).show();
        new CancelSeminar().execute();
    }

    public void deactivateSeminar(View v){
//        Toast.makeText(this, "Deactivate clicked", Toast.LENGTH_SHORT).show();
        new UpdateConfig().execute();
    }



}
