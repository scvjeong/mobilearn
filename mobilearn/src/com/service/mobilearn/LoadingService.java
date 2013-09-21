package com.service.mobilearn;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoadingService extends Service{
	private TextView tv;
	private LinearLayout ll;
	
	@Override
	public void onCreate() {
		super.onCreate();

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

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(tv != null)
		{
			((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(ll);
			tv = null;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}