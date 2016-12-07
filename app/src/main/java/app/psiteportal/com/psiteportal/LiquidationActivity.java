package app.psiteportal.com.psiteportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TableRow;
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

import app.psiteportal.com.adapter.TransactionAdapter;
import app.psiteportal.com.model.Transaction;
import app.psiteportal.com.utils.Config;


public class LiquidationActivity extends AppCompatActivity {

    TextView total_collection, total_expenses, balance;
    ListView list;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    String user_pid, seminar_id;
    private List<Transaction> transactionList = new ArrayList<>();
    private FloatingActionButton btnFab;
    int collection, expenses;
    int pid;
    private String transactionsUrl = "transactions.php";
    private TransactionAdapter transactionAdapter;
    Map<String, Integer> mMap = new HashMap<String, Integer>();
    int promotion, food_catering, event_rentals, transportation, printing, audio_visual, venue_costs,
            on_site_expenses, others, other_expenses, sponsorship, registration_fees, donations;

    TextView promotion_tv, food_tv, event_rentals_tv, transportation_tv, printing_tv, audio_visual_tv, venue_cost_tv,
            on_site_expenses_tv, others_tv, other_expenses_tv, sponsorship_tv, registration_fee_tv, donations_tv, expense_summary, income_summary;

    TableRow table_promotion, table_food, table_rentals, table_transportation, table_printing, table_audio,
            table_venue_cost,table_on_site, table_others_expenses, table_sponsorship, table_registration,
            table_donations, table_others;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liquidation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            user_pid = bundle.getString("pid");
            seminar_id = bundle.getString("sid");
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        total_collection = (TextView) findViewById(R.id.total_collection);
        total_expenses = (TextView) findViewById(R.id.total_expenses);
        balance = (TextView) findViewById(R.id.total_balance);
        btnFab = (FloatingActionButton) findViewById(R.id.btnFloatingAction);


        promotion_tv = (TextView) findViewById(R.id.promotion_tv);
        food_tv = (TextView) findViewById(R.id.food_tv);
        event_rentals_tv = (TextView) findViewById(R.id.rentals_tv);
        transportation_tv = (TextView) findViewById(R.id.transportation_tv);
        printing_tv = (TextView) findViewById(R.id.printing_tv);
        audio_visual_tv = (TextView) findViewById(R.id.audiovisual_tv);
        venue_cost_tv = (TextView) findViewById(R.id.venue_costs_tv);
        on_site_expenses_tv = (TextView) findViewById(R.id.on_site_tv);
        other_expenses_tv = (TextView) findViewById(R.id.others_tv);
        others_tv = (TextView) findViewById(R.id.others_income_tv);
        sponsorship_tv = (TextView) findViewById(R.id.sponsorship_tv);
        registration_fee_tv = (TextView) findViewById(R.id.registration_fees_tv);
        donations_tv = (TextView) findViewById(R.id.donations_tv);
        expense_summary = (TextView) findViewById(R.id.expense_summary);
        income_summary = (TextView) findViewById(R.id.income_summary);


        table_promotion = (TableRow) findViewById(R.id.table_promotion);
        table_food = (TableRow) findViewById(R.id.table_food);
        table_rentals = (TableRow) findViewById(R.id.table_rentals);
        table_transportation = (TableRow) findViewById(R.id.table_transportation);
        table_printing = (TableRow) findViewById(R.id.table_printing);
        table_audio = (TableRow) findViewById(R.id.table_audio);
        table_venue_cost = (TableRow) findViewById(R.id.table_venue_cost);
        table_on_site = (TableRow) findViewById(R.id.table_on_site);
        table_others_expenses = (TableRow) findViewById(R.id.table_others_expenses);
        table_sponsorship = (TableRow) findViewById(R.id.table_sponsorship);
        table_registration = (TableRow) findViewById(R.id.table_registration);
        table_donations = (TableRow) findViewById(R.id.table_donations);
        table_others = (TableRow) findViewById(R.id.table_others);

        progressDialog = new ProgressDialog(this);
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


                                Integer previousValue = mMap.get(transaction_category);
                                if (previousValue == null) {
                                    previousValue = 0;
                                }
                                mMap.put(transaction_category, previousValue + Integer.parseInt(transaction_amount));

//                                mMap.put(transaction_category, Integer.parseInt(transaction_amount));

                                Log.wtf("my array", "" + mMap.toString());


//                                transaction = new Transaction(id, sid, pid, item_name, transaction_amount, check_number, transaction_photo, transaction_type, transaction_date);
//                                transactionList.add(transaction);

                                if (transaction_type.equals("Collection")) {
                                    collection = collection + Integer.parseInt(transaction_amount);
                                } else {
                                    expenses = expenses + Integer.parseInt(transaction_amount);
                                }


                                if (transaction_category.equals("Promotion")) {
                                    table_promotion.setVisibility(View.VISIBLE);
                                    promotion = promotion + Integer.parseInt(transaction_amount);
                                } else if (transaction_category.equals("Food and Catering")) {
                                    table_food.setVisibility(View.VISIBLE);
                                    food_catering = food_catering + Integer.parseInt(transaction_amount);
                                } else if (transaction_category.equals("Event Rentals")) {
                                    table_rentals.setVisibility(View.VISIBLE);
                                    event_rentals = event_rentals + Integer.parseInt(transaction_amount);
                                } else if (transaction_category.equals("Transportation")) {
                                    table_transportation.setVisibility(View.VISIBLE);
                                    transportation = transportation + Integer.parseInt(transaction_amount);
                                } else if (transaction_category.equals("Printing")) {
                                    table_printing.setVisibility(View.VISIBLE);
                                    printing = printing + Integer.parseInt(transaction_amount);
                                } else if (transaction_category.equals("Audio/Visual")) {
                                    table_audio.setVisibility(View.VISIBLE);
                                    audio_visual = audio_visual + Integer.parseInt(transaction_amount);
                                } else if (transaction_category.equals("Venue Costs")) {
                                    table_venue_cost.setVisibility(View.VISIBLE);
                                    venue_costs = venue_costs + Integer.parseInt(transaction_amount);
                                } else if (transaction_category.equals("On-Site Expenses")) {
                                    table_on_site.setVisibility(View.VISIBLE);
                                    on_site_expenses = on_site_expenses + Integer.parseInt(transaction_amount);
                                } else if (transaction_category.equals("Other Expenses")) {
                                    table_others_expenses.setVisibility(View.VISIBLE);
                                    other_expenses = other_expenses + Integer.parseInt(transaction_amount);
                                } else if (transaction_category.equals("Sponsorship")) {
                                    table_sponsorship.setVisibility(View.VISIBLE);
                                    sponsorship = sponsorship + Integer.parseInt(transaction_amount);
                                } else if (transaction_category.equals("Donations")) {
                                    table_donations.setVisibility(View.VISIBLE);
                                    donations = donations + Integer.parseInt(transaction_amount);
                                } else if (transaction_category.equals("Registration Fees")) {
                                    table_registration.setVisibility(View.VISIBLE);
                                    registration_fees = registration_fees + Integer.parseInt(transaction_amount);
                                } else if (transaction_category.equals("Others")) {
                                    table_others.setVisibility(View.VISIBLE);
                                    others = others + Integer.parseInt(transaction_amount);
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        total_collection.setText("₱ " + collection + "");
                        total_expenses.setText("₱ " + expenses + "");
                        balance.setText("₱ " + (collection - expenses) + "");

                        promotion_tv.setText("₱ " + promotion + "");
                        food_tv.setText("₱ " + food_catering + "");
                        event_rentals_tv.setText("₱ " + event_rentals + "");
                        transportation_tv.setText("₱ " + transportation + "");
                        printing_tv.setText("₱ " + printing + "");
                        audio_visual_tv.setText("₱ " + audio_visual + "");
                        venue_cost_tv.setText("₱ " + venue_costs + "");
                        on_site_expenses_tv.setText("₱ " + on_site_expenses + "");
                        other_expenses_tv.setText("₱ " + other_expenses + "");
                        sponsorship_tv.setText("₱ " + sponsorship + "");
                        donations_tv.setText("₱ " + donations + "");
                        registration_fee_tv.setText("₱ " + registration_fees + "");
                        others_tv.setText("₱ " + others + "");
                        income_summary.setText("₱ " + collection + "");
                        expense_summary.setText("₱ " + expenses + "");

//                        transactionAdapter.notifyDataSetChanged();
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

//        transactionAdapter = new TransactionAdapter(this, transactionList);

        setupUI();

    }

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void setupUI() {
        btnFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(LiquidationActivity.this, AddExpenseActivity.class);
                i.putExtra("sid", seminar_id);
                i.putExtra("pid", user_pid);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.liquidation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.view_listings:
                Intent i = new Intent(LiquidationActivity.this, TransactionListings.class);
                i.putExtra("seminar_id", seminar_id);
                startActivity(i);
                return true;
            case R.id.summary_report:
                Intent e = new Intent(LiquidationActivity.this, SummaryReport.class);
                e.putExtra("seminar_id", seminar_id);
                startActivity(e);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
