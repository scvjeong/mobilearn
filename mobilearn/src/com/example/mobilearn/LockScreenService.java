package com.example.mobilearn;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

public class LockScreenService extends Service {
	private KeyguardManager km = null; 
	private KeyguardManager.KeyguardLock keylock = null;
	

	private BroadcastReceiver mReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(context, "서비스 실행중!", Toast.LENGTH_LONG).show();
            String action = intent.getAction();
			if(action.equals("android.intent.action.SCREEN_OFF")){
            	Intent i = new Intent(context, MainActivity.class);
            	i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	context.startActivity(i);
            }
			else if(action.equals(Intent.ACTION_SCREEN_ON)){
            	Intent i = new Intent(context, MainActivity.class);
            	i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
			else if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
            	Intent i = new Intent(context, MainActivity.class);
            	i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
		}
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		Log.d("LockScreenService", "LockScreenService::onCreate");
		// TODO Auto-generated method stub
		super.onCreate();
		
		km=(KeyguardManager) this.getSystemService(Activity.KEYGUARD_SERVICE);
        if(km!=null){
        	keylock = km.newKeyguardLock("test");
        	keylock.disableKeyguard();
        }
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		//IntentFilter filter = new IntentFilter("com.androidhuman.action.isAlive");
		IntentFilter filter = new IntentFilter(intent.ACTION_SCREEN_ON);
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

