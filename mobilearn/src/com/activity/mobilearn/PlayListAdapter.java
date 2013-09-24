package com.activity.mobilearn;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.mobilearn.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class PlayListAdapter extends BaseAdapter implements Filterable{

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private Filter questionFilter;
	private static LayoutInflater inflater=null;
	
	public PlayListAdapter(Activity a, ArrayList<HashMap<String, String>> d ){
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
	public HashMap<String, String> getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}
	
	public void removeItem(int position) {
		data.remove(position);
	}
	
	public void removeItemByOid(long oid) {
		int i;
		long keyOid;
		for(i=0; i<data.size(); i++){
			keyOid = Long.parseLong(data.get(i).get(MainProvider.KEY_OID));
			if(keyOid == oid)
				data.remove(i);
		}
	}

	public void removeAllItem() {
		data.clear();
	}
	
	public int setItem(HashMap<String, String> item){
		data.add(0, item);
		return (data.size()-1);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if(convertView == null)
			vi = inflater.inflate(R.layout.playlist_list_row, null);
		TextView title = (TextView)vi.findViewById(R.id.title_of_playlist);
		
		HashMap<String, String> question = new HashMap<String, String>();
		question = data.get(position);
		
		title.setText(question.get(MainProvider.KEY_TITLE));

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
			FilterResults results = new FilterResults();
			if (constraint == null || constraint.length() == 0) {
				results.values = fData;
				results.count = fData.size();
			}
			else {
				ArrayList<HashMap<String, String>> nData = new ArrayList<HashMap<String, String>>();
				
				for (HashMap<String, String> d : fData) {
					if(d.get(MainProvider.KEY_TITLE).toUpperCase().startsWith(constraint.toString().toUpperCase()))
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
			if (results.count == 0)
			    notifyDataSetInvalidated();
			else {
			    data = (ArrayList<HashMap<String, String>>) results.values;
			    notifyDataSetChanged();
			}
		}
	}
}
