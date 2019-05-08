package com.example.mahua.funage.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mahua.funage.domain.Ticket;
import com.example.mahua.funage.projection.R;

public class OrderAdapter extends BaseAdapter {
	
	Context context;
	
	List<Ticket> tickets;
	
	LayoutInflater inflater;
	
	public OrderAdapter(Context context, List<Ticket> tickets) {
		this.context = context;
		this.tickets = tickets;
		
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return tickets.size();
	}
	
	@Override
	public Object getItem(int position) {
		return tickets.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Ticket ticket = (Ticket)getItem(position);
		ViewHoder viewHoder = null;
		if (convertView == null) {
			viewHoder = new ViewHoder();
			convertView = inflater.inflate(R.layout.list_item_order, null);
			viewHoder.rl_item = (RelativeLayout)convertView.findViewById(R.id.rl_item);
			viewHoder.tv_sign = (TextView)convertView.findViewById(R.id.tv_sign);
			viewHoder.iv_is_sign = (ImageView)convertView.findViewById(R.id.iv_is_sign);
			viewHoder.tv_item_ticket_info = (TextView)convertView.findViewById(R.id.tv_item_ticket_info);
			viewHoder.tv_item_ticket_status = (TextView)convertView.findViewById(R.id.tv_item_ticket_status);
			viewHoder.tv_item_ticket_seatremark = (TextView)convertView.findViewById(R.id.tv_item_ticket_seatremark);
			convertView.setTag(viewHoder);
			
		} else {
			viewHoder = (ViewHoder)convertView.getTag();
		}
		
		viewHoder.tv_item_ticket_info.setText(ticket.getTicketNo()+"("+ticket.getGrade()+")");
		
		if("1".equals(ticket.getIsPrint())){
			viewHoder.tv_item_ticket_status.setText("已打印");	
			viewHoder.tv_item_ticket_status.setTextColor(context.getResources().getColor(R.color.green));
		}else{
			viewHoder.tv_item_ticket_status.setText("未打印");
			viewHoder.tv_item_ticket_status.setTextColor(context.getResources().getColor(R.color.red));
		}
		
//		viewHoder.tv_item_ticket_status.setText("1".equals(ticket.getIsPrint())?"已打印":"未打印");
		viewHoder.tv_item_ticket_seatremark.setText(ticket.getSeat_remark());
		
		// 是否已打印 (签到)
		if ("1".equals(ticket.getIsPrint())) {
			viewHoder.iv_is_sign.setVisibility(View.VISIBLE);
			viewHoder.rl_item.setBackgroundResource(R.drawable.customer_item_tran);
			viewHoder.tv_sign.setVisibility(View.GONE);
		} else {
			viewHoder.iv_is_sign.setVisibility(View.GONE);
			// 如果没有签到的
			if (ticket.isSelectd()) {
				// 如果是选中
				viewHoder.rl_item.setBackgroundResource(R.drawable.customer_item_blue);
				if (ticket.isChecked()) {
					// 要取消选中了
					viewHoder.tv_sign.setVisibility(View.VISIBLE);
					viewHoder.tv_sign.setText("取消");
					viewHoder.tv_sign.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							ticket.setChecked(false);
							ticket.setSelectd(false);
							notifyDataSetChanged();
						}
					});
					
				} else {
					viewHoder.tv_sign.setVisibility(View.GONE);
				}
			} else {
				viewHoder.rl_item.setBackgroundResource(R.drawable.customer_item_tran);
				if (ticket.isChecked()) {
					// 要选中了
					viewHoder.tv_sign.setVisibility(View.VISIBLE);
					viewHoder.tv_sign.setText("签到");
					viewHoder.tv_sign.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							ticket.setChecked(false);
							ticket.setSelectd(true);
							notifyDataSetChanged();
						}
					});
					
				} else {
					viewHoder.tv_sign.setVisibility(View.GONE);
				}
				
			}
			
		}
		
		return convertView;
	}
	
	public static class ViewHoder {
		
		RelativeLayout rl_item;
		
		TextView tv_sign;
		
		ImageView iv_is_sign;
		
		TextView tv_item_ticket_info;
		
		TextView tv_item_ticket_status;
		
		TextView tv_item_ticket_seatremark;
		
	}
}
