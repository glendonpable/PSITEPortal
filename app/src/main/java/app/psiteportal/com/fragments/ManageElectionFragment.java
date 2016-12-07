package app.psiteportal.com.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import app.psiteportal.com.psiteportal.CreateElection;
import app.psiteportal.com.psiteportal.EditElection;
import app.psiteportal.com.psiteportal.MyCertificatesActivity;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.Config;

/**
 * Created by fmpdroid on 9/18/2016.
 */
public class ManageElectionFragment extends Fragment implements View.OnClickListener{

    TextView convention, nomination_sched, election_sched, nomination_status, election_status;
    RelativeLayout relativeLayout;
    LinearLayout info_layout, nomination_buttons, election_buttons;
    CheckBox checkbox;
    Button btnEdit, btnCancel, btnStartNom, btnStopNom, btnStartElec, btnStopElec;
    ProgressDialog progressDialog;
    private String get_status = "get_status.php";
    private String update_status = "update_status.php";
    private String cancel_election = "cancel_election.php";
    private String release_result = "release_result.php";
    private String is_viewable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.manage_election, container, false);
        setHasOptionsMenu(true);
        checkbox = (CheckBox) rootView.findViewById(R.id.checkbox_result);
        convention = (TextView) rootView.findViewById(R.id.txtConvention);
        nomination_sched = (TextView) rootView.findViewById(R.id.txtNomDate);
        election_sched = (TextView) rootView.findViewById(R.id.txtElecDate);
        nomination_status = (TextView) rootView.findViewById(R.id.txtNomStatus);
        election_status = (TextView) rootView.findViewById(R.id.txtElecStatus);

        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.result_layout);
        info_layout = (LinearLayout) rootView.findViewById(R.id.election_info_buttons);
        nomination_buttons = (LinearLayout) rootView.findViewById(R.id.nomination_buttons);
        election_buttons = (LinearLayout) rootView.findViewById(R.id.election_buttons);

        btnEdit = (Button) rootView.findViewById(R.id.btnEdit);
        btnCancel = (Button) rootView.findViewById(R.id.btnCancel);
        btnStartNom = (Button) rootView.findViewById(R.id.btnStartNom);
        btnStopNom = (Button) rootView.findViewById(R.id.btnStopNom);
        btnStartElec = (Button) rootView.findViewById(R.id.btnStartElec);
        btnStopElec = (Button) rootView.findViewById(R.id.btnStopElec);

        checkbox.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnStartNom.setOnClickListener(this);
        btnStopNom.setOnClickListener(this);
        btnStartElec.setOnClickListener(this);
        btnStopElec.setOnClickListener(this);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        getElectionStatus();
        return rootView;
    }

    private void getElectionStatus() {

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + get_status,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        hidePDialog();
//                        relativeLayout.setVisibility(View.GONE);
                        try {
                            SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
                            SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
                            SimpleDateFormat dateParse = new SimpleDateFormat("yyyy/MM/dd");
                            SimpleDateFormat timeParse = new SimpleDateFormat("HH:mm");

                            JSONArray jsonArray = new JSONArray(s.toString());
                            Log.wtf("glendon", jsonArray.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String nom_status = null, status = null;
                                String convention_title;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                is_viewable = jsonObject.getString("is_viewable");
                                convention_title = jsonObject.getString("election_title");
                                //formatting date and time retrieved
                                String nomination = dateFormatter.format(dateParse.parse(jsonObject.getString("nom_date").replace("-", "/"))) +
                                        " | " + timeFormatter.format(timeParse.parse(jsonObject.getString("nom_start_time"))) +
                                        " - " + timeFormatter.format(timeParse.parse(jsonObject.getString("nom_end_time")));
                                String election = dateFormatter.format(dateParse.parse(jsonObject.getString("election_date").replace("-", "/"))) +
                                        " | " + timeFormatter.format(timeParse.parse(jsonObject.getString("start_time"))) +
                                        " - " + timeFormatter.format(timeParse.parse(jsonObject.getString("end_time")));

                                convention.setText(convention_title);
                                nomination_sched.setText(nomination);
                                election_sched.setText(election);


                                if (jsonObject.getString("isCancel").equals("1")) {
                                    nom_status = "Cancelled";
                                    status = "Cancelled";
                                    for (int ii = 0; ii < info_layout.getChildCount(); ii++) {
                                        View child = info_layout.getChildAt(ii);
                                        child.setEnabled(false);
                                    }
                                    for (int ii = 0; ii < nomination_buttons.getChildCount(); ii++) {
                                        View child = nomination_buttons.getChildAt(ii);
                                        child.setEnabled(false);
                                    }
                                    for (int ii = 0; ii < election_buttons.getChildCount(); ii++) {
                                        View child = election_buttons.getChildAt(ii);
                                        child.setEnabled(false);
                                    }
                                } else {
                                    if (jsonObject.getString("nom_status").equals("0")
                                            && jsonObject.getString("status").equals("0")) {
                                        nom_status = "Standby";
                                        status = "Standby";
                                        btnStartNom.setEnabled(true);
                                        btnStopNom.setEnabled(false);
                                        btnStartElec.setEnabled(false);
                                        btnStopElec.setEnabled(false);
                                    } else if (jsonObject.getString("nom_status").equals("1")
                                            && jsonObject.getString("status").equals("0")) {
                                        nom_status = "Ongoing";
                                        status = "Standby";
                                        btnStartNom.setEnabled(false);
                                        btnStopNom.setEnabled(true);
                                        btnStartElec.setEnabled(false);
                                        btnStopElec.setEnabled(false);
                                    } else if (jsonObject.getString("nom_status").equals("2")
                                            && jsonObject.getString("status").equals("0")) {
                                        nom_status = "Completed";
                                        status = "Standby";
                                        btnStartNom.setEnabled(false);
                                        btnStopNom.setEnabled(false);
                                        btnStartElec.setEnabled(true);
                                        btnStopElec.setEnabled(false);
                                    } else if (jsonObject.getString("nom_status").equals("2")
                                            && jsonObject.getString("status").equals("1")) {
                                        nom_status = "Completed";
                                        status = "Ongoing";
                                        btnStartNom.setEnabled(false);
                                        btnStopNom.setEnabled(false);
                                        btnStartElec.setEnabled(false);
                                        btnStopElec.setEnabled(true);
                                    } else if (jsonObject.getString("nom_status").equals("2")
                                            && jsonObject.getString("status").equals("2")) {
                                        nom_status = "Completed";
                                        status = "Completed";
                                        btnStartNom.setEnabled(false);
                                        btnStopNom.setEnabled(false);
                                        btnStartElec.setEnabled(false);
                                        btnStopElec.setEnabled(false);
                                        btnCancel.setEnabled(false);
                                        btnEdit.setEnabled(false);
                                    }
                                }
                                nomination_status.setText(nom_status);
                                election_status.setText(status);

                                if (!(nomination_status.getText().toString().equals("Completed") && election_status.getText().toString().equals("Completed"))) {

                                    for (int j = 0; j < relativeLayout.getChildCount(); j++) {
                                        View child = relativeLayout.getChildAt(j);
                                        child.setEnabled(false);
                                    }
                                }
                                if (is_viewable.equals("0")) {
                                    checkbox.setChecked(false);
                                } else {
                                    checkbox.setChecked(true);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.wtf("hahay", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        queue.add(sr);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnEdit:
                Toast.makeText(getActivity(), "Edit", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), EditElection.class);
                startActivityForResult(intent, 10002);
                break;
            case R.id.btnCancel:
                showDialog();
                break;
            case R.id.btnStartNom:
                showDialog(1,0);
                break;
            case R.id.btnStopNom:
                showDialog(2,0);
                break;
            case R.id.btnStartElec:
                showDialog(2,1);
                break;
            case R.id.btnStopElec:
                showDialog(2,2);
                break;
            case R.id.checkbox_result:
                showDialog(!checkbox.isChecked());
                break;
        }
    }

    private void showDialog(){
        new AlertDialog.Builder(getActivity())
                .setTitle("Warning!")
                .setMessage("Are you sure to cancel the current election?")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
//                                    addAnnouncement();
                        cancelElection();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void showDialog(final boolean is_checked){
        String message;
        final int viewable;
        if(is_checked){
            message = "Are you sure to hide the election results?";
            viewable = 0;
        }else{
            message = "Are you sure to release the election results?";
            viewable = 1;
        }
        new AlertDialog.Builder(getActivity())
                .setTitle("Warning!")
                .setMessage(message)
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        releaseResult(viewable);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkbox.setChecked(is_checked);
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        checkbox.setChecked(is_checked);
                    }
                })
                .show();

    }

    private void showDialog(final int nom_status, final int status){
        new AlertDialog.Builder(getActivity())
                .setTitle("Warning!")
                .setMessage("Are you sure of your action?")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        updateStatus(nom_status, status);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void cancelElection(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest sr = new StringRequest(Request.Method.GET, Config.ROOT_URL + Config.WEB_SERVICES + cancel_election,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        String message;
                        int success;
                        hidePDialog();
                        try {
                            JSONObject object = new JSONObject(s.toString());
                            Log.wtf("glendon", object.toString());
                            success = object.getInt("success");
                            message = object.getString("message");
                            Log.wtf("success", String.valueOf(success));
                            Log.wtf("message", object.getString("message"));
                            if(success==1){
                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                Fragment currentFragment = getFragmentManager().findFragmentById(R.id.navigation_container);
                                FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                                fragTransaction.detach(currentFragment);
                                fragTransaction.attach(currentFragment);
                                fragTransaction.commit();
                            }else{
                                Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.wtf("hahay", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        queue.add(sr);
    }
    //updates nomination or election status
    private void updateStatus(final int nom_status, final int status) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + update_status,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        String message;
                        int success;
                        hidePDialog();
                        try {
                            JSONObject object = new JSONObject(s.toString());
                            Log.wtf("glendon", object.toString());
                            success = object.getInt("success");
                            message = object.getString("message");
                            Log.wtf("success", String.valueOf(success));
                            Log.wtf("message", object.getString("message"));
                            if(success==1){
                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                Fragment currentFragment = getFragmentManager().findFragmentById(R.id.navigation_container);
                                FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                                fragTransaction.detach(currentFragment);
                                fragTransaction.attach(currentFragment);
                                fragTransaction.commit();
                            }else{
                                Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.wtf("hahay", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("nom_status", String.valueOf(nom_status));
                params.put("status", String.valueOf(status));
                return params;
            }
        };
        queue.add(sr);
    }

    private void releaseResult(final int viewable){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest sr = new StringRequest(Request.Method.POST, Config.ROOT_URL + Config.WEB_SERVICES + release_result,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        String message;
                        int success;
                        hidePDialog();
                        try {
                            JSONObject object = new JSONObject(s.toString());
                            Log.wtf("glendon", object.toString());
                            success = object.getInt("success");
                            message = object.getString("message");
                            Log.wtf("success", String.valueOf(success));
                            Log.wtf("message", object.getString("message"));
                            if(success==1){
                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                Fragment currentFragment = getFragmentManager().findFragmentById(R.id.navigation_container);
                                FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                                fragTransaction.detach(currentFragment);
                                fragTransaction.attach(currentFragment);
                                fragTransaction.commit();
                            }else{
                                Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.wtf("hahay", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("is_viewable", String.valueOf(viewable));
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.manage_election_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.create_election:
                intent = new Intent(getContext(), CreateElection.class);
                startActivityForResult(intent, 10001);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode == 10001) && (resultCode == CreateElection.RESULT_OK)){
            Fragment currentFragment = getFragmentManager().findFragmentById(R.id.navigation_container);
            FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
            fragTransaction.detach(currentFragment);
            fragTransaction.attach(currentFragment);
            fragTransaction.commit();
        }else if((requestCode == 10002) && (resultCode == EditElection.RESULT_OK)){
            Fragment currentFragment = getFragmentManager().findFragmentById(R.id.navigation_container);
            FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
            fragTransaction.detach(currentFragment);
            fragTransaction.attach(currentFragment);
            fragTransaction.commit();
        }
    }
}
