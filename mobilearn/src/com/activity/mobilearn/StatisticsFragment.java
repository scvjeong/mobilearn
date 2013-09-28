package com.activity.mobilearn;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.mobilearn.R;
import com.service.mobilearn.LockScreenService;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.SearchView.OnQueryTextListener;
import android.view.animation.Animation;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class StatisticsFragment extends Fragment{
    
    public static final String ARG_PAGE = "page";

    private int qPageNumber;
    private View rootView;

    public static StatisticsFragment create(int pageNumber) {
    	StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public StatisticsFragment() {
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
    		rootView = inflater.inflate(R.layout.statistics_1, container, false);
    		
    		LinearLayout layoutChartRadar = (LinearLayout) rootView.findViewById(R.id.layout_chart_radar);
       		float chartData[] = {40, 26, 40, 28, 40};
       		View chartRaderView = new ChartRaderView(getActivity(), chartData, "statistics");
       		chartRaderView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
       		layoutChartRadar.addView(chartRaderView);
       		
    		break;
    	case 1:
    		rootView = inflater.inflate(R.layout.statistics_2, container, false);
    		
    		LinearLayout layoutChartLine = (LinearLayout) rootView.findViewById(R.id.layout_chart_line);
    		float chartData2[] = {200, 100, 50, 120, 100, 180, 160, 105};
       		View chartLineView = new ChartLineView(getActivity(), chartData2, "statistics");
       		chartLineView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
       		layoutChartLine.addView(chartLineView);
    	}
    	
        return rootView;
    }

    public int getPageNumber() {
        return qPageNumber;
    }
}
