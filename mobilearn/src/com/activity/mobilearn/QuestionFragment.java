package com.activity.mobilearn;

import java.util.ArrayList;
import java.util.Random;

import com.example.mobilearn.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QuestionFragment extends Fragment{
    
    public static final String ARG_PAGE = "page";

    private int qPageNumber, answerNum;
    private long oidQuestion;
    private View rootView;
    
    private MainProvider mp;
	private TextView q1, v;
	private static int REPLY_MAX_NUM = 4;
	private static int NO_ANSWER_MAX_NUM = 100;
	private boolean isFirst = true;
	private boolean isVibrate = true;
	private boolean ingAnim = false;
	private Vibrator vibrator = null;
	private long pattern[] = new long[] {0, 200, 100, 200};
	
	private LinearLayout fLayout, bLayout;
	
	private Interpolator accelerator = new AccelerateInterpolator();
    private Interpolator decelerator = new DecelerateInterpolator();
    
    public static QuestionFragment create(int pageNumber, ArrayList<Long> arrOidQuestion) {
    	QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putLong(MainProvider.KEY_OID_QUESTION, arrOidQuestion.get(pageNumber));
        fragment.setArguments(args);
        return fragment;
    }

    public QuestionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        qPageNumber = getArguments().getInt(ARG_PAGE);
        oidQuestion = getArguments().getLong(MainProvider.KEY_OID_QUESTION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
    	rootView = (View) inflater.inflate(R.layout.question, container, false);
    	fLayout = (LinearLayout) rootView.findViewById(R.id.question_front);
    	bLayout = (LinearLayout) rootView.findViewById(R.id.question_back);
    	TextView btnModeQuestion = (TextView) rootView.findViewById(R.id.btn_mode_question);
    	TextView btnModeView = (TextView) rootView.findViewById(R.id.btn_mode_view);
    	
    	btnModeQuestion.setOnClickListener(modeQuestionListener);
    	btnModeView.setOnClickListener(modeViewListener);
    	
    	if(QuestionActivity.MODE == "question") {
    		bLayout.setVisibility(View.GONE);
    		fLayout.setVisibility(View.VISIBLE);
    	}
    	else if(QuestionActivity.MODE == "view") {
    		fLayout.setVisibility(View.GONE);
    		bLayout.setVisibility(View.VISIBLE);
    	}
    	
		vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
		
        mp = new MainProvider(getActivity());
        mp.open();

		Cursor result = mp.fetchOneQuestion(oidQuestion);
		
		
		Random random = new Random();
		if(result.move(0)){
			q1 = (TextView)rootView.findViewById(R.id.q1);
			v = (TextView)rootView.findViewById(R.id.view);
			q1.setText( result.getString(1) );
			v.setText( result.getString(1) );
			
			mp.updateCountQuestion(oidQuestion);
			
			int i, idx = 0;
			int answerCount;
			String answerString = "";
			Cursor answerResult = mp.fetchTrueAnswer(oidQuestion);
			answerCount = answerResult.getCount();
			idx = random.nextInt(answerCount);	
			if( answerResult.move(idx) ){
				answerString = answerResult.getString(0);
			}
			
			Cursor replyResult = mp.fetchFalseAnswer(oidQuestion, NO_ANSWER_MAX_NUM);
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
			
	    	Cursor settingResult = mp.fetchSetting("vibration");
			if(settingResult.getCount() < 1)
				isVibrate = true;
			else if(result.move(0)) {
	    		String value = settingResult.getString(0);
	    		if(value.equals("ON"))
	    			isVibrate = true;
	    		else
	    			isVibrate = false;
	    	}
			
			makingAnswer(answerString, answerNum, reply);
		}
		
		mp.close();

    	return this.rootView;
    }

    public int getPageNumber() {
        return qPageNumber;
    }
    
    private void makingAnswer(String answer, int answerNum, String[] reply){
		
		this.answerNum = answerNum;
		LinearLayout answerView = (LinearLayout)rootView.findViewById(R.id.answer_list);
		TextView answerText;
		
		int i;
		int replyNum = reply.length;
		for(i=0; i<REPLY_MAX_NUM; i++){
			answerText = new TextView(getActivity());
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
				
			answerText.setOnClickListener(answerListener);
			answerView.addView(answerText);
		}
				
	}
	
    private OnClickListener answerListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if( makingAnswerClick(v.getId()) )
				((QuestionActivity)getActivity()).setCurrentPagerItem(qPageNumber+1);
			
		}
	};
	
	private OnClickListener modeQuestionListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			QuestionActivity.MODE = "question";
			if(!ingAnim && fLayout.getVisibility() > 0) {
				flipFront();
			}
		}
	};
	
	private OnClickListener modeViewListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			QuestionActivity.MODE = "view";
			if(!ingAnim && bLayout.getVisibility() > 0) {
				flipBack();
				
			}
		}
	};
	
	private boolean makingAnswerClick(int id)
	{
		boolean ca = false;
		if( id == this.answerNum ) {
			ca = true;
			if(this.isFirst) {
				mp.open();
				mp.updateCorrectQuestion(oidQuestion);
				mp.close();
			}
		} else {
			if(isVibrate)
    			vibrator.vibrate(pattern, -1);
    		else
    			vibrator = null;
		}
		this.isFirst= false;
		
		return ca;
	}
	
	// flip front
	private void flipFront() {
		ingAnim = true;
        final LinearLayout visible = bLayout;
        final LinearLayout invisible = fLayout;
    	
        ObjectAnimator visToInvis = ObjectAnimator.ofFloat(visible, "rotationY", 0f, 90f);
        visToInvis.setDuration(500);
        visToInvis.setInterpolator(accelerator);
        final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(invisible, "rotationY",
                -90f, 0f);
        invisToVis.setDuration(500);
        invisToVis.setInterpolator(decelerator);
        visToInvis.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
            	visible.setVisibility(View.GONE);
                invisToVis.start();
                invisible.setVisibility(View.VISIBLE);
                ((QuestionActivity)getActivity()).notifyDataSetChanged(qPageNumber);
                ingAnim = false;
            }
        });
        visToInvis.start();
    }
	
	private void flipBack() {
		ingAnim = true;
        final LinearLayout visible = fLayout;
        final LinearLayout invisible = bLayout;

        ObjectAnimator visToInvis = ObjectAnimator.ofFloat(visible, "rotationY", 0f, 90f);
        visToInvis.setDuration(500);
        visToInvis.setInterpolator(accelerator);
        final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(invisible, "rotationY",
                -90f, 0f);
        invisToVis.setDuration(500);
        invisToVis.setInterpolator(decelerator);
        visToInvis.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
            	visible.setVisibility(View.GONE);
                invisToVis.start();
                invisible.setVisibility(View.VISIBLE);
                ((QuestionActivity)getActivity()).notifyDataSetChanged(qPageNumber);
                ingAnim = false;
            }
        });
        visToInvis.start();
    }
}
