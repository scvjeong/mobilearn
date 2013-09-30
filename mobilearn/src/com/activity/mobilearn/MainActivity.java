package com.activity.mobilearn;

import com.example.mobilearn.R;
import com.service.mobilearn.LockScreenService;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends Activity{
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mTitle;
    private String[] mList;
    private int pageNum;
    private Fragment fragment;
    
    public static ComponentName lockScreenService = null;
    
    LockScreenService lService;
    boolean mBound = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.learn_list);
        
        // title
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);        
        
        // menu
        mList = getResources().getStringArray(R.array.menu_list);
        MenuAdapter mAdapter = new MenuAdapter(this, mList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        //mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.menu_list_item, mList));
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        int loginFlag = 0;
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null) 
			loginFlag = (Integer) bundle.get(MainProvider.KEY_LOGIN_FLAG);
		if(loginFlag==1)
			selectItem(4);
		else
			selectItem(0);
        
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    
    @Override
    protected void onStart(){
    	super.onStart();
    }
    
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
   
    private void selectItem(int position) {
        // update the main content by replacing fragments
    	pageNum = position;
    	
        fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(MainFragment.MENU_NUMBER, position);
        fragment.setArguments(args);
        
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mList[position]);
        setTitleBackground(position);
        mDrawerLayout.closeDrawer(mDrawerList);
        
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
       
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void setTitle(CharSequence title) {
    	/*
        mtitle = (TextView)findViewById(R.id.title);
        mtitle.setText(title);
        */
    	mTitle = title;
        getActionBar().setTitle(mTitle);
    }

	public void setTitleBackground(int position) {
		switch(position)
		{
		case 0:
			getActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(114, 162, 150)));
			break;
		case 1:
			getActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(233, 163, 62)));
			break;
		case 2:
			getActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(58, 36, 4)));
			break;
		case 3:
			getActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(127, 50, 59)));
			break;
		case 4:
			getActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(172, 172, 172)));
			break;
		case 5:
			getActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(67, 67, 67)));
			break;
		}
    }
}
