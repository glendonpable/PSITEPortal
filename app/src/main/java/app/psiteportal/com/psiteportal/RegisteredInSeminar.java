package app.psiteportal.com.psiteportal;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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

import app.psiteportal.com.adapter.UsersAdapter;
import app.psiteportal.com.model.User;
import app.psiteportal.com.utils.Config;

/**
 * Created by Lawrence on 3/16/2016.
 */
public class RegisteredInSeminar extends AppCompatActivity {

    String seminar_id;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    private String listUrl = "registered_in_seminar.php";
    private UsersAdapter userAdapter;
    private List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_in_seminar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            seminar_id = extras.getString("seminar_id");
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        progressDialog = new ProgressDialog(RegisteredInSeminar.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + listUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        hidePDialog();
                        try {
                            JSONArray jsonArray = new JSONArray(s.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String firstname, lastname, activated, prof_pic;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                User user;
                                firstname = jsonObject.getString("firstname");
                                lastname = jsonObject.getString("lastname");
                                activated = jsonObject.getString("activated");
                                prof_pic = jsonObject.getString("prof_pic");

                                user = new User(firstname, lastname, activated, prof_pic);
                                userList.add(user);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        userAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("sid", seminar_id);
                return params;
            }
        };
        queue.add(sr);

        userAdapter = new UsersAdapter(this, userList);
        recyclerView.setAdapter(userAdapter);
    }

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
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

}
