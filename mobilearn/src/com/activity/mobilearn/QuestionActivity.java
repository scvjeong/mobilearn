package com.activity.mobilearn;

import java.util.Random;

import com.example.mobilearn.R;
import com.service.mobilearn.LoadingService;

import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

public class QuestionActivity extends Activity implements OnClickListener{

	private MainProvider mp;
	
	private TextView date;
	private TextView q1;
	
	private static int REPLY_MAX_NUM = 4;
	private static int NO_ANSWER_MAX_NUM = 100;
	private int answerNum;
	private boolean is_first = true;
	private long oid_question;
	private Vibrator vibrator = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question);
		
		Log.e("QuestionActivity","Start : ");
		
		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		
        mp = new MainProvider(this);
        mp.open();
		
        long oid_library = 16391;
        this.oid_question = (Integer) getIntent().getExtras().get(MainProvider.KEY_OID_QUESTION);
		Cursor result = mp.fetchOneQuestion(oid_library, oid_question);
		q1 = (TextView)findViewById(R.id.q1);
		
		Random random = new Random();
		if(result.move(0)){
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
		/*
		if( this.makingAnswerClickListener(v.getId()) )
			finish();
		*/
	}
	
	public boolean makingAnswerClickListener(int id)
	{
		mp.open();
		boolean ca = false;
		if( id == this.answerNum ) {
			Toast.makeText(this, "correct", Toast.LENGTH_SHORT).show();
			ca = true;
			if(this.is_first) {
				mp.updateCorrectQuestion(this.oid_question);
			}
		} else {
			long pattern[] = new long[] {0, 200, 100, 200};
			Cursor result = mp.fetchSetting("vibration");
			if(result.getCount() < 1)
				vibrator.vibrate(pattern, -1);
			else if(result.move(0)) {
        		String value = result.getString(0);
        		if(value.equals("ON"))
        			vibrator.vibrate(pattern, -1);
        		else
        			vibrator = null;
        	}	
		}
		this.is_first= false;
		mp.close();
		
		return ca;
	}
}
