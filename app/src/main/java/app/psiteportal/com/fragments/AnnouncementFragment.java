package app.psiteportal.com.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
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

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.psiteportal.com.model.Announcement;
import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.psiteportal.AddAnnouncementActivity;
import app.psiteportal.com.psiteportal.AddSeminarActivity;
import app.psiteportal.com.psiteportal.EditAnnouncementActivity;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.AnnouncementAdapter;
import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.Config;
import app.psiteportal.com.utils.SntpClient;

/**
 * Created by fmpdroid on 2/29/2016.
 */
public class AnnouncementFragment extends Fragment{
    RecyclerView recyclerView;
    private AnnouncementAdapter adapter;
    private List<Announcement> announcementLIst = new ArrayList<>();
    private Announcement a;
    private static String announcementUrl = "get_announcements.php";
    private ProgressDialog progressDialog;

    private String user_pid;
    public static String user_usertype;
    private FloatingActionButton fabButton;
    private FrameLayout fabLayout;
    private RelativeLayout relativeLayout;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.announcement, container, false);

        Bundle bundle = getArguments();
        if(bundle != null){
            user_pid = bundle.getString("user_pid");
            user_usertype = bundle.getString("usertype");
            Log.wtf("bundle data", user_pid + " " + user_usertype);
        }else{
            Log.wtf("bundle", "bundle is empty");
        }

        recyclerView = (RecyclerView) rootView.findViewById(R.id.announcement_recycler_view);

        fabButton = (FloatingActionButton) rootView.findViewById(R.id.announcementBtnFloatingAction);
        fabLayout = (FrameLayout) rootView.findViewById(R.id.announcementLayoutInner);

        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.loadingPanel);
        relativeLayout.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        if(user_usertype.equals("President")||user_usertype.contains("Officer")){
            setupUI(rootView);
        }else{
            fabLayout.setVisibility(View.GONE);
        }

        getAnnouncements();
        return rootView;
    }

    private void getAnnouncements(){
            progressDialog = new ProgressDialog(getActivity());
            // Showing progress dialog before making http request
            progressDialog.setMessage("Fetching...");
            progressDialog.show();

        JsonArrayRequest request = new JsonArrayRequest(Config.ROOT_URL + Config.WEB_SERVICES + announcementUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
//                    relativeLayout.setVisibility(View.GONE);
                        hidePDialog();
                        for(int i = 0; i < jsonArray.length(); i++){
                            try {
                                Log.wtf("length", String.valueOf(jsonArray.length()));
                                SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
                                SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                String aid, pid, title, details, venue, name, date, time, formattedTime, formattedDate;
                                String success;
                                Date formatDate;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Announcement announcement;
                                success = jsonObject.getString("success");
                                if(success.equals("0")){
                                    relativeLayout.setVisibility(View.VISIBLE);
                                }else {
                                    aid = jsonObject.getString("aid");
                                    pid = jsonObject.getString("pid");
                                    title = jsonObject.getString("announcement_title");
                                    details = jsonObject.getString("announcement_details");
                                    date = jsonObject.getString("date").replace("-", "/");
                                    formatDate = formatter.parse(date);
                                    formattedDate = dateFormatter.format(formatDate);
                                    formattedTime = timeFormatter.format(formatDate);
                                    venue = jsonObject.getString("announcement_venue");
                                    name = jsonObject.getString("firstname") + " " + jsonObject.getString("lastname");
                                    announcement = new Announcement(aid, pid, title, details, formattedDate, formattedTime, venue, name);

                                    announcementLIst.add(announcement);
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                                Log.w("wewewe", e.getMessage());
                            }

                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                hidePDialog();
                volleyError.printStackTrace();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.navigation_container, retryFragment());
                fragmentTransaction.commit();

            }
        });
        AppController.getInstance().addToRequestQueue(request);
        adapter = new AnnouncementAdapter(getActivity(),announcementLIst);
        recyclerView.setAdapter(adapter);
    }

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
    Fragment retryFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);
        bundle.putString("usertype", user_usertype);
        bundle.putString("fragment", "announcement");
        RetryFragment retryFragment = new RetryFragment();
        retryFragment.setArguments(bundle);

        return retryFragment;
    }

    private void setupUI(View rootView) {
        fabButton = (FloatingActionButton) rootView.findViewById(R.id.announcementBtnFloatingAction);
        fabButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                Toast.makeText(getActivity(), "Hello FAB!", Toast.LENGTH_SHORT).show();
                // TODO issue: Rotate animation in pre-lollipop works only once, issue to be resolved!
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    RotateAnimation rotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setDuration(500);
                    rotateAnimation.setFillAfter(true);
                    rotateAnimation.setInterpolator(new FastOutSlowInInterpolator());
                    fabButton.startAnimation(rotateAnimation);
                } else {
                    fabButton.clearAnimation();
                    ViewPropertyAnimatorCompat animatorCompat = ViewCompat.animate(fabButton);
                    animatorCompat.setDuration(500);
                    animatorCompat.setInterpolator(new FastOutSlowInInterpolator());
                    animatorCompat.rotation(180);
                    animatorCompat.start();
                }

                Intent i = new Intent(getActivity(),AddAnnouncementActivity.class);
                i.putExtra("usertype", user_usertype);
                i.putExtra("user_pid", user_pid);
                startActivityForResult(i,10001);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10001 && resultCode == AddAnnouncementActivity.RESULT_OK){
            Fragment currentFragment = getFragmentManager().findFragmentById(R.id.navigation_container);
            FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
            fragTransaction.detach(currentFragment);
            fragTransaction.attach(currentFragment);
            fragTransaction.commit();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final String aid = AppController.getInstance().getAid();
            if (item.getTitle() == "Edit Announcement") {
                Intent intent = new Intent(getActivity(), EditAnnouncementActivity.class);
                intent.putExtra("user_pid", user_pid);
                intent.putExtra("usertype", user_usertype);
                intent.putExtra("aid", aid);
                startActivity(intent);
            } else if (item.getTitle() == "Delete Announcement") {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Warning!")
                        .setMessage("Once deleted, action can't be undone. Proceed?")
                        .setIcon(R.drawable.ic_warning)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteAnnouncement(aid);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }

        return super.onContextItemSelected(item);
    }

    public void deleteAnnouncement(final String m_aid){
        progressDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        progressDialog.setMessage("Deleting...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + "delete_announcement.php",
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
                                Toast.makeText(getActivity(), "Successfully deleted announcement. Please refresh page to update info", Toast.LENGTH_SHORT).show();
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
                hidePDialog();
                Toast.makeText(getActivity(),"There's something wrong while processing your request. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("aid", m_aid);
                return params;
            }
        };queue.add(sr);

    }

}
