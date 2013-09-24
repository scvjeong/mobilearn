package com.activity.mobilearn;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

public class MyPagerAdapter extends FragmentStatePagerAdapter {
    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return MyFragment.create(position);
    }
    
    @Override
    public int getCount() {
        //return MainFragment.NUM_PAGES;
    	return MainFragment.QUESTION_NUM_PAGES;
    }
    
    @Override
    public void notifyDataSetChanged() {
    	// TODO Auto-generated method stub
    	super.notifyDataSetChanged();
    }
}
