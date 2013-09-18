package com.example.mobilearn;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LockScreenService extends Service {
	
	private KeyguardManager km = null; 
	private KeyguardManager.KeyguardLock keylock = null;
	public static final String ACTION_CHECK_ALIVE = "com.example.mobilearn.action.CHECK_ALIVE";

	private BroadcastReceiver mReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(context, "서비스 실행중!", Toast.LENGTH_LONG).show();
			
            String action = intent.getAction();
			if(action.equals("android.intent.action.SCREEN_OFF")){
            	Intent i = new Intent(context, MainActivity.class);
            	//i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            	i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            	context.startActivity(i);
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
}

