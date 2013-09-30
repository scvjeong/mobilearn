package com.activity.mobilearn;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.mobilearn.R;

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

public class MenuAdapter extends BaseAdapter implements Filterable{

	private Activity activity;
	private String[] data;
	private static LayoutInflater inflater=null;
	
	public MenuAdapter(Activity a, String[] d ){
		activity = a;
		data = d;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.length;
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return data[position];
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
			vi = inflater.inflate(R.layout.menu_list_item, null);
		TextView title = (TextView)vi.findViewById(R.id.menu_item);
		ImageView icon = (ImageView)vi.findViewById(R.id.menu_item_icon);
		title.setText(data[position]);
		
		switch(position)
		{
		case 0:
			icon.setImageResource(R.drawable.icon_main);
			break;
		case 1:
			icon.setImageResource(R.drawable.icon_my);
			break;
		case 2:
			icon.setImageResource(R.drawable.icon_library);
			break;
		case 3:
			icon.setImageResource(R.drawable.icon_sta);
			break;
		case 4:
			icon.setImageResource(R.drawable.icon_market);
			break;
		case 5:
			icon.setImageResource(R.drawable.icon_setting);
			break;
		}
		return vi;
	}

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
