package app.psiteportal.com.psiteportal;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import app.psiteportal.com.utils.Config;
import app.psiteportal.com.utils.JSONParser;

/**
 * Created by Lawrence on 9/26/2015.
 */
public class AddExpenseActivity extends AppCompatActivity {

    private int year, month, day;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String IMAGE_DIRECTORY_NAME = "PSITE Portal";
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    Button uploadBtn, save_expense;
    TextView date;
    ImageView imgPreview;
    EditText item;
    EditText amount;
    EditText check_number;
    EditText recipient_et;
    EditText routing_num_et;
    EditText account_num_et;
    EditText memo_et;
    Spinner liq_type;
    Spinner category_type;
    ProgressDialog pDialog;
    String encodedImage;
    String pid;
    String sid;
    CheckBox cash_check;
    LinearLayout check_layout;
    String fileName;
    boolean checked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pid = extras.getString("pid");
            sid = extras.getString("sid");
        }

        item = (EditText) findViewById(R.id.item_et);
        amount = (EditText) findViewById(R.id.amount_et);
        check_number = (EditText) findViewById(R.id.cash_num_et);
        date = (TextView) findViewById(R.id.date_text);
        imgPreview = (ImageView) findViewById(R.id.expense_image_preview);
        uploadBtn = (Button) findViewById(R.id.upload_btn);
        save_expense = (Button) findViewById(R.id.expense_save_btn);
        liq_type = (Spinner) findViewById(R.id.spinner);
        category_type = (Spinner) findViewById(R.id.category_spinner);
        cash_check = (CheckBox) findViewById(R.id.check_cb);
        check_layout = (LinearLayout) findViewById(R.id.check_payment_layout);
        recipient_et = (EditText) findViewById(R.id.recipient_et);
        routing_num_et = (EditText) findViewById(R.id.routing_number);
        account_num_et = (EditText) findViewById(R.id.account_number);
        memo_et = (EditText) findViewById(R.id.memo_et);

        liq_type.setSelection(0);

        ArrayAdapter<String> categoryTypeAdapter = new ArrayAdapter<String>(AddExpenseActivity.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.collection_type));
        category_type.setAdapter(categoryTypeAdapter);
        category_type.setSelection(0);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        date.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("-").append(day).append("-")
                .append(year).append(" "));


        liq_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (String.valueOf(liq_type.getSelectedItem()).equals("Expense")) {
                    ArrayAdapter<String> categoryTypeAdapter = new ArrayAdapter<String>(AddExpenseActivity.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.expense_type));
                    category_type.setAdapter(categoryTypeAdapter);
                    category_type.setSelection(0);
//                    category_type.setVisibility(View.VISIBLE);
                } else {
                    ArrayAdapter<String> categoryTypeAdapter = new ArrayAdapter<String>(AddExpenseActivity.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.collection_type));
                    category_type.setAdapter(categoryTypeAdapter);
                    category_type.setSelection(0);
//                    category_type.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        cash_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    check_layout.setVisibility(View.VISIBLE);
                    checked = true;
                } else {
                    check_layout.setVisibility(View.GONE);
                    checked = false;
                }
            }
        });


        save_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checked) {
                    if (item.getText().toString() != null && !item.getText().toString().equals("")
                            && amount.getText().toString() != null && !amount.getText().toString().equals("")
                            && check_number.getText().toString() != null && !amount.getText().toString().equals("")
                            && recipient_et.getText().toString() != null && !recipient_et.getText().toString().equals("")
                            && routing_num_et.getText().toString() != null && !routing_num_et.getText().toString().equals("")
                            && account_num_et.getText().toString() != null && !account_num_et.getText().toString().equals("")
                            && memo_et.getText().toString() != null && !memo_et.getText().toString().equals("")) {

                        new AddExpense().execute();
                    } else {
                        Toast.makeText(AddExpenseActivity.this, "Please fill up the necessary fields!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (item.getText().toString() != null && !item.getText().toString().equals("")
                            && amount.getText().toString() != null && !amount.getText().toString().equals("")) {

                        new AddExpense().execute();

                    } else {
                        Toast.makeText(AddExpenseActivity.this, "Please fill up the necessary fields!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                captureImage();
                selectImage();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        byte[] imageBytes = bytes.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        fileName = destination.getName().toString();

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imgPreview.setVisibility(View.VISIBLE);
        imgPreview.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        File file = new File(selectedImagePath);
        fileName = file.getName().toString();

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] imageBytes = bytes.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        imgPreview.setVisibility(View.VISIBLE);
        imgPreview.setImageBitmap(bm);
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddExpenseActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    //date dialog methods ------------------------------------------------------------


    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2 + 1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        date.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    class AddExpense extends AsyncTask<String, String, String> {

        int success;
        String message;
        JSONParser jsonParser = new JSONParser();
        Resources res = getResources();
        String ADD_EXPENSE_URL = Config.ROOT_URL + Config.WEB_SERVICES + "add_expense.php";
        String dateValue = date.getText().toString();
        String itemValue = item.getText().toString();
        String amountValue = amount.getText().toString();
        String checkValue = check_number.getText().toString();
        String transType = String.valueOf(liq_type.getSelectedItem());
        String transCategory = String.valueOf(category_type.getSelectedItem());
        String recipient = recipient_et.getText().toString();
        String routing_number = routing_num_et.getText().toString();
        String account_number = account_num_et.getText().toString();
        String memo = memo_et.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddExpenseActivity.this);
            pDialog.setMessage("Uploading transaction. . . ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();


                params.add(new BasicNameValuePair("sid", sid));
                params.add(new BasicNameValuePair("pid", pid));
                params.add(new BasicNameValuePair("transaction_item", itemValue));
                params.add(new BasicNameValuePair("transaction_amount", amountValue));

                if (checked) {
                    params.add(new BasicNameValuePair("check_number", checkValue));
                    params.add(new BasicNameValuePair("recipient", recipient));
                    params.add(new BasicNameValuePair("routing_number", routing_number));
                    params.add(new BasicNameValuePair("account_number", account_number));
                }

                params.add(new BasicNameValuePair("image_name", fileName));
                params.add(new BasicNameValuePair("transaction_photo", encodedImage));
                params.add(new BasicNameValuePair("transaction_type", transType));
                params.add(new BasicNameValuePair("transaction_category", transCategory));
                params.add(new BasicNameValuePair("transaction_date", dateValue));
                params.add(new BasicNameValuePair("memo", memo));

                Log.e("params to be passed", params.toString());

                JSONObject json = jsonParser.makeHttpRequest(ADD_EXPENSE_URL, "POST", params);

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    message = json.getString(TAG_MESSAGE);
                } else {
                    message = json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();

            Toast.makeText(AddExpenseActivity.this, message, Toast.LENGTH_SHORT).show();
            finish();

        }
    }


}
