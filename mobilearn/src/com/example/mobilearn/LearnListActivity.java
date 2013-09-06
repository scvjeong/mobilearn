package com.example.mobilearn;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;



public class LearnListActivity extends Activity{
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private TextView mtitle;
    private String[] mList;
    
    static final String KEY_QUESTION = "mobile";
    
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
        Log.d("LearnListActivity", "setAdapter start");
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.menu_list_item, mList));
        Log.d("LearnListActivity", "setOnItemClickListener start");
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        Log.d("LearnListActivity", "onCreate end");
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
        mDrawerLayout.closeDrawer(mDrawerList);
        Log.d("LearnListActivity", "selectItem end");
    }
    
    @Override
    public void setTitle(CharSequence title) {
        mtitle = (TextView)findViewById(R.id.title);
        mtitle.setText(title);
    }

    public static class ContentFragment extends Fragment {
    	
    	private MainProvider mp;
    	private Switch swc;
    	private ListView list;
        public static final String ARG_PLANET_NUMBER = "planet_number";

        public ContentFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	Log.d("LearnListActivity","ContentFragment:onCreateView start" );
            View rootView;
            ArrayList<HashMap<String, String>> questionList = new ArrayList<HashMap<String, String>>();
            ListView qList;
            QuestionAdapter qAdapter;    
            
            int i = getArguments().getInt(ARG_PLANET_NUMBER);
            
            Log.d("LearnListActivity","i : " + i );
            switch(i){
            case 0:
            	rootView = inflater.inflate(R.layout.question_list, container, false);
            	qList = (ListView)rootView.findViewById(R.id.list);
            	mp = new MainProvider(getActivity());
            	
            	mp.open();
            	Cursor result = mp.fetchAllQuestion();
            	while(result.moveToNext()){
            		HashMap<String, String> value = new HashMap<String, String>();            	
                	value.put(KEY_QUESTION, result.getString(1));
                	questionList.add(value);
        		}
            	mp.close();
            	
            	qAdapter = new QuestionAdapter(getActivity(), questionList);
            	qList.setAdapter(qAdapter);
            	
            	break;
            case 3:
            	rootView = inflater.inflate(R.layout.setting, container, false);
            	swc = (Switch)rootView.findViewById(R.id.switch_lock_screen); 
            	swc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
            			if(isChecked){
            				getActivity().startService(new Intent(getActivity(), LockScreenService.class));
            			} else{
            				getActivity().stopService(new Intent(getActivity(), LockScreenService.class));
            			}
            		}            		
            	});
            	break;
           	default:
           		rootView = inflater.inflate(R.layout.fragment_menu, container, false);
            }
            return rootView;
        }
    }
}
