package app.psiteportal.com.psiteportal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.psiteportal.com.utils.Config;
import app.psiteportal.com.utils.JSONParser;

public class EditSeminar extends AppCompatActivity implements AdapterView.OnItemClickListener {

    int day, month, year, hourOfDay, minute;
    private Calendar calendar;
    TextView seminar_date, time_start, time_end, seminar_end_date;
    String fin_date, fin_end_date;
    EditText seminar_title;
    EditText attendance_code;
    EditText et_seminar_fee;
    EditText et_discounted_fee;
    AutoCompleteTextView seminar_venue;
    EditText et_about;
    EditText bonus_points;
    EditText edt_points_fee;
    private Bitmap bitmap;
    String encodedImage;
    ImageView banner_holder;
    String picturePath;
    Button add_seminar;
    Button cancel;
    Button edit_banner;
    ImageButton dateToActivate, select_time_start, select_time_end, select_date_end;
    String pass_day, pass_month, pass_year;
    ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    String sid, title, date, end_date, start_time, end_time, bonus, seminar_fee,
            discounted_fee, points_cost, venue, about, atten_code, is_active, out_activated;
    String photoName;
    String TAG_SUCCESS = "success";
    int success;

    private static final String LOG_TAG = "GPA";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyCKUK-Xwh1nlZWVVL_KFUgezxy4B-lJxOg";
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int PICK_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_seminar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_seminar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sid = extras.getString("sid");
        }

        dateToActivate = (ImageButton) findViewById(R.id.SelectDate);
        select_time_start = (ImageButton) findViewById(R.id.select_start_time);
        select_time_end = (ImageButton) findViewById(R.id.select_end_time);
        select_date_end = (ImageButton) findViewById(R.id.SelectEndDate);
        add_seminar = (Button) findViewById(R.id.update_seminar);
        cancel = (Button) findViewById(R.id.cancel_btn);
        seminar_title = (EditText) findViewById(R.id.seminar_title);
        seminar_date = (TextView) findViewById(R.id.selectedDate);
        seminar_end_date = (TextView) findViewById(R.id.selectedEndDate);
        time_start = (TextView) findViewById(R.id.start_time);
        time_end = (TextView) findViewById(R.id.end_time);
        bonus_points = (EditText) findViewById(R.id.bonus_points);
        et_seminar_fee = (EditText) findViewById(R.id.edit_seminar_fee);
        et_discounted_fee = (EditText) findViewById(R.id.discount_fee);
        attendance_code = (EditText) findViewById(R.id.attendance_code);
        seminar_venue = (AutoCompleteTextView) findViewById(R.id.seminar_venue);
        et_about = (EditText) findViewById(R.id.edit_about);
        edt_points_fee = (EditText) findViewById(R.id.edt_points_fee);

        edit_banner = (Button) findViewById(R.id.edit_banner_btn);
        banner_holder = (ImageView) findViewById(R.id.banner_holder);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        getSeminar(sid);

        seminar_venue.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        seminar_venue.setOnItemClickListener(EditSeminar.this);

        add_seminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new UpdateSeminarTask().execute();
            }
        });
        edit_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && ContextCompat.checkSelfPermission(EditSeminar.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditSeminar.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
//                    dialog.dismiss();
//                    return;
                }else{
                    selectImageFromGallery();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
            } else {
                // User refused to grant permission.
            }
        }
    }

    public void setDate(View view) {
        showDialog(999);
    }

    public void setStartTime(View view) {
        showDialog(888);
    }

    public void setEndTime(View view) {
        showDialog(777);
    }

    public void setEndDate(View view) {
        showDialog(666);
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        } else if (id == 888) {
            return new TimePickerDialog(this, myTimeListener, hourOfDay, minute, true);
        } else if (id == 777) {
            return new TimePickerDialog(this, myTimeListener2, hourOfDay, minute, true);
        }else if (id == 666) {
            return new DatePickerDialog(this, myEndDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
//             TODO Auto-generated method stub
            pass_day = "" + day;
            pass_month = "" + month;
            pass_year = "" + year;
            showDate(year, month + 1, day);
        }
    };

    private DatePickerDialog.OnDateSetListener myEndDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
//             TODO Auto-generated method stub
            pass_day = "" + day;
            pass_month = "" + month;
            pass_year = "" + year;
            showEndDate(year, month + 1, day);
        }
    };


    private TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            showTimeStart(hourOfDay, minute);
        }
    };

    private TimePickerDialog.OnTimeSetListener myTimeListener2 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            showTimeEnd(hourOfDay, minute);
        }
    };

    private void showDate(int year, int month, int day) {

        String fin_day,fin_month;

        seminar_date.setText(new StringBuilder().append(year).append("/")
                .append(month).append("/").append(day));

        if(day<10){
            fin_day = "0"+day;
        }else{
            fin_day = day+"";
        }

        if(month<10){
            fin_month = "0"+month;
        }else{
            fin_month = month+"";
        }

        fin_date = year +"/"+ fin_month +"/"+ fin_day;
    }

    private void showEndDate(int year, int month, int day) {

        String fin_day,fin_month;

        seminar_end_date.setText(new StringBuilder().append(year).append("/")
                .append(month).append("/").append(day));

        if(day<10){
            fin_day = "0"+day;
        }else{
            fin_day = day+"";
        }

        if(month<10){
            fin_month = "0"+month;
        }else{
            fin_month = month+"";
        }

        fin_end_date = year +"/"+ fin_month +"/"+ fin_day;
    }

    private void showTimeStart(int hourOfDay, int minute) {
        String time = hourOfDay + ":" + minute;
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(time);
            start_time = new SimpleDateFormat("HH:mm").format(dateObj) + "";
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        time_start.setText(start_time);
    }

    private void showTimeEnd(int hourOfDay, int minute) {
        String time = hourOfDay + ":" + minute;
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(time);
            end_time = new SimpleDateFormat("HH:mm").format(dateObj) + "";
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        time_end.setText(end_time);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //wala ra
    }

    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:ph");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return (String) resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }


    class UpdateSeminarTask extends AsyncTask<String, String, String> {

        String seminar_title_str = seminar_title.getText().toString();
        String seminar_date_str = seminar_date.getText().toString();
        String seminar_date_end = seminar_end_date.getText().toString();
        String seminar_time_start = time_start.getText().toString();
        String seminar_time_end = time_end.getText().toString();
        String seminar_fee_str = et_seminar_fee.getText().toString();
        String seminar_venue_str = seminar_venue.getText().toString();
        String about_str = et_about.getText().toString();
        String discounted_fee_str = et_discounted_fee.getText().toString();
        String bonus_str = bonus_points.getText().toString();
        String attendance_code_str = attendance_code.getText().toString();
        String points_fee = edt_points_fee.getText().toString();

        int total_points = Integer.parseInt(seminar_fee_str) - Integer.parseInt(discounted_fee_str);

        Resources res = getResources();

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(EditSeminar.this);
            pDialog.setMessage("Updating Seminar . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        protected String doInBackground(String... args) {
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("sid", sid));
                params.add(new BasicNameValuePair("seminar_title", seminar_title_str));
                params.add(new BasicNameValuePair("seminar_date", seminar_date_str));
                params.add(new BasicNameValuePair("seminar_start_time", seminar_time_start));
                params.add(new BasicNameValuePair("seminar_end_date", seminar_date_end));
                params.add(new BasicNameValuePair("seminar_end_time", seminar_time_end));
                params.add(new BasicNameValuePair("bonus_points", bonus_str));
                params.add(new BasicNameValuePair("seminar_fee", seminar_fee_str));
                params.add(new BasicNameValuePair("discounted_fee", discounted_fee_str));
                params.add(new BasicNameValuePair("points_cost", points_fee));
                params.add(new BasicNameValuePair("venue", seminar_venue_str));
                params.add(new BasicNameValuePair("attendance_code", attendance_code_str));
                params.add(new BasicNameValuePair("about", about_str));
                params.add(new BasicNameValuePair("image_name", photoName));
                params.add(new BasicNameValuePair("seminar_banner", encodedImage));

                Log.e("to be passed", params.toString());

                JSONObject json = jsonParser.makeHttpRequest(Config.ROOT_URL + Config.WEB_SERVICES + "update_seminar.php",
                        "POST", params);

                success = json.getInt(TAG_SUCCESS);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            if (success == 1) {
                Toast.makeText(EditSeminar.this, "Update successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EditSeminar.this, "Oops! Something went wrong.", Toast.LENGTH_SHORT).show();
            }
            finish();
        }

    }

    public void getSeminar(String id) {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        String url = "get_seminar.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("sid", id);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                Config.ROOT_URL + Config.WEB_SERVICES + url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        hidePDialog();
                        try {
                            sid = jsonObject.getString("sid");
                            title = jsonObject.getString("seminar_title");
                            date = jsonObject.getString("seminar_date");
                            start_time = jsonObject.getString("seminar_start_time");
                            end_date = jsonObject.getString("seminar_end_date");
                            end_time = jsonObject.getString("seminar_end_time");
                            bonus = jsonObject.getString("bonus_points");
                            seminar_fee = jsonObject.getString("seminar_fee");
                            discounted_fee = jsonObject.getString("discounted_fee");
                            points_cost = jsonObject.getString("points_cost");
                            venue = jsonObject.getString("venue_address");
                            atten_code = jsonObject.getString("attendance_code");
                            about = jsonObject.getString("about");

                            Log.d("seminar_item", jsonObject.toString());

                            seminar_title.setText(title);
                            seminar_date.setText(date);
                            time_start.setText(start_time);
                            seminar_end_date.setText(end_date);
                            time_end.setText(end_time);
                            bonus_points.setText(bonus);
                            et_seminar_fee.setText(seminar_fee);
                            et_discounted_fee.setText(discounted_fee);
                            seminar_venue.setText(venue);
                            attendance_code.setText(atten_code);
                            et_about.setText(about);
                            edt_points_fee.setText(points_cost);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
        queue.add(jsonObjectRequest);

    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
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

    //new codes added
    public void selectImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select photo"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
                && null != data) {
            if (Build.VERSION.SDK_INT < 19) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();
            } else {
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                String document_id = cursor.getString(0);
                document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
                cursor.close();

                cursor = getContentResolver().query(
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
                cursor.moveToFirst();
                picturePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
            }

            decodeFile(picturePath);

        }
    }

    public void decodeFile(String filePath) {

        File imageFile = new File(filePath);

        if (imageFile.exists()) {

            photoName = imageFile.getName().toString();
            Toast.makeText(this, photoName, Toast.LENGTH_SHORT).show();

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 1024;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            bitmap = BitmapFactory.decodeFile(filePath, o2);

            banner_holder.setImageBitmap(bitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        } else {
            Toast.makeText(this, "Image file does not exist.", Toast.LENGTH_SHORT).show();
        }

    }

}