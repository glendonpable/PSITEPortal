package app.psiteportal.com.psiteportal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import app.psiteportal.com.fragments.MembershipActivationFragment;
import app.psiteportal.com.utils.Config;
import app.psiteportal.com.utils.SntpClient;

/**
 * Created by fmpdroid on 3/15/2016.
 */
public class MemberActivationActivity extends AppCompatActivity {
    private Button btnScan, btnActivate;
    private TextView txtName, txtAddress, txtEmail, txtInstitution, txtPoints, txtStatus;
    private EditText reg_fee;
    private String pid, activate_pid, user_status, user_usertype;
    private ProgressDialog progressDialog;
    private String school_year, user_points, activation_points;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    private String qr_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activate_user);

        btnScan = (Button) findViewById(R.id.user_scan);
        btnActivate = (Button) findViewById(R.id.user_activation);

        txtName = (TextView) findViewById(R.id.holder_name);
        txtAddress = (TextView) findViewById(R.id.holder_address);
        txtEmail = (TextView) findViewById(R.id.holder_email);
        txtInstitution = (TextView) findViewById(R.id.holder_institution);
        txtPoints = (TextView) findViewById(R.id.holder_points);
        txtStatus = (TextView) findViewById(R.id.holder_status);

        reg_fee = (EditText) findViewById(R.id.reg_fee);

        pid = getIntent().getExtras().getString("user_pid");
        user_usertype = getIntent().getExtras().getString("usertype");
        getFee();
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(ACTION_SCAN);
                    startActivityForResult(intent, 0);
                } catch (Exception e) {
                    showDialog(MemberActivationActivity.this, "No Scanner Found", "Download the scanner application?", "Yes", "No").show();
                }
            }
        });

        btnActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_status.equals("Member")) {
                    Toast.makeText(getApplicationContext(), "User is already a member.", Toast.LENGTH_SHORT).show();
                } else {
                    if (activate_pid != null) {
                        Log.wtf("activate pid", activate_pid);
                        new AlertDialog.Builder(MemberActivationActivity.this)
                                .setTitle("Activate User")
                                .setMessage("Are you sure to activate this user?")
                                .setIcon(R.drawable.ic_warning)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        new MyAsyncTask().execute();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Scan a user first!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

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

                String[] resultArr = contents.split(",");
                String[] result = contents.split("\\s+");
                qr_id = result[0];
                Log.e("user ID", resultArr[0]);
                getUserTask();
            }
        }
    }

    public void getUserTask() {
        progressDialog = new ProgressDialog(MemberActivationActivity.this);
        // Showing progress dialog before making http request
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        String url = Config.ROOT_URL + Config.WEB_SERVICES + "get_user.php";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST,
                url,new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        hidePDialog();
                        LinearLayout layout = (LinearLayout) findViewById(R.id.holder);
                        String name, email, address, institution, status = null;
                        //hidePDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(s.toString());
                            activate_pid = jsonObject.getString("pid");
                            name = jsonObject.getString("firstname") + " " + jsonObject.getString("lastname");
                            institution = jsonObject.getString("institution");
                            address = jsonObject.getString("address");
                            email = jsonObject.getString("email");
                            user_points = jsonObject.getString("points");
                            if (Integer.parseInt(jsonObject.getString("activated")) == 0) {
                                status = "Non-Member";
                            } else if (Integer.parseInt(jsonObject.getString("activated")) == 1) {
                                status = "Member";
                            }

                            user_status = status;
                            txtName.setText(name);
                            txtEmail.setText(email);
                            txtInstitution.setText(institution);
                            txtAddress.setText(address);
                            txtPoints.setText(user_points);
                            txtStatus.setText(status);

                            layout.setVisibility(View.VISIBLE);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Activity activity = MemberActivationActivity.this;
                        if (volleyError instanceof NoConnectionError) {
                            String errormsg = "Check your internet connection";
                            Toast.makeText(activity, errormsg, Toast.LENGTH_LONG).show();
                        }
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("qr_id", qr_id);
                return params;
            }
        };
        queue.add(sr);
    }

    public void activateUser(final String activated_at) {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + "activate_member.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.wtf("result", s.substring(0));
                int result;
                try {
                    JSONObject object = new JSONObject(s.toString());
                    result = object.getInt("success");
                    if (result == 1) {
                        activate_pid = null;
                        txtName.setText(null);
                        txtEmail.setText(null);
                        txtInstitution.setText(null);
                        txtAddress.setText(null);
                        txtPoints.setText(null);
                        txtStatus.setText(null);
                        Toast.makeText(getApplicationContext(), "User successfully activated!", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = object.getString("message");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {

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
                Log.wtf("PID HERE", pid);
                int total_points = Integer.parseInt(user_points) + Integer.parseInt(activation_points);

                params.put("activate_pid", activate_pid);
                params.put("pid", pid);
                params.put("activated_at", activated_at);
                params.put("registration_fee", reg_fee.getText().toString());
                params.put("school_year", school_year);
                params.put("points", String.valueOf(total_points));
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

    private class MyAsyncTask extends AsyncTask<String, String, String> {
        long time_result, current_time;
        Date current, target;
        boolean check;
        int success;
        SntpClient client;
        String date;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MemberActivationActivity.this);
            // Showing progress dialog before making http request
            progressDialog.setMessage("Checking...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            //Sntp Experiment starts here
            client = new SntpClient();
            try {
                if (client.requestTime("0.pool.ntp.org", 30000)) {
                    check = true;
                    current_time = client.getNtpTime();
                    Date current_date = new Date(current_time);
                    //test
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    date = formatter.format(current_date);

                    Log.d("date", date);

                } else {
                    Log.d("time here", "failed");
                    check = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return date;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            hidePDialog();
            activateUser(result);
            finish();
//            FragmentManager fragmentManager = getFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.navigation_container, membersFragment());
//            fragmentTransaction.commit();
        }
    }
    Fragment membersFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", pid);
        bundle.putString("usertype", user_usertype);
        MembershipActivationFragment memberFragment = new MembershipActivationFragment();
        memberFragment.setArguments(bundle);

        return memberFragment;
    }

    public void getFee() {
        progressDialog = new ProgressDialog(MemberActivationActivity.this);
        // Showing progress dialog before making http request
        progressDialog.setMessage("Checking...");
        progressDialog.show();
        String get_fee = "get_fee.php";

        Calendar cal = Calendar.getInstance();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + get_fee, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                hidePDialog();
                Log.wtf("result", s.substring(0));
                String result;
                try {
                    JSONObject object = new JSONObject(s.toString());
                    result = object.getString("fee");
                    school_year = object.getString("SY");
                    activation_points = object.getString("points");
                    reg_fee.setText(result);
                    Log.wtf("result", result);
                } catch (Exception e) {
                    e.printStackTrace();
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
                Log.wtf("PID HERE", pid);
                return params;
            }
        };
        queue.add(sr);
    }


}
