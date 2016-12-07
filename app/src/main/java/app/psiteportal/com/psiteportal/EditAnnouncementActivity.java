package app.psiteportal.com.psiteportal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import app.psiteportal.com.fragments.AnnouncementFragment;
import app.psiteportal.com.utils.Config;

/**
 * Created by fmpdroid on 5/25/2016.
 */
public class EditAnnouncementActivity extends AppCompatActivity {

    private ImageButton date, time;
    private Button btnCreate;
    private TextView textView_date, textView_time;
    private int mHour, mMinute;
    private Calendar calendar;
    private int mYear, mMonth, mDay;
    private static final int DATE = 0, TIME = 1;
    private EditText title, details, venue;
    private ProgressDialog progressDialog;
    private String aid, user_pid, usertype;
    private String schedule, schedule_time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_announcement);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Edit Announcement");

//        date = (ImageButton) findViewById(R.id.edit_selectAnnouncementDate);
//        time = (ImageButton) findViewById(R.id.edit_selectAnnouncementTime);
        btnCreate = (Button) findViewById(R.id.edit_announcementBtnSubmit);
//        textView_date = (TextView) findViewById(R.id.edit_txt_announcementDate);
//        textView_time = (TextView) findViewById(R.id.edit_txt_announcementTime);
        title = (EditText) findViewById(R.id.edit_editText_title);
        details = (EditText) findViewById(R.id.edit_editText_details);
        venue = (EditText) findViewById(R.id.edit_editText_venue);

        aid = getIntent().getExtras().getString("aid");
        user_pid = getIntent().getExtras().getString("user_pid");
        usertype = getIntent().getExtras().getString("usertype");
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
                    Toast.makeText(EditAnnouncementActivity.this, "Oh no! One or more fields are missing!", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(EditAnnouncementActivity.this)
                            .setTitle("Edit Announcement")
                            .setMessage("Are you sure to edit this announcement?")
                            .setIcon(R.drawable.ic_warning)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    editAnnouncement();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            }
        });
        getAnnouncementDetails();
    }

    public void getAnnouncementDetails() {
        progressDialog = new ProgressDialog(EditAnnouncementActivity.this);
        // Showing progress dialog before making http request
        progressDialog.setMessage("Fetching...");
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(EditAnnouncementActivity.this);
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + "edit_announcement.php",
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
                            if(success==1){
                                String timestamp, m_date, m_time, m_title, m_details, m_venue;
//                            timestamp = object.getString("date");
//                            m_date = timestamp.substring(0,10);
//                            m_time = timestamp.substring(11, 16);
                            m_title = object.getString("announcement_title");
                            m_details = object.getString("announcement_details");
                            m_venue = object.getString("announcement_venue");
                            Log.wtf("values here", m_title + m_details + m_venue);
//                            textView_date.setText(m_date);
//                            textView_time.setText(m_time);
                            title.setText(m_title);
                            details.setText(m_details);
                            venue.setText(m_venue);
                            }else{
                                Toast.makeText(EditAnnouncementActivity.this,"There's something wrong while processing your request. Please try again.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EditAnnouncementActivity.this,"There's something wrong while processing your request. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("aid", aid);
                params.put("activity", "get");
                return params;
            }
        };queue.add(sr);
    }

    public void editAnnouncement(){
        //final String sched = textView_date.getText().toString() + " " + textView_time.getText().toString()+":00";
        progressDialog = new ProgressDialog(EditAnnouncementActivity.this);
        // Showing progress dialog before making http request
        progressDialog.setMessage("Editing...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(EditAnnouncementActivity.this);
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + "edit_announcement.php",
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
                            if(success==1){
                                finish();
//                                Fragment frag = announcementFragment();
//                                FragmentManager fragmentManager = getSupportFragmentManager();
//                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                                fragmentTransaction.replace(R.id.navigation_container, frag);
//                                fragmentTransaction.detach(frag);
//                                fragmentTransaction.attach(frag);
//                                fragmentTransaction.commit();
                                Toast.makeText(EditAnnouncementActivity.this, "Successfully edited announcement. Please refresh page to update info", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(EditAnnouncementActivity.this,"There's something wrong while processing your request. Please try again.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EditAnnouncementActivity.this,"There's something wrong while processing your request. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("aid", aid);
                params.put("title",title.getText().toString());
                params.put("details",details.getText().toString());
                params.put("venue",venue.getText().toString());
                params.put("activity", "edit");
                return params;
            }
        };queue.add(sr);

    }


    @Override
    protected Dialog onCreateDialog(int id){
        if(id == DATE){
            return  new DatePickerDialog(EditAnnouncementActivity.this, datePickerListener, mYear, mMonth, mDay);
        }else if(id == TIME){
            return new TimePickerDialog(EditAnnouncementActivity.this, timePickerListener, mHour, mMinute, true);
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

    Fragment announcementFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);
        bundle.putString("usertype", usertype);
        AnnouncementFragment announcementFragment = new AnnouncementFragment();
        announcementFragment.setArguments(bundle);

        return announcementFragment;
    }
}
