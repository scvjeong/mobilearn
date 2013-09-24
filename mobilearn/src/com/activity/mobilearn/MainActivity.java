package com.activity.mobilearn;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.mobilearn.R;
import com.lib.mobilearn.JSONParser;
import com.service.mobilearn.LoadingService;
import com.service.mobilearn.LockScreenService;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity{
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mList;
    
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
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.menu_list_item, mList));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        lockScreenService = startService(new Intent(this, LockScreenService.class));
        selectItem(0);
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
        Fragment fragment = new MainFragment();
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
