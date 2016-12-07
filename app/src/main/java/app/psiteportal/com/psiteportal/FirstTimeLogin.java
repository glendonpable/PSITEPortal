package app.psiteportal.com.psiteportal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.psiteportal.com.test.MainActivity;
import app.psiteportal.com.utils.Config;

/**
 * Created by fmpdroid on 7/3/2016.
 */
public class FirstTimeLogin extends AppCompatActivity{

    private EditText username_txt, password_txt, repassword_txt, contact_txt, email_txt, address_txt;
    private ProgressDialog progressDialog;
    private Button register;
    private String pid, usertype, has_voted, has_nominated;

    public static final String MyPREFERENCES = "MyPrefsPSITE" ;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_time_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Registration");

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        username_txt = (EditText) findViewById(R.id.username_txt);
        password_txt = (EditText) findViewById(R.id.password_txt);
        repassword_txt = (EditText) findViewById(R.id.repassword_txt);
        contact_txt = (EditText) findViewById(R.id.contact_txt);
        email_txt = (EditText) findViewById(R.id.email_txt);
        address_txt = (EditText) findViewById(R.id.address_txt);
        register = (Button) findViewById(R.id.register_btn);

        username_txt.setText(getIntent().getStringExtra("username"));
        pid = getIntent().getStringExtra("pid");
        usertype = getIntent().getStringExtra("usertype");
        has_voted = getIntent().getStringExtra("has_voted");
        has_nominated = getIntent().getStringExtra("has_nominated");

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username, password, repassword, contact, email, address;
                username = username_txt.getText().toString();
                password = password_txt.getText().toString();
                repassword = repassword_txt.getText().toString();
                contact = contact_txt.getText().toString();
                email = email_txt.getText().toString();
                address = address_txt.getText().toString();

                if(username.isEmpty() || password.isEmpty() || repassword.isEmpty() ||
                        contact.isEmpty() || email.isEmpty() || address.isEmpty()){
                    Toast.makeText(getApplicationContext(), "One or more fields are missing. Please check and try again.",
                            Toast.LENGTH_LONG).show();
                }else{
                    if(!password.equals(repassword)){
                        Toast.makeText(getApplicationContext(), "Passwords did not match. Try again",
                                Toast.LENGTH_LONG).show();
                        password_txt.setText("");
                        repassword_txt.setText("");
                    }else{
                        new AlertDialog.Builder(FirstTimeLogin.this)
                                .setTitle("Warning!")
                                .setMessage("Once you click OK, your username and password will be permanent. Shall we proceed?")
                                .setIcon(R.drawable.ic_warning)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        registerData(username, password, contact, email, address);
                                        editor = sharedpreferences.edit();
                                        editor.putString("pid", pid);
                                        editor.putString("usertype", usertype);
                                        editor.putString("has_voted", has_voted);
                                        editor.putString("has_nominated", has_nominated);
                                        editor.putString("username", username);
                                        editor.commit();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();
                    }
                }

            }
        });
    }

    public void registerData(final String username, final String password, final String contact, final String email, final String address){
        progressDialog = new ProgressDialog(FirstTimeLogin.this);
        // Showing progress dialog before making http request
        progressDialog.setMessage("Adding Info...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = Config.ROOT_URL + "updateUser/"+pid;
        StringRequest sr = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        int success;
                        String message;
                        hidePDialog();
                        try {
                            JSONObject object = new JSONObject(s.toString());
                            success = object.getInt("success");
                            message = object.getString("message");
                            Log.wtf("success", String.valueOf(success));
                            Log.wtf("message", object.getString("message"));
//                            success = object.getInt("success");
                            if(success==1){
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(FirstTimeLogin.this, MainActivity.class);
                                i.putExtra("pid", pid);
                                i.putExtra("usertype", usertype);
                                i.putExtra("has_voted", has_voted);
                                i.putExtra("has_nominated", has_nominated);
                                startActivity(i);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
                            }

                            //JSONObject jsonObject = jsonArray.getJSONObject(i);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                hidePDialog();
                Toast.makeText(getApplicationContext(),"There's something wrong while processing your request. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("pid", pid);
                params.put("username", username);
                params.put("password", password);
                params.put("contact", contact);
                params.put("email", email);
                params.put("address", address);
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
}
