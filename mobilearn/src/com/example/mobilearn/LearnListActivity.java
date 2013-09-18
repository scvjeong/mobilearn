package com.example.mobilearn;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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



public class LearnListActivity extends Activity{
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private TextView mtitle;
    private String[] mList;
    
    private static ComponentName lockScreenService = null;
    
    LockScreenService lService;
    boolean mBound = false;
    
    static final String KEY_QUESTION = "question_name";
    static final String KEY_PERSENT = "persent";
    static final String KEY_STATE = "state";
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d("LearnListActivity", "onCreate start");
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
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
    
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
   
    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putInt(ContentFragment.ARG_PLANET_NUMBER, position);
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
        mtitle = (TextView)findViewById(R.id.title);
        mtitle.setText(title);
    }

	public void setTitleBackground(int position) {
		LinearLayout titleBackground = (LinearLayout)findViewById(R.id.title_bg);
		switch(position)
		{
		case 0:
			titleBackground.setBackgroundColor(Color.rgb(76, 162, 141));
			break;
		case 1:
			titleBackground.setBackgroundColor(Color.rgb(233, 143, 14));
			break;
		case 5:
			titleBackground.setBackgroundColor(Color.rgb(67, 67, 67));
			break;
		}
    }
		
    public static class ContentFragment extends Fragment implements LoaderCallbacks<JSONObject>{
    	
    	private ArrayList<HashMap<String, String>> questionList = new ArrayList<HashMap<String, String>>();
    	private ListView qList;
        private QuestionAdapter qAdapter;
    	private MainProvider mp;
    	private Switch swc;
    	private View rootView;
    	
    	private final class RemoveWindow implements Runnable {
            public void run() {
                removeWindow();
            }
        }
    	
    	private RemoveWindow mRemoveWindow = new RemoveWindow();
    	Handler mHandler = new Handler();
    	private WindowManager mWindowManager;
        private TextView mDialogText;
        private boolean mShowing;
        private boolean mReady;
        private char mPrevLetter = Character.MIN_VALUE;
        private SearchView mSearchView;
    	
        public static final String ARG_PLANET_NUMBER = "planet_number";
		private static final OnQueryTextListener OnQueryTextListener = null;

        public ContentFragment() {
            // Empty constructor required for fragment subclasses
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

        	int i = getArguments().getInt(ARG_PLANET_NUMBER);
            Cursor result;
            Bundle args;
            String url;
            
            switch(i){
            case 0:
           		rootView = inflater.inflate(R.layout.fragment_menu, container, false);
           		TextView serviceState = (TextView)rootView.findViewById(R.id.service_state);
           		if(lockScreenService != null)
           			serviceState.setText("ON");
           		else
           			serviceState.setText("OFF");
           		break;
            case 1:
            	createMenuMY(inflater, container);
            	break;
            case 2:
            	rootView = inflater.inflate(R.layout.question_list, container, false);
            	qList = (ListView)rootView.findViewById(R.id.list);
            	questionList = new ArrayList<HashMap<String, String>>();
            	
            	mp = new MainProvider(getActivity());
            	mp.open();
            	result = mp.fetchAllLibrary();
            	while(result.moveToNext()){
            		HashMap<String, String> value = new HashMap<String, String>();            	
                	value.put(KEY_QUESTION, result.getString(1));
                	questionList.add(value);
        		}
            	mp.close();
            	
            	qAdapter = new QuestionAdapter(getActivity(), questionList);
            	qList.setAdapter(qAdapter);
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
			ContentsManager cm = new ContentsManager(getActivity());
			String action = "";
			
			try {
				action = json.getString("action");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(action ==  "question"){
				cm.setQuestions(json);
			} else if(action == "answer"){
				cm.setAnswers(json);
			} else if(action == "library"){
				cm.setLibrary(json);
			}
		}

		@Override
		public void onLoaderReset(Loader<JSONObject> arg0) {
			// TODO Auto-generated method stub
			
		}

		public void createMenuMY(LayoutInflater inflater, ViewGroup container){
	    	String state;
	    	Cursor result;
	    	
			rootView = inflater.inflate(R.layout.question_list, container, false);
        	qList = (ListView)rootView.findViewById(R.id.list);
        	mSearchView = (SearchView)rootView.findViewById(R.id.search_view);
        	questionList = new ArrayList<HashMap<String, String>>();
       	
        	mp = new MainProvider(getActivity());
        	mp.open();
        	result = mp.fetchAllQuestion();
        	while(result.moveToNext()){
        		HashMap<String, String> value = new HashMap<String, String>();            	
            	value.put(KEY_QUESTION, result.getString(1));
            	value.put(KEY_PERSENT, result.getString(3) + "/" + result.getString(2));
            	
            	switch(result.getInt(4))
            	{
            	case 0:
            		state = "기초학습";
            		break;
            	default:
            		state = "기초학습";
            	}
            	value.put(KEY_STATE, state);
            	
            	questionList.add(value);
    		}
        	mp.close();
        	
        	qAdapter = new QuestionAdapter(getActivity(), questionList);
        	qList.setAdapter(qAdapter);
        	qList.setTextFilterEnabled(true);
        	setupSearchView();
        	
        	mWindowManager = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
        	
        	qList.setOnScrollListener(osl);
            
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
		}
		
		TouchListenerClass touchListener = new TouchListenerClass();
		
		class TouchListenerClass implements View.OnTouchListener {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					((TextView)v).setBackgroundColor(Color.rgb(166, 156, 142));
					((TextView)v).setBackgroundColor(Color.rgb(255, 255, 255));
					return true;
				} 
				return false;
			} 
	    }
		
		public void createMenuSetting(LayoutInflater inflater, ViewGroup container){
			
			Cursor result;
			rootView = inflater.inflate(R.layout.setting, container, false);
			TextView sls = (TextView)rootView.findViewById(R.id.setting_lock_screen);
			TextView sv = (TextView)rootView.findViewById(R.id.setting_vibration);
			TextView t1 = (TextView)rootView.findViewById(R.id.time_1);
			TextView t2 = (TextView)rootView.findViewById(R.id.time_2);
			TextView t3 = (TextView)rootView.findViewById(R.id.time_3);
			TextView t4 = (TextView)rootView.findViewById(R.id.time_4);
			TextView t5 = (TextView)rootView.findViewById(R.id.time_5);
			TextView t6 = (TextView)rootView.findViewById(R.id.time_6);
			TextView t7 = (TextView)rootView.findViewById(R.id.time_7);
			TextView t8 = (TextView)rootView.findViewById(R.id.time_8);
			TextView t9 = (TextView)rootView.findViewById(R.id.time_9);
			TextView t10 = (TextView)rootView.findViewById(R.id.time_10);
			TextView t11 = (TextView)rootView.findViewById(R.id.time_11);
			TextView t12 = (TextView)rootView.findViewById(R.id.time_12);
			
			t1.setOnTouchListener(touchListener);
			t2.setOnTouchListener(touchListener);
			t3.setOnTouchListener(touchListener);
			t4.setOnTouchListener(touchListener);
			t5.setOnTouchListener(touchListener);
			t6.setOnTouchListener(touchListener);
			t7.setOnTouchListener(touchListener);
			t8.setOnTouchListener(touchListener);
			t9.setOnTouchListener(touchListener);
			t10.setOnTouchListener(touchListener);
			t11.setOnTouchListener(touchListener);
			t12.setOnTouchListener(touchListener);
			
			mp = new MainProvider(getActivity());
        	mp.open();
        	
        	result = mp.fetchSetting("lock_screen");
        	if(result.getCount() < 1) {
        		mp.insertSetting("lock_screen", "ON");
        		lockScreenService = getActivity().startService(new Intent(getActivity(), LockScreenService.class));
        		sls.setText("ON");
        	}
        	if(lockScreenService != null) {
        		sls.setText("ON");
        		mp.updateSettingItem("lock_screen", "ON");
        	} else {
       			sls.setText("OFF");
       			mp.updateSettingItem("lock_screen", "OFF");
        	}
       		
        	result = mp.fetchSetting("vibration");
        	if(result.getCount() < 1) {
        		mp.insertSetting("vibration", "ON");
        		sv.setText("ON");
        	}
        	else if(result.move(0)) {
        		String value = result.getString(0);
        		if(value.equals("ON")){
        			mp.updateSettingItem("vibration", "ON");
            		sv.setText("ON");
        		}
        		else{
        			mp.updateSettingItem("vibration", "OFF");
    				sv.setText("OFF");
        		}
        	}
        	mp.close();
        	
        	sls.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mp.open();
					String s = ((TextView)v).getText().toString();
					if(s == "OFF"){
        				lockScreenService = getActivity().startService(new Intent(getActivity(), LockScreenService.class));
        				mp.updateSettingItem("lock_screen", "ON");
        				((TextView)v).setText("ON");
        				//getActivity().startService(new Intent(getActivity(), LockScreenService.class));
        			} else{
        				//getActivity().stopService(new Intent(getActivity(), LockScreenService.class));
        				Intent i = new Intent();
        				i.setComponent(lockScreenService);
        				getActivity().stopService(i);
        				lockScreenService = null;
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
					if(s == "OFF"){
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
		
		private OnScrollListener osl = new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
		            int visibleItemCount, int totalItemCount) {
		        if (mReady) {
		            char firstLetter = questionList.get(firstVisibleItem).get(KEY_QUESTION).charAt(0);
		            
		            if (!mShowing && firstLetter != mPrevLetter) {

		                mShowing = true;
		                mDialogText.setVisibility(View.VISIBLE);
		            }
		            mDialogText.setText(((Character)firstLetter).toString());
		            mHandler.removeCallbacks(mRemoveWindow);
		            mHandler.postDelayed(mRemoveWindow, 500);
		            mPrevLetter = firstLetter;
		        }
		    }
		};

		private void removeWindow() {
	        if (mShowing) {
	            mShowing = false;
	            mDialogText.setVisibility(View.INVISIBLE);
	        }
	    }
    
		private void setupSearchView() {
	        mSearchView.setIconifiedByDefault(false);
	        mSearchView.setOnQueryTextListener(qtl);
	        mSearchView.setSubmitButtonEnabled(false);
	    }
		
		private OnQueryTextListener qtl = new OnQueryTextListener() {
			
			@Override
		    public boolean onQueryTextChange(String newText) {
				
		        if (TextUtils.isEmpty(newText)) {
		        	qList.clearTextFilter();
		        } else {
		        	qList.setFilterText(newText.toString());
		        }
		        return true;
		    }
			
			@Override
		    public boolean onQueryTextSubmit(String query) {
		        return false;
		    }
		};
    }

    public static class GetServerData extends AsyncTaskLoader<JSONObject>{
    	
        private String url;
        private String action;
        
        public GetServerData(Context context, String url, String action){
        	super(context);
        	this.url = url;        	
        	this.action = action;
        }
        
        @Override
    	public JSONObject loadInBackground(){
        	JSONParser jParser = new JSONParser();
    		return jParser.getJSONFromUrl(this.url, this.action);
    	}
    	
    	protected void onStartLoading(){
        	forceLoad();
    	}
    }
}
