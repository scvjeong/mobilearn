package com.activity.mobilearn;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.mobilearn.R;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class LoginActivity extends Activity implements LoaderCallbacks<JSONObject>{
	
	private LoginActivity t;
	private Bundle args = new Bundle();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		t = this;
		
		TextView btnLogin = (TextView)findViewById(R.id.btn_login);
		TextView btnJoin = (TextView)findViewById(R.id.btn_join);
		
		btnLogin.setOnClickListener(loginClickListener);
		
		String url = "http://lyd.co.kr:3000/auth/request_token/";
        args.putString("url", url);
        args.putString("action", "init");
        getLoaderManager().initLoader(0, args, t);
	}
	
	private OnClickListener loginClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			args.putString("action", "login");
			getLoaderManager().restartLoader(0, args, t);
		}		
	};

	@Override
	public Loader<JSONObject> onCreateLoader(int ID, Bundle args) {
		// TODO Auto-generated method stub
		if(args != null){
			String url = args.getString("url");
			String action = args.getString("action");
			return new GetServerData(this, url, action);
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
			if(action.equals("login")){
				login();
			}
		}
		
	}

	@Override
	public void onLoaderReset(Loader<JSONObject> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void login(){
		Intent i = new Intent(this, MainActivity.class);
		i.putExtra(MainProvider.KEY_LOGIN_FLAG, 1);
        startActivity(i);
		finish();
	}
}
