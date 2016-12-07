package app.psiteportal.com.fragments;

import android.app.DownloadManager;
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
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.psiteportal.ElectionResultsActivity;
import app.psiteportal.com.psiteportal.NoElection;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.Config;
import app.psiteportal.com.utils.ElectionAdapter;
import app.psiteportal.com.utils.JSONParser;
import app.psiteportal.com.utils.SntpClient;

/**
 * Created by fmpdroid on 2/3/2016.
 */
public class ElectionActivity2 extends Fragment implements SearchView.OnQueryTextListener {

    RecyclerView recyclerView;
    private List<Nominee> nomineeList = new ArrayList<>();
    private ElectionAdapter adapter;
    private Button btnVote;
    private Nominee n;
    private ProgressDialog progressDialog;
    private TextView timer;
    private static String nominees = "for_election.php";
    private static String elect = "elect.php";
    private static String check_for_election = "check_for_election.php";
    private static final String FORMAT = "%02d:%02d:%02d:%02d";
    private long result;
    private long current_time;
    private SntpClient client;
    private String positions;
    private String pid;
    private String usertype;
    private int count = 0;
    private String electionDate, electionStartTime, electionEndTime, num_needed, sid, election_id;
    private int voted, activated;
    private CheckBox checkBox;
    private SearchView searchView;
    private ArrayList<String> username = new ArrayList<String>();
    private String is_viewable = "0";
    private RelativeLayout relativeLayout;
    private TextView txtViewMessage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.gp_election1, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        btnVote = (Button) rootView.findViewById(R.id.btn_submit);
        timer = (TextView) rootView.findViewById(R.id.election_timer);
        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.loadingPanel);
        relativeLayout.setVisibility(View.GONE);
        btnVote.setVisibility(View.INVISIBLE);

        txtViewMessage = (TextView) rootView.findViewById(R.id.txtView_loadingPanel);
        Bundle bundle = this.getArguments();
        positions = bundle.getString("position", null);
        pid = bundle.getString("user_pid", null);
        usertype = bundle.getString("usertype", null);

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
                        Toast.makeText(getActivity(), "You need to vote " + num_needed + " members",
                                Toast.LENGTH_SHORT).show();
                    } else {
//                        if (nominated == 1) {
//                            Toast.makeText(getActivity(), "You have already nominated! Wait for further notice for the results.", Toast.LENGTH_LONG).show();
//                        } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Election!");
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

        /*btnVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int result = AppController.getInstance().get_has_voted();
                int activated = AppController.getInstance().getActivated();
                if(result==0) {
                    if(activated==1){
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Election!");
                    alertDialog.setMessage("Are you sure of your choices?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    vote();
                                    AppController.getInstance().set_has_voted(1);
//                                    recyclerView.getChildAt(1).setEnabled(false);
//                                    btnVote.setClickable(false);
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(alertDialog.BUTTON_NEGATIVE, "Cancel",
                            new DialogInterface.OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();}
                    else{
                        Toast.makeText(getActivity(),"Your account is not yet activated.", Toast.LENGTH_SHORT).show();
                    }
                }else{
//                    btnVote.setClickable(false);
//                    recyclerView.getChildAt(1).setEnabled(false);
                    Toast.makeText(getActivity(), "You have already voted! Wait for further notice for the results.", Toast.LENGTH_LONG).show();

                }
            }
        });*/
        return rootView;
    }


    private class TestAsync extends AsyncTask<String, String, Void> {
        long time_result;
        Date current, target;
        boolean check, has_not_started, ongoing_but_has_voted = false, is_done;
        String status;
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
//            electionDate = AppController.getInstance().getElectionDate();
//            electionStartTime = AppController.getInstance().getElectionStartTime();
//            electionEndTime = AppController.getInstance().getElectionEndTime();

            try {
                JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("pid", pid));
                JSONObject json = jsonParser.makeHttpRequest(Config.ROOT_URL + Config.WEB_SERVICES + check_for_election, "POST", params);
                success = json.getInt("success");
                if (success == 1) {
                    electionDate = json.getString("election_date").replace("-", "/");
                    electionStartTime = json.getString("start_time") + ":00";
                    electionEndTime = json.getString("end_time") + ":00";
                    num_needed = json.getString("num_needed");
                    status = json.getString("status");
                    voted = Integer.parseInt(json.getString("has_voted"));
                    activated = Integer.parseInt(json.getString("activated"));
//                    AppController.getInstance().set_has_voted(Integer.parseInt(json.getString("has_voted")));
                    AppController.getInstance().setNum_positions_needed(Integer.parseInt(num_needed));
                    Log.wtf("POSITIONS NEEDED", num_needed);
                    sid = json.getString("sid");
                    election_id = json.getString("election_id");
                    is_viewable = json.getString("is_viewable");
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
                    current = formatter.parse(test);
                    target = formatter.parse(electionDate + " " + electionEndTime);
                    start = formatter.parse(electionDate + " " + electionStartTime);
                    time_result = target.getTime() - current.getTime();

                    if (current.before(start)) {
                        has_not_started = true;
                        Log.wtf("heya!", "has not started yet");
                    }

                    if (current.after(start) && current.before(target) && voted == 1) {//AppController.getInstance().get_has_voted()==1){
                        ongoing_but_has_voted = true;
                    }
                    if (current.after(start) && current.after(target)) {
                        is_done = true;
                    }


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
            hidePDialog();
            if(success==0){
                relativeLayout.setVisibility(View.VISIBLE);
            }else
            if (check) {
                if (has_not_started || status.equals("0")) {
                    relativeLayout.setVisibility(View.VISIBLE);
//                    noElection();
                } else {
                    if (/*is_done && */is_viewable.equals("1")) {
//                        Toast.makeText(getActivity(), "results", Toast.LENGTH_LONG).show();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.navigation_container, listOfPeopleFragment("results"));
                        fragmentTransaction.commit();
                    } else if (ongoing_but_has_voted){// || (!ongoing_but_has_voted && status.equals("2"))) {
                        //view voted here
//                        Toast.makeText(getActivity(), "Here are the lists", Toast.LENGTH_SHORT).show();
                        if(usertype.equals("President")){
                            FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.navigation_container, listOfPeopleFragment("special"));
                        fragmentTransaction.commit();
                        }else {
                            relativeLayout.setVisibility(View.VISIBLE);
                            txtViewMessage.setText("You have already voted. Please wait for the results");
                        }
                        hidePDialog();
                    } else {
                        Log.wtf("Check result time", String.valueOf(result));
                        if (result <= 0) {
                            relativeLayout.setVisibility(View.VISIBLE);
//                            noElection();
                        } else {
//                            relativeLayout.setVisibility(View.GONE);
                            btnVote.setVisibility(View.VISIBLE);
                            setHasOptionsMenu(true);
                            getData();

                            new CountDownTimer(result, 1000) {
                                @Override
                                public void onTick(long l) {
                                    timer.setText("Election ends in " + String.format(FORMAT, TimeUnit.MILLISECONDS.toDays(l),
                                            TimeUnit.MILLISECONDS.toHours(l) - TimeUnit.DAYS.toHours(
                                                    TimeUnit.MILLISECONDS.toDays(l)),
                                            TimeUnit.MILLISECONDS.toMinutes(l) - TimeUnit.HOURS.toMinutes(
                                                    TimeUnit.MILLISECONDS.toHours(l)),
                                            TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(
                                                    TimeUnit.MILLISECONDS.toMinutes(l))));
                                }

                                @Override
                                public void onFinish() {
                                    Log.wtf("check", "onFinish is here");
//                                    noElection();
                                    setHasOptionsMenu(false);
                                    relativeLayout.setVisibility(View.VISIBLE);
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

    Fragment retryFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("positions", positions);
        bundle.putString("usertype", usertype);
        bundle.putString("user_pid", pid);
        bundle.putString("fragment", "election");
        RetryFragment retryFragment = new RetryFragment();
        retryFragment.setArguments(bundle);

        return retryFragment;
    }

    Fragment listOfPeopleFragment(String activity) {
        Bundle bundle = new Bundle();
        bundle.putString("usertype", usertype);
        bundle.putString("user_pid", pid);
        bundle.putString("election_id", election_id);
        bundle.putLong("time", result);
        bundle.putString("activity", activity);
        ListOfPeopleFragment listOfPeopleFragment = new ListOfPeopleFragment();
        listOfPeopleFragment.setArguments(bundle);

        return listOfPeopleFragment;
    }

    private void noElection() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.navigation_container, new NoElection());
        fragmentTransaction.commit();
    }

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void insertData(/*final String username*/final ArrayList<String> username, final String pid) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + elect, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("response", s.substring(0));
                if(usertype.equals("President")) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.navigation_container, listOfPeopleFragment("election"));
                    fragmentTransaction.commit();
                }else{
                    relativeLayout.setVisibility(View.VISIBLE);
                    txtViewMessage.setText("Please wait for the results.");
                }
                Toast.makeText(getActivity(), "You have successfully voted!", Toast.LENGTH_SHORT).show();
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
                params.put("pid", pid);
                params.put("election_id", election_id);
                params.put("count", String.valueOf(AppController.getInstance().getNum_positions_needed()));
                int i = 0;
                for (String object : username) {
                    params.put("username[" + i + "]", object);
                    Log.wtf("username[" + i + "]", object);
                    i++;
                }
                return params;
            }
        };
        queue.add(sr);
    }

    private void vote() {
        String data = "";
        List<Nominee> nom = adapter.getNomineeList();
        List elect = new ArrayList();
        for (int i = 0; i < nom.size(); i++) {
            n = nom.get(i);
            if (n.isSelected() == true) {
                elect.add(n.getUsername());
                count++;
            }
        }
        if (count == AppController.getInstance().getNum_positions_needed()) {
            for (int j = 0; j < elect.size(); j++) {
//                Log.d("tae", elect.get(j).toString());
//                insertData(elect.get(j).toString(), pid);
                username.add(elect.get(j).toString());
            }
            insertData(username, pid);
        } else {
            count = 0;
            Toast.makeText(getActivity(), "You need to vote " + AppController.getInstance().getNum_positions_needed() + " nominees", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.info:
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "You need to vote " + AppController.getInstance().getNum_positions_needed() + " nominee/s",
                        Snackbar.LENGTH_LONG).show();
//                Toast.makeText(getActivity(),"You need to vote "+AppController.getInstance().getNum_positions_needed()+" nominees", Toast.LENGTH_SHORT).show();
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_search:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;
            case R.id.election_result:
                Intent intent = new Intent(getActivity(), ElectionResultsActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.election_menu, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    public void getData() {

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + nominees,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
//                        hidePDialog();
                        try {
                            JSONArray jsonArray = new JSONArray(s.toString());
                            Log.wtf("glendon", jsonArray.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
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

                                nomineeList.add(nominee);
                            }
                            adapter = new ElectionAdapter(getActivity(), nomineeList);
                            recyclerView.setAdapter(adapter);
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
                Log.wtf("sid", sid);
                params.put("election_id", election_id);//String.valueOf(AppController.getInstance().getNomination_sid()));
                return params;
            }
        };
        queue.add(sr);
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
