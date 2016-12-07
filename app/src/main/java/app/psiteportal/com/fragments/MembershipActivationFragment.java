package app.psiteportal.com.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.psiteportal.com.model.Member;
import app.psiteportal.com.psiteportal.MemberActivationActivity;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.psiteportal.TestActivity;
import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.Config;
import app.psiteportal.com.utils.JSONParser;
import app.psiteportal.com.utils.MembershipActivationAdapter;

/**
 * Created by fmpdroid on 3/11/2016.
 */
public class MembershipActivationFragment extends Fragment implements SearchView.OnQueryTextListener, PopupMenu.OnMenuItemClickListener{

    private static String get_members = "get_members.php";
    private static String delete_user = "delete_user.php";
    private static String update_position = "update_position.php";
    private static String get_position = "get_position.php";
    private List<Member> memberList;
    private RecyclerView recyclerView;
    private MembershipActivationAdapter adapter;
    private String pid;
    public static String user_usertype;
    private ProgressDialog progressDialog, pDialog;
    private SearchView searchView;
    private String usertype;
    private String current_position;
    private String username;
    private RelativeLayout relativeLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.member_activation, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.members_recycler_view);
        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.loadingPanel);
        setHasOptionsMenu(true);

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
        JsonArrayRequest request = new JsonArrayRequest(Config.ROOT_URL + Config.WEB_SERVICES + get_members,
                    new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        relativeLayout.setVisibility(View.GONE);
                        hidePDialog();
                        String  username, name, email, status = "";
                        String imageUrl, institution, contact, address;
                        Member member;
                        int position;
                        for(int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject object = jsonArray.getJSONObject(i);
                                username = object.getString("username");
                                name = object.getString("lastname") + ", " + object.getString("firstname");
                                email = object.getString("email");
                                position = Integer.parseInt(object.getString("usertype_id"));
                                switch (position){
                                    case 0:
                                        status = "Non-Member";
                                        break;
                                    case 1:
                                        status = "Member";
                                        break;
                                    case 2:
                                        status = "Representative-Institution";
                                        break;
                                    case 3:
                                        status = "Officer";
                                        break;
                                    case 4:
                                        status = "Admin";
                                        break;
                                    case 5:
                                        status = "President";
                                        break;
                                }
//                                if(Integer.parseInt(object.getString("activated"))==1){
//                                    status = "Member";
//                                }else{
//                                    status = "Non-Member";
//                                }
                                imageUrl = object.getString("prof_pic");
                                institution = object.getString("institution_name");
                                contact = object.getString("contact");
                                address = object.getString("address");
                                member = new Member(username, imageUrl, name, institution, contact, email, address, status);
                                memberList.add(member);

                            } catch (Exception e) {

                            }
                            adapter = new MembershipActivationAdapter(getActivity(), memberList);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(user_usertype.contains("Officer")||user_usertype.contains("President")){
            inflater.inflate(R.menu.search_menu, menu);
        }else{
            inflater.inflate(R.menu.simple_search_menu, menu);
        }


        final MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.activate_user:
                intent = new Intent(getContext(), MemberActivationActivity.class);
                intent.putExtra("user_pid", pid);
                intent.putExtra("usertype", user_usertype);
                startActivity(intent);
                break;
            case R.id.disabled_users:
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.navigation_container, disabledUsersFragment());
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

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        username = MembershipActivationAdapter.username;

        if(item.getTitle()=="Disable User"){
            new AlertDialog.Builder(getActivity())
                    .setTitle("Disable user")
                    .setMessage("Are you sure to disable this user?")
                    .setIcon(R.drawable.ic_warning)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            deleteUser(username);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }else if(item.getTitle()=="Change Position"){
//            String name = MembershipActivationAdapter.username;
//            Toast.makeText(getActivity(), username, Toast.LENGTH_LONG).show();
            getPosition(username);
//            new TestAsync().execute();
//            getPosition(email);
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//            View view = info.targetView;
//            PopupMenu popupMenu = new PopupMenu(getContext(), recyclerView);
//            popupMenu.setOnMenuItemClickListener(this);
//            popupMenu.inflate(R.menu.popup_menu);
//            popupMenu.show();
//            Toast.makeText(getActivity(), "promote", Toast.LENGTH_SHORT).show();

            LayoutInflater li = LayoutInflater.from(getActivity());

            View promptsView = li.inflate(R.layout.dialog_spinner_layout, null);

        }else if(item.getTitle()=="View Profile"){
//            getPosition(email);
            Intent intent = new Intent(getActivity(), TestActivity.class);
            intent.putExtra("prof_pic", AppController.getInstance().getProf_pic());
            intent.putExtra("name", AppController.getInstance().getName());
            intent.putExtra("institution", AppController.getInstance().getInstitution());
            intent.putExtra("contact", AppController.getInstance().getContact());
            intent.putExtra("email", AppController.getInstance().getEmail());
            intent.putExtra("address", AppController.getInstance().getAddress());
            AppController.getInstance().setActivity("member");
            startActivity(intent);

        }
        return super.onContextItemSelected(item);
    }


    public void deleteUser(final String username){
        progressDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        progressDialog.setMessage("Deleting...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + delete_user,
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
                                fragmentTransaction.replace(R.id.navigation_container, membersFragment());
                                fragmentTransaction.commit();
                                Toast.makeText(getActivity(), "Successfully deleted user.", Toast.LENGTH_SHORT).show();
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

    public void updatePosition(final String username, final String usertype){
        progressDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        progressDialog.setMessage("Updating...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + update_position,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        int success;
                        hidePDialog();
                        try {
                            JSONObject object = new JSONObject(s.toString());
                            success = object.getInt("success");
                            Log.wtf("read", s.toString());
                            Log.wtf("success", String.valueOf(success));
                            Log.wtf("message", object.getString("message"));
//                            success = object.getInt("success");
                            if(success==1){
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.navigation_container, membersFragment());
                                fragmentTransaction.commit();
                                Toast.makeText(getActivity(), "Successfully updated position.", Toast.LENGTH_SHORT).show();
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
                params.put("usertype",usertype);//String.valueOf(AppController.getInstance().getNomination_sid()));
                params.put("username",username);
                return params;
            }
        };queue.add(sr);

    }

    public void getPosition(final String username){
        final List<String> list = new ArrayList<String>();
        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Fetching Data...");
        pDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + get_position,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
//                        hidePDialog();
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                JSONParser jsonParser = new JSONParser();
                                List<NameValuePair> params = new ArrayList<NameValuePair>();
                                params.add(new BasicNameValuePair("username", username));
                                JSONObject json = jsonParser.makeHttpRequest(Config.ROOT_URL + Config.WEB_SERVICES + get_position, "POST", params);
                                try {
                                    current_position = json.getString("current_position");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        t.start();
                        try {
                            t.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        try {
                                JSONArray array = new JSONArray(s.toString());
                            for(int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
//                                String text = object.getString("usertype_name");
                                list.add(object.getString("usertype_name"));
                            }list.remove(current_position);
                            list.remove("Non-Member");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

//                                String[] s = { "Member-Individual", "Member-Institutional", "Officer-Member", "Admin", "President"};
                                final ArrayAdapter<String> adp = new ArrayAdapter<String>(getActivity(),
                                        android.R.layout.simple_spinner_dropdown_item, list);

                                alertDialogBuilder.setTitle("Update Position");
                                alertDialogBuilder.setMessage("Current position: "+ current_position + "\n\nChange position to:");
                                alertDialogBuilder.setIcon(R.drawable.ic_warning);
                                alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        updatePosition(username, usertype);
                                    }
                                });
                                alertDialogBuilder.setNegativeButton(android.R.string.no, null);

                                final Spinner spinner = new Spinner(getActivity());
                                spinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    spinner.setDropDownWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                                }
                                spinner.setAdapter(adp);
                                alertDialogBuilder.setView(spinner);
//            final Spinner mSpinner= (Spinner) promptsView
//                    .findViewById(R.id.spinner);

                                // reference UI elements from my_dialog_layout in similar fashion
                                final AlertDialog alertDialog = alertDialogBuilder.create();
                                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    Toast.makeText(getActivity(),String.valueOf(position+1)+parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                                        usertype = parent.getSelectedItem().toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                                // show it
                                alertDialog.show();
                                alertDialog.setCanceledOnTouchOutside(false);
                            }
                        });
                        pDialog.dismiss();
                        pDialog = null;


//                        try {
//                            JSONObject object = new JSONObject(s.toString());
//                            position = object.getInt("usertype_id")-1;
//                            Toast.makeText(getActivity(),"Pos "+ String.valueOf(position), Toast.LENGTH_SHORT).show();
////                            success = object.getInt("success");
//                            //JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }

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
//                params.put("email",email);//String.valueOf(AppController.getInstance().getNomination_sid()));
                return params;
            }
        };queue.add(sr);
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
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

}
