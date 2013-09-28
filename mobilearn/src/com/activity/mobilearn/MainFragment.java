package com.activity.mobilearn;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilearn.R;
import com.service.mobilearn.LockScreenService;

public class MainFragment extends Fragment implements LoaderCallbacks<JSONObject>{
	
	private ArrayList<HashMap<String, String>> libraryList = new ArrayList<HashMap<String, String>>();
	private ListView lList;
    private LibraryAdapter lAdapter;
	private MainProvider mp;
	private View rootView;
	private ViewPager myPager, mPager, sPager;
	private PagerAdapter myPagerAdapter, mPagerAdapter, sPagerAdapter;
	
	public static final String MENU_NUMBER = "menu_number";
	public static final int MY_NUM_PAGES = 2;
	public static final int STATISTICS_NUM_PAGES = 2;
	public static final int NUM_PAGES = 2;
	
    static final String KEY_MARKET_NAME = "market_name";
    static final String KEY_LIBRARY_NAME = "library_name";
    static final String KEY_THUMB_URL = "thumb_url";
    static final String KEY_QUESTION = "question_name";
    static final String KEY_PERSENT = "persent";
    static final String KEY_STATE = "state";	
	
    public MainFragment() {
        // Empty constructor required for fragment subclasses
    }
    
    @Override
	public void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    	int i = getArguments().getInt(MENU_NUMBER);
    	if(i == 1 || i == 4) 
    		setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	int i = getArguments().getInt(MENU_NUMBER);
    	switch(i){
        case 0:
        	mp.open();
        	createMenuMainSetScore(rootView, mp);
        	createMenuMainSetSkip(rootView, mp);
        	mp.close();
        	break;
    	}
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

    	int i = getArguments().getInt(MENU_NUMBER);
        Bundle args;
        String url;
        
        switch(i){
        case 0:
        	createMenuMain(inflater, container);
       		break;
        case 1:
        	createMenuMY(inflater, container);
        	break;
        case 2:
        	createMenuLibrary(inflater, container);
        	break;
        case 3:
        	createMenuStatistics(inflater, container);
        	break;
        case 4:
        	createMenuMarket(inflater, container);
        	break;
        case 5:
        	createMenuSetting(inflater, container);
        	break;
        case 6:
        	args = new Bundle();
        	//String url = "http://img.kr:3000/res/library/update/16391";
        	url = "http://img.kr:3000/res/library/question/16391";
   	        args.putString("url", url);
   	        args.putString("action", "question");
   	        getLoaderManager().initLoader(0, args, this);
        	break;
        case 7:
        	args = new Bundle();
        	//String url = "http://img.kr:3000/res/library/update/16391";
        	url = "http://img.kr:3000/res/library/answer/16391";
   	        args.putString("url", url);
   	        args.putString("action", "answer");
   	        getLoaderManager().initLoader(0, args, this);
        	break;
        case 8:
        	args = new Bundle();
   	        url = "http://img.kr:3000/res/library";
   	        args.putString("url", url);
	        args.putString("action", "library");
	        getLoaderManager().initLoader(0, args, this);
        	break;
        case 9:
        	mp = new MainProvider(getActivity());
        	mp.init();
        	break;
       	default:
       		rootView = inflater.inflate(R.layout.fragment_menu, container, false);
       		
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
    	int i = getArguments().getInt(MENU_NUMBER);
    	switch(i){
    	case 1:
    		inflater.inflate(R.menu.question, menu);
    		break;
    	case 4:
    		inflater.inflate(R.menu.market, menu);
    		break;
    	}    	
    	super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	int i = getArguments().getInt(MENU_NUMBER);
    	switch(i){
    	case 1:
            switch (item.getItemId()) {
	        case R.id.action_list:
	        	myPager.setCurrentItem(0);
                return true;
            case R.id.action_playlist:
            	myPager.setCurrentItem(1);
                return true;
	        }    		break;
    	case 4:
            switch (item.getItemId()) {
	        case R.id.action_home:
                mPager.setCurrentItem(0);
                return true;
            case R.id.action_list:
            	mPager.setCurrentItem(1);
                return true;
	        }
    		break;
    	}    	
        return super.onOptionsItemSelected(item);
    }
    
	@Override
	public Loader<JSONObject> onCreateLoader(int ID, Bundle args) {
		// TODO Auto-generated method stub
		if(args != null){
			String url = args.getString("url");
			String action = args.getString("action");
			Log.d("LearnListActivity", "onCreateLoader");
			return new GetServerData(getActivity(), url, action);
		}
		else
			return null;
	}
	
	
	@Override
	public void onLoadFinished(Loader<JSONObject> loader, JSONObject json) {
		// TODO Auto-generated method stub
		if( json != null ) {
			ContentsManager cm = new ContentsManager(getActivity());
			String action = "";
			
			try {
				action = json.getString("action");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(action.equals("question")){
				cm.setQuestions(json);
				//cm.setUpdateQuestions(json);
			} else if(action.equals("answer")){
				cm.setAnswers(json);
			} else if(action.equals("library")){
				cm.setLibrary(json);
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<JSONObject> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/** createMenuMain start **/
	
	public void createMenuMain(LayoutInflater inflater, ViewGroup container){
		rootView = inflater.inflate(R.layout.fragment_menu, container, false);
   		
   		Cursor result;
   		mp = new MainProvider(getActivity());
   		mp.open();
   		
   		// lock screen setting
   		TextView serviceState = (TextView)rootView.findViewById(R.id.service_state);
   		result = mp.fetchSetting("lock_screen");
    	if(result.getCount() < 1) {
    		mp.insertSetting("lock_screen", "ON");
    		MainActivity.lockScreenService = getActivity().startService(new Intent(getActivity(), LockScreenService.class));
    		serviceState.setText("ON");
    	} else if(result.move(0)) {
    		if(result.getString(0).equals("ON") && MainActivity.lockScreenService == null) {
    	    	serviceState.setText("ON");
    	    	MainActivity.lockScreenService = getActivity().startService(new Intent(getActivity(), LockScreenService.class));
    		} else if(result.getString(0).equals("ON") && MainActivity.lockScreenService != null) {
    			serviceState.setText("ON");
    		} else if(result.getString(0).equals("OFF") && MainActivity.lockScreenService == null) {
    			serviceState.setText("OFF");
    		} else if(result.getString(0).equals("OFF") && MainActivity.lockScreenService != null) {
    			serviceState.setText("OFF");
    			Intent intent = new Intent();
    			intent.setComponent(MainActivity.lockScreenService);
				getActivity().stopService(intent);
				MainActivity.lockScreenService = null;
    		}
    	} else {
    		serviceState.setText("OFF");
    	}
    	
    	createMenuMainSetScore(rootView, mp);
   		
   		LinearLayout layoutChartRadar = (LinearLayout) rootView.findViewById(R.id.layout_chart_radar);
   		float chartData[] = {30, 26, 15, 28, 24};
   		View chartRaderView = new ChartRaderView(getActivity(), chartData, "main");
   		chartRaderView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
   		layoutChartRadar.addView(chartRaderView);
   		
   		LinearLayout layoutChartLine = (LinearLayout) rootView.findViewById(R.id.layout_chart_line);
		float chartData2[] = {200, 100, 50, 120, 100, 180, 160, 105};
   		View chartLineView = new ChartLineView(getActivity(), chartData2, "main");
   		chartLineView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
   		layoutChartLine.addView(chartLineView);
   		
   		mp.close();
	}
	
	public void createMenuMainSetScore(View v, MainProvider mp){
		// score
   		TextView scoreView = (TextView)rootView.findViewById(R.id.score_main);
   		long old_library = 16391;
   		int score = 0;
   		
   		Cursor result = mp.fetchScoreForMain(old_library);
   		if(result.move(0))
  			score = result.getInt(0);
   		scoreView.setText(String.valueOf(score));
	}

	// skip
	public void createMenuMainSetSkip(View v, MainProvider mp){

   		TextView skipView = (TextView)rootView.findViewById(R.id.skip_main);
   		String skip = "0";
   		
   		Cursor result = mp.fetchSetting("skip_current");
   		if(result.move(0))
  			skip = result.getString(0);
   		skipView.setText(skip);
	}
	
	/** createMenuMain end **/
	
	public void createMenuMY(LayoutInflater inflater, ViewGroup container){
		rootView = inflater.inflate(R.layout.view_pager, container, false);
        // Instantiate a ViewPager and a PagerAdapter.
		myPager = (ViewPager) rootView.findViewById(R.id.pager);
		myPagerAdapter = new MyPagerAdapter(getFragmentManager());
        myPager.setAdapter(myPagerAdapter);
	}
	
	public void createMenuStatistics(LayoutInflater inflater, ViewGroup container){
		rootView = inflater.inflate(R.layout.view_pager, container, false);
        // Instantiate a ViewPager and a PagerAdapter.
        sPager = (ViewPager) rootView.findViewById(R.id.pager);
        sPagerAdapter = new StatisticsPagerAdapter(getFragmentManager());
        sPager.setAdapter(sPagerAdapter);
	}
	
	public void createMenuLibrary(LayoutInflater inflater, ViewGroup container){
    	Cursor result;
    	
		rootView = inflater.inflate(R.layout.library, container, false);
    	lList = (ListView)rootView.findViewById(R.id.list);
    	//mSearchView = (SearchView)rootView.findViewById(R.id.search_view);
    	libraryList = new ArrayList<HashMap<String, String>>();
   	
    	mp = new MainProvider(getActivity());
    	mp.open();
    	result = mp.fetchAllLibrary();
    	while(result.moveToNext()){
    		HashMap<String, String> value = new HashMap<String, String>();
        	value.put(KEY_LIBRARY_NAME, result.getString(1));
        	value.put(KEY_THUMB_URL, "http://lyd.kr:3000" + result.getString(3));
        	libraryList.add(value);
		}
    	mp.close();
    	
    	lAdapter = new LibraryAdapter(getActivity(), libraryList);
    	lList.setAdapter(lAdapter);
    	lList.setTextFilterEnabled(true);
    	/*
    	setupSearchView();
    	
    	mWindowManager = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
    	
    	lList.setOnScrollListener(osl);
        
        LayoutInflater inflate = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        mDialogText = (TextView) inflate.inflate(R.layout.list_position, null);
        mDialogText.setVisibility(View.INVISIBLE);
        
        mHandler.post(new Runnable() {

            public void run() {
                mReady = true;
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                mWindowManager.addView(mDialogText, lp);
            }
        });
        */
	}
	
	public void createMenuMarket(LayoutInflater inflater, ViewGroup container){
    	if( !isOnline() ){
    		rootView = inflater.inflate(R.layout.no_connection, container, false);
    	} else {
			rootView = inflater.inflate(R.layout.view_pager, container, false);
			
            // Instantiate a ViewPager and a PagerAdapter.
            mPager = (ViewPager) rootView.findViewById(R.id.pager);
            mPagerAdapter = new MarketPagerAdapter(getFragmentManager());
            mPager.setAdapter(mPagerAdapter);
    	}
	}
	
	public boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	TouchListenerClass touchListener = new TouchListenerClass();
	
	class TouchListenerClass implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub

			if(event.getAction() == MotionEvent.ACTION_DOWN ) {
				v.setBackgroundColor(Color.rgb(166, 156, 142));
				TextView tv = (TextView)v;
				String i = tv.getText().toString();
				MainProvider mp = new MainProvider(getActivity());
				mp.open();
				Cursor result = mp.fetchSetting("time_" + i);
		    	if(result.move(0)) {
		    		String value = result.getString(0);
		    		if(value.equals("OFF")){
		    			mp.updateSettingItem("time_" + i, "ON");
		    			v.setBackgroundColor(Color.rgb(166, 156, 142));
		    		}
		    		else{
		    			mp.updateSettingItem("time_" + i, "OFF");
		    			v.setBackgroundColor(Color.rgb(255, 255, 255));
		    		}
		    	}
				mp.close();

				return true;
			} 
			
			return false;
		}
    }
	
	public void createMenuSetting(LayoutInflater inflater, ViewGroup container){
		
		Cursor result;
		rootView = inflater.inflate(R.layout.setting, container, false);

		mp = new MainProvider(getActivity());
    	mp.open();
    	
    	// time table
    	TableLayout timeTable = (TableLayout)rootView.findViewById(R.id.time_table);
		int i, j, idx=1;
		TableRow tr;
		TextView td;
		for(i=0; i<timeTable.getChildCount(); i++) {
			tr = (TableRow)timeTable.getChildAt(i);
			for(j=0; j<tr.getChildCount(); j++) {
				td = (TextView)tr.getChildAt(j);
				if((i+1)!=timeTable.getChildCount()||(j+1)!=tr.getChildCount()) {
					td.setOnTouchListener(touchListener);

					result = mp.fetchSetting("time_" + idx);
			    	if(result.getCount() < 1) {
			    		mp.insertSetting("time_" + idx, "ON");
			    		td.setBackgroundColor(Color.rgb(166, 156, 142));
			    	} else if(result.move(0)) {
			    		String value = result.getString(0);
			    		if(value.equals("ON")){
			    			mp.updateSettingItem("time_" + idx, "ON");
			    			td.setBackgroundColor(Color.rgb(166, 156, 142));
			    		}
			    		else{
			    			mp.updateSettingItem("time_" + idx, "OFF");
			    			td.setBackgroundColor(Color.rgb(255, 255, 255));
			    		}
			    	}
			    	idx++;
				}
			}
		}
		
		// lock screen 
		TextView sls = (TextView)rootView.findViewById(R.id.setting_lock_screen);
    	result = mp.fetchSetting("lock_screen");
    	if(result.getCount() < 1) {
    		mp.insertSetting("lock_screen", "ON");
    		MainActivity.lockScreenService = getActivity().startService(new Intent(getActivity(), LockScreenService.class));
    		//lockScreenService = getActivity().startService(new Intent(getActivity(), LoadingService.class));
    		sls.setText("ON");
    	}
    	
    	if(MainActivity.lockScreenService != null) {
    		sls.setText("ON");
    		mp.updateSettingItem("lock_screen", "ON");
    	} else {
   			sls.setText("OFF");
   			mp.updateSettingItem("lock_screen", "OFF");
    	}
   		
    	// vibration
    	TextView sv = (TextView)rootView.findViewById(R.id.setting_vibration);
    	result = mp.fetchSetting("vibration");
    	if(result.getCount() < 1) {
    		mp.insertSetting("vibration", "ON");
    		sv.setText("ON");
    	}
    	else if(result.move(0)) {
    		String value = result.getString(0);
    		if(value.equals("ON")){
    			//mp.updateSettingItem("vibration", "ON");
        		sv.setText("ON");
    		}
    		else{
    			//mp.updateSettingItem("vibration", "OFF");
				sv.setText("OFF");
    		}
    	}
    	
    	// skip
    	TextView ss = (TextView)rootView.findViewById(R.id.setting_skip);
    	result = mp.fetchSetting("skip");
    	if(result.getCount() < 1) {
    		mp.insertSetting("skip", "10");
    		ss.setText("10 ¡å");
    	}
    	else if(result.move(0)) {
    		String value = result.getString(0);
    		ss.setText(value+" ¡å");
    	}
    	
    	result = mp.fetchSetting("skip_current");
    	if(result.getCount() < 1) {
    		mp.insertSetting("skip_current", "10");
    	}
    	
    	mp.close();
    	
    	ss.setOnClickListener(new View.OnClickListener() {
    		private TextView clickView;
			@Override
			public void onClick(View v) {
				clickView = (TextView)v; 
				// TODO Auto-generated method stub
				/*
				ArrayList<String> menuItems = new ArrayList<String>();
				menuItems.add("0");
				menuItems.add("10");
				menuItems.add("20");
				menuItems.add("30");
				
				//LayoutInflater mLayoutInflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
				LayoutInflater mLayoutInflater = (LayoutInflater) getActivity().getLayoutInflater();
				View mView = mLayoutInflater.inflate(R.layout.skip_menu, null);
				LinearLayout skipListView = (LinearLayout) mView.findViewById(R.id.skip_menu_list);
				int i;
				for(i=0; i<menuItems.size(); i++) {
					TextView tv = new TextView(getActivity());
					tv.setText(menuItems.get(i));
					tv.setTextColor(Color.rgb(255, 255, 255));
					tv.setTextSize(20);
					tv.setBackgroundColor(Color.rgb(127, 50, 59));
					tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
					skipListView.addView(tv);
				}
				
				PopupWindow mPopupWindow = new PopupWindow(mView,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT, false);
				mPopupWindow.showAsDropDown(v, 0, (int)v.getY());
				*/
				
				PopupMenu popup = new PopupMenu(getActivity(), v);
		        popup.getMenuInflater().inflate(R.menu.skip_menu, popup.getMenu());
		        
		        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
		            public boolean onMenuItemClick(MenuItem item) {
		            	clickView.setText(item.getTitle() + " ¡å");
		            	mp.open();
		            	mp.updateSettingItem("skip", item.getTitle().toString());
		        		mp.updateSettingItem("skip_current", item.getTitle().toString());
		        		mp.close();
		                return true;
		            }
		        });

		        popup.show();
		        
			}
		});
    	
    	sls.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mp.open();
				String s = ((TextView)v).getText().toString();
				if(s.equals("OFF")){
					MainActivity.lockScreenService = getActivity().startService(new Intent(getActivity(), LockScreenService.class));
					//lockScreenService = getActivity().startService(new Intent(getActivity(), LoadingService.class));
    				mp.updateSettingItem("lock_screen", "ON");
    				((TextView)v).setText("ON");
    				//getActivity().startService(new Intent(getActivity(), LockScreenService.class));
    			} else{
    				//getActivity().stopService(new Intent(getActivity(), LockScreenService.class));
    				Intent i = new Intent();
    				i.setComponent(MainActivity.lockScreenService);
    				getActivity().stopService(i);
    				MainActivity.lockScreenService = null;
    				mp.updateSettingItem("lock_screen", "OFF");
    				((TextView)v).setText("OFF");
    			}
				mp.close();
			}
		});
    	
    	sv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mp.open();
				String s = ((TextView)v).getText().toString();
				if(s.equals("OFF")){
    				mp.updateSettingItem("vibration", "ON");
    				((TextView)v).setText("ON");
    			} else{
    				mp.updateSettingItem("vibration", "OFF");
    				((TextView)v).setText("OFF");
    			}
				mp.close();
			}
		});
	}
	
}
