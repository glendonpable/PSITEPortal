package app.psiteportal.com.psiteportal;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.psiteportal.com.model.Seminar;
import app.psiteportal.com.utils.Config;
import app.psiteportal.com.utils.MyCertificatesAdapter;

/**
 * Created by fmpdroid on 8/16/2016.
 */
public class MyCertificatesActivity extends AppCompatActivity{

    private List<Seminar> seminarList = new ArrayList<>();
    private String get_certificates = "get_certificates.php";
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    MyCertificatesAdapter adapter;
    RelativeLayout loading;
    RelativeLayout no_cert;
    public static String pid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_certificates_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("My Seminars Attended");

        loading = (RelativeLayout) findViewById(R.id.loadingPanel);
        no_cert = (RelativeLayout) findViewById(R.id.no_certificates);
        recyclerView = (RecyclerView) findViewById(R.id.my_seminars_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        pid = String.valueOf(getIntent().getIntExtra("pid", 0));
        no_cert.setVisibility(View.GONE);
        getCertificates();
    }

    public void getCertificates(){

        RequestQueue queue = Volley.newRequestQueue(MyCertificatesActivity.this);
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + get_certificates,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        hidePDialog();
                        loading.setVisibility(View.GONE);
                        try {
                            JSONArray jsonArray = new JSONArray(s.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String sid, seminarName, date;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Seminar seminar;
                                if(jsonObject.getString("success").equals("0")){
                                    //has not attended seminars yet
                                    no_cert.setVisibility(View.VISIBLE);
                                }else{
                                    sid = jsonObject.getString("sid");
                                    seminarName = jsonObject.getString("seminar_title");
                                    date = jsonObject.getString("seminar_date");
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                                    Date newDate = dateFormat.parse(date);
                                    dateFormat = new SimpleDateFormat("MM/dd/yyyy");

                                    seminar = new Seminar(sid, seminarName, dateFormat.format(newDate));
                                    seminarList.add(seminar);
                                }
                            }
                            adapter = new MyCertificatesAdapter(getApplicationContext(), seminarList);
                            recyclerView.setAdapter(adapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.wtf("hahay", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("pid", pid);
                return params;
            }
        };
        queue.add(sr);
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
