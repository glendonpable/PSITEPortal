package app.psiteportal.com.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

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

import app.psiteportal.com.model.Member;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.Config;
import app.psiteportal.com.utils.DisabledUsersAdapter;

/**
 * Created by fmpdroid on 3/28/2016.
 */
public class DisabledUsersFragment extends Fragment implements SearchView.OnQueryTextListener{

    private static String get_disabled_users = "get_disabled_users.php";
    private static String restore_user = "restore_user.php";
    private List<Member> memberList;
    private RecyclerView recyclerView;
    private DisabledUsersAdapter adapter;
    private String pid, user_usertype;
    private ProgressDialog progressDialog;
    private SearchView searchView;
    private RelativeLayout relativeLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.member_activation, container, false);
        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.members_recycler_view);
        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.loadingPanel);
        memberList = new ArrayList<>();

        Bundle bundle = this.getArguments();
        pid = bundle.getString("user_pid");
        user_usertype = bundle.getString("usertype");

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        progressDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        progressDialog.setMessage("Fetching...");
//        progressDialog.show();
        populate();


        return rootView;
    }

    public void populate(){
        JsonArrayRequest request = new JsonArrayRequest(Config.ROOT_URL + Config.WEB_SERVICES + get_disabled_users,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        hidePDialog();
                        relativeLayout.setVisibility(View.GONE);
                        String  username, name, email, status;
                        Member member;
                        for(int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject object = jsonArray.getJSONObject(i);
                                username = object.getString("username");
                                name = object.getString("lastname") + ", " + object.getString("firstname");
                                email = object.getString("email");
                                if(Integer.parseInt(object.getString("activated"))==1){
                                    status = "Active";
                                }else{
                                    status = "Inactive";
                                }
                                member = new Member(username, name, email, status);
                                memberList.add(member);

                            } catch (Exception e) {

                            }
                            adapter = new DisabledUsersAdapter(getActivity(), memberList);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        });
        AppController.getInstance().addToRequestQueue(request);

    }

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu2, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.enabled_users:
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.navigation_container, membersFragment());
                fragmentTransaction.commit();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String query) {

        final List<Member> filteredMemberList = filter(memberList, query);
        adapter.animateTo(filteredMemberList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<Member> filter(List<Member> members, String query) {
        query = query.toLowerCase();

        final List<Member> filteredModelList = new ArrayList<>();
        for (Member member : members) {
            final String text = member.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(member);
            }
        }
        return filteredModelList;
    }

    Fragment membersFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", pid);
        bundle.putString("usertype", user_usertype);
        MembershipActivationFragment memberFragment = new MembershipActivationFragment();
        memberFragment.setArguments(bundle);

        return memberFragment;
    }
    Fragment disabledUsersFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", pid);
        bundle.putString("usertype", user_usertype);
        DisabledUsersFragment disabledUsersFragment = new DisabledUsersFragment();
        disabledUsersFragment.setArguments(bundle);

        return disabledUsersFragment;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final String username = DisabledUsersAdapter.username;
        if (item.getTitle() == "Restore User") {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Delete user")
                    .setMessage("Are you sure to re-enable this user?")
                    .setIcon(R.drawable.ic_warning)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            restoreUser(username);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }
        return super.onContextItemSelected(item);
    }


    public void restoreUser(final String username){
        progressDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        progressDialog.setMessage("Restoring...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + restore_user,
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
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.navigation_container, disabledUsersFragment());
                                fragmentTransaction.commit();
                                Toast.makeText(getActivity(), "Successfully restored user.", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getActivity(),"There's something wrong while processing your request. Please try again.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(),"There's something wrong while processing your request. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("username",username);//String.valueOf(AppController.getInstance().getNomination_sid()));
                return params;
            }
        };queue.add(sr);

    }
}
