package com.activity.mobilearn;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.mobilearn.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class QuestionAdapter extends BaseAdapter implements Filterable{

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private String mode;
	private Filter questionFilter;
	private static LayoutInflater inflater=null;
	
	public QuestionAdapter(Activity a, ArrayList<HashMap<String, String>> d, String m ){
		activity = a;
		data = d;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mode = m;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public HashMap<String, String> getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}
	
	public void removeItem(int position) {
		data.remove(position);
	}
	
	public void removeAllItem() {
		data.clear();
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
		if(mode.equals("state") || mode.equals("daily") || mode.equals("library")) {
			if(convertView == null)
				vi = inflater.inflate(R.layout.question_list_row_for_state, null);
			TextView title = (TextView)vi.findViewById(R.id.title_of_question);
			
			HashMap<String, String> question = new HashMap<String, String>();
			question = data.get(position);
	
			title.setText(question.get(MainProvider.KEY_QUESTION));
			vi.setBackgroundColor((position & 1) == 1 ? Color.WHITE : Color.rgb(245,245,245) );
		
		} else {
			if(convertView == null)
				vi = inflater.inflate(R.layout.question_list_row, null);
			TextView title = (TextView)vi.findViewById(R.id.title_of_question);
			TextView persent = (TextView)vi.findViewById(R.id.persent);
			TextView state = (TextView)vi.findViewById(R.id.state);
			TextView score = (TextView)vi.findViewById(R.id.score);
			
			HashMap<String, String> question = new HashMap<String, String>();
			question = data.get(position);
	
			title.setText(question.get(MainProvider.KEY_QUESTION));
			persent.setText(question.get(MainProvider.KEY_PERSENT));
			state.setText(question.get(MainProvider.KEY_STATE));
			score.setText(question.get(MainProvider.KEY_SCORE));
			//vi.setBackgroundColor((position & 1) == 1 ? Color.WHITE : Color.rgb(245,245,245) );
		}
		return vi;
	}
	
	@Override
	public Filter getFilter(){
		if (questionFilter == null)
			questionFilter = new QuestionFilter();
	     
	    return questionFilter;
	}
	
	private class QuestionFilter extends Filter{
		
		ArrayList<HashMap<String, String>> fData = data;
		
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			Log.e("QuestionFilter", "performFiltering");
			// TODO Auto-generated method stub
			FilterResults results = new FilterResults();
			// We implement here the filter logic
			if (constraint == null || constraint.length() == 0) {
				// No filter implemented we return all the list
				results.values = fData;
				results.count = fData.size();
			}
			else {
				// We perform filtering operation
				ArrayList<HashMap<String, String>> nData = new ArrayList<HashMap<String, String>>();
				
				for (HashMap<String, String> d : fData) {
					if(d.get(MainProvider.KEY_QUESTION).toUpperCase().startsWith(constraint.toString().toUpperCase()))
						nData.add(d);
				}

				results.values = nData;
				results.count = nData.size();
			}
			return results;
		}
		
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			// TODO Auto-generated method stub
			if (results.count == 0)
			    notifyDataSetInvalidated();
			else {
			    data = (ArrayList<HashMap<String, String>>) results.values;
			    notifyDataSetChanged();
			}
		}
	}
}
