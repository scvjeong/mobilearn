package com.service.mobilearn;

import java.util.Iterator;
import java.util.List;

import com.Activity.mobilearn.LockScreenActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LockScreenService extends Service {
	
	private TextView tv;
	private LinearLayout ll = null;
	private Intent in; 
	private Context mCx;
	
	private KeyguardManager km = null; 
	private KeyguardManager.KeyguardLock keylock = null;
	public static final String ACTION_INSERT_LOADING_PAGE = "com.service.mobilearn.action.Insert_Loading_Page";
	public static final String ACTION_DELETE_LOADING_PAGE = "com.service.mobilearn.action.Delete_Loading_Page";
	
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("Receive", "action : " + action);
            mCx = context;
            Intent in = new Intent(context, LockScreenActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            
			if(action.equals("android.intent.action.SCREEN_OFF")){
            	//i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            	context.startActivity(in);
            }
			else if(action.equals(ACTION_INSERT_LOADING_PAGE)){
				insertLoadingPage();
				context.startActivity(in);
			}
			else if(action.equals(ACTION_DELETE_LOADING_PAGE)){
				deleteLoadingPage();
			}
			/*
			else if(action.equals(Intent.ACTION_SCREEN_ON)){
            	Intent i = new Intent(context, MainActivity.class);
            	i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }
			else if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
            	Intent i = new Intent(context, MainActivity.class);
            	i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }
            */
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		km=(KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        if(km!=null){
        	keylock = km.newKeyguardLock("test");
        	keylock.disableKeyguard();
        }
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		//IntentFilter filter = new IntentFilter("com.androidhuman.action.isAlive");
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(ACTION_INSERT_LOADING_PAGE);
		filter.addAction(ACTION_DELETE_LOADING_PAGE);
		registerReceiver(mReceiver, filter);
		return Service.START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy(){
		if(keylock!=null){
				keylock.reenableKeyguard();
		}
		 
		if(mReceiver != null)
			unregisterReceiver(mReceiver);
		Toast.makeText(this, "서비스 종료", Toast.LENGTH_LONG).show();
	}
	
	public void insertLoadingPage() {
		tv = new TextView(this);
		tv.setText("이 뷰는 항상 위에 있다.");
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		tv.setTextColor(Color.BLUE);
		
		ll = new LinearLayout(this);
		ll.setLayoutParams(new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		ll.setBackgroundColor(Color.rgb(255, 255, 255));
		ll.addView(tv);
		
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
			WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
			WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
		
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		wm.addView(ll, params); 
	}
	
	public void deleteLoadingPage() {
		if(ll != null)
		{
			((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(ll);
			ll = null;
		}
	}
}

