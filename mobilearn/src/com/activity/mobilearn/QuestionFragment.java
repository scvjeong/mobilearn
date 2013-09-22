package com.activity.mobilearn;

import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.mobilearn.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.SearchView.OnQueryTextListener;
import android.view.animation.Animation;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class QuestionFragment extends Fragment{
    
    public static final String ARG_PAGE = "page";

    private int qPageNumber;
    private View rootView;
    private ArrayList<HashMap<String, String>> questionList = new ArrayList<HashMap<String, String>>();
	private ListView qList;
	private ListView bList;
	private LinearLayout fLayout;
	private LinearLayout bLayout;
    private ViewGroup mContainer;
	private QuestionAdapter qAdapter;
    private PlayListAdapter pAdapter;
    private MainProvider mp;
    
	private final class RemoveWindow implements Runnable {
        public void run() {
            removeWindow();
        }
    }
	
	private RemoveWindow mRemoveWindow = new RemoveWindow();
	Handler mHandler = new Handler();
	private WindowManager mWindowManager;
    private TextView mDialogText;
    private boolean mShowing;
    private boolean mReady;
    private char mPrevLetter = Character.MIN_VALUE;
    private SearchView mSearchView;

    private Interpolator accelerator = new AccelerateInterpolator();
    private Interpolator decelerator = new DecelerateInterpolator();
    
    public static QuestionFragment create(int pageNumber) {
    	QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public QuestionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        qPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
    	rootView = (View) inflater.inflate(R.layout.loading, container, false);    	
    	switch(qPageNumber)
    	{
    	case 0:
    		loadQuestionList(inflater, container);
    		break;
    	case 1:
    		loadPlayListList(inflater, container);
    		break;
    	}
    	
        return rootView;
    }

    public int getPageNumber() {
        return qPageNumber;
    }
	
	public void loadQuestionList(LayoutInflater inflater, ViewGroup container){
		String state;
    	Cursor result;
    	
		rootView = inflater.inflate(R.layout.question_list, container, false);
    	qList = (ListView)rootView.findViewById(R.id.list);
    	mSearchView = (SearchView)rootView.findViewById(R.id.search_view);
    	questionList = new ArrayList<HashMap<String, String>>();
   	
    	mp = new MainProvider(getActivity());
    	mp.open();
    	result = mp.fetchAllQuestion();
    	while(result.moveToNext()){
    		HashMap<String, String> value = new HashMap<String, String>();            	
        	value.put(MainActivity.KEY_QUESTION, result.getString(1));
        	value.put(MainActivity.KEY_PERSENT, result.getString(3) + "/" + result.getString(2));
        	
        	switch(result.getInt(4))
        	{
        	case 0:
        		state = "기초학습";
        		break;
        	default:
        		state = "기초학습";
        	}
        	value.put(MainActivity.KEY_STATE, state);
        	
        	questionList.add(value);
		}
    	mp.close();
    	
    	qAdapter = new QuestionAdapter(getActivity(), questionList);
    	qList.setAdapter(qAdapter);
    	qList.setTextFilterEnabled(true);
    	
    	setupSearchView();
    	
    	getActivity();
		mWindowManager = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
    	
    	qList.setOnScrollListener(osl);
        
        getActivity();
		LayoutInflater inflate = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        mDialogText = (TextView) inflate.inflate(R.layout.list_position, null);
        mDialogText.setVisibility(View.INVISIBLE);
        
        mHandler.post(new Runnable() {

            public void run() {
                mReady = true;
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
                mWindowManager.addView(mDialogText, lp);
            }
        });
	}
	
	private OnClickListener qAddPlayListListener = new OnClickListener() {
        public void onClick(View v) {
        	DialogFragment newFragment = new NewPlayListDialogFragment();
            newFragment.show(getFragmentManager(), "dialog");
        }
    };
    
    private OnClickListener completePlayListListener = new OnClickListener() {
        public void onClick(View v) {
        	flipit();
        }
    };
    
    private OnClickListener editPlayListListener = new OnClickListener() {
        public void onClick(View v) {
        	//pAdapter
        	//bList.removeAllViews();
        	//bList.setAdapter(pAdapter);
        }
    };
    
    private OnItemClickListener detailPlayListListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			flipit();
		}
    };
    
	public void loadPlayListList(LayoutInflater inflater, ViewGroup container){
    	Cursor result;
    	
		rootView = inflater.inflate(R.layout.playlist, container, false);
		
		TextView btnAddPlayList = (TextView)rootView.findViewById(R.id.btn_add_playlist);
		btnAddPlayList.setOnClickListener(qAddPlayListListener);
				
		fLayout = (LinearLayout)rootView.findViewById(R.id.playlist_front);
		bLayout = (LinearLayout)rootView.findViewById(R.id.playlist_back);
		
    	qList = (ListView)rootView.findViewById(R.id.list);
    	bList = (ListView)rootView.findViewById(R.id.list_back);
    	questionList = new ArrayList<HashMap<String, String>>();
   	
    	mp = new MainProvider(getActivity());
    	mp.open();
    	result = mp.fetchAllPlayList();
    	while(result.moveToNext()){
    		HashMap<String, String> value = new HashMap<String, String>();            	
        	value.put(MainProvider.KEY_TITLE, result.getString(1));
        	questionList.add(value);
		}
    	mp.close();
    	
    	pAdapter = new PlayListAdapter(getActivity(), questionList);
    	qList.setAdapter(pAdapter);
    	bList.setAdapter(pAdapter);
    	qList.setOnItemClickListener(detailPlayListListener);
    	
    	// complete
    	TextView btnCompletePlayList = (TextView)rootView.findViewById(R.id.btn_complete_playlist);
    	btnCompletePlayList.setOnClickListener(completePlayListListener);
    	
    	// edit
    	TextView btnEditPlayList = (TextView)rootView.findViewById(R.id.btn_edit_playlist);
    	btnEditPlayList.setOnClickListener(editPlayListListener);
	}
	
	private OnScrollListener osl = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
	            int visibleItemCount, int totalItemCount) {
	        if (mReady) {
	            //char firstLetter = questionList.get(firstVisibleItem).get(MainActivity.KEY_QUESTION).charAt(0);
	        	HashMap<String, String> firstItem = (HashMap<String, String>)qList.getAdapter().getItem(firstVisibleItem); 
	        	char firstLetter = firstItem.get(MainActivity.KEY_QUESTION).charAt(0);
	        	
	            if (!mShowing && firstLetter != mPrevLetter) {

	                mShowing = true;
	                mDialogText.setVisibility(View.VISIBLE);
	            }
	            mDialogText.setText(((Character)firstLetter).toString());
	            mHandler.removeCallbacks(mRemoveWindow);
	            mHandler.postDelayed(mRemoveWindow, 500);
	            mPrevLetter = firstLetter;
	        }
	    }
	};

	private void removeWindow() {
        if (mShowing) {
            mShowing = false;
            mDialogText.setVisibility(View.INVISIBLE);
        }
    }
	
	private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(qtl);
        mSearchView.setSubmitButtonEnabled(false);
    }
	
	private OnQueryTextListener qtl = new OnQueryTextListener() {
		@Override
	    public boolean onQueryTextChange(String newText) {
			
	        if (TextUtils.isEmpty(newText)) {
	        	qList.clearTextFilter();
	        } else {
	        	qList.setFilterText(newText.toString());
	        }
	        return true;
	    }
		
		@Override
	    public boolean onQueryTextSubmit(String query) {
	        return false;
	    }
	};
	
	// Dialog
	public static class NewPlayListDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();

            builder.setView(inflater.inflate(R.layout.dialog_playlist, null))
                   .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int id) {
                    	   EditText et = (EditText)NewPlayListDialogFragment.this.getDialog().findViewById(R.id.playlist);
                    	   String titleOfPlayList = et.getText().toString();
                    	   int oid_library = 1;
                    	   if( titleOfPlayList != null && titleOfPlayList.length() > 0 ) {
                    		   NewPlayListDialogFragment.this.createNewPlayList(titleOfPlayList, oid_library);
                    	   }
                       }
                   })
                   .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                    	   NewPlayListDialogFragment.this.getDialog().cancel();
                       }
                   });
            return builder.create();
        }
    
    	public void createNewPlayList(String title, int oid_library)
    	{
    		MainProvider mp = new MainProvider(getActivity());
        	mp.open();
        	mp.insertPlayList(title, oid_library);
        	mp.close();    	
    	}
    	
	}
	
	// flip
	private void flipit() {
        final LinearLayout visible;
        final LinearLayout invisible;
        if (fLayout.getVisibility() == View.GONE) {
        	visible = bLayout;
        	invisible = fLayout;
        } else {
        	invisible = bLayout;
            visible = fLayout;
        }
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
            }
        });
        visToInvis.start();
    }
}
