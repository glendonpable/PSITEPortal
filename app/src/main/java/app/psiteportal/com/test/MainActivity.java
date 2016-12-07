package app.psiteportal.com.test;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import app.psiteportal.com.fragments.AnnouncementFragment;
import app.psiteportal.com.fragments.CountdownFragment;
import app.psiteportal.com.fragments.ElectionActivity2;
import app.psiteportal.com.fragments.HomeFragment;
import app.psiteportal.com.fragments.ManageElectionFragment;
import app.psiteportal.com.fragments.MembershipActivationFragment;
import app.psiteportal.com.fragments.NominationFragment;
import app.psiteportal.com.fragments.QRFragment;
import app.psiteportal.com.fragments.SeminarsFragment;
import app.psiteportal.com.fragments.ViewPagerFragment;
import app.psiteportal.com.psiteportal.LoginActivity;
import app.psiteportal.com.psiteportal.NoElection;
import app.psiteportal.com.psiteportal.NoNomination;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.psiteportal.UserProfileActivity;
import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.CircleImageView;
import app.psiteportal.com.utils.Config;
import app.psiteportal.com.utils.SntpClient;
import br.liveo.interfaces.OnItemClickListener;
import br.liveo.interfaces.OnPrepareOptionsMenuLiveo;
import br.liveo.model.HelpLiveo;
import br.liveo.navigationliveo.NavigationLiveo;

/**
 * Created by fmpdroid on 2/27/2016.
 */
public class MainActivity extends NavigationLiveo implements OnItemClickListener {
    private HelpLiveo mHelpLiveo;

    public static ArrayList<String> user_type = new ArrayList<String>();

    private static String url = "get_info.php";//election_check.php";
    private static String check_for_nomination = "check_for_nomination.php";
    private static String check_for_election ="check_for_election.php";
    private Map<String, String> params = new HashMap<String, String>();
    private boolean check;
    private String election_needed;
    private ProgressDialog progressDialog;


    String user_pid="";
    String user_fname;
    String user_lname;
    String user_gender;
    String user_contact;
    String user_email;
    String user_address;
    String user_institution;
    int user_points;
    String user_qr;
    String prof_pic;

    String user_usertype;

    int has_voted;
    int has_nominated;
    int value;
    Bundle extras;
    Fragment mFragment;

    private CircleImageView imageView;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    Date current, nomination_start, nomination_end, election_start, election_end;
    private Bitmap bitmap;
    private String success;
    boolean election_checker;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
//    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    @Override
    public void onInt(Bundle savedInstanceState) {

        Log.wtf("PACKAGE", getApplicationContext().getPackageName());
//        StrictMode.setThreadPolicy(policy);
        Log.wtf("CALLED", "OnInt called");
        extras = getIntent().getExtras();

//        try {
//            sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
//            user_pid = sharedpreferences.getString("pid", "");
//            user_usertype = sharedpreferences.getString("usertype", "");
//            has_nominated = Integer.parseInt(sharedpreferences.getString("has_nominated", ""));
//            has_voted = Integer.parseInt(sharedpreferences.getString("has_voted", ""));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

            if (extras != null) {
                user_pid = extras.getString("pid");
                user_fname = extras.getString("firstname");
                user_lname = extras.getString("lastname");
                user_gender = extras.getString("gender");
                user_contact = extras.getString("contact");
                user_email = extras.getString("email");
                user_address = extras.getString("address");
                user_institution = extras.getString("institution");
                user_points = extras.getInt("points");
                user_usertype = extras.getString("usertype");
                user_type.add(user_usertype);
                has_nominated = Integer.parseInt(extras.getString("has_nominated"));
                has_voted = Integer.parseInt(extras.getString("has_voted"));
                prof_pic = extras.getString("prof_pic");
            }
//        if (user_pid.equals("")) {
//            user_pid = extras.getString("pid");
//        }
        //this is where the async task used to be
        //new MyAsyncTask().execute();
        getData();
//        getDataForNomination();
//        getDataForElection();


//        this.userName.setText(user_fname + " " + user_lname);
//        this.userEmail.setText(user_email);
//        this.userPhoto.setImageResource(R.drawable.psite);
        this.userBackground.setImageResource(R.drawable.background_final);//background_final);

        mHelpLiveo = new HelpLiveo();
        mHelpLiveo.add("Home", R.drawable.ic_home_black_24dp);
        mHelpLiveo.add("Profile", R.drawable.ic_person_black_24dp);
        mHelpLiveo.add("Seminars & Conventions", R.drawable.ic_drawer_seminar);
        mHelpLiveo.add("Announcements", R.drawable.ic_textsms_black_24dp);

        mHelpLiveo.add("Nomination", R.drawable.ic_nom);
        mHelpLiveo.add("Election", R.drawable.ic_ele);
        mHelpLiveo.addSeparator();
        mHelpLiveo.add("My QR Code", R.drawable.ic_fingerprint_black_24dp);
//        if (user_usertype.toLowerCase().contains("officer") || user_usertype.toLowerCase().contains("president")) {
            mHelpLiveo.add("Users", R.drawable.ic_members);
//        }
        if (user_usertype.toLowerCase().contains("president")) {
//            mHelpLiveo.add("Publish/Override", R.drawable.ic_override);
            mHelpLiveo.add("Manage Election", R.drawable.ic_override);
        }

//        mHelpLiveo.add("Configuration", R.drawable.ic_settings);
        with(this)
                .startingPosition(0)
                .addAllHelpItem(mHelpLiveo.getHelp())
                .footerItem("Log out", R.drawable.ic_power_settings_new_black_24dp)
                .setOnClickFooter(onClickFooter)
                .build();
    }

    @Override //The "R.id.container" should be used in "beginTransaction (). Replace"
    public void onItemClick(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String title = "PSITE Portal";

        switch (position){
            case 0:
                mFragment = homeFragment();
                title = "Home";
                break;
            case 1:
                getData();
                userProfile();
                break;
            case 2:
                mFragment = seminarsFragment();
                title = "Seminars & Conventions";
                break;
            case 3:
                mFragment = announcementFragment();
                title = "Announcements";
                break;
            case 4:
                mFragment = nominationFragment();
                title = "Nomination";
                break;
            case 5:
                mFragment = electionFragment();
                title ="Election";
                break;
            case 7:
                mFragment = qrFragment();
                title = "My QR Code";
                break;
            case 8:
                mFragment = membersFragment();
                title = "Users";
                break;
            case 9:
                mFragment = manageElectionFragment();
                title = "Manage Election";
                break;
        }
        if (mFragment != null) {
            Log.wtf("fragment is replaced", "fragment replaced");
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.navigation_container, mFragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle(title);
        }
    }

    void userProfile(){
        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        intent.putExtra("user_pid", user_pid);
        startActivity(intent);
    }

    Fragment qrFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);
        bundle.putString("usertype", user_usertype);
        QRFragment qrFragment = new QRFragment();
        qrFragment.setArguments(bundle);

        return qrFragment;
    }


    Fragment membersFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);
        bundle.putString("usertype", user_usertype);
        bundle.putString("position", election_needed);
        MembershipActivationFragment memberFragment = new MembershipActivationFragment();
        memberFragment.setArguments(bundle);

        return memberFragment;
    }


    Fragment nominationFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);
        bundle.putString("usertype", user_usertype);
        bundle.putString("position", election_needed);
        NominationFragment nominationFragment = new NominationFragment();
        nominationFragment.setArguments(bundle);

        return nominationFragment;
    }

    Fragment announcementFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);
        bundle.putString("usertype", user_usertype);
        bundle.putString("position", election_needed);
        AnnouncementFragment announcementFragment = new AnnouncementFragment();
        announcementFragment.setArguments(bundle);

        return announcementFragment;
    }

    Fragment countdownFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);
        bundle.putString("usertype", user_usertype);
        bundle.putString("position", election_needed);
        CountdownFragment countdownFragment = new CountdownFragment();
        countdownFragment.setArguments(bundle);

        return countdownFragment;
    }

    Fragment electionFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);
        bundle.putString("usertype", user_usertype);
        bundle.putString("position", election_needed);
        ElectionActivity2 electionFragment = new ElectionActivity2();
        electionFragment.setArguments(bundle);

        return electionFragment;
    }

    Fragment seminarsFragment() {
        Bundle bundle = new Bundle();
        bundle.putInt("user_pid", Integer.parseInt(user_pid));
        bundle.putString("user_usertype", user_usertype);
        SeminarsFragment seminarsFragment = new SeminarsFragment();
        seminarsFragment.setArguments(bundle);

        return seminarsFragment;
    }

    Fragment noNominationFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);
        bundle.putString("usertype", user_usertype);
        bundle.putString("position", election_needed);
        NoNomination noNomination = new NoNomination();
        noNomination.setArguments(bundle);

        return noNomination;
    }

    Fragment noElectionFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);
        bundle.putString("usertype", user_usertype);
        bundle.putString("position", election_needed);
        NoElection noElection = new NoElection();
        noElection.setArguments(bundle);

        return noElection;
    }

    Fragment overrideFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);
        bundle.putString("usertype", user_usertype);
        bundle.putString("position", election_needed);
//        OverrideFragment overrideFragment = new OverrideFragment();
//        overrideFragment.setArguments(bundle);
        ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
        viewPagerFragment.setArguments(bundle);

        return viewPagerFragment;
    }

    Fragment homeFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);
        bundle.putString("usertype", user_usertype);
        bundle.putString("position", election_needed);
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);

        return homeFragment;
    }
    Fragment manageElectionFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);
        bundle.putString("usertype", user_usertype);
        bundle.putString("position", election_needed);
        ManageElectionFragment manageElectionFragment = new ManageElectionFragment();
        manageElectionFragment.setArguments(bundle);

        return manageElectionFragment;
    }

    private OnPrepareOptionsMenuLiveo onPrepare = new OnPrepareOptionsMenuLiveo() {
        @Override
        public void onPrepareOptionsMenu(Menu menu, int position, boolean visible) {
        }
    };


    //class to get the current time from the internet
    class MyAsyncTask extends AsyncTask<String, String, Date> {
        Date current_date;
        SntpClient client;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            // Showing progress dialog before making http request
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected Date doInBackground(String... strings) {
            client = new SntpClient();
            Long current_time;
            if (client.requestTime("0.pool.ntp.org", 30000)) {
                current_time = client.getNtpTime();
                current_date = new Date(current_time);
            }
            getDataForNomination();
            return current_date;
        }

        @Override
        protected void onPostExecute(Date date) {
            super.onPostExecute(date);
            hidePDialog();
            current = date;
            Log.wtf("date is set here", current.toString());
            boolean check;
            check= has_started(current, nomination_start, nomination_end);
            Log.wtf("date results", nomination_start.toString());
            Log.wtf("date results", nomination_end.toString());
            Log.wtf("date results", current.toString());

            if (check) {
                Log.wtf("has started", "boolean check is true");
                mFragment = nominationFragment();
//                title = "Nomination";
//                        nomination_start = null;
                Toast.makeText(getApplicationContext(), "Nomination", Toast.LENGTH_SHORT).show();
            } else {
                Log.wtf("has started", "boolean check is false");
                mFragment = noNominationFragment();
//                title = "Nomination";
//                        nomination_start = null;
                Toast.makeText(getApplicationContext(), "No Nomination", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //method to retrieve data from web server
//    private void getData() {
//        Log.wtf("AWOO", user_pid);
//        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String s) {
//                String e_username, e_name, e_email, e_institution, e_contact, e_address, e_points;
//                int activated;
//                String e_date=null, e_start=null, e_end=null, num_needed=null;
//                String n_date=null, n_start=null, n_end=null;
//
//                URL url;
//                Bitmap bitmap;
//                int has_voted;
//                SimpleDateFormat formatter;
//                try {
//                    formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//                    JSONObject object = new JSONObject(s.toString());
//                    Log.wtf("testing", object.getString("prof_pic"));
//                    Log.wtf("message", object.getString("success"));
//                    success = object.getString("success");
//                    has_voted = Integer.parseInt(object.getString("has_voted"));
//                    url = new URL(object.getString("prof_pic"));
//                    e_username = object.getString("username");
//                    e_name = object.getString("firstname") + " " + object.getString("lastname");
//                    e_email = object.getString("email");
//                    e_institution = object.getString("institution_name");
//                    e_contact = object.getString("contact");
//                    e_address = object.getString("address");
//                    e_points = object.getString("points");
//                    activated = object.getInt("activated");
//
//                    if(success.equals("3")){
//                        Log.wtf("check", "false");
//                        n_date = object.getString("nomination_date");
//                        n_start = object.getString("nomination_start_time")+":00";
//                        n_end = object.getString("nomination_end_time")+":00";
//                        e_date = object.getString("election_date");
//                        e_start = object.getString("start_time")+":00";
//                        e_end = object.getString("end_time")+":00";
//                        num_needed = object.getString("num_needed");
//                        election_checker = false;
////                        nomination_start = formatter.parse(n_date + " " + n_start);
////                        nomination_end = formatter.parse(n_date + " " + n_end);
////                        election_start = formatter.parse(e_date + " " + e_start);
////                        election_end = formatter.parse(e_date + " " + e_end);
//
//                    }else if(success.equals("2")){
//                        Log.wtf("check", "true");
//                        election_checker = true;
//                        e_date = object.getString("election_date");
//                        e_start = object.getString("start_time")+":00";
//                        e_end = object.getString("end_time")+":00";
//                        num_needed = object.getString("num_needed");
//
////                        election_start = formatter.parse(e_date + " " + e_start);
////                        election_end = formatter.parse(e_date + " " + e_end);
//                    }
//
//
//                    try {
//                        userPhoto.setImageBitmap(getBitmapFromURL(object.getString("prof_pic")));
//                    }catch (Exception e){
//                        e.printStackTrace();
//                        Log.wtf("image", e.getMessage());
//                    }
//                    //formats the strings into an object Date
//
//                    userName.setText(e_name);
//                    userEmail.setText(e_email);
////                    userPhoto.setImageBitmap(bitmap);
//
//                    //set profile image on the navigation drawer
//                   // imageView.setImageUrl(prof_pic, imageLoader);
//
//
//
//                    //stores the other data retrieved to the global class
//                    AppController.getInstance().setActivated(activated);
//                    AppController.getInstance().setNum_positions_needed(Integer.parseInt(num_needed));
//                    AppController.getInstance().setElectionDate(e_date);
//                    AppController.getInstance().setElectionStartTime(e_start);
//                    AppController.getInstance().setElectionEndTime(e_end);
//                    AppController.getInstance().setNominationDate(n_date);
//                    AppController.getInstance().setNominationStartTime(n_start);
//                    AppController.getInstance().setNominationEndTime(n_end);
//
//                        //userPhoto.setImageBitmap(bitmap);
//
//                } catch (Exception e) {
//                    Log.wtf("testing1", e.getMessage());
//                }
////                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("user", user_pid);
//                return params;
//            }
//        };queue.add(sr);
//    }


    private void getData() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                try {
                    String activated;
                    JSONObject object = new JSONObject(s.toString());

                    //setting info on drawer
                    userName.setText(object.getString("firstname") + " " + object.getString("lastname"));
                    userEmail.setText(object.getString("email"));
                    final String photo = object.getString("prof_pic");


                    Target target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            userPhoto.setImageBitmap(bitmap);
                            Log.wtf("bitmap", "success");
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            Log.wtf("bitmap", "failed");
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            Log.wtf("bitmap", "preparing");
                        }
                    };
                    Picasso.with(getApplicationContext()).load(Config.ROOT_URL + Config.PROF_PICS + object.getString("prof_pic")).into(target);


//                    user_usertype = object.getString("usertype_name");
//                    AppController.getInstance().set_has_nominated(Integer.parseInt(object.getString("has_nominated")));
//                    AppController.getInstance().set_has_voted(Integer.parseInt(object.getString("has_voted")));
                    AppController.getInstance().setActivated(Integer.parseInt(object.getString("activated")));

//                    userPhoto.setImageBitmap(getBitmapFromURL(object.getString("prof_pic").replace("http://www.psite7.org","http://192.168.2.4:8080/public_html")));


//                    Thread t = new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            bitmap = getBitmapFromURL(photo);
//                        }
//                    });
//                    t.start();
//                    try {
//                        t.join();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    userPhoto.setImageBitmap(bitmap);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("user", user_pid);
                return params;
            }
        };queue.add(sr);
    }

    //method to compare the current data to the election start date
    private boolean compareDate(Date current, Date target){
        if(!current.after(target)||current.equals(target)){
            Log.wtf("COUNTDOWN","we're still counting down");
            return true;
        }
        else{
            Log.wtf("ELECTION", "let's go to election now");
            return false;
        }
    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }

    private View.OnClickListener onClickFooter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            logout();
        }
    };


    private String compareDates(Date current, Date start, Date end){
        String result = null;
        if(current.before(start)){
            result = "before";
        }else if(current.after(start)&&current.before(end)){
            result = "within";
        }else if(current.after(end)){
            result = "after";
        }
        return result;
    }

    private boolean has_started(Date current, Date start, Date end){
        boolean check;
        if(current == null || start == null || end == null)
        {
            check = false;
        }else {
            if(current.after(start)&&current.before(end)){
                check = true;
            }else{
                check = false;
            }
        }
        return check;
    }

    private void getDataForElection() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + check_for_election, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                String date = null, start = null, end = null, num_needed = null;
                int success;
                int nomination_sid;
                SimpleDateFormat formatter;
                try {
                    formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    JSONObject object = new JSONObject(s.toString());
                    Log.wtf("message", object.getString("success"));
                    success = object.getInt("success");

                    if(success==1) {
                        date = object.getString("election_date");
                        start = object.getString("start_time")+":00";
                        end = object.getString("end_time")+":00";
                        num_needed = object.getString("num_needed");
//                        nomination_sid = Integer.parseInt(object.getString("sid"));

                        election_start = formatter.parse(date + " " + start);
                        election_end = formatter.parse(date + " " + end);

                        //stores the other data retrieved to the global class
                        AppController.getInstance().setNum_positions_needed(Integer.parseInt(num_needed));
                        AppController.getInstance().setElectionDate(date);
                        AppController.getInstance().setElectionStartTime(start);
                        AppController.getInstance().setElectionEndTime(end);
//                        AppController.getInstance().setNomination_sid(nomination_sid);
                    }else if(success==0){
                        Log.wtf("unsuccesful", "no");
                        election_start = null;
                        election_end = null;
                    }

                } catch (Exception e) {
                    Log.wtf("testing1", e.getMessage());
                }
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("user", user_pid);
                return params;
            }
        };queue.add(sr);
    }


    private void getDataForNomination(){
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + check_for_nomination,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject object) {
                        String date = null, start = null, end = null, num_needed = null;
                        int success;
                        int nomination_sid;
                        SimpleDateFormat formatter;
                        try {
                            formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Log.wtf("message", object.getString("success"));
                            success = object.getInt("success");

                            if(success==1) {
                                check = true;
                                Log.wtf("successful", "why2");
                                date = object.getString("nomination_date");
                                start = object.getString("nomination_start_time")+":00";
                                end = object.getString("nomination_end_time")+":00";
                                num_needed = object.getString("num_needed");
                                nomination_sid = Integer.parseInt(object.getString("sid"));
                                Log.wtf("getDataForNomination", String.valueOf(nomination_sid));
                                nomination_start = formatter.parse(date + " " + start);
                                nomination_end = formatter.parse(date + " " + end);

                                //stores the other data retrieved to the global class
                                AppController.getInstance().setNum_positions_needed(Integer.parseInt(num_needed));
                                AppController.getInstance().setNominationDate(date);
                                AppController.getInstance().setNominationStartTime(start);
                                AppController.getInstance().setNominationEndTime(end);
                                AppController.getInstance().setNomination_sid(nomination_sid);

                                mFragment = nominationFragment();

                            }else{
                                check = false;
                                nomination_start = null;
                                nomination_end = null;
                                AppController.getInstance().setNominationDate(null);
                                AppController.getInstance().setNominationStartTime(null);
                                AppController.getInstance().setNominationEndTime(null);
                                mFragment = noNominationFragment();
                            }
                        } catch (Exception e) {
                            Log.wtf("testing1", e.getMessage());
                        }
                        //adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        AppController.getInstance().addToRequestQueue(request);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Back?
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Back
            moveTaskToBack(true);
            return true;
        }
        else {
            // Return
            return super.onKeyDown(keyCode, event);
        }
    }

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void logout(){
        progressDialog = new ProgressDialog(MainActivity.this);
        // Showing progress dialog before making http request
        progressDialog.setMessage("Logging out...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + "logout.php",
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
                                SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.clear();
                                editor.commit();
                                finish();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                closeDrawer();
//            AppController.getInstance().clear();
                                AppController.getInstance().setNum_positions_needed(0);
                                AppController.getInstance().set_has_voted(0);
                                AppController.getInstance().set_has_nominated(0);
//            NominationAdapter.counter = 0;
                                Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(MainActivity.this,"There's something wrong while processing your request. Please try again.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this,"There's something wrong while processing your request. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("pid", user_pid);
                return params;
            }
        };queue.add(sr);

    }




}
