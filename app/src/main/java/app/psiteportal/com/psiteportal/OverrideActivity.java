package app.psiteportal.com.psiteportal;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.utils.Config;
import app.psiteportal.com.utils.OverrideAdapter;
import app.psiteportal.com.utils.SntpClient;

/**
 * Created by fmpdroid on 2/3/2016.
 */
public class OverrideActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    RecyclerView recyclerView;
    private List<Nominee> nomineeList = new ArrayList<>();
    private OverrideAdapter adapter;
    private Nominee n;
    private ProgressDialog progressDialog;
    private static String get_list_for_override = "get_list_for_override.php";
    private static String override = "override.php";
    private static String check_for_election = "check_for_election.php";
    private static final String FORMAT = "%02d:%02d:%02d:%02d";
    private long result;
    private long current_time;
    private SntpClient client;
    private String positions;
    private String pid;
    private int count = 0;
    private String electionDate, electionStartTime, electionEndTime, num_needed, sid, election_id;
    private CheckBox checkBox;
    private SearchView searchView;
    private ArrayList<String> add_username = new ArrayList<String>();
    private ArrayList<String> remove_username = new ArrayList<String>();
    private Button btnSubmit;
    private RelativeLayout relativeLayout, relativeLayout1;

//    public static OverrideFragment newInstance(String pid, String usertype, String positions){
//        OverrideFragment overrideFragment = new OverrideFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("user_pid", pid);
//        bundle.putString("usertype", usertype);
//        bundle.putString("position", positions);
//        overrideFragment.setArguments(bundle);
//        return overrideFragment;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.override_container);
//        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Override Nomination");

        recyclerView = (RecyclerView) findViewById(R.id.override_recycler_view);
        btnSubmit = (Button) findViewById(R.id.override_btn_submit);
        relativeLayout = (RelativeLayout) findViewById(R.id.loadingPanel);
        relativeLayout1 = (RelativeLayout) findViewById(R.id.loadingPanel1);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(OverrideActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        getData();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(OverrideActivity.this).create();
                alertDialog.setTitle("Override!");
                alertDialog.setMessage("Are you sure of your choices?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<Nominee> ovr = adapter.getNomineeList();
                        List override_add = new ArrayList();
                        List override_remove = new ArrayList();
                        for (int i = 0; i < ovr.size(); i++) {
                            n = ovr.get(i);
                            if (n.isSelected() == true) {
                                Log.wtf("isSelected", n.getName());
                                override_remove.add(n.getUsername());
                            }else{
                                override_add.add(n.getUsername());
                            }
                        }
                        for (int j = 0; j < override_remove.size(); j++) {
                            remove_username.add(override_remove.get(j).toString());
                        }
                        for (int j = 0; j < override_add.size(); j++) {
                            add_username.add(override_add.get(j).toString());
                        }
                        insertData(add_username, remove_username);
                    }
                });
                alertDialog.setButton(alertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        final View rootView = inflater.inflate(R.layout.override_container, container, false);
//        recyclerView = (RecyclerView) rootView.findViewById(R.id.override_recycler_view);
//        btnSubmit = (Button) rootView.findViewById(R.id.override_btn_submit);
//        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.loadingPanel);
//        relativeLayout1 = (RelativeLayout) rootView.findViewById(R.id.loadingPanel1);
//        Bundle bundle = this.getArguments();
//        positions = bundle.getString("position", null);
//        pid = bundle.getString("user_pid", null);
//
//        recyclerView.setHasFixedSize(true);
//        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(llm);
//        getData();
//
//        btnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
//                alertDialog.setTitle("Override!");
//                alertDialog.setMessage("Are you sure of your choices?");
//                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        List<Nominee> ovr = adapter.getNomineeList();
//                        List override_add = new ArrayList();
//                        List override_remove = new ArrayList();
//                        for (int i = 0; i < ovr.size(); i++) {
//                            n = ovr.get(i);
//                            if (n.isSelected() == true) {
//                                Log.wtf("isSelected", n.getName());
//                                override_remove.add(n.getEmail());
//                            }else{
//                                override_add.add(n.getEmail());
//                            }
//                        }
//                        for (int j = 0; j < override_remove.size(); j++) {
//                            remove_emails.add(override_remove.get(j).toString());
//                        }
//                        for (int j = 0; j < override_add.size(); j++) {
//                            add_emails.add(override_add.get(j).toString());
//                        }
//                        insertData(add_emails, remove_emails);
//                    }
//                });
//                alertDialog.setButton(alertDialog.BUTTON_NEGATIVE, "No",
//                        new DialogInterface.OnClickListener(){
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                alertDialog.show();
//            }
//        });
//        return rootView;
//    }


//    Fragment retryFragment() {
//        Bundle bundle = new Bundle();
//        bundle.putString("user_pid", pid);
//        bundle.putString("fragment", "election");
//        RetryFragment retryFragment = new RetryFragment();
//        retryFragment.setArguments(bundle);
//
//        return retryFragment;
//    }
//    Fragment listOfPeopleFragment(){
//        Bundle bundle = new Bundle();
//        bundle.putString("user_pid", pid);
//        bundle.putString("election_id", election_id);
//        bundle.putLong("time", result);
//        bundle.putString("activity", "election");
//        ListOfPeopleFragment listOfPeopleFragment = new ListOfPeopleFragment();
//        listOfPeopleFragment.setArguments(bundle);
//
//        return listOfPeopleFragment;
//    }
//
//    private void noElection(){
//        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.navigation_container, new NoElection());
//        fragmentTransaction.commit();
//    }

    private void hidePDialog(){
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void insertData(/*final String username*/final ArrayList<String> add_username, final ArrayList<String> remove_username){
        RequestQueue queue = Volley.newRequestQueue(OverrideActivity.this);
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + override, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("response", s.substring(0));
                Toast.makeText(OverrideActivity.this,"Successfully saved changes", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(OverrideActivity.this, "There's something wrong with your connection, try again later.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                int i = 0;
                int ii= 0;
                for(String object : add_username) {
                    params.put("add_username[" + i + "]", object);
                    Log.wtf("email[" + i + "]", object);
                    i++;
                }
                for(String object : remove_username) {
                    params.put("remove_username[" + ii + "]", object);
                    Log.wtf("email[" + ii + "]", object);
                    ii++;
                }
                params.put("add_count", String.valueOf(i));
                params.put("remove_count", String.valueOf(ii));
                return params;
            }
        };queue.add(sr);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
//            case R.id.info:
//                Snackbar.make(getActivity().findViewById(android.R.id.content),
//                        "You need to vote "+AppController.getInstance().getNum_positions_needed()+" nominees",
//                        Snackbar.LENGTH_LONG).show();
////                Toast.makeText(getActivity(),"You need to vote "+AppController.getInstance().getNum_positions_needed()+" nominees", Toast.LENGTH_SHORT).show();
//                // User chose the "Settings" item, show the app settings UI...
//                return true;

            case R.id.action_search:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.override_menu, menu);
//
//        final MenuItem item = menu.findItem(R.id.action_search);
//        searchView = (SearchView) MenuItemCompat.getActionView(item);
//        searchView.setOnQueryTextListener(this);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.override_menu, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    public void getData(){

        RequestQueue queue = Volley.newRequestQueue(OverrideActivity.this);
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + get_list_for_override,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
//                        hidePDialog();
                        relativeLayout.setVisibility(View.GONE);
                        try {
                            JSONArray jsonArray = new JSONArray(s.toString());
                            Log.wtf("glendon", jsonArray.toString());
                            for(int i = 0; i < jsonArray.length(); i++) {
                                String imageUrl, username, name, first_name, last_name, institution, contact, email, address, is_auto_added, is_excluded;
                                boolean checked = false;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Nominee nominee;
                                if(jsonObject.getString("success").equals("0")){
                                    relativeLayout1.setVisibility(View.VISIBLE);
                                }else {
                                    imageUrl = jsonObject.getString("prof_pic");
                                    username = jsonObject.getString("username");
                                    first_name = jsonObject.getString("firstname");
                                    last_name = jsonObject.getString("lastname");
                                    institution = jsonObject.getString("institution_name");
                                    contact = jsonObject.getString("contact");
                                    email = jsonObject.getString("email");
                                    address = jsonObject.getString("address");
                                    is_auto_added = jsonObject.getString("is_auto_added");
                                    is_excluded = jsonObject.getString("is_excluded");
                                    name = first_name + " " + last_name;
                                    if (Integer.parseInt(is_excluded) == 0) {
                                        checked = true;
                                    } else if (Integer.parseInt(is_excluded) == 1) {
                                        checked = false;
                                    }
                                    nominee = new Nominee(imageUrl,username, name, institution, contact, email, address, checked, "");

                                    nomineeList.add(nominee);
                                }
                            }
                            adapter = new OverrideAdapter(OverrideActivity.this,nomineeList);
                            recyclerView.setAdapter(adapter);
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.wtf("hahay", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });queue.add(sr);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if(!query.isEmpty()){
            btnSubmit.setVisibility(View.GONE);
        }else{
            btnSubmit.setVisibility(View.VISIBLE);
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



//    public void noNomination(){
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.navigation_container, new NoNomination());
//        fragmentTransaction.commit();
//    }
}
