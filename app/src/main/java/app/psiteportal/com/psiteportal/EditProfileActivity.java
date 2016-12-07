package app.psiteportal.com.psiteportal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.psiteportal.com.utils.Config;
import app.psiteportal.com.utils.JSONParser;

/**
 * Created by fmpdroid on 4/19/2016.
 */
public class EditProfileActivity extends AppCompatActivity {

    private EditText edt_first_name, edt_last_name, edt_contact, edt_email, edt_address;
    private ProgressDialog progressDialog;
    private String pid;
    private Spinner spinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Edit Information");

        edt_first_name = (EditText) findViewById(R.id.editText_firstname);
        edt_last_name = (EditText) findViewById(R.id.editText_lastname);
        edt_contact = (EditText) findViewById(R.id.editText_contact);
        edt_email = (EditText) findViewById(R.id.editText_email);
        edt_address = (EditText) findViewById(R.id.editText_address);
        spinner = (Spinner) findViewById(R.id.edit_institution_spinner);

        pid = getIntent().getExtras().getString("pid");
        getUserTask();
    }

    public void getUserTask() {
        final List<String> institutions = new ArrayList<String>();
        progressDialog = new ProgressDialog(EditProfileActivity.this);
        // Showing progress dialog before making http request
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String url = Config.ROOT_URL + Config.WEB_SERVICES + "get_user.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        hidePDialog();
                        LinearLayout layout = (LinearLayout) findViewById(R.id.holder);
                        final String firstname, lastname, email, address, institution, contact;
                        //hidePDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(s.toString());
                            firstname = jsonObject.getString("firstname");
                            lastname = jsonObject.getString("lastname");
                            institution = jsonObject.getString("institution");
                            address = jsonObject.getString("address");
                            email = jsonObject.getString("email");
                            contact = jsonObject.getString("contact");
                            Log.wtf("edit", firstname);
                            edt_first_name.setText(firstname);
                            edt_last_name.setText(lastname);
                            edt_email.setText(email);
//                            edt.setText(institution);
                            edt_address.setText(address);
                            edt_contact.setText(contact);


                            Thread t = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    JSONParser jsonParser = new JSONParser();
                                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                                    params.add(new BasicNameValuePair("email", email));
                                    JSONObject json = jsonParser.makeHttpRequest(Config.ROOT_URL + Config.WEB_SERVICES + "get_institutions.php", "POST", params);
                                    try {
                                        JSONArray jsonArray = json.getJSONArray("institutions");
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject c = jsonArray.getJSONObject(i);
                                            institutions.add(c.getString("institution_name"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            t.start();
                            try {
                                t.join();
                                final ArrayAdapter<String> adp = new ArrayAdapter<String>(EditProfileActivity.this,
                                        android.R.layout.simple_spinner_dropdown_item, institutions);
                                spinner.setAdapter(adp);
                                int spinnerPosition = adp.getPosition(institution);
                                spinner.setSelection(spinnerPosition);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        Log.wtf("error edit",volleyError.getMessage());
                        hidePDialog();
                        Activity activity = EditProfileActivity.this;
                        if (volleyError instanceof NoConnectionError) {
                            String errormsg = "Check your internet connection";
                            Toast.makeText(activity, errormsg, Toast.LENGTH_LONG).show();
                        }
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("pid", pid);
                return params;
            }
        };
        queue.add(sr);
    }

    public void updateUser() {
        progressDialog = new ProgressDialog(EditProfileActivity.this);
        // Showing progress dialog before making http request
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String url = Config.ROOT_URL + Config.WEB_SERVICES + "update_user.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        hidePDialog();
                        Log.wtf("result", s.substring(0));
                        try {
                            JSONObject object = new JSONObject(s);
                            if(object.getString("success").equals("1")){
                                Toast.makeText(getApplicationContext(), "User information successfully updated! Please refresh the page.", Toast.LENGTH_LONG).show();
                                finish();
                            }else if(object.getString("success").equals("0")){
                                Toast.makeText(getApplicationContext(),"Update failed. Please try again.", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Update failed. Please try again.", Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Activity activity = EditProfileActivity.this;
                        if (volleyError instanceof NoConnectionError) {
                            String errormsg = "Check your internet connection";
                            Toast.makeText(activity, errormsg, Toast.LENGTH_LONG).show();
                        }
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("email",email);//String.valueOf(AppController.getInstance().getNomination_sid()));
                params.put("pid", pid);
                params.put("firstname", edt_first_name.getText().toString());
                params.put("lastname", edt_last_name.getText().toString());
                params.put("contact", edt_contact.getText().toString());
                params.put("email", edt_email.getText().toString());
                params.put("address", edt_address.getText().toString());
                params.put("institution", spinner.getSelectedItem().toString());
                return params;
            }
            };queue.add(sr);
    }

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_done:
                // do someing
                updateUser();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
