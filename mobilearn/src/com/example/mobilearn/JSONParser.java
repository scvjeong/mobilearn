package com.example.mobilearn;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
 
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {
	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	static int responseCode = 0;
	
	public JSONParser(){
		
	}
	
	public JSONObject getJSONFromUrl(String u, String action) {
		 
		HttpURLConnection conn = null;
		
        // Making HTTP request
        try {
        	
			// HttpURLConnection
			URL url = new URL(u);
			conn = (HttpURLConnection)url.openConnection();
			
			conn.setConnectTimeout(3 * 1000);
			conn.setReadTimeout(3 * 1000);
			conn.setRequestMethod("GET");
			
			conn.setRequestProperty("Cache-Control", "no-cache");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");

			is = new BufferedInputStream(conn.getInputStream());
			responseCode = conn.getResponseCode();
 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "UTF8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        } finally{
        	conn.disconnect();
        }
        
        try {
        	jObj = new JSONObject(json);
        	jObj.put("action", action);
        	
	    } catch (JSONException e) {
	        e.printStackTrace();
	    }          

        return jObj;
    }
}
