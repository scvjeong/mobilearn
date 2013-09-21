package com.activity.mobilearn;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.mobilearn.R;
import com.lib.mobilearn.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class LibraryAdapter extends BaseAdapter implements Filterable{
	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private Filter libraryFilter;
	private static LayoutInflater inflater=null;
	public ImageLoader imageLoader; 
	
	public LibraryAdapter(Activity a, ArrayList<HashMap<String, String>> d ){
		activity = a;
		data = d;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader=new ImageLoader(activity.getApplicationContext());
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
			vi = inflater.inflate(R.layout.library_list_row, null);
		TextView title = (TextView)vi.findViewById(R.id.title_of_library);
		ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image);

		HashMap<String, String> library = new HashMap<String, String>();
		library = data.get(position);
		
		title.setText(library.get(MainActivity.KEY_LIBRARY_NAME));
		imageLoader.DisplayImage(library.get(MainActivity.KEY_THUMB_URL), thumb_image);
		
		//vi.setBackgroundColor((position & 1) == 1 ? Color.WHITE : Color.rgb(172,172,172) );

		return vi;
	}
	
	@Override
	public Filter getFilter(){
		if (libraryFilter == null)
			libraryFilter = new LibraryFilter();
	     
	    return libraryFilter;
	}
	
	private class LibraryFilter extends Filter{
		
		ArrayList<HashMap<String, String>> fData = data;
		
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
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
					if(d.get(MainActivity.KEY_LIBRARY_NAME).toUpperCase().startsWith(constraint.toString().toUpperCase()))
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
