package app.psiteportal.com.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.psiteportal.NoNomination;
import app.psiteportal.com.psiteportal.OverrideActivity;
import app.psiteportal.com.psiteportal.OverrideActivityAdd;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.Config;
import app.psiteportal.com.utils.JSONParser;
import app.psiteportal.com.utils.NominationAdapter;
import app.psiteportal.com.utils.SntpClient;

/**
 * Created by fmpdroid on 2/14/2016.
 */
public class NominationFragment extends Fragment implements SearchView.OnQueryTextListener {

    RecyclerView recyclerView;
    private List<Nominee> nomineeList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private RelativeLayout progressBar;
    private NominationAdapter adapter;
    private static String eligible_members = "nominee_check.php";
    private static String check_for_nomination = "check_for_nomination.php";
    private static String nominate = "nominate.php";
    private Button btnVote;
    private Nominee n;
    private int count = 0;
    private String positions;
    private String pid, usertype;
    private TextView timer;
    private SntpClient client;
    private long result;
    private long current_time;
    private String nominationDate, nominationStartTime, nominationEndTime, election_id, nomination_sid, num_needed;
    private int nominated, activated;
    private static final String FORMAT = "%02d:%02d:%02d:%02d";
    private SearchView searchView;
    private ArrayList<String> username = new ArrayList<String>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gp_nomination, container, false);
//        NominationAdapter.counter = 0;
        recyclerView = (RecyclerView) rootView.findViewById(R.id.nomination_recyclerView);
        btnVote = (Button) rootView.findViewById(R.id.nomination_btn_submit);
        timer = (TextView) rootView.findViewById(R.id.nomination_timer);
        progressBar = (RelativeLayout) rootView.findViewById(R.id.loadingPanel);
        progressBar.setVisibility(View.GONE);
        btnVote.setVisibility(View.INVISIBLE);
        Bundle bundle = this.getArguments();
        pid = bundle.getString("user_pid");
        usertype = bundle.getString("usertype");
        Log.wtf("PID HERE", pid);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        new TestAsync().execute();

        btnVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = adapter.getCounter();
//                int result = AppController.getInstance().get_has_nominated();
                if (activated == 0) {
                    Toast.makeText(getActivity(), "Your account is not yet activated.", Toast.LENGTH_SHORT).show();
                } else {
                    if (count != Integer.parseInt(num_needed)) {
                        Toast.makeText(getActivity(), "You need to nominate " + num_needed + " members",
                                Toast.LENGTH_SHORT).show();
                    } else {
//                        if (nominated == 1) {
//                            Toast.makeText(getActivity(), "You have already nominated! Wait for further notice for the results.", Toast.LENGTH_LONG).show();
//                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                            alertDialog.setTitle("Nomination");
                            alertDialog.setMessage("Are you sure of your choices?");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            vote();
//                                            AppController.getInstance().set_has_nominated(1);

                                            //btnVote.setClickable(false);
                                            dialog.dismiss();
                                            //show list of people that you nominated

                                        }
                                    });
                            alertDialog.setButton(alertDialog.BUTTON_NEGATIVE, "Cancel",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
//                        }
                    }
                }
            }
        });
//        getData();
//        adapter = new NominationAdapter(getActivity(),nomineeList);
//        recyclerView.setAdapter(adapter);


        return rootView;
    }

    private void vote() {
        String data = "";
        List<Nominee> nom = adapter.getNomineeList();
        List nominate = new ArrayList();
        for (int i = 0; i < nom.size(); i++) {
            n = nom.get(i);
            if (n.isSelected() == true) {
//                insertData(n.getEmail(), pid);
                Log.wtf("isSelected", n.getName());
                nominate.add(n.getUsername());
                count++;
            }
        }
//        for(int i = 0; i < AppController.getInstance().getNominee().size(); i++){
//            Log.wtf("look here", AppController.getInstance().getNominee().get(i).toString());
//        }
        if (count == AppController.getInstance().getNum_positions_needed()) {
            for (int j = 0; j < nominate.size(); j++) {
//                insertData(nominate.get(j).toString(), pid);
                username.add(nominate.get(j).toString());
            }
            insertData(username, pid);
        } else {
            count = 0;
            Toast.makeText(getActivity(), "You need to nominate " + AppController.getInstance().getNum_positions_needed() + " person/s", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertData(/*final String username*/ final ArrayList<String> username, final String pid) {
//        progressDialog = new ProgressDialog(getActivity());
//        // Showing progress dialog before making http request
//        progressDialog.setMessage("Submitting...");
//        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + nominate, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("utot", s.substring(0));
                hidePDialog();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.navigation_container, listOfPeopleFragment());
                fragmentTransaction.commit();
                Toast.makeText(getActivity(), "You have successfully nominated!", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getContext(), "There's something wrong with your connection, try again later.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                Log.wtf("PID HERE", pid);
//                params.put("username", username);
                params.put("pid", pid);
                params.put("election_id", election_id);
                params.put("count", String.valueOf(AppController.getInstance().getNum_positions_needed()));
                int i = 0;
                for (String object : username) {
                    params.put("username[" + i + "]", object);
                    Log.wtf("username[" + i + "]", object);
                    i++;
                }//params.put("email", name);
                return params;
            }
        };
        queue.add(sr);
    }

    private class TestAsync extends AsyncTask<String, String, Void> {
        long time_result;
        Date current, target;
        boolean check, has_not_started, ongoing_but_has_nominated;
        int success;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            // Showing progress dialog before making http request
            progressDialog.setMessage("Checking...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {

            //Sntp Experiment starts here
            client = new SntpClient();
            Long result_time;
//            nominationDate = AppController.getInstance().getNominationDate();
//            nominationStartTime = AppController.getInstance().getNominationStartTime();
//            nominationEndTime = AppController.getInstance().getNominationEndTime();

            try {
                JSONParser jsonParser = new JSONParser();
//                JSONObject json = jsonParser.getJSONFromUrl(check_for_nomination);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("pid", pid));
                JSONObject json = jsonParser.makeHttpRequest(Config.ROOT_URL + Config.WEB_SERVICES + check_for_nomination, "POST", params);
                success = json.getInt("success");
                if (success == 1) {
                    nominationDate = json.getString("nom_date").replace("-", "/");
                    nominationStartTime = json.getString("nom_start_time") + ":00";
                    nominationEndTime = json.getString("nom_end_time") + ":00";
                    num_needed = json.getString("num_needed");
                    activated = Integer.parseInt(json.getString("activated"));
                    nominated = Integer.parseInt(json.getString("has_nominated"));
//                    AppController.getInstance().set_has_nominated(Integer.parseInt(json.getString("has_nominated")));
                    AppController.getInstance().setNum_positions_needed(Integer.parseInt(num_needed));
                    Log.wtf("POSITIONS NEEDED", num_needed);
                    election_id = json.getString("election_id");
                    nomination_sid = json.getString("sid");
                } else if (success == 0) {

                }
                if (client.requestTime("0.pool.ntp.org", 30000)) {
                    check = true;
                    current_time = client.getNtpTime();
                    Date current_date = new Date(current_time);

                    //test
                    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
                    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd");
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    String test = formatter.format(current_date);
                    String formattedTime = sdfTime.format(current_date);
                    String formattedDate = sdfDate.format(current_date);

                    long curMillis = current_date.getTime();

                    Date start;


                    Log.wtf("BASA DIRI", nominationDate + " " + nominationEndTime);
                    current = formatter.parse(test);
                    target = formatter.parse(nominationDate + " " + nominationEndTime);
                    start = formatter.parse(nominationDate + " " + nominationStartTime);
                    time_result = target.getTime() - current.getTime();
                    Log.wtf("ANIMAL", String.valueOf(time_result));
                    Date testTime = sdfTime.parse(formattedTime);
                    Date testEndTime = sdfTime.parse("23:59:00");
                    long time = testTime.getTime();
                    long end_time = testEndTime.getTime();
                    //time_result = testEndTime.getTime() - testTime.getTime();

                    if (current.before(start)) {
                        has_not_started = true;
                        Log.wtf("heya!", "has not started yet");
                    }

                    if (current.after(start) && current.before(target) && nominated == 1){//AppController.getInstance().get_has_nominated() == 1) {
                        ongoing_but_has_nominated = true;
                    }

                    long diffSeconds = time_result / 1000 % 60;
                    long diffMinutes = time_result / (60 * 1000) % 60;
                    long diffHours = time_result / (60 * 60 * 1000) % 24;
                    long diffDays = time_result / (24 * 60 * 60 * 1000);

                    Date result_date = new Date(time_result);
                    String formattedTime1 = sdfTime.format(result_date);
                    Log.d("result here", String.valueOf(diffHours) + ":" + String.valueOf(diffMinutes) + " " + String.valueOf(diffSeconds) + ":" + String.valueOf(diffDays));
                    Log.d("result here", testTime.toString());
                    Log.d("result here", String.valueOf(time_result));
                    Log.d("result here", testEndTime.toString());
                    Log.d("result here", result_date.toString());

                    Log.d("time here", String.valueOf(current_time));
                    Log.d("date", test);
                    Log.d("date", formattedDate);
                    Log.d("time", formattedTime);


                } else {
                    Log.d("time here", "failed");
                    check = false;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            result = time_result;

            Log.wtf("checking SID async", election_id);
            //checks if it was able to fetch time on the internet
            if (check) {
                //checks if current time < start time
                if (has_not_started) {
                    hidePDialog();
                    progressBar.setVisibility(View.VISIBLE);
//                    noNomination();
                } else {
                    if (ongoing_but_has_nominated) {
                        hidePDialog();
                        Log.wtf("time here", String.valueOf(result));
                        Log.wtf("time here1", String.valueOf(time_result));
                        //show list of people that you nominated
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.navigation_container, listOfPeopleFragment());
                        fragmentTransaction.commit();
//                        setHasOptionsMenu(true);
//                        hidePDialog();
                    } else {
                        //result = nomination end time - current time
                        if (result <= 0) {
                            hidePDialog();
                            progressBar.setVisibility(View.VISIBLE);
//                            noNomination();
                        } else {
//                            progressBar.setVisibility(View.GONE);
                            hidePDialog();
                            btnVote.setVisibility(View.VISIBLE);
                            setHasOptionsMenu(true);
                            getData();

                            new CountDownTimer(result, 1000) {
                                @Override
                                public void onTick(long l) {
                                    timer.setText("Nomination ends in " + String.format(FORMAT, TimeUnit.MILLISECONDS.toDays(l),
                                            TimeUnit.MILLISECONDS.toHours(l) - TimeUnit.DAYS.toHours(
                                                    TimeUnit.MILLISECONDS.toDays(l)),
                                            TimeUnit.MILLISECONDS.toMinutes(l) - TimeUnit.HOURS.toMinutes(
                                                    TimeUnit.MILLISECONDS.toHours(l)),
                                            TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(
                                                    TimeUnit.MILLISECONDS.toMinutes(l))));
                                }

                                @Override
                                public void onFinish() {
                                    AppController.getInstance().setIndicator(2);
                                    Log.wtf("Nomination is done", "it's done");
                                    progressBar.setVisibility(View.VISIBLE);
                                    setHasOptionsMenu(false);
//                                    noNomination();
                                }
                            }.start();
                        }
                    }
                }
            } else {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.navigation_container, retryFragment());
                fragmentTransaction.commit();
                hidePDialog();
            }
        }
    }

    Fragment nominationFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", pid);
        NominationFragment nominationFragment = new NominationFragment();
        nominationFragment.setArguments(bundle);

        return nominationFragment;
    }

    public void noNomination() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.navigation_container, new NoNomination());
        fragmentTransaction.commit();
    }

    Fragment retryFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", pid);
        bundle.putString("usertype", usertype);
        bundle.putString("fragment", "nomination");
        RetryFragment retryFragment = new RetryFragment();
        retryFragment.setArguments(bundle);

        return retryFragment;
    }

    Fragment listOfPeopleFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", pid);
        bundle.putString("election_id", election_id);
        bundle.putLong("time", result);
        bundle.putString("activity", "nomination");
        ListOfPeopleFragment listOfPeopleFragment = new ListOfPeopleFragment();
        listOfPeopleFragment.setArguments(bundle);

        return listOfPeopleFragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.info:
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "You need to nominate " + num_needed + " member/s",
                        Snackbar.LENGTH_LONG).show();
//                Toast.makeText(getActivity(),"You need to vote "+AppController.getInstance().getNum_positions_needed()+" nominees", Toast.LENGTH_SHORT).show();
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_search:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            case R.id.nomination_override_add:
                intent = new Intent(getActivity(), OverrideActivityAdd.class);
                startActivity(intent);
                break;

            case R.id.nomination_override:
                intent = new Intent(getActivity(), OverrideActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (usertype.equals("President")) {
            inflater.inflate(R.menu.president_nomination_menu, menu);
        } else {
            inflater.inflate(R.menu.nomination_menu, menu);
        }
        final MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    public void getData() {
        Log.wtf("checking SID volley", String.valueOf(AppController.getInstance().getNomination_sid()));
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + eligible_members,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
//                        hidePDialog();
                        try {
                            JSONArray jsonArray = new JSONArray(s.toString());
                            Log.wtf("glendon", jsonArray.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Log.wtf("glendon", "diri");
                                String imageUrl, username, name, first_name, last_name, institution, contact, email, address;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Nominee nominee;
                                imageUrl = jsonObject.getString("prof_pic");
                                username = jsonObject.getString("username");
                                first_name = jsonObject.getString("firstname");
                                last_name = jsonObject.getString("lastname");
                                institution = jsonObject.getString("institution_name");
                                contact = jsonObject.getString("contact");
                                email = jsonObject.getString("email");
                                address = jsonObject.getString("address");
                                name = first_name + " " + last_name;
                                nominee = new Nominee(imageUrl, username, name, institution, contact, email, address);
                                Log.wtf("glendon", name);
                                nomineeList.add(nominee);
                            }
                            adapter = new NominationAdapter(getActivity(), nomineeList);
                            recyclerView.setAdapter(adapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.wtf("hahay", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.navigation_container, retryFragment());
//                fragmentTransaction.commit();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("sid", nomination_sid);//String.valueOf(AppController.getInstance().getNomination_sid()));
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

    @Override
    public boolean onQueryTextChange(String query) {
        if (!query.isEmpty()) {
            btnVote.setVisibility(View.GONE);
        } else {
            btnVote.setVisibility(View.VISIBLE);
        }
        final List<Nominee> filteredMemberList = filter(nomineeList, query);
        adapter.animateTo(filteredMemberList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<Nominee> filter(List<Nominee> nominees, String query) {
        query = query.toLowerCase();

        final List<Nominee> filteredModelList = new ArrayList<>();
        for (Nominee nominee : nominees) {
            final String text = nominee.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(nominee);
            }
        }
        return filteredModelList;
    }

}
