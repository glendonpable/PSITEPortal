package app.psiteportal.com.psiteportal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.view.ViewGroup;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.psiteportal.com.utils.Config;
import app.psiteportal.com.utils.JSONParser;

/**
 * Created by Lawrence on 10/6/2015.
 */
public class AddSeminarActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private Spinner spinner;
    private int choice;
    ImageButton dateToActivate, select_time_start, select_end_date, select_time_end;
    TextView date, time_start, time_end, date_end_tv;
    Button add_seminar;
    Button cancel;
    EditText seminar_title;
    EditText seminar_fee;
    EditText discounted_fee;
    AutoCompleteTextView seminar_venue;
    EditText about;
    EditText bonus_points;
    EditText point_cost;
    ImageView banner_holder;
    Button select_banner;
    String picturePath;
    String photoName;
    String encodedImage;
    String start_time, end_time, seminar_date, seminar_end_date;
    JSONParser jsonParser = new JSONParser();
    String seminar_title_str;
    String seminar_fee_str;
    String discounted_fee_str;
    String bonus_str;
    String seminar_venue_str;
    String about_str;
    int success;
    String message;
    int day, month, year, hourOfDay, minute;
    private Calendar calendar;
    private ProgressDialog pDialog;
    private static final int PICK_IMAGE = 1;
    private Bitmap bitmap;
    private static String url_create_seminar = "add_seminar.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static String url_send_message = "http://psite7.org/portal/gcm_server_php/send_message.php";
    int point_cost_str;
    int normal_fee;
    int discount_fee;

    private static final String LOG_TAG = "GPA";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyCKUK-Xwh1nlZWVVL_KFUgezxy4B-lJxOg";
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_seminar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Create Seminar/Convention");

        spinner = (Spinner) findViewById(R.id.sem_spinner);
        dateToActivate = (ImageButton) findViewById(R.id.SelectDate);
        date = (TextView) findViewById(R.id.selectedDate);
        date_end_tv = (TextView) findViewById(R.id.end_date);
        select_time_start = (ImageButton) findViewById(R.id.select_start_time);
        select_end_date = (ImageButton) findViewById(R.id.select_end_date);
        select_time_end = (ImageButton) findViewById(R.id.select_end_time);
        time_start = (TextView) findViewById(R.id.start_time);
        time_end = (TextView) findViewById(R.id.end_time);
        seminar_title = (EditText) findViewById(R.id.seminar_title);
        seminar_fee = (EditText) findViewById(R.id.seminar_fee);
        about = (EditText) findViewById(R.id.about);
        seminar_venue = (AutoCompleteTextView) findViewById(R.id.seminar_venue);
        add_seminar = (Button) findViewById(R.id.update_seminar);
        bonus_points = (EditText) findViewById(R.id.bonus_points);
        discounted_fee = (EditText) findViewById(R.id.discount_fee);
        point_cost = (EditText) findViewById(R.id.edt_points_fee);

        cancel = (Button) findViewById(R.id.cancel_btn);
        banner_holder = (ImageView) findViewById(R.id.banner_holder);
        select_banner = (Button) findViewById(R.id.select_banner_btn);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
//        showDate(day, month + 1, year);

        seminar_venue.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        seminar_venue.setOnItemClickListener(this);


        select_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && ContextCompat.checkSelfPermission(AddSeminarActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddSeminarActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
//                    dialog.dismiss();
//                    return;
                }else{
                    selectImageFromGallery();
                }
            }
        });

        add_seminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                seminar_title_str = seminar_title.getText().toString();
                seminar_fee_str = seminar_fee.getText().toString();
                discounted_fee_str = discounted_fee.getText().toString();
                point_cost_str = Integer.parseInt(point_cost.getText().toString());
                bonus_str = bonus_points.getText().toString();
                seminar_venue_str = seminar_venue.getText().toString();
                about_str = about.getText().toString();

                if ((seminar_title_str != null && !seminar_title_str.equals(""))
                        && (seminar_fee_str != null && !seminar_fee_str.equals(""))
                        && (discounted_fee_str != null && !seminar_fee_str.equals(""))
                        && (point_cost_str !=0 && !point_cost.getText().equals(""))
                        && (bonus_str != null && !bonus_str.equals(""))
                        && (seminar_venue_str != null && !seminar_venue_str.equals(""))
                        && (about_str != null && !about_str.equals(""))
                        && (time_start.getText() != null && !time_start.getText().equals(""))
                        && (time_end.getText() != null && !time_end.getText().equals(""))
                        && (date.getText() != null && !date.getText().equals(""))
                        && (date_end_tv.getText() != null && !date_end_tv.getText().equals(""))
                        && (choice!=0)){

                    normal_fee = Integer.parseInt(seminar_fee_str);
                    discount_fee = Integer.parseInt(discounted_fee_str);

                    if (normal_fee > discount_fee) {
//                        point_cost_str = normal_fee - discount_fee;
                        new CreateSeminar().execute();
                    } else {
                        Toast.makeText(AddSeminarActivity.this, "Discount fee must be lesser than Normal fee!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddSeminarActivity.this, "Please fill up neccessary fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        String[] items = new String[] {"Please Choose one", "Seminar", "Convention"};
        List<String> list = new ArrayList<String>();
        list.add("tae");   //  Initial dummy entry
        list.add("string1");
        list.add("string2");
        list.add("string3");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items){
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = null;

                // If this is the initial dummy entry, make it hidden
                if (position == 0) {
                    TextView tv = new TextView(getContext());
                    tv.setHeight(0);
                    tv.setVisibility(View.GONE);
                    v = tv;
                }
                else {
                    // Pass convertView as null to prevent reuse of special case views
                    v = super.getDropDownView(position, null, parent);
                }

                // Hide scroll bar because it appears sometimes unnecessarily, this does not prevent scrolling
                parent.setVerticalScrollBarEnabled(false);
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                choice = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

    public void setEndDate(View view) {
        showDialog(666);
    }

    public void setEndTime(View view) {
        showDialog(777);
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
//          TODO Auto-generated method stub
            showDate(year, month + 1, day);
        }
    };

    private DatePickerDialog.OnDateSetListener myEndDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
//          TODO Auto-generated method stub
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

        date.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));

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

        seminar_date = year +"/"+ fin_month +"/"+ fin_day;

    }

    private void showEndDate(int year, int month, int day) {
        String fin_day,fin_month;

        date_end_tv.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));

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

        seminar_end_date = year +"/"+ fin_month +"/"+ fin_day;

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
//        fin_time_start = start_time;
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
//        fin_time_end = time;
        time_end.setText(end_time);
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
//        String str = (String) parent.getItemAtPosition(position);
//        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
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


    class CreateSeminar extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(AddSeminarActivity.this);
            pDialog.setMessage("Creating Seminar . . .");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        protected String doInBackground(String... args) {
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("seminar_title", seminar_title_str));
                params.add(new BasicNameValuePair("seminar_date", seminar_date));
                params.add(new BasicNameValuePair("seminar_start_time", start_time));
                params.add(new BasicNameValuePair("seminar_end_date", seminar_end_date));
                params.add(new BasicNameValuePair("seminar_end_time", end_time));
                params.add(new BasicNameValuePair("bonus_points", bonus_str));
                params.add(new BasicNameValuePair("seminar_fee", seminar_fee_str));
                params.add(new BasicNameValuePair("discounted_fee", discounted_fee_str));
                params.add(new BasicNameValuePair("points_cost", String.valueOf(point_cost_str)));
                params.add(new BasicNameValuePair("venue", seminar_venue_str));
                params.add(new BasicNameValuePair("about", about_str));
                params.add(new BasicNameValuePair("attendance_code", randomString(5)));
                params.add(new BasicNameValuePair("image_name", photoName));
                params.add(new BasicNameValuePair("is_convention", String.valueOf(choice-1)));
                params.add(new BasicNameValuePair("seminar_banner", encodedImage));


                Log.e("to be passed", params.toString());
                JSONObject json = jsonParser.makeHttpRequest(Config.ROOT_URL + Config.WEB_SERVICES + url_create_seminar,
                        "POST", params);

                    success = json.getInt(TAG_SUCCESS);
                message = json.getString(TAG_MESSAGE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            if (success == 1) {
                Toast.makeText(AddSeminarActivity.this, message, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // failed to create product
                Toast.makeText(AddSeminarActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }

    }

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

    String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }


}
