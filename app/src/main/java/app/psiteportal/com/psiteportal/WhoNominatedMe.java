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
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.Config;
import app.psiteportal.com.utils.WhoNominatedMeAdapter;

public class WhoNominatedMe extends AppCompatActivity{

    private RecyclerView recyclerView;
    private WhoNominatedMeAdapter adapter;
    private ProgressDialog progressDialog;
    private List<Nominee> nomineeList = new ArrayList<>();
    private String email;
    private String get_who_nominated = "get_who_nominated.php";
    private TextView timer, info;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.who_nominated_container);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        Toolbar toolbar = new Toolbar(this);
//        Toolbar.LayoutParams toolBarParams = new Toolbar.LayoutParams(
//                Toolbar.LayoutParams.MATCH_PARENT,
//                R.attr.actionBarSize
//        );
//        toolbar.setLayoutParams(toolBarParams);
//        toolbar.setBackgroundColor(Color.BLUE);
//        toolbar.setPopupTheme(android.R.style.ThemeOverlay_Material_Dark_ActionBar);
//        toolbar.setVisibility(View.VISIBLE);
//        setSupportActionBar(toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setElevation(10f);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(WhoNominatedMe.this));

        email = getIntent().getStringExtra("email");
        getData(email);
    }

    public void getData(final String email) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + get_who_nominated,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        hidePDialog();
                        try {
                            JSONArray jsonArray = new JSONArray(s.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String imageUrl, username, name, first_name, last_name, institution, contact, email, address;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Nominee nominee;
                                imageUrl = jsonObject.getString("prof_pic");
                                username = jsonObject.getString("username");
                                first_name = jsonObject.getString("firstname");
                                last_name = jsonObject.getString("lastname");
                                institution = jsonObject.getString("institution_name");
                                contact = jsonObject.getString("contact");
                                email = jsonObject.getString("email");
                                address = jsonObject.getString("address");
                                name = first_name + " " + last_name;
                                nominee = new Nominee(imageUrl, username, name, institution, contact, email, address);
                                Log.wtf("glendon", name);
                                nomineeList.add(nominee);
                            }

                            adapter = new WhoNominatedMeAdapter(WhoNominatedMe.this, nomineeList);
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
                params.put("election_id", AppController.getInstance().getElection_id());//String.valueOf(AppController.getInstance().getNomination_sid()));
                params.put("email", email);
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
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
