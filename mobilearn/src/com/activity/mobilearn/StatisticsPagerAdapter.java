package com.activity.mobilearn;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

public class StatisticsPagerAdapter extends FragmentStatePagerAdapter {
    public StatisticsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return StatisticsFragment.create(position);
    }
    
    @Override
    public int getCount() {
    	return MainFragment.STATISTICS_NUM_PAGES;
    }
    
    @Override
    public void notifyDataSetChanged() {
    	// TODO Auto-generated method stub
    	super.notifyDataSetChanged();
    }
}
