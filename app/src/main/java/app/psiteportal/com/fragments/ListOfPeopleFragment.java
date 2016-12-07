package app.psiteportal.com.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import app.psiteportal.com.model.Member;
import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.Config;
import app.psiteportal.com.utils.ListOfPeopleAdapter;
import app.psiteportal.com.utils.MembershipActivationAdapter;
import app.psiteportal.com.utils.NominationAdapter;

/**
 * Created by fmpdroid on 3/18/2016.
 */
public class ListOfPeopleFragment extends Fragment{
    private static String get_list = "get_list.php";
    private static final String FORMAT = "%02d:%02d:%02d:%02d";
    private RecyclerView recyclerView;
    private ListOfPeopleAdapter adapter;
    private ProgressDialog progressDialog;
    private List<Nominee> nomineeList = new ArrayList<>();
    private String election_id, pid, activity, usertype;
    private long time;
    private TextView timer, counter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.list_of_people_container, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.list_recycler_view);
        timer = (TextView) rootView.findViewById(R.id.list_timer);
        counter = (TextView) rootView.findViewById(R.id.list_count);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        usertype = getArguments().getString("usertype");
        election_id = getArguments().getString("election_id");
        pid = getArguments().getString("user_pid");
        time = getArguments().getLong("time");
        activity = getArguments().getString("activity");
        AppController.getInstance().setActivity(activity);
        AppController.getInstance().setElection_id(election_id);
        Log.w("time here", String.valueOf(time));
        if(activity.equals("election")&&usertype.equals("President")){
            activity = "special";
        }
//        progressDialog = new ProgressDialog(getActivity());
//        // Showing progress dialog before making http request
//        progressDialog.setMessage("Checking...");
//        progressDialog.show();
        if(activity.equals("results")){
            getData();
            timer.setVisibility(View.GONE);
        }else {
            getData();
            new CountDownTimer(time, 1000) {
                @Override
                public void onTick(long l) {
                    String output = Character.toUpperCase(activity.charAt(0)) + activity.substring(1);
                    if (output.equals("Special")) {
                        output = "Election";
                    }
                    timer.setText(output + " ends in " + String.format(FORMAT, TimeUnit.MILLISECONDS.toDays(l),
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
//                                    noNomination();
                }
            }.start();
        }

        return rootView;
    }

    private class TestAsync extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public void getData() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + get_list,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        hidePDialog();
                        try {
                            JSONArray jsonArray = new JSONArray(s.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String imageUrl, username, name, first_name, last_name, institution, contact, email, address, count;
                                boolean choice = false;
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
                                name = last_name + ", " + first_name;
                                if(activity.equals("nomination")) {
                                    count = jsonObject.getString("count");
                                    if (jsonObject.getString("my_choice").equals("true")) {
                                        choice = true;
                                    } else if (jsonObject.getString("my_choice").equals("false")) {
                                        choice = false;
                                    }
                                    nominee = new Nominee(imageUrl, name, institution, contact, email, address, count, choice);
                                }else if((activity.equals("special")&& usertype.equals("President"))
                                        ||activity.equals("results")){
                                    count = jsonObject.getString("count");
                                    AppController.getInstance().setActivity("election");
                                    nominee = new Nominee(imageUrl,username, name, institution, contact, email, address, count);
                                }
                                else{// if(activity.equals("election")){
                                    nominee = new Nominee(imageUrl, username,  name, institution, contact, email, address);
                                }
                                Log.wtf("glendon", name);
                                nomineeList.add(nominee);
                            }

                            adapter = new ListOfPeopleAdapter(getActivity(), nomineeList);
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
                params.put("election_id", election_id);//String.valueOf(AppController.getInstance().getNomination_sid()));
                params.put("pid", pid);
                params.put("activity", activity);
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

}
