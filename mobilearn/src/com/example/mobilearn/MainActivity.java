package com.example.mobilearn;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.database.Cursor;

public class MainActivity extends Activity implements OnClickListener{

	private MainProvider mp;
	
	private TextView date;
	private TextView q1;
	private TextView tv1;
	private TextView tv2; 
	private TextView tv3; 
	private TextView tv4; 
	private TextView tv5; 
	private TextView tv6; 
	/*
	@Override
	public void onAttachedToWindow(){
		
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG|WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onAttachedToWindow();
	}
	*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_main);
		
        mp = new MainProvider(this);
        mp.open();
        mp.init();
		
		Log.d("Main", "Activity :: MainActivity");
		Cursor result = mp.fetchAllQuestion();
		q1 = (TextView)findViewById(R.id.q1);
		while(result.moveToNext()){
			q1.setText( result.getString(1) );
		}
		
		date = (TextView)findViewById(R.id.date);
		
		String time = android.text.format.DateFormat.format("yyyy-MM-dd aa h:mm", System.currentTimeMillis()).toString();

		date = (TextView)findViewById(R.id.date);
		date.setText(time);
				
		tv1 = (TextView)findViewById(R.id.answer1);
		tv2 = (TextView)findViewById(R.id.answer2);
		tv3 = (TextView)findViewById(R.id.answer3);
		tv4 = (TextView)findViewById(R.id.answer4);
		tv5 = (TextView)findViewById(R.id.answer5);
		tv6 = (TextView)findViewById(R.id.answer6);
		tv1.setOnClickListener(this);
		tv2.setOnClickListener(this);
		tv3.setOnClickListener(this);
		tv4.setOnClickListener(this);
		tv5.setOnClickListener(this);
		tv6.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if( this.markingQuestion(v.getId()) )
			finish();
	}
	
	boolean markingQuestion(int id)
	{
		int ca_num = 2;
		boolean ca = false;
		switch(id){
		case R.id.answer1:
			if( ca_num == 1 ) {
				Toast.makeText(this, "correct", Toast.LENGTH_SHORT).show();
				ca = true;
			}
			else
				Toast.makeText(this, "incorrect", Toast.LENGTH_SHORT).show();
			break;
		case R.id.answer2:
			if( ca_num == 2 ) {
				Toast.makeText(this, "correct", Toast.LENGTH_SHORT).show();
				ca = true;
			}
			else
				Toast.makeText(this, "incorrect", Toast.LENGTH_SHORT).show();
			break;
		case R.id.answer3:
			if( ca_num == 3 ) {
				Toast.makeText(this, "correct", Toast.LENGTH_SHORT).show();
				ca = true;
			}
			else
				Toast.makeText(this, "incorrect", Toast.LENGTH_SHORT).show();
			break;
		case R.id.answer4:
			if( ca_num == 4 ) {
				Toast.makeText(this, "correct", Toast.LENGTH_SHORT).show();
				ca = true;
			}
			else
				Toast.makeText(this, "incorrect", Toast.LENGTH_SHORT).show();
			break;
		case R.id.answer5:
			if( ca_num == 5 ) {
				Toast.makeText(this, "correct", Toast.LENGTH_SHORT).show();
				ca = true;
			}
			else
				Toast.makeText(this, "incorrect", Toast.LENGTH_SHORT).show();
			break;
		case R.id.answer6:
			if( ca_num == 6 ) {
				Toast.makeText(this, "correct", Toast.LENGTH_SHORT).show();
				ca = true;
			}
			else
				Toast.makeText(this, "incorrect", Toast.LENGTH_SHORT).show();
			break;
		}
		
		return ca;
	}
	/*
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	*/
	
	@Override
	public void onBackPressed(){
		return;		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event){
		if((keyCode == KeyEvent.KEYCODE_HOME)||(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)||(keyCode == KeyEvent.KEYCODE_POWER)||(keyCode == KeyEvent.KEYCODE_VOLUME_UP)||(keyCode == KeyEvent.KEYCODE_CAMERA)){
			return true;
		}
		return false;
	}
			
}
