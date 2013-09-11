package com.example.mobilearn;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class ContentsManager {
	
	private final Context mCtx;
	private MainProvider mp;
	
	public ContentsManager(Context ctx){
		this.mCtx = ctx;
	}
	
	public void setQuestions(String json){
		JSONArray contacts = null;
		mp = new MainProvider(mCtx);
		mp.open();
		
	    try {
			JSONObject jObj = new JSONObject(json);
	        // Getting Array of Contacts
	        contacts = jObj.getJSONArray("learn");
	         
	        // looping through All Contacts
	        for(int j = 0; j < contacts.length(); j++){
	            JSONObject c = contacts.getJSONObject(j);
	             
	            // Storing each json item in variable
	            String oid  = c.getString("oid");
	            String question  = c.getString("question");
	            int oid_learn  = Integer.parseInt(c.getString("oid_learn"));
	            mp.createQuestion(question, oid_learn);
	        }
	    } catch (JSONException e) {
	        e.printStackTrace();
	    }            
	}
	
	/*
	Boolean isUpdateForMyContents = false;
	if(isUpdateForMyContents) {
       	String url = "http://img.kr:3000/res/library/16391";
       	Bundle args = new Bundle();
       	args.putString("url", url);
       	args.putBoolean("update_check", false);
       	getLoaderManager().initLoader(0, args, this);
       }
       else {
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
       }
	
	qList = (ListView)rootView.findViewById(R.id.list);
	
	JSONArray contacts = null;
	
    try {
		JSONObject jObj = new JSONObject(json);
        // Getting Array of Contacts
        contacts = jObj.getJSONArray("learn_update_date");
         
        // looping through All Contacts
        for(int j = 0; j < contacts.length(); j++){
            JSONObject c = contacts.getJSONObject(j);
             
            // Storing each json item in variable
            String update_date  = c.getString("update_date");
            HashMap<String, String> value = new HashMap<String, String>();
            value.put(KEY_QUESTION, update_date);
        	questionList.add(value);
        }
    } catch (JSONException e) {
        e.printStackTrace();
    }            
	
	//qAdapter = new QuestionAdapter(getActivity(), questionList);
	//qList.setAdapter(qAdapter);
    */
}
