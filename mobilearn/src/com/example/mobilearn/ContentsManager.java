package com.example.mobilearn;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class ContentsManager {
	
	private final Context mCtx;
	private MainProvider mp;
	
	public ContentsManager(Context ctx){
		this.mCtx = ctx;
	}
	
	public void setQuestions(JSONObject json){
		JSONArray contacts = null;
		mp = new MainProvider(mCtx);
		mp.open();
		
	    try {
	        // Getting Array of Contacts
	        contacts = json.getJSONArray("question_list");
	         
	        // looping through All Contacts
	        for(int j = 0; j < contacts.length(); j++){
	            JSONObject c = contacts.getJSONObject(j);
	             
	            // Storing each json item in variable
	            int oid  = Integer.parseInt(c.getString("oid"));
	            String question  = c.getString("question");
	            int oid_library  = Integer.parseInt(c.getString("oid_library"));
	            mp.createQuestion(oid, question, oid_library);
	        }
	    } catch (JSONException e) {
	        e.printStackTrace();
	    } finally{
        	mp.close();
        }      
	}
	
	public void setAnswers(JSONObject json){
		JSONArray contacts = null;
		mp = new MainProvider(mCtx);
		mp.open();
		
	    try {
	        // Getting Array of Contacts
	        contacts = json.getJSONArray("answers");
	         
	        // looping through All Contacts
	        for(int j = 0; j < contacts.length(); j++){
	            JSONObject c = contacts.getJSONObject(j);
	             
	            // Storing each json item in variable
	            int oid  = Integer.parseInt(c.getString("oid"));
	            String reply  = c.getString("reply");
	            int answer  = Integer.parseInt(c.getString("answer"));
	            int oid_question  = Integer.parseInt(c.getString("oid_question"));
	            mp.insertAnswer(oid, reply, answer, oid_question);
	        }
	    } catch (JSONException e) {
	        e.printStackTrace();
	    } finally{
        	mp.close();
        }      
	}
	
	public void setLibrary(JSONObject json){
		JSONArray contacts = null;
		mp = new MainProvider(mCtx);
		mp.open();
		
	    try {
	        // Getting Array of Contacts
	        contacts = json.getJSONArray("library");
	         
	        // looping through All Contacts
	        for(int j = 0; j < contacts.length(); j++){
	            JSONObject c = contacts.getJSONObject(j);
	             
	            // Storing each json item in variable
	            int oid  = Integer.parseInt(c.getString("oid"));
	            String name  = c.getString("name");
	            int type  = Integer.parseInt(c.getString("type"));
	            String update_date  = c.getString("update_date");
	            int is_use = 1;
	            
	            mp.insertLibrary(oid, name, type, update_date, is_use);
	        }
	    } catch (JSONException e) {
	        e.printStackTrace();
	    } finally{
        	mp.close();
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
