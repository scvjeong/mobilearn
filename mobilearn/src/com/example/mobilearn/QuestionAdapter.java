package com.example.mobilearn;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class QuestionAdapter extends BaseAdapter{

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater=null;
	
	public QuestionAdapter(Activity a, ArrayList<HashMap<String, String>> d ){
		activity = a;
		data = d;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View vi = convertView;
		if(convertView == null)
			vi = inflater.inflate(R.layout.question_list_row, null);
		TextView title = (TextView)vi.findViewById(R.id.title_of_question);
		TextView persent = (TextView)vi.findViewById(R.id.persent);
		TextView state = (TextView)vi.findViewById(R.id.state);
		
		HashMap<String, String> question = new HashMap<String, String>();
		question = data.get(position);
		
		title.setText(question.get(LearnListActivity.KEY_QUESTION));
		persent.setText(question.get(LearnListActivity.KEY_PERSENT));
		state.setText(question.get(LearnListActivity.KEY_STATE));
		
		return vi;
	}
	
}
