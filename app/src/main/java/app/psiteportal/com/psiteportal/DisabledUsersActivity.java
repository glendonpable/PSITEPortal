package app.psiteportal.com.psiteportal;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.model.Member;
import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.Config;
import app.psiteportal.com.utils.MembershipActivationAdapter;

/**
 * Created by fmpdroid on 3/26/2016.
 */
public class DisabledUsersActivity extends AppCompatActivity{

    private static String get_disabled_users = "get_disabled_users.php";
    private List<Member> memberList;
    private RecyclerView recyclerView;
    private MembershipActivationAdapter adapter;
    private String pid, user_usertype;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_activation);

        recyclerView = (RecyclerView) findViewById(R.id.members_recycler_view);
        memberList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        progressDialog = new ProgressDialog(DisabledUsersActivity.this);
        // Showing progress dialog before making http request
        progressDialog.setMessage("Fetching...");
        progressDialog.show();
        populate();
    }

    public void populate(){
        JsonArrayRequest request = new JsonArrayRequest(Config.ROOT_URL + Config.WEB_SERVICES + get_disabled_users,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        hidePDialog();
                        String  name, email, status;
                        Member member;
                        for(int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject object = jsonArray.getJSONObject(i);
                                name = object.getString("lastname") + ", " + object.getString("firstname");
                                email = object.getString("email");
                                if(Integer.parseInt(object.getString("activated"))==1){
                                    status = "Active";
                                }else{
                                    status = "Inactive";
                                }
                                member = new Member(name, email, status);
                                memberList.add(member);

                            } catch (Exception e) {

                            }
                            adapter = new MembershipActivationAdapter(getApplicationContext(), memberList);
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
}
