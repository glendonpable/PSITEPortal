package app.psiteportal.com.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import app.psiteportal.com.psiteportal.NoNomination;
import app.psiteportal.com.psiteportal.R;
import app.psiteportal.com.utils.TabPagerItem;
import app.psiteportal.com.utils.ViewPagerAdapter;

/**
 * Created by fmpdroid on 7/6/2016.
 */
public class ViewPagerFragment extends Fragment{

    private String positions, pid, usertype;

    private List<TabPagerItem> mTabs = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createTabPagerItem();
        Bundle bundle = this.getArguments();
        positions = bundle.getString("position", null);
        pid = bundle.getString("user_pid", null);
        usertype = bundle.getString("usertype", null);
    }

    private void createTabPagerItem(){

        mTabs.add(new TabPagerItem("Election Info", new AdminNominationElection()));
        mTabs.add(new TabPagerItem("Override", OverrideFragment.newInstance(pid, usertype, positions)));
        //mTabs.add(new TabPagerItem(getString(R.string.documents), MainFragment.newInstance(getString(R.string.documents))));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_pager, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT ));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewPager);

        mViewPager.setOffscreenPageLimit(mTabs.size());
        mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), mTabs));
        TabLayout mSlidingTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSlidingTabLayout.setElevation(15);
        }
        mSlidingTabLayout.setupWithViewPager(mViewPager);
    }
}
