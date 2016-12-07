package app.psiteportal.com.psiteportal;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import app.psiteportal.com.adapter.TransactionAdapter;
import app.psiteportal.com.model.Transaction;
import app.psiteportal.com.utils.Config;

/**
 * Created by Lawrence on 3/15/2016.
 */
public class TransactionListings extends AppCompatActivity {


    ProgressDialog progressDialog;
    private String transactionsUrl = "transactions.php";
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList = new ArrayList<>();
    String seminar_id;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            seminar_id = bundle.getString("seminar_id");
            Log.e("sem id sa listing", seminar_id);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        progressDialog = new ProgressDialog(TransactionListings.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + transactionsUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        hidePDialog();
                        try {
                            JSONArray jsonArray = new JSONArray(s.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String id, sid, pid, item_name, transaction_amount, check_number, recipient, routing_number,
                                        account_number, transaction_photo, transaction_type, transaction_category, transaction_date;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Transaction transaction;
                                id = jsonObject.getString("transaction_id");
                                sid = jsonObject.getString("sid");
                                pid = jsonObject.getString("pid");
                                item_name = jsonObject.getString("item_name");
                                transaction_amount = jsonObject.getString("transaction_amount");
                                check_number = jsonObject.getString("check_number");
                                recipient = jsonObject.getString("recipient");
                                routing_number = jsonObject.getString("routing_number");
                                account_number = jsonObject.getString("account_number");
                                transaction_photo = jsonObject.getString("transaction_photo");
                                transaction_type = jsonObject.getString("transaction_type");
                                transaction_category = jsonObject.getString("transaction_category");
                                transaction_date = jsonObject.getString("transaction_date");

                                transaction = new Transaction(id, sid, pid, item_name, transaction_amount, check_number, transaction_photo, transaction_type, transaction_date);
                                transactionList.add(transaction);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        transactionAdapter.notifyDataSetChanged();
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

        transactionAdapter = new TransactionAdapter(this, transactionList);
        recyclerView.setAdapter(transactionAdapter);

    }

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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