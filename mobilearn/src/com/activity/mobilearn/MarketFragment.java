package com.activity.mobilearn;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.mobilearn.R;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;

public class MarketFragment extends Fragment implements LoaderCallbacks<JSONObject>{
    
    public static final String ARG_PAGE = "page";

    private int mPageNumber;
    private ViewGroup rootView;

    public static MarketFragment create(int pageNumber) {
    	MarketFragment fragment = new MarketFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MarketFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
    	Bundle args;
    	String url;
    	rootView = (ViewGroup) inflater.inflate(R.layout.loading, container, false);    	
    	
    	switch(mPageNumber)
    	{
    	case 0:
    		rootView = (ViewGroup) inflater.inflate(R.layout.market_home, container, false);
    		break;
    	case 1:
    		args = new Bundle();
        	url = "http://img.kr:3000/res/market/contents/";
   	        args.putString("url", url);
   	        args.putString("action", "MARKET_CONTENTS_LIST");
   	        getLoaderManager().initLoader(0, args, this);
    		break;
    	}
    	
        return rootView;
    }

    public int getPageNumber() {
        return mPageNumber;
    }

	@Override
	public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		if(args != null){
			String url = args.getString("url");
			String action = args.getString("action");
			return new GetServerData(getActivity(), url, action);
		}
		else
			return null;
	}

	@Override
	public void onLoadFinished(Loader<JSONObject> loader, JSONObject json) {
		// TODO Auto-generated method stub
		if( json != null ) {
			String action = "";
			try {
				action = json.getString("action");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(action.equals("MARKET_CONTENTS_LIST")) {
				marketContentsList(json);
			}			
		}
	}

	@Override
	public void onLoaderReset(Loader<JSONObject> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void marketContentsList(JSONObject json){
		MarketAdapter mAdapter;
		JSONArray contacts;
		ListView mList = new ListView(getActivity());
		mList.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		ArrayList<HashMap<String, String>> contentsList = new ArrayList<HashMap<String, String>>();
		rootView.removeAllViews();
		
		try {
	        // Getting Array of Contacts
	        contacts = json.getJSONArray("market_contents");
	         
	        // looping through All Contacts
	        for(int j = 0; j < contacts.length(); j++){
	            JSONObject c = contacts.getJSONObject(j);
	             
	            // Storing each json item in variable
	            String content_name  = c.getString("content_name");
	            String nickname  = c.getString("nickname");
	            String price  = c.getString("price");
	            String thumbnail_url  = c.getString("thumbnail_url");
	            HashMap<String, String> value = new HashMap<String, String>();
	            value.put(MainActivity.KEY_MARKET_NAME, content_name);
	            value.put(MainActivity.KEY_MARKET_OWNER, nickname);
	            value.put(MainActivity.KEY_MARKET_PRICE, price);
	            value.put(MainActivity.KEY_THUMB_URL, thumbnail_url);
	            contentsList.add(value);
	        }
	    } catch (JSONException e) {
	        e.printStackTrace();
	    }            
		
		mAdapter = new MarketAdapter(getActivity(), contentsList);
		mList.setAdapter(mAdapter);
		rootView.addView(mList);
	}
}
