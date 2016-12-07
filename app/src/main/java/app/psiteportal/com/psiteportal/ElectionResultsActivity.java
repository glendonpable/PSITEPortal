package app.psiteportal.com.psiteportal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.psiteportal.com.utils.Config;

/**
 * Created by fmpdroid on 7/6/2016.
 */
public class ElectionResultsActivity extends AppCompatActivity {

    private TextView title;
    private TextView nomination_date_time;
    private TextView election_date_time;
    private TextView nomination_status;
    private TextView election_status;
    private String get_status = "get_status.php";
    private String register_data = "register_data.php";
    private RelativeLayout relativeLayout;
    private Button button;
    private String is_viewable;
    private ProgressDialog progressDialog;
    private int result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_nomination_election);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Release Election Result");

        title = (TextView) findViewById(R.id.title);
        nomination_date_time = (TextView) findViewById(R.id.nomination_date_time);
        election_date_time = (TextView) findViewById(R.id.election_date_time);
        nomination_status = (TextView) findViewById(R.id.nomination_status);
        election_status = (TextView) findViewById(R.id.election_status);
        relativeLayout = (RelativeLayout) findViewById(R.id.loadingPanel);
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ElectionResultsActivity.this)
                        .setTitle("Confirmation!")
                        .setMessage("Are you sure?")
                        .setIcon(R.drawable.ic_warning)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                if(is_viewable.equals("0")){
                                    result = 1;
                                }else{
                                    result = 0;
                                }
                                registerData(result);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        getData();
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.admin_nomination_election, container, false);
//
//        title = (TextView) rootView.findViewById(R.id.title);
//        nomination_date_time = (TextView) rootView.findViewById(R.id.nomination_date_time);
//        election_date_time = (TextView) rootView.findViewById(R.id.election_date_time);
//        nomination_status = (TextView) rootView.findViewById(R.id.nomination_status);
//        election_status = (TextView) rootView.findViewById(R.id.election_status);
//        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.loadingPanel);
//        button = (Button) rootView.findViewById(R.id.button);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new AlertDialog.Builder(ElectionResultsActivity.this)
//                        .setTitle("Confirmation!")
//                        .setMessage("Are you sure?")
//                        .setIcon(R.drawable.ic_warning)
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                if(is_viewable.equals("0")){
//                                    result = 1;
//                                }else{
//                                    result = 0;
//                                }
//                                registerData(result);
//                            }
//                        })
//                        .setNegativeButton(android.R.string.no, null).show();
//            }
//        });
//
//        getData();
//
//        return rootView;
//    }


    public void getData(){

        RequestQueue queue = Volley.newRequestQueue(ElectionResultsActivity.this);
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + get_status,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
//                        hidePDialog();
                        relativeLayout.setVisibility(View.GONE);
                        try {
                            JSONArray jsonArray = new JSONArray(s.toString());
                            Log.wtf("glendon", jsonArray.toString());
                            for(int i = 0; i < jsonArray.length(); i++) {
                                String nom_status = null, status = null;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                is_viewable = jsonObject.getString("is_viewable");
                                title.setText(jsonObject.getString("election_title"));
                                nomination_date_time.setText(jsonObject.getString("nom_date") + "(" +
                                        jsonObject.getString("nom_start_time") + "-" + jsonObject.getString("nom_end_time") + ")");
                                election_date_time.setText(jsonObject.getString("election_date") + "(" +
                                        jsonObject.getString("start_time") + "-" + jsonObject.getString("end_time") + ")");

                                if(jsonObject.getString("isCancel").equals("1")) {
                                    nom_status = "Cancelled";
                                    status = "Cancelled";
                                }else {
                                    if (jsonObject.getString("nom_status").equals("0")) {
                                        nom_status = "Inactive";
                                    } else if (jsonObject.getString("nom_status").equals("1")) {
                                        nom_status = "Active";
                                    } else if (jsonObject.getString("nom_status").equals("2")) {
                                        nom_status = "Completed";
                                    }
                                    if (jsonObject.getString("status").equals("0")) {
                                        status = "Inactive";
                                    } else if (jsonObject.getString("status").equals("1")) {
                                        status = "Active";
                                    } else if (jsonObject.getString("status").equals("2")) {
                                        status = "Completed";
                                    }
                                }
                                nomination_status.setText(nom_status);
                                election_status.setText(status);

                                if(!(nomination_status.getText().toString().equals("Completed")&&election_status.getText().toString().equals("Completed"))){
//                                    button.setClickable(false);
                                    button.setVisibility(View.GONE);
                                }
                                if (is_viewable.equals("0")) {
                                    button.setText("Publish Result");
                                }else {
                                    button.setText("Unpublish Result");
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.wtf("hahay", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });queue.add(sr);
    }

    public void registerData(final int result){
        progressDialog = new ProgressDialog(ElectionResultsActivity.this);
        // Showing progress dialog before making http request
        progressDialog.setMessage("Adding Info...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(ElectionResultsActivity.this);
        String url = Config.ROOT_URL + Config.WEB_SERVICES + register_data;//"http://psite7.org/portal/webservices/register_data.php";
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
                                Toast.makeText(ElectionResultsActivity.this, message, Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(ElectionResultsActivity.this,message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ElectionResultsActivity.this,"There's something wrong while processing your request. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("is_viewable", String.valueOf(result));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
