package com.example.mahua.funage.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mahua.funage.domain.City;
import com.example.mahua.funage.projection.LoginActivity.ItemType;
import com.example.mahua.funage.projection.R;

public class CustomerSpinnerAdapter<T> extends ArrayAdapter<T> {
	
	private Context context;
	
	private List<T> objects;
	
	private ItemType type;
	
	public CustomerSpinnerAdapter(Context context, int textViewResourceId, List<T> objects, ItemType type) {
		super(context, textViewResourceId);
		this.context = context;
		this.objects = objects;
		this.type = type;
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			
			convertView = View.inflate(context, R.layout.row_spinner_arrow_filter, null);
		}
		TextView tv = (TextView)convertView.findViewById(R.id.spinner_tv);
		switch (type) {
			case city:
				
				System.out.println("name " + ((City)objects.get(position)).getRegion_name());
				
				tv.setText(((City)objects.get(position)).getRegion_name());
				break;
			case theater:
				
				break;
			case drama:
				
				break;
			default:
				break;
		}
		
		// return super.getView(position, convertView,
		// parent);
		return convertView;
	}
	
}
