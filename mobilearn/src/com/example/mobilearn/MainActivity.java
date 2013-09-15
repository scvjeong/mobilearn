package com.example.mobilearn;

import java.util.Random;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.database.Cursor;

public class MainActivity extends Activity implements OnClickListener{

	private MainProvider mp;
	
	private TextView date;
	private TextView q1;
	
	private static int QUESTION_NUM = 50;
	private static int REPLY_MAX_NUM = 6;
	private static int NO_ANSWER_MAX_NUM = 100;
	private int answerNum;
	private boolean is_first = true;
	private long oid_question;

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
		
		Log.d("Main", "Activity :: MainActivity");
		Cursor result = mp.fetchQuestion(16391, QUESTION_NUM);
		q1 = (TextView)findViewById(R.id.q1);
		
		Random random = new Random();
		int num = random.nextInt(QUESTION_NUM);
		if(result.move(num)){
			q1.setText( result.getString(1) );
			this.oid_question = result.getInt(0);
			mp.updateCountQuestion(this.oid_question);
			
			int i, idx = 0;
			int answerCount;
			String answerString = "";
			Cursor answerResult = mp.fetchTrueAnswer(this.oid_question);
			answerCount = answerResult.getCount();
			idx = random.nextInt(answerCount);	
			if( answerResult.move(idx) ){
				answerString = answerResult.getString(0);
			}
			
			Cursor replyResult = mp.fetchFalseAnswer(this.oid_question, NO_ANSWER_MAX_NUM);
			int replyNum = 0;
			String[] reply = new String[REPLY_MAX_NUM];
			for(i=0; i<REPLY_MAX_NUM; i++){
				replyNum = random.nextInt(replyResult.getCount());
				if( replyResult.move(replyNum) ){
					reply[i] = replyResult.getString(0);
					replyResult.moveToFirst();
				}
			}
			this.answerNum = random.nextInt(REPLY_MAX_NUM);  
			makingAnswer(answerString, answerNum, reply);
		}
		else {
			mp.close();
			finish();
		}
		
		String time = android.text.format.DateFormat.format("yyyy-MM-dd aa h:mm", System.currentTimeMillis()).toString();

		date = (TextView)findViewById(R.id.date);
		date.setText(time);
		
		mp.close();
	}
	
	public void makingAnswer(String answer, int answerNum, String[] reply){
		
		this.answerNum = answerNum;
		LinearLayout answerView = (LinearLayout)findViewById(R.id.answer_list);
		TextView answerText;
		
		int i;
		int replyNum = reply.length;
		for(i=0; i<REPLY_MAX_NUM; i++){
			answerText = new TextView(this);
			answerText.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			answerText.setGravity(0x11);
			answerText.setTextSize(20);
			answerText.setPadding(0, 15, 0, 15);
			answerText.setId(i);
			if(i==answerNum) 
				answerText.setText(answer);
			else if(i < replyNum)
				answerText.setText(reply[i]);
			else
				answerText.setText("");
				
			answerText.setOnClickListener(this);
			answerView.addView(answerText);
		}
				
	}
	
	@Override
	public void onClick(View v) {
		if( this.makingAnswerClickListener(v.getId()) )
			finish();
	}
	
	public boolean makingAnswerClickListener(int id)
	{
		boolean ca = false;
		if( id == this.answerNum ) {
			Toast.makeText(this, "correct", Toast.LENGTH_SHORT).show();
			ca = true;
			if(this.is_first) {
				mp.open();
				mp.updateCorrectQuestion(this.oid_question);
				mp.close();
			}
		} else {
			Toast.makeText(this, "incorrect", Toast.LENGTH_SHORT).show();
		}
		this.is_first= false;
		/*
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
		*/
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
