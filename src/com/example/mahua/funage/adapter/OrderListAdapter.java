package com.example.mahua.funage.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mahua.funage.domain.Order;
import com.example.mahua.funage.projection.R;
import com.example.mahua.funage.utils.DateTimeUtils;

public class OrderListAdapter extends BaseAdapter {
	
	List<Order> orderList;
	
	Context context;
	
	LayoutInflater layoutInflater;
	
	String start_time;
	
	public OrderListAdapter(Context context, List<Order> orderList, String start_time) {
		super();
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.orderList = orderList;
		this.start_time = start_time;
	}
	
	@Override
	public int getCount() {
		return orderList.size();
	}
	
	@Override
	public Object getItem(int position) {
		return orderList.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.list_item_order_list, null);
			holder = new ViewHolder();
			holder.tv_drama_time = (TextView)convertView.findViewById(R.id.tv_drama_time);
			holder.tv_address = (TextView)convertView.findViewById(R.id.tv_address);
			holder.tv_name = (TextView)convertView.findViewById(R.id.tv_name);
			holder.tv_phone = (TextView)convertView.findViewById(R.id.tv_phone);
			holder.tv_drama_name = (TextView)convertView.findViewById(R.id.tv_drama_name);
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		Order order = (Order)getItem(position);
		holder.tv_phone.setText(getOrderString(order.getTel()));
		holder.tv_address.setText(getOrderString(order.getTrafficInfo()));
		holder.tv_drama_name.setText(getOrderString(order.getName()));
		holder.tv_drama_time.setText(DateTimeUtils.getTotalDayTime(Long.parseLong(start_time)));
		
		return convertView;
	}
	
	private String getOrderString(String str) {
		if (TextUtils.isEmpty(str)) {
			return "";
			
		} else {
			return str;
		}
		
	}
	
	private class ViewHolder {
		TextView tv_drama_time;
		
		TextView tv_address;
		
		TextView tv_name;
		
		TextView tv_phone;
		
		TextView tv_drama_name;
	}
}
