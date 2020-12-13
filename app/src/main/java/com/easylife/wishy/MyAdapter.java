package com.easylife.wishy;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class MyAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;
    String data;

    public MyAdapter(Context context, FragmentManager fm, int totalTabs,String data) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
        this.data=data;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Tomorrow tomorrow= new Tomorrow(data);
                return tomorrow;

            case 1:
                Today today= new Today(data);
                return today;

            case 2:
                Upcoming upcoming= new Upcoming(data);
                return upcoming;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
