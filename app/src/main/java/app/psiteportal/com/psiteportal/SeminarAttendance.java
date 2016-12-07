package app.psiteportal.com.psiteportal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Rating;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.psiteportal.com.model.Seminar;
import app.psiteportal.com.utils.Config;
import app.psiteportal.com.utils.JSONParser;
import app.psiteportal.com.utils.MyCertificatesAdapter;

/**
 * Created by Lawrence on 3/6/2016.
 */
public class SeminarAttendance extends AppCompatActivity implements View.OnClickListener{

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    String post, post_rate, post_comm;
    Button submit;
    RatingBar rating;
    TextView rate;
    TextView seminar_title_tv;
    EditText comment;
    ProgressDialog pDialog;
    String pid, sid, title, attendance_code;
    RelativeLayout qr_layout;
    LinearLayout rating_layout;
    Button scan_btn;

    String sequence;
    //
    RatingBar ratingBar;
    TextView textView;
    List<RatingBar> allratingBars = new ArrayList<RatingBar>();
    TextView txtComments;
    EditText edtComments;
    Button button;
    ArrayList<String> criteria = new ArrayList<String>();
    ArrayList<String> ratings = new ArrayList<String>();
    String get_criteria = "get_criteria.php";
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sem_attendance);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Attendance");

        rating = (RatingBar) findViewById(R.id.ratingBar);
        rate = (TextView) findViewById(R.id.txtRatingValue);
        comment = (EditText) findViewById(R.id.comm);
        submit = (Button) findViewById(R.id.btnSubmit);
        qr_layout = (RelativeLayout) findViewById(R.id.qr_layout);
        rating_layout = (LinearLayout) findViewById(R.id.rating_layout);
        scan_btn = (Button) findViewById(R.id.scan_code);
        seminar_title_tv = (TextView) findViewById(R.id.seminar_title_tv);

        txtComments = (TextView) findViewById(R.id.lblResult1);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pid = extras.getString("user_id");
            sid = extras.getString("seminar_id");
            title = extras.getString("seminar_title");
            attendance_code = extras.getString("attendance_code");
            sequence = extras.getString("sequence");
        }

//        txtComments = new TextView(SeminarAttendance.this);
//        txtComments.setText("Comments:");
//        txtComments.setLayoutParams(params1);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            txtComments.setTextAppearance(android.R.style.TextAppearance_Small);
//        }else{
//            txtComments.setTextAppearance(SeminarAttendance.this, android.R.style.TextAppearance_Small);
//        }
//        rating_layout.addView(txtComments);
//        getCriteria();
        getCertificates();
        submit.setOnClickListener(SeminarAttendance.this);
        seminar_title_tv.setText(title);


        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(ACTION_SCAN);
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, 0);
                } catch (ActivityNotFoundException anfe) {
                    showDialog(SeminarAttendance.this, "No Scanner Found", "Download the scanner application?", "Yes", "No").show();
                }
            }
        });

//        addListenerOnRatingBar();

    }

    private static AlertDialog showDialog(final FragmentActivity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return downloadDialog.show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                if(contents.equals(attendance_code)){
                    qr_layout.setVisibility(View.GONE);
                    rating_layout.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(this, "Code did not match, try again.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void addListenerOnRatingBar() {

        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                rate.setText(String.valueOf(rating));

            }
        });
    }

    @Override
    public void onClick(View v) {
         String strings[] = new String[allratingBars.size()];

        for(int i=0; i < allratingBars.size(); i++){
            ratings.add(String.valueOf((int)allratingBars.get(i).getRating()));

            Log.wtf("data", strings[i]);
        }
        new PostComment().execute();
    }

    class PostComment extends AsyncTask<String, String, String> {

        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        String ATTENDANCE_URL = Config.ROOT_URL + Config.WEB_SERVICES + "upload_attendance.php";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            post_rate = String.valueOf(rating.getRating());
            post_comm = comment.getText().toString();

            pDialog = new ProgressDialog(SeminarAttendance.this);
            pDialog.setMessage("Posting Comment...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("sid", sid));
                params.add(new BasicNameValuePair("pid", pid));
                params.add(new BasicNameValuePair("rating", 0 + ""));//post_rate));
                params.add(new BasicNameValuePair("comment", post_comm));

                params.add(new BasicNameValuePair("sequence", sequence));
                params.add(new BasicNameValuePair("count", count+""));
                int i = 0;
                int ii = 0;
                for(String object : criteria) {
                    params.add(new BasicNameValuePair("add_criteria[" + i + "]", object));
                    Log.wtf("email[" + i + "]", object);
                    i++;
                }
                for(String object : ratings) {
                    params.add(new BasicNameValuePair("add_rating[" + ii + "]", object));
                    Log.wtf("email[" + ii + "]", object);
                    ii++;
                }

                Log.d("request!", params.toString());

                // Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(ATTENDANCE_URL,
                        "POST", params);

                // full json response
                Log.d("Post Comment attempt", json.toString());

                // json success element
                success = json.getInt("success");
                if (success == 1) {
                    Log.d("Comment Added!", json.toString());
                    return json.getString("message");
                } else {
                    Log.d("Comment Failure!", json.getString("message"));
                    return json.getString("message");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(SeminarAttendance.this, file_url, Toast.LENGTH_LONG)
                        .show();
                finish();
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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

    private void getCriteria(){
        Log.wtf("sid", sid);

        RequestQueue requestQueue = Volley.newRequestQueue(SeminarAttendance.this);
        StringRequest stringRequest = new StringRequest(Config.ROOT_URL + Config.WEB_SERVICES + get_criteria,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String crits;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            JSONArray j = new JSONArray(response.toString());
                            Log.wtf("response", response.toString());
                            for(int i = 0; i < j.length(); i++){
                                JSONObject jsonObject = j.getJSONObject(i);
                                crits = jsonObject.getString("criteria_name");
                                count = Integer.parseInt(jsonObject.getString("count"));
                                criteria.add(crits);
                            }
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.gravity = Gravity.CENTER_HORIZONTAL;
                            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            rating_layout.removeAllViews();
                            rating_layout.addView(seminar_title_tv);
                            for(int i = 1; i <= count; i++){
                                ratingBar = new RatingBar(SeminarAttendance.this);
                                textView = new TextView(SeminarAttendance.this);
                                allratingBars.add(ratingBar);
                                ratingBar.setNumStars(5);
                                ratingBar.setStepSize(1);
                                ratingBar.setLayoutParams(params);
                                textView.setText(Html.fromHtml("<font color='#009688'>" + criteria.get(i) + "</font>"));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    textView.setTextAppearance(android.R.style.TextAppearance_Small);
                                }else{
                                    textView.setTextAppearance(SeminarAttendance.this, android.R.style.TextAppearance_Small);
                                }
                                textView.setLayoutParams(params1);
                                rating_layout.addView(textView);
                                rating_layout.addView(ratingBar);
                            }
                            rating_layout.addView(txtComments);
                            rating_layout.addView(comment);
                            rating_layout.addView(submit);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("sid", sid);
                return params;
            }
        };

        //Creating a request queue

        //Adding request to the queue
        requestQueue.add(stringRequest);

    }

    public void getCertificates(){

        RequestQueue queue = Volley.newRequestQueue(SeminarAttendance.this);
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + get_criteria,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        String crits;
//                        hidePDialog();
//                        loading.setVisibility(View.GONE);
                        try {
                            Log.wtf("bogo", s.toString());
                            JSONArray jsonArray = new JSONArray(s.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                crits = jsonObject.getString("criteria_name");
                                count = Integer.parseInt(jsonObject.getString("count"));
                                criteria.add(crits);
                            }
//                                if(jsonObject.getString("success").equals("0")){
//
//                                }else{
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT);
                                    params.gravity = Gravity.CENTER_HORIZONTAL;
                                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT);
                                    rating_layout.removeAllViews();
                                    rating_layout.addView(seminar_title_tv);
                                    for(int j = 1; j <= count; j++){
                                        ratingBar = new RatingBar(SeminarAttendance.this);
                                        textView = new TextView(SeminarAttendance.this);
                                        allratingBars.add(ratingBar);
                                        ratingBar.setNumStars(5);
                                        ratingBar.setStepSize(1);
                                        ratingBar.setLayoutParams(params);
                                        textView.setText(Html.fromHtml("<font color='#009688'>" + criteria.get(j-1) + "</font>"));
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            textView.setTextAppearance(android.R.style.TextAppearance_Small);
                                        }else{
                                            textView.setTextAppearance(SeminarAttendance.this, android.R.style.TextAppearance_Small);
                                        }
                                        textView.setLayoutParams(params1);
                                        rating_layout.addView(textView);
                                        rating_layout.addView(ratingBar);
                                    }
                                    rating_layout.addView(txtComments);
                                    rating_layout.addView(comment);
                                    rating_layout.addView(submit);
//                                }

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
                params.put("sid", sid);
                return params;
            }
        };
        queue.add(sr);
    }
}
