package com.murugamani.example.chathouse.Fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.murugamani.example.chathouse.Adapters.TabPageAdapter;
import com.murugamani.example.chathouse.R;


public class FriendsFragment extends Fragment {

    private View rootView;

    private TabPageAdapter tabPageAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        viewPager = rootView.findViewById(R.id.container);
        tabLayout = rootView.findViewById(R.id.tabs);

        tabPageAdapter = new TabPageAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(tabPageAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        return rootView;
    }

}
