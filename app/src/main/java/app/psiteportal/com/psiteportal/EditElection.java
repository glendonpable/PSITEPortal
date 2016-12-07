package app.psiteportal.com.psiteportal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import app.psiteportal.com.utils.Config;

/**
 * Created by fmpdroid on 9/19/2016.
 */
public class EditElection extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText nomDate, nomStartTime, nomEndTime, electDate, electStartTime, electEndTime, venue, numToVote;
    private Button btnEdit;
    private int mHour, mMinute;
    private Calendar calendar;
    private int mYear, mMonth, mDay;
    private static final int DATE = 0, TIME = 1, NOMINATION = 0, ELECTION = 1, START = 0, END = 1;
    private int status, status2;
    private String schedule, schedule_time;
    private ProgressDialog progressDialog;
    private String get_conventions = "get_conventions.php";
    private String edit_election = "edit_election.php";
    private String get_latest_election = "get_latest_election.php";
    private String convention_id, convention_name;

    private ArrayList<String> conventions;
    private JSONArray result;
    private Spinner spinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_election);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Edit Election");

        conventions = new ArrayList<String>();

        btnEdit = (Button) findViewById(R.id.btnEdit);
        nomDate = (EditText) findViewById(R.id.edit_text_nom_date);
        nomStartTime = (EditText) findViewById(R.id.edit_text_nom_start_time);
        nomEndTime = (EditText) findViewById(R.id.edit_text_nom_end_time);
        electDate = (EditText) findViewById(R.id.edit_text_elect_date);
        electStartTime = (EditText) findViewById(R.id.edit_text_elect_start_time);
        electEndTime = (EditText) findViewById(R.id.edit_text_elect_end_time);
        venue = (EditText) findViewById(R.id.edit_text_elect_venue);
        numToVote = (EditText) findViewById(R.id.edit_text_num_to_vote);

        spinner = (Spinner) findViewById(R.id.convention_spinner);

        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        btnEdit.setOnClickListener(this);
        nomDate.setOnClickListener(this);
        nomStartTime.setOnClickListener(this);
        nomEndTime.setOnClickListener(this);
        electDate.setOnClickListener(this);
        electStartTime.setOnClickListener(this);
        electEndTime.setOnClickListener(this);

        spinner.setOnItemSelectedListener(this);

//        getSeminars();
        getLatestElection();
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if(id == DATE){
            return  new DatePickerDialog(EditElection.this, datePickerListener, mYear, mMonth, mDay);
        }else if(id == TIME){
            return new TimePickerDialog(EditElection.this, timePickerListener, mHour, mMinute, true);
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
                    if (status==NOMINATION) {
                        nomDate.setText(schedule);
                    }else if(status==ELECTION){
                        electDate.setText(schedule);
                    }
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
                    if(status==NOMINATION){
                        if(status2==START) {
                            nomStartTime.setText(schedule_time);
                        }else if(status2==END){
                            nomEndTime.setText(schedule_time);
                        }
                    }else if(status==ELECTION){
                        if(status2==START) {
                            electStartTime.setText(schedule_time);
                        }else if(status2==END){
                            electEndTime.setText(schedule_time);
                        }
                    }
                }
            };

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_text_nom_date:
                showDialog(DATE);
                status = NOMINATION;
                break;
            case R.id.edit_text_nom_start_time:
                showDialog(TIME);
                status = NOMINATION;
                status2 = START;
                break;
            case R.id.edit_text_nom_end_time:
                showDialog(TIME);
                status = NOMINATION;
                status2 = END;
                break;
            case R.id.edit_text_elect_date:
                showDialog(DATE);
                status = ELECTION;
                break;
            case R.id.edit_text_elect_start_time:
                showDialog(TIME);
                status = ELECTION;
                status2 = START;
                break;
            case R.id.edit_text_elect_end_time:
                showDialog(TIME);
                status = ELECTION;
                status2 = END;
                break;
            case R.id.btnEdit:
                boolean check;
                check = validate(new EditText[]{nomDate, nomStartTime, nomEndTime, electDate, electStartTime, electEndTime, venue, numToVote});
                if(check) {
                    showDialog();
                }else{
                    Toast.makeText(EditElection.this, "One or more fields are missing. Please check and try again.", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void getSeminars(final String title){
        StringRequest stringRequest = new StringRequest(Config.ROOT_URL + Config.WEB_SERVICES + get_conventions,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
//                            j = new JSONObject(response);
                            JSONArray jsonArray = new JSONArray(response.toString());

//                            j = new JSONArray(response.toString());
                            //Storing the Array of JSON String to our JSON Array
                            result = jsonArray;

                            //Calling method getStudents to get the students from the JSON Array
                            getConventions(result, title);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);

    }

    private void getLatestElection(){
        StringRequest stringRequest = new StringRequest(Config.ROOT_URL + Config.WEB_SERVICES + get_latest_election,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray j = null;
                        try {
                            String title;
                            JSONObject object = new JSONObject(response.toString());
                            title = object.getString("election_title");
                            nomDate.setText(object.getString("nom_date"));
                            nomStartTime.setText(object.getString("nom_start_time"));
                            nomEndTime.setText(object.getString("nom_end_time"));
                            electDate.setText(object.getString("election_date"));
                            electStartTime.setText(object.getString("start_time"));
                            electEndTime.setText(object.getString("end_time"));
                            venue.setText(object.getString("election_venue"));
                            numToVote.setText(object.getString("num_needed"));

                            getSeminars(title);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);

    }

    private void getConventions(JSONArray j, String convention_title) {
        //Traversing through all the items in the json array
        String title;
        int position = 0;
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);
                Log.wtf("json object", json.toString());

                title = json.getString("seminar_title");
                Log.wtf("json object" + i, title);
                //Adding the name of the student to array list
                conventions.add(title);
                if(convention_title.equals(title)){
                    position = i;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        spinner.setAdapter(new ArrayAdapter<String>(EditElection.this, android.R.layout.simple_spinner_dropdown_item, conventions));
        spinner.setSelection(position);
    }

    private String getConventionName(int position){
        String conventionName="";
        try {
            JSONObject json = result.getJSONObject(position);
            conventionName = json.getString("seminar_title");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return conventionName;
    }

    private String getConventionID(int position){
        String conventionID="";
        try {
            JSONObject json = result.getJSONObject(position);
            conventionID = json.getString("sid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return conventionID;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        convention_id = getConventionID(position);
        convention_name = getConventionName(position);
//        Toast.makeText(CreateElection.this, getConventionID(position), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void showDialog(){
        new AlertDialog.Builder(EditElection.this)
                .setTitle("Warning!")
                .setMessage("Are you sure to edit election information?")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        editElection(convention_id, convention_name, nomDate.getText().toString(), nomStartTime.getText().toString(),
                                nomEndTime.getText().toString(), electDate.getText().toString(),
                                electStartTime.getText().toString(), electEndTime.getText().toString(),
                                venue.getText().toString(), numToVote.getText().toString());
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void editElection(final String convention_id, final String convention_name, final String nomDate, final String nomStart, final String nomEnd,
                                final String electDate, final String electStart, final String electEnd, final String venue, final String numToVote) {
        RequestQueue queue = Volley.newRequestQueue(EditElection.this);

        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + edit_election,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        String message;
                        int success;
                        hidePDialog();
                        try {
                            Log.wtf("glendon message", s);
                            JSONObject object = new JSONObject(s.toString());
                            Log.wtf("glendon", object.toString());
                            success = object.getInt("success");
                            message = object.getString("message");
                            Log.wtf("success", String.valueOf(success));
                            Log.wtf("message", object.getString("message"));
                            if(success==1){
                                Toast.makeText(EditElection.this, message, Toast.LENGTH_LONG).show();
                                setResult(EditElection.RESULT_OK);
                                finish();
                            }else{
                                Toast.makeText(EditElection.this,message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.wtf("hahay", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("sid", convention_id);
                params.put("title", convention_name);
                params.put("nom_date", nomDate);
                params.put("nom_start_time", nomStart);
                params.put("nom_end_time", nomEnd);
                params.put("election_date", electDate);
                params.put("start_time", electStart);
                params.put("end_time", electEnd);
                params.put("venue", venue);
                params.put("num_needed", numToVote);
                return params;
            }
        };
        queue.add(sr);
    }

    private boolean validate(EditText[] fields){
        for(int i=0; i<fields.length; i++){
            EditText currentField=fields[i];
            if(currentField.getText().toString().length()<=0){
                return false;
            }
        }
        return true;
    }
}
