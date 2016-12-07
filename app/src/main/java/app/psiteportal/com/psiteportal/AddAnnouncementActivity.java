package app.psiteportal.com.psiteportal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import app.psiteportal.com.utils.Config;
import app.psiteportal.com.utils.SntpClient;

/**
 * Created by fmpdroid on 5/3/2016.
 */
public class AddAnnouncementActivity extends AppCompatActivity{

    private ImageButton date, time;
    private Button btnCreate;
    private TextView textView_date, textView_time;
    private int mHour, mMinute;
    private Calendar calendar;
    private int mYear, mMonth, mDay;
    private static final int DATE = 0, TIME = 1;
    private EditText title, details, venue;
    private ProgressDialog progressDialog;
    private String pid;
    private String schedule, schedule_time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_announcement);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Add Announcement");

//        date = (ImageButton) findViewById(R.id.selectAnnouncementDate);
//        time = (ImageButton) findViewById(R.id.selectAnnouncementTime);
        btnCreate = (Button) findViewById(R.id.announcementBtnSubmit);
//        textView_date = (TextView) findViewById(R.id.txt_announcementDate);
//        textView_time = (TextView) findViewById(R.id.txt_announcementTime);
        title = (EditText) findViewById(R.id.editText_title);
        details = (EditText) findViewById(R.id.editText_details);
        venue = (EditText) findViewById(R.id.editText_venue);

        pid = getIntent().getExtras().getString("user_pid");

        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

//        date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDialog(DATE);
//            }
//        });
//
//        time.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDialog(TIME);
//            }
//        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().isEmpty() || details.getText().toString().isEmpty()) {
                    Toast.makeText(AddAnnouncementActivity.this, "Oh no! One or more fields are missing!", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(AddAnnouncementActivity.this)
                            .setTitle("Add Announcement")
                            .setMessage("Are you sure to add this announcement?")
                            .setIcon(R.drawable.ic_warning)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
//                                    addAnnouncement();
                                    new AnnouncementAsync().execute();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            }
        });
    }

    private class AnnouncementAsync extends AsyncTask<Void, Void, String>{

        String final_date;

        @Override
        protected String doInBackground(Void... params) {
            SntpClient client = new SntpClient();
            if (client.requestTime("0.pool.ntp.org", 30000)) {
                long current_time = client.getNtpTime();
                Date current_date = new Date(current_time);
                SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd");
                String date = sdfDate.format(current_date);
                String time = sdfTime.format(current_date);
                final_date  = date + " " + time;
                Log.wtf("announcement date", date);
                Log.wtf("announcement time", time);

            }else{
                final_date = "";
            }
            return final_date;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            addAnnouncement(result);
        }
    }

    public void addAnnouncement(String schedule){


//        sched = textView_date.getText().toString() + " " + textView_time.getText().toString()+":00";
        final String sched = schedule;
        if(!sched.isEmpty()) {
            progressDialog = new ProgressDialog(AddAnnouncementActivity.this);
            // Showing progress dialog before making http request
            progressDialog.setMessage("Creating...");
            progressDialog.show();
            RequestQueue queue = Volley.newRequestQueue(AddAnnouncementActivity.this);
            StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + "add_announcement.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            int success;
                            hidePDialog();
                            try {
                                JSONObject object = new JSONObject(s.toString());
                                success = object.getInt("success");
                                Log.wtf("success", String.valueOf(success));
                                Log.wtf("message", object.getString("message"));
//                            success = object.getInt("success");
                                if (success == 1) {
                                    finish();
                                    setResult(AddAnnouncementActivity.RESULT_OK);
                                    Toast.makeText(AddAnnouncementActivity.this, "Successfully added announcement.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddAnnouncementActivity.this, "There's something wrong while processing your request. Please try again.", Toast.LENGTH_SHORT).show();
                                }

                                //JSONObject jsonObject = jsonArray.getJSONObject(i);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                    hidePDialog();
                    Toast.makeText(AddAnnouncementActivity.this, "There's something wrong while processing your request. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("pid", pid);
                    params.put("title", title.getText().toString());
                    params.put("details", details.getText().toString());
                    params.put("venue", venue.getText().toString());
                    params.put("schedule", sched);
                    return params;
                }
            };
            sr.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(sr);
        }else{
            Toast.makeText(getApplication(), "empty date and time", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    protected Dialog onCreateDialog(int id){
        if(id == DATE){
            return  new DatePickerDialog(AddAnnouncementActivity.this, datePickerListener, mYear, mMonth, mDay);
        }else if(id == TIME){
            return new TimePickerDialog(AddAnnouncementActivity.this, timePickerListener, mHour, mMinute, true);
        }
        return null;
    }

    protected DatePickerDialog.OnDateSetListener datePickerListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    monthOfYear++;
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;

                    schedule = year + "-";
                    if (monthOfYear < 10) {
                        schedule += "0" + monthOfYear + "-";
                    } else {
                        schedule += monthOfYear + "-";
                    }
                    if (dayOfMonth < 10) {
                        schedule += "0" + dayOfMonth;
                    } else {
                        schedule += dayOfMonth;
                    }

                    textView_date.setText(schedule);
                }
            };

    protected TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mHour = hourOfDay;
                    mMinute = minute;

                    if (hourOfDay < 10) {
                        schedule_time = "0" + hourOfDay + ":";
                    } else {
                        schedule_time = hourOfDay + ":";
                    }
                    if (minute < 10){
                        schedule_time += "0" + minute;
                    }else {
                        schedule_time += minute;
                    }
                    textView_time.setText(schedule_time);
                }
            };

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_done, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
