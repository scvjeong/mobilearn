package com.activity.mobilearn;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

public class MarketPagerAdapter extends FragmentStatePagerAdapter {
    public MarketPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return MarketFragment.create(position);
    }
    
    @Override
    public int getCount() {
        //return MainFragment.NUM_PAGES;
    	return MainFragment.NUM_PAGES;
    }
}
