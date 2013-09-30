package com.activity.mobilearn;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.mobilearn.R;
import com.lib.mobilearn.Utils;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class StatisticsFragment extends Fragment{
    
    public static final String ARG_PAGE = "page";

    private int qPageNumber;
    private View rootView;
    private ListView qList;
    private QuestionAdapter qAdapter;
    private MainProvider mp;

    public static StatisticsFragment create(int pageNumber) {
    	StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public StatisticsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        qPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
    	rootView = (View) inflater.inflate(R.layout.loading, container, false);
    	float chartData[];
    	int i, max = 0, total = 0;
    			
    	switch(qPageNumber)
    	{
    	case 0:
    		rootView = inflater.inflate(R.layout.statistics_1, container, false);
    		
    		total = 0;
    		Cursor result;
    		chartData = new float[5];
    		mp = new MainProvider(getActivity());
    		mp.open();
    		long oidLibrary = 16391;
    		for(i=0; i<4; i++){
    			result = mp.fetchQuestionCountInLibraryForState(oidLibrary, i);
        		if(result.move(0)){
        			chartData[i+1] = result.getInt(0);
        			total += result.getInt(0);
        		}
    		}
    		chartData[0] = total;
    		mp.close();
    		
    		LinearLayout layoutChartRadar = (LinearLayout) rootView.findViewById(R.id.layout_chart_radar);       		
       		View chartRaderView = new ChartRaderView(getActivity(), chartData, "statistics");
       		chartRaderView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
       		layoutChartRadar.addView(chartRaderView);
       		
       		loadQuestionListForState(inflater, container, 0);
       		LinearLayout stateMenu = (LinearLayout)rootView.findViewById(R.id.state_menu);

       		for(i=0; i<stateMenu.getChildCount(); i++) {
       			TextView item = (TextView)stateMenu.getChildAt(i);
       			item.setOnClickListener(stateMenuClickListener);
       		}
       		
    		break;
    	case 1:
    		rootView = inflater.inflate(R.layout.statistics_2, container, false);
    		
    		chartData = new float[8];
    		
    		long qTime, cTime = System.currentTimeMillis();

    		mp = new MainProvider(getActivity());
    		mp.open();
    		oidLibrary = 16391;
    		for(i=0; i<7; i++){
            	qTime = cTime + 1000*60*60*24*(i-6);
            	String regDate = android.text.format.DateFormat.format("yyyyMMdd000000", qTime).toString();
    			result = mp.fetchQuestionCountInLibraryForDaily(oidLibrary, regDate, 1);
        		if(result.move(0)){
        			chartData[i+1] = result.getInt(0);
        			if(max < result.getInt(0))
        				max = result.getInt(0);
        		}
    		}
    		
    		chartData[0] = 250;
    		for(i=0;i<7;i++){
    			chartData[i+1] = Math.round(chartData[i+1] / max * 200); 
    		}    		

    		mp.close();
    		
    		LinearLayout layoutChartLine = (LinearLayout) rootView.findViewById(R.id.layout_chart_line);
       		View chartLineView = new ChartLineView(getActivity(), chartData, "statistics");
       		chartLineView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
       		layoutChartLine.addView(chartLineView);
       		
       		loadQuestionListForDaily(inflater, container, 0);
       		LinearLayout dailyMenu = (LinearLayout)rootView.findViewById(R.id.daily_menu);
       		for(i=0; i<dailyMenu.getChildCount(); i++) {
       			TextView item = (TextView)dailyMenu.getChildAt(i);
       			item.setOnClickListener(dailyMenuClickListener);
       		}
    	}
    	
        return rootView;
    }

    public int getPageNumber() {
        return qPageNumber;
    }

    private OnClickListener stateMenuClickListener = new OnClickListener() {
        public void onClick(View v) {
        	ViewGroup vg = (ViewGroup) rootView.findViewById(R.id.state_menu);
        	for(int i=0; i<vg.getChildCount(); i++) {
        		TextView tv = (TextView)vg.getChildAt(i);
        		tv.setBackgroundResource(R.drawable.statis_tab);
    			tv.setTextColor(Color.rgb(0, 0, 0));
    		}
			
        	TextView tv = (TextView)v;
    		tv.setBackgroundResource(R.drawable.statis_tab_selected);
			tv.setTextColor(Color.rgb(127, 50, 59));
			
        	switch(v.getId()){
        	case R.id.state_1:
        		qAdapter = getQuestionAdapterForState(0);
        		break;
        	case R.id.state_2:
        		qAdapter = getQuestionAdapterForState(1);
        		break;
        	case R.id.state_3:
        		qAdapter = getQuestionAdapterForState(2);
        		break;
        	case R.id.state_4:
        		qAdapter = getQuestionAdapterForState(3);
        		break;
        	}
        	qList.setAdapter(qAdapter);
        	qList.setOnItemClickListener(detailQuestionListener);
        }
    };
    
    
    public void loadQuestionListForState(LayoutInflater inflater, ViewGroup container, int stateFlag){

    	qList = (ListView)rootView.findViewById(R.id.list);
   	
    	qAdapter = getQuestionAdapterForState(stateFlag);

    	qList.setAdapter(qAdapter);
    	qList.setOnItemClickListener(detailQuestionListener);
	}
    public QuestionAdapter getQuestionAdapterForState(int stateFlag){
		String state;
    	Cursor result;
    	ArrayList<HashMap<String, String>> questionList = new ArrayList<HashMap<String, String>>();
    	long oidLibrary = 16391;
    	
    	mp = new MainProvider(getActivity());
    	mp.open();
    	result = mp.fetchQuestionInLibraryForState(oidLibrary, stateFlag);
    	if(result.getCount()>0) {
	    	do {
	    		HashMap<String, String> value = new HashMap<String, String>();
	    		value.put(MainProvider.KEY_OID, result.getString(0));
	        	value.put(MainProvider.KEY_QUESTION, result.getString(1));
	        	value.put(MainProvider.KEY_SCORE, "+" + result.getString(5));
	        	
	        	int correct = result.getInt(3);
	        	int cnt = result.getInt(2);
	        	String persent;
	        	if(cnt==0)
	        		persent = "0%";
	        	else
	        		persent = Math.round(correct/cnt*100) + "%";
	        	
	        	value.put(MainProvider.KEY_PERSENT, persent);
	        	
	        	switch(result.getInt(4))
	        	{
	        	case 0:
	        		state = "기초학습";
	        		break;
	        	default:
	        		state = "기초학습";
	        	}
	        	value.put(MainProvider.KEY_STATE, state);
	        	
	        	questionList.add(value);
			} while(result.moveToNext());
    	}
    	mp.close();
    	return new QuestionAdapter(getActivity(), questionList, "state");
    }
    
    private OnClickListener dailyMenuClickListener = new OnClickListener() {
        public void onClick(View v) {
        	ViewGroup vg = (ViewGroup) rootView.findViewById(R.id.daily_menu);
        	for(int i=0; i<vg.getChildCount(); i++) {
        		TextView tv = (TextView)vg.getChildAt(i);
        		tv.setBackgroundResource(R.drawable.statis_tab);
    			tv.setTextColor(Color.rgb(0, 0, 0));
    		}
			
        	TextView tv = (TextView)v;
    		tv.setBackgroundResource(R.drawable.statis_tab_selected);
			tv.setTextColor(Color.rgb(127, 50, 59));
        	switch(v.getId()){
        	case R.id.daily_1:
        		qAdapter = getQuestionAdapterForDaily(0);
        		break;
        	case R.id.daily_2:
        		qAdapter = getQuestionAdapterForDaily(1);
        		break;
        	case R.id.daily_3:
        		qAdapter = getQuestionAdapterForDaily(2);
        		break;
        	case R.id.daily_4:
        		qAdapter = getQuestionAdapterForDaily(3);
        		break;
        	case R.id.daily_5:
        		qAdapter = getQuestionAdapterForDaily(4);
        		break;
        	case R.id.daily_6:
        		qAdapter = getQuestionAdapterForDaily(5);
        		break;
        	case R.id.daily_7:
        		qAdapter = getQuestionAdapterForDaily(6);
        		break;
        	}
        	qList.setAdapter(qAdapter);
        	qList.setOnItemClickListener(detailQuestionListener);
        }
    };
    public void loadQuestionListForDaily(LayoutInflater inflater, ViewGroup container, int dailyFlag){

    	qList = (ListView)rootView.findViewById(R.id.list);
   	
    	qAdapter = getQuestionAdapterForDaily(dailyFlag);

    	qList.setAdapter(qAdapter);
    	qList.setOnItemClickListener(detailQuestionListener);
	}    
    public QuestionAdapter getQuestionAdapterForDaily(int dailyFlag){
		String state;
    	Cursor result;
    	ArrayList<HashMap<String, String>> questionList = new ArrayList<HashMap<String, String>>();
    	long oid_library = 16391;
    	
    	long qTime, cTime = System.currentTimeMillis();
    	int d = 0, wFlag = 0;
    	
    	Utils utils = new Utils();
    	wFlag = utils.getWeekForNumber(android.text.format.DateFormat.format("EEE", cTime).toString());
    	
    	if(dailyFlag == wFlag)
    		d = 0;
    	else if(dailyFlag < wFlag)
    		d = dailyFlag - wFlag;
    	else if(dailyFlag > wFlag)
    		d = dailyFlag - wFlag - 7;
    	
    	qTime = cTime + 1000*60*60*24*d;
    	String regDate = android.text.format.DateFormat.format("yyyyMMdd000000", qTime).toString();
    	
    	mp = new MainProvider(getActivity());
    	mp.open();
    	result = mp.fetchQuestionInLibraryForDaily(oid_library, regDate, 1);
    	if(result.getCount() > 0) {
	    	do {
	    		HashMap<String, String> value = new HashMap<String, String>();
	    		value.put(MainProvider.KEY_OID, result.getString(0));
	        	value.put(MainProvider.KEY_QUESTION, result.getString(1));
	        	value.put(MainProvider.KEY_SCORE, "+" + result.getString(5));
	        	int correct = result.getInt(3);
	        	int cnt = result.getInt(2);
	        	String persent;
	        	if(cnt==0)
	        		persent = "0%";
	        	else
	        		persent = Math.round(correct/cnt*100) + "%";
	        	
	        	value.put(MainProvider.KEY_PERSENT, persent);
	        	
	        	switch(result.getInt(4))
	        	{
	        	case 0:
	        		state = "기초학습";
	        		break;
	        	default:
	        		state = "기초학습";
	        	}
	        	value.put(MainProvider.KEY_STATE, state);
	        	
	        	questionList.add(value);
			} while(result.moveToNext());
    	}
    	mp.close();
    	return new QuestionAdapter(getActivity(), questionList, "daily");
    }
    
    private OnItemClickListener detailQuestionListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			long oidLibrary = 16391;
			long oidPlayList = 0;
			Intent i = new Intent(getActivity(), QuestionActivity.class);
	        i.putExtra(MainProvider.KEY_OID_QUESTION, Long.parseLong(qAdapter.getItem(position).get(MainProvider.KEY_OID)));
	        i.putExtra(MainProvider.KEY_OID_LIBRARY, oidLibrary);
	        i.putExtra(MainProvider.KEY_OID_PLAYLIST, oidPlayList);
	        getActivity().startActivity(i);
			getActivity().overridePendingTransition(0, 0);
		}
    };
    
}
