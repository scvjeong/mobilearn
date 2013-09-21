package com.activity.mobilearn;

import com.example.mobilearn.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MarketFragment extends Fragment {
    
    public static final String ARG_PAGE = "page";

    private int mPageNumber;
    private ViewGroup rootView;

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
        
    	switch(mPageNumber)
    	{
    	case 0:
    		rootView = (ViewGroup) inflater.inflate(R.layout.market_home, container, false);
    		break;
    	case 1:
    		rootView = (ViewGroup) inflater.inflate(R.layout.market_list, container, false);
    		break;
    	}
    	
        return rootView;
    }

    public int getPageNumber() {
        return mPageNumber;
    }
}
