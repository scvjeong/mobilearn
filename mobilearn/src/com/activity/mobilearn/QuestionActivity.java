package com.activity.mobilearn;

import java.util.ArrayList;
import java.util.Random;

import com.example.mobilearn.R;
import com.service.mobilearn.LoadingService;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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

public class QuestionActivity extends Activity{

	private ViewPager qPager;
	private PagerAdapter qPagerAdapter;
	
	public static int QUESTION_PAGE_NUM = 1;
	public static ArrayList<Long> ARR_OID_QUESTION;
	public static String MODE = "question";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_pager);
		
		long oidLibrary = (Long) getIntent().getExtras().get(MainProvider.KEY_OID_LIBRARY);
		long oidPlayList = (Long) getIntent().getExtras().get(MainProvider.KEY_OID_PLAYLIST);
		long OidQuestion = (Long) getIntent().getExtras().get(MainProvider.KEY_OID_QUESTION);
		
		MainProvider mp = new MainProvider(this);
		mp.open();
		Cursor result;
		if(oidPlayList > 0)
			result = mp.fetchPlayListQuestion(oidPlayList);
		else
			result = mp.fetchAllQuestionInLibrary(oidLibrary);
		
		if(result.getCount() == 1)
			QUESTION_PAGE_NUM = result.getCount();
		else
			QUESTION_PAGE_NUM = result.getCount()-1;
		
		ARR_OID_QUESTION = new ArrayList<Long>();
		if(QUESTION_PAGE_NUM > 0) {
			int currentIdx = 0;
			int idx = 0;
			long oid;
			while(result.moveToNext()){
				oid = result.getLong(0);
				ARR_OID_QUESTION.add(oid);
				if(OidQuestion == oid)
					currentIdx = idx;
				idx++;
			}
			mp.close();
			qPager = (ViewPager) findViewById(R.id.pager);
			qPagerAdapter = new QuestionPagerAdapter(getFragmentManager());
			qPager.setAdapter(qPagerAdapter);
			qPager.setCurrentItem(currentIdx);
		}
		else
			mp.close();
	}
	
	public void setCurrentPagerItem(int item){
		qPager.setCurrentItem(item); 
	}
	
	public void notifyDataSetChanged(int page){
		qPagerAdapter = qPager.getAdapter();
		qPagerAdapter.notifyDataSetChanged();
		qPager.setAdapter(qPagerAdapter);
		qPager.setCurrentItem(page);
	}
}
