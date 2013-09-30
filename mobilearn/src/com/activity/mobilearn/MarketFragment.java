package com.activity.mobilearn;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.mobilearn.R;
import com.lib.mobilearn.ImageLoader;
import com.lib.mobilearn.Utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class MarketFragment extends Fragment implements LoaderCallbacks<JSONObject>{
    
    public static final String ARG_PAGE = "page";

    private int mPageNumber;
    private ViewGroup rootView;
    private FragmentManager fragmentManager;
    private MarketAdapter mAdapter;
    
    private LinearLayout fLayout, bLayout;
    private Interpolator accelerator = new AccelerateInterpolator();
    private Interpolator decelerator = new DecelerateInterpolator();

    public static MarketFragment create(int pageNumber) {
    	MarketFragment fragment = new MarketFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MarketFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
    	Bundle args;
    	String url;
    	
    	rootView = (ViewGroup) inflater.inflate(R.layout.loading, container, false);    	

    	switch(mPageNumber)
    	{
    	case 1:
    		rootView = (ViewGroup) inflater.inflate(R.layout.market_home, container, false);
    		
    		break;
    	case 0:
    		args = new Bundle();
        	url = "http://img.kr:3000/res/market/contents/";
   	        args.putString("url", url);
   	        args.putString("action", "MARKET_CONTENTS_LIST");
   	        getLoaderManager().initLoader(0, args, this);
   	        
   	        fragmentManager = getFragmentManager();
   	        fragmentManager.addOnBackStackChangedListener(new OnBackStackChangedListener() {
   	        	@Override
   	        	public void onBackStackChanged() {
   	        		// TODO Auto-generated method stub
   	        		if(fLayout.getVisibility()>0)
   	        			flipFront();
   	        	}
   	        });
    		break;
    	}

    	return rootView;
    }

    
    public int getPageNumber() {
        return mPageNumber;
    }

	@Override
	public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		if(args != null){
			String url = args.getString("url");
			String action = args.getString("action");
			return new GetServerData(getActivity(), url, action);
		}
		else
			return null;
	}

	@Override
	public void onLoadFinished(Loader<JSONObject> loader, JSONObject json) {
		// TODO Auto-generated method stub
		if( json != null ) {
			String action = "";
			try {
				action = json.getString("action");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(action.equals("MARKET_CONTENTS_LIST")) {
				marketContentsList(json);
			} else if(action.equals("MARKET_CONTENTS")) {
				marketContents(json);
			} else if(action.equals("BUY_MARKET_CONTENTS")) {
				insertMarketContent(json);
			}			
		}
	}

	@Override
	public void onLoaderReset(Loader<JSONObject> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void marketContentsList(JSONObject json){
		JSONArray contacts;
		
		LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout mLayout = (LinearLayout)inflater.inflate(R.layout.market_list, null);
		mLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		fLayout = (LinearLayout)mLayout.findViewById(R.id.market_front);
		bLayout = (LinearLayout)mLayout.findViewById(R.id.market_back);		
		ListView mList = (ListView) mLayout.findViewById(R.id.list);
		ArrayList<HashMap<String, String>> contentsList = new ArrayList<HashMap<String, String>>();
		rootView.removeAllViews();
		
		try {
	        // Getting Array of Contacts
	        contacts = json.getJSONArray("market_contents");
	         
	        // looping through All Contacts
	        for(int j = 0; j < contacts.length(); j++){
	            JSONObject c = contacts.getJSONObject(j);
	             
	            // Storing each json item in variable
	            String oidMarket  = c.getString("oid");
	            String contentName  = c.getString("content_name");
	            String nickname  = c.getString("nickname");
	            String price  = c.getString("price");
	            String thumbnailUrl  = c.getString("thumbnail_url");
	            HashMap<String, String> value = new HashMap<String, String>();
	            value.put(MainProvider.KEY_OID_MARKET, oidMarket);
	            value.put(MainProvider.KEY_NAME, contentName);
	            value.put(MainProvider.KEY_OWNER, nickname);
	            value.put(MainProvider.KEY_PRICE, price);
	            value.put(MainProvider.KEY_THUMB_URL, thumbnailUrl);
	            contentsList.add(value);
	        }
	    } catch (JSONException e) {
	        e.printStackTrace();
	    }            
		
		mAdapter = new MarketAdapter(getActivity(), contentsList);
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(detailMarketItemListener);
		rootView.addView(mLayout);
	}
	
	private OnItemClickListener detailMarketItemListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			fragmentManager.beginTransaction().addToBackStack("market").commit();
			loadMarketContents(mAdapter.getItem(position).get(MainProvider.KEY_OID_MARKET));
		}
    };
    
    public void loadMarketContents(String oidMarket){
		Bundle args = new Bundle();
    	String url = "http://img.kr:3000/res/market/contents/"+oidMarket;
        args.putString("url", url);
        args.putString("action", "MARKET_CONTENTS");
        getLoaderManager().restartLoader(0, args, this);
    }
    
    public void marketContents(JSONObject json){
    	flipBack();
    	JSONArray contacts;
    	ImageLoader imageLoader = new ImageLoader(getActivity()); 
		try {
			TextView contentNameView = (TextView)rootView.findViewById(R.id.title_of_market_back);
			TextView nickameView = (TextView)rootView.findViewById(R.id.nickname_back);
			TextView priceView = (TextView)rootView.findViewById(R.id.price_back);
			ImageView thumbImage = (ImageView)rootView.findViewById(R.id.image);
	        // Getting Array of Contacts
	        contacts = json.getJSONArray("market_content");
	         
	        // looping through All Contacts
	        for(int j = 0; j < contacts.length(); j++){
	            JSONObject c = contacts.getJSONObject(j);
	             
	            contentNameView.setText(c.getString("content_name"));
	            nickameView.setText(c.getString("nickname"));
	            priceView.setText(c.getString("price"));
	            priceView.setTag(c.getString("oid"));
	            String thumbnailUrl  = c.getString("thumbnail_url");
	            	    		
	    		if( !thumbnailUrl.equals("null") ) {
	    			imageLoader.DisplayImage("http://lyd.kr:3000" + thumbnailUrl, thumbImage);
	    		}
	        }
	        priceView.setOnClickListener(onClickPrice);
	        
	        contacts = json.getJSONArray("questions");
	        ArrayList<HashMap<String, String>> questionList = new ArrayList<HashMap<String, String>>();
	        for(int j = 0; j < contacts.length(); j++){
	            JSONObject c = contacts.getJSONObject(j);
	            
	            HashMap<String, String> value = new HashMap<String, String>();
	        	value.put(MainProvider.KEY_QUESTION, c.getString("question"));
	        	questionList.add(value);
	        }
	        ListView qList = (ListView)rootView.findViewById(R.id.list_back);
	        qList.setAdapter(new QuestionAdapter(getActivity(), questionList, "library"));
	        
	    } catch (JSONException e) {
	        e.printStackTrace();
	    }            
    }

    OnClickListener onClickPrice = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String oidMarket = String.valueOf(v.getTag());
			buyMarketContent(oidMarket);
		}
	};
    
	public void buyMarketContent(String oidMarket) {
		Bundle args = new Bundle();
    	String url = "http://img.kr:3000/res/market/contents/"+oidMarket;
        args.putString("url", url);
        args.putString("action", "BUY_MARKET_CONTENTS");
        getLoaderManager().restartLoader(0, args, this);
	}
	
	public void insertMarketContent(JSONObject json){
		flipBack();
    	JSONArray contacts;
    	ImageLoader imageLoader = new ImageLoader(getActivity()); 
		try {
			MainProvider mp = new MainProvider(getActivity());
			mp.open();
	        contacts = json.getJSONArray("market_content");
	         
	        for(int j = 0; j < contacts.length(); j++){
	            JSONObject c = contacts.getJSONObject(j);
	            
	            long oid = Long.parseLong(c.getString("oid"));
	            String contentName = c.getString("content_name");
	            String nickname = c.getString("nickname");
	            String updateDate = c.getString("update_date");
	            String thumbnailUrl = c.getString("thumbnail_url");

	            mp.insertLibrary(oid, contentName, nickname, 1, updateDate, 0, thumbnailUrl);
	        }

	        contacts = json.getJSONArray("questions");
	         
	        for(int j = 0; j < contacts.length(); j++){
	            JSONObject c = contacts.getJSONObject(j);
	            
	            
	            long oidQuestion = Long.parseLong(c.getString("oid_question"));
	            long oidLibrary = Long.parseLong(c.getString("oid_library"));
	            long oidAnswer = Long.parseLong(c.getString("oid_answer"));
	            int score = Integer.parseInt(c.getString("score"));
	            String question = c.getString("question");
	            String reply = c.getString("reply");
	            int answer = Integer.parseInt(c.getString("answer"));
	            
	            mp.createQuestion(oidQuestion, question, oidLibrary, score, 0);
	            mp.insertAnswer(oidAnswer, reply, answer, oidQuestion);
	        }
	        mp.close();
	        
	    } catch (JSONException e) {
	        e.printStackTrace();
	    }
	}
	
    // flip
 	public void flipFront() {
 		
        final LinearLayout visible;
        final LinearLayout invisible;
        invisible = fLayout;
        visible = bLayout;
         
        ObjectAnimator visToInvis = ObjectAnimator.ofFloat(visible, "rotationY", 0f, 90f);
        visToInvis.setDuration(500);
        visToInvis.setInterpolator(accelerator);
        final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(invisible, "rotationY", -90f, 0f);
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
 	
 	public void flipBack() {
        final LinearLayout visible;
        final LinearLayout invisible;
		invisible = bLayout;
		visible = fLayout;
        
        ObjectAnimator visToInvis = ObjectAnimator.ofFloat(visible, "rotationY", 0f, 90f);
        visToInvis.setDuration(500);
        visToInvis.setInterpolator(accelerator);
        final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(invisible, "rotationY", -90f, 0f);
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
