package app.psiteportal.com.psiteportal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.psiteportal.com.model.Nominee;
import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.CircleImageView;
import app.psiteportal.com.utils.Config;
import app.psiteportal.com.utils.NomineesAdapter;

/**
 * Created by fmpdroid on 2/23/2016.
 */
public class TestActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private LinearLayout mTitleContainer;
    private TextView mTitle;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;

    private CircleImageView imageView;
    private TextView txtName;
    private TextView txtInstitution;
    private TextView txtContact;
    private TextView txtEmail;
    private TextView txtAddress;
    private Bitmap bitmap;
    private String imageUrl, name, institution, contact, email, address, activity_name;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    private RecyclerView recyclerView;
    private NomineesAdapter adapter;
    private List<Nominee> nomineeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_election2);
//        recyclerView = (RecyclerView) findViewById(R.id.rec_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        getData();
//        nomineeList = new ArrayList<>();

        bindActivity();

        mToolbar.setTitle("");
        mAppBarLayout.addOnOffsetChangedListener(this);

        Intent intent = getIntent();
//        byte[] bytes = intent.getByteArrayExtra("prof_pic");
//        bitmap= BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageUrl = intent.getStringExtra("prof_pic");
        name = intent.getStringExtra("name");
        institution = intent.getStringExtra("institution");
        contact = intent.getStringExtra("contact");
        email = intent.getStringExtra("email");
        address = intent.getStringExtra("address");
        activity_name = AppController.getInstance().getActivity();
//
        txtName.setText(name);
        mTitle.setText(name);
        imageView.setImageUrl(imageUrl, imageLoader);
        txtInstitution.setText(institution);
        txtContact.setText(contact);
        txtEmail.setText(email);
        txtAddress.setText(address);

        if(activity_name.equals("election")) {
            setSupportActionBar(mToolbar);
        }
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);

    }

    private void bindActivity() {
        mToolbar        = (Toolbar) findViewById(R.id.main_toolbar);
        mTitle          = (TextView) findViewById(R.id.main_textview_title);
        mTitleContainer = (LinearLayout) findViewById(R.id.main_linearlayout_title);
        mAppBarLayout   = (AppBarLayout) findViewById(R.id.main_appbar);

        txtName = (TextView) findViewById(R.id.profile_name);
        imageView = (CircleImageView) findViewById(R.id.avatar_pic);
        txtInstitution = (TextView)findViewById(R.id.info_institution);
        txtContact = (TextView)findViewById(R.id.info_contact);
        txtEmail = (TextView)findViewById(R.id.info_email);
        txtAddress = (TextView)findViewById(R.id.info_location);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.who_nominated, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.who_nominated:
//                openContextMenu();
                Intent intent = new Intent(TestActivity.this, WhoNominatedMe.class);
                intent.putExtra("email", email);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    public void getData() {
        String url = Config.ROOT_URL + Config.WEB_SERVICES + "nomineesv2.php";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONArray jsonArray = new JSONArray(s.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String imageUrl, name, first_name, last_name, institution, contact, email, address;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Nominee nominee;
//                                imageUrl = jsonObject.getString("prof_pic");
                                first_name = jsonObject.getString("firstname");
                                last_name = jsonObject.getString("lastname");
//                                institution = jsonObject.getString("institution_name");
//                                contact = jsonObject.getString("contact");
//                                email = jsonObject.getString("email");
//                                address = jsonObject.getString("address");
                                name = first_name + " " + last_name;
                                nominee = new Nominee(name);
                                Log.wtf("glendon", name);
                                nomineeList.add(nominee);
                            }
                            adapter = new NomineesAdapter(getApplicationContext(), nomineeList);
                            recyclerView.setAdapter(adapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.wtf("hahayuuu", e.getMessage());
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
                params.put("email", email);
                return params;
            }
        };
        queue.add(sr);
    }
}
