package app.psiteportal.com.psiteportal;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
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

import java.util.HashMap;
import java.util.Map;

import app.psiteportal.com.model.Transaction;
import app.psiteportal.com.utils.Config;

/**
 * Created by Lawrence on 3/16/2016.
 */
public class SummaryReport extends AppCompatActivity {

    String seminar_id;
    TextView id_tv, date_tv, item_tv, type_tv, category_tv, amount_tv, total_income_tv, total_expense_tv, balance_tv;
    ProgressDialog progressDialog;
    private String transactionsUrl = "transactions.php";
    StringBuilder idBuilder = new StringBuilder();
    StringBuilder dateBuilder = new StringBuilder();
    StringBuilder itemBuilder = new StringBuilder();
    StringBuilder typeBuilder = new StringBuilder();
    StringBuilder categoryBuilder = new StringBuilder();
    StringBuilder amountBuilder = new StringBuilder();
    int collection, expense, balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_report);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            seminar_id = bundle.getString("seminar_id");
            Log.e("sem id sa summary", seminar_id);
        }


        id_tv = (TextView) findViewById(R.id.trans_id_tv);
        date_tv = (TextView) findViewById(R.id.date_tv);
        item_tv = (TextView) findViewById(R.id.item_tv);
        type_tv = (TextView) findViewById(R.id.type_tv);
        category_tv = (TextView) findViewById(R.id.category_tv);
        amount_tv = (TextView) findViewById(R.id.amount_tv);
        total_income_tv = (TextView) findViewById(R.id.total_income_tv);
        total_expense_tv = (TextView) findViewById(R.id.total_expense_tv);
        balance_tv = (TextView) findViewById(R.id.balance_tv);


        progressDialog = new ProgressDialog(SummaryReport.this);
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

                                if (transaction_type.equals("Collection")) {
                                    collection = collection + Integer.parseInt(transaction_amount);
                                } else {
                                    expense = expense + Integer.parseInt(transaction_amount);
                                }


                                idBuilder.append(id + "\n\n");
                                dateBuilder.append(transaction_date +"\n\n");
                                itemBuilder.append(item_name + "\n\n");
                                typeBuilder.append(transaction_type + "\n\n");
                                categoryBuilder.append(transaction_category + "\n\n");
                                amountBuilder.append("₱" +transaction_amount + "\n\n");

                                id_tv.setText(idBuilder.toString());
                                date_tv.setText(dateBuilder.toString());
                                item_tv.setText(itemBuilder.toString());
                                type_tv.setText(typeBuilder.toString());
                                category_tv.setText(categoryBuilder.toString());
                                amount_tv.setText(amountBuilder.toString());

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        total_income_tv.setText("₱" + collection);
                        total_expense_tv.setText("₱" + expense);
                        balance_tv.setText("₱" + (collection - expense));

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
