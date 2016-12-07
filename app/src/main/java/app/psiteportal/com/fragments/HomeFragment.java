package app.psiteportal.com.fragments;

/**
 * Created by Ravi on 29/07/15.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.psiteportal.com.model.Announcement;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.AppController;
import app.psiteportal.com.utils.Config;


public class HomeFragment extends Fragment {

    private static String announcementUrl = "get_latest_announcement.php";
    private RelativeLayout relativeLayout, relativeLayout1;
    private TextView a_title, a_details, a_venue, a_date, a_time, a_poster;

    public HomeFragment() {
        // Required empty public constructor
    }
    CarouselView carouselView;
    int[] sampleImages = {R.drawable.carousel_education, R.drawable.carousel_extension,
            R.drawable.carousel_partnership, R.drawable.carousel_research};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        carouselView = (CarouselView) rootView.findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(sampleImages[position]);
            }
        });
        //TextViews
        a_title = (TextView) rootView.findViewById(R.id.home_announcement_title);
        a_details = (TextView) rootView.findViewById(R.id.home_announcement_details);
        a_venue = (TextView) rootView.findViewById(R.id.home_announcement_venue);
        a_date = (TextView) rootView.findViewById(R.id.home_announcement_date);
        a_time = (TextView) rootView.findViewById(R.id.home_announcement_time);
        a_poster = (TextView) rootView.findViewById(R.id.home_announcement_op);

        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.homeLoadingPanel);
        relativeLayout1 = (RelativeLayout) rootView.findViewById(R.id.homeLoadingPanel1);
        relativeLayout1.setVisibility(View.GONE);

        //call method to get latest announcement
        getAnnouncement();

        // Inflate the layout for this fragment
        return rootView;
    }

    private void getAnnouncement(){
        JsonArrayRequest request = new JsonArrayRequest(Config.ROOT_URL + Config.WEB_SERVICES + announcementUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        relativeLayout.setVisibility(View.GONE);
                        for(int i = 0; i < jsonArray.length(); i++){
                            try {
                                SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
                                SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                String aid, pid, title, details, venue, name, date, time, formattedTime, formattedDate;
                                String success;
                                Date formatDate;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                success = jsonObject.getString("success");
                                if(success.equals("0")){
                                    relativeLayout1.setVisibility(View.VISIBLE);
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
                                    a_title.setText(title);
                                    a_details.setText(Html.fromHtml("<font color='#009688'>Details: </font>" + details));
                                    if(!venue.isEmpty()) {
                                        a_venue.setText(Html.fromHtml("<font color='#009688'>Venue: </font>" + venue));
                                    }else{
                                        a_venue.setVisibility(View.GONE);
                                    }
                                    a_date.setText(Html.fromHtml("<font color='#009688'>Date & Time Posted: </font>" +
                                            formattedDate + " " + formattedTime));
//                                    a_time.setText(Html.fromHtml("<font color='#009688'>Time: </font>" + formattedTime));
                                    a_time.setVisibility(View.GONE);
                                    a_poster.setText(Html.fromHtml("<font color='#009688'>Posted by: </font>" + name));
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                                Log.w("wewewe", e.getMessage());
                            }

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                volleyError.getMessage();
            }
        });
        AppController.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
