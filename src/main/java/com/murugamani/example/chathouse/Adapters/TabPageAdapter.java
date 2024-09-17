package com.murugamani.example.chathouse.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.murugamani.example.chathouse.Fragments.FriendRequestFragment;
import com.murugamani.example.chathouse.Fragments.FriendFragment;

/**
 * Created by Murugamani on 3/4/2018.
 */

public class TabPageAdapter extends FragmentStatePagerAdapter {
    public TabPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new FriendFragment();
            case 1:
                return new FriendRequestFragment();
        }
        return null;
    }

    @Override
    public int getItemPosition(Object object) {
        return object.hashCode();
    }

    @Override
    public int getCount() {
        return 2;
    }
}
