package com.activity.mobilearn;

import org.json.JSONObject;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.lib.mobilearn.JSONParser;

public class GetServerData extends AsyncTaskLoader<JSONObject>{
	
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