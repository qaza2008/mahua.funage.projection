package com.example.mahua.funage.projection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.mahua.funage.action.OrderAction;
import com.example.mahua.funage.adapter.OrderAdapter;
import com.example.mahua.funage.application.MyApplication;
import com.example.mahua.funage.domain.Order;
import com.example.mahua.funage.domain.Ticket;
import com.example.mahua.funage.utils.Constants;
import com.example.mahua.funage.utils.DateTimeUtils;
import com.example.mahua.funage.utils.JsonTools;
import com.example.mahua.funage.utils.LogUtil;
import com.example.mahua.funage.utils.ToastUtils;
import com.example.mahua.funage.utils.Utils;
import com.example.mahua.funage.utils.VolleyMyListener;
import com.example.mahua.funage.utils.VolleyTools;

import de.greenrobot.event.EventBus;

@EActivity(R.layout.activity_order)
public class OrderActivity extends BaseActivity {
	public static final String TAG = OrderActivity.class.getSimpleName();
	
	@ViewById
	TextView tv_name;
	
	@ViewById
	TextView tv_phone;
	
	@ViewById
	TextView tv_drama_name;
	
	@ViewById
	TextView tv_drama_time;
	
	@ViewById
	TextView tv_address;
	
	@ViewById
	ListView lv_orderDetail;
	
	@ViewById
	TextView tv_no_data;
	
	@ViewById
	LinearLayout ll_data;
	
	@ViewById
	LinearLayout ll_submit;
	
	@Bean
	VolleyTools volleyTools;
	
	List<Order> orders;
	
	Order order;
	
	@App
	MyApplication myApplication;
	
	OrderAdapter orderAdapter;
	
	boolean isAllPrinted = false;
	
	@ViewById
	TextView tv_submit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		volleyTools = new VolleyTools(this);
		EventBus.getDefault().register(this);
		
	}
	
	String no;
	
	String type;
	
	int position = 0;
	
	@AfterViews
	void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			no = intent.getStringExtra("no");
			type = intent.getStringExtra("type");
		} else {
			ToastUtils.showShortToast(this, "参数异常,请返回上一页");
			return;
		}
		if ("2".equals(type)) {
			order = (Order)intent.getSerializableExtra("order");
			position = intent.getIntExtra("position", 0);
			if (order != null) {
				sortTickets();
				showData();
				post(new OrderAction());
			} else {
				showNoData();
			}
		} else {
			
			loadData();
		}
	}
	
	private void loadData() {
		VolleyMyListener myCityListener = new VolleyMyListener() {
			
			@Override
			public void parseJSONObject(JSONObject jsonObject) {
				Map<String, String> map = getReturnSource(jsonObject);
				// 200 成功
				// 201 参数错误
				// 203 找不到此票或此票已打
				// 205 找不到订单信息
				// 206 换票类型错误
				if (Constants.HTTP_STATUS_OK.equals(map.get(Constants.HTTP_STATUS))) {
					showData();
					orders = JsonTools.getCollListFromJson(map.get(Constants.HTTP_DATA), Order.class);
					if (orders != null) {
						if (orders.size() > position) {
							order = orders.get(position);
							sortTickets();
						}
					}
					post(new OrderAction());
				} else {
					ToastUtils.showLongToast(OrderActivity.this, map.get(Constants.HTTP_MESSAGE));
					showNoData();
					
				}
				
			}
			
			@Override
			public void getAccessError(String errorLog) {
				ToastUtils.showLongToast(OrderActivity.this, errorLog);
				post(new OrderAction());
			}
		};
		String url_path =
			Constants.URL_PATH + Constants.PATH_LOAD_ORDER + "?no=" + no + "&type=" + type + "&drama_id="
				+ myApplication.getDrama_id();
		System.out.println("url_path ::  " + url_path);
		
		volleyTools.getJSONByVolley(Request.Method.GET, url_path, null, myCityListener);
	}
	
	private void showData() {
		tv_no_data.setVisibility(View.GONE);
		ll_data.setVisibility(View.VISIBLE);
		lv_orderDetail.setVisibility(View.VISIBLE);
		ll_submit.setVisibility(View.VISIBLE);
	}
	
	private void showNoData() {
		tv_no_data.setVisibility(View.VISIBLE);
		ll_data.setVisibility(View.GONE);
		lv_orderDetail.setVisibility(View.GONE);
		ll_submit.setVisibility(View.GONE);
	}
	
	public void onEventMainThread(OrderAction orderAction) {
		// initview
		if (order != null) {
			// tv_name.setText(getOrderString(order.getName()));
			tv_phone.setText(getOrderString(order.getTel()));
			tv_address.setText(getOrderString(order.getTrafficInfo()));
			tv_drama_name.setText(getOrderString(order.getName()));
			tv_drama_time.setText(DateTimeUtils.getTotalDayTime(Long.parseLong(myApplication.getStart_time())));
			//判断是否已全部打印 ,如果全部打印过 就变成返回
			int i = 0;
			for (Ticket ticket : order.getTicket_info()) {
				if ("1".equals(ticket.getIsPrint())) {
					i++;
				} else {
					break;
				}
			}
			if (i == order.getTicket_info().size()) {
				isAllPrinted = true;
				tv_submit.setText("返回");
			}else{
				tv_submit.setText("提交");
			}
			
			// init listview
			if (order.getTicket_info() == null) {
				order.setTicket_info(new ArrayList<Ticket>());
			}
			orderAdapter = new OrderAdapter(this, order.getTicket_info());
			lv_orderDetail.setAdapter(orderAdapter);
			
		}
	}
	
	@ItemClick
	void lv_orderDetailItemClicked(Ticket ticket) {
		ticket.setChecked(!ticket.isChecked());
		
		orderAdapter.notifyDataSetChanged();
	}
	
	private String getOrderString(String str) {
		if (TextUtils.isEmpty(str)) {
			return "";
			
		} else {
			return str;
		}
		
	}
	
	@Click({R.id.ll_submit, R.id.tv_submit})
	void onSubmit() {
		if (!isAllPrinted) {
			boolean hasSelected = false;
			StringBuffer sb = new StringBuffer();
			if (order.getTicket_info() != null) {
				for (Ticket ticket : order.getTicket_info()) {
					if (ticket.isSelectd()) {
						hasSelected = true;
						LogUtil.i(TAG, "id =" + ticket.getId());
						sb.append(ticket.getId() + ",");
						
					}
					
				}
				
			}
			if (hasSelected) {
				if (sb.length() > 1) {
					sb.deleteCharAt(sb.length() - 1);
					submitTicket(sb.toString());
					
				}
				
			} else {
				ToastUtils.showShortToast(this, "请选择要提交的票");
			}
		} else {
			Return(null);
			
		}
		
	}
	
	private void submitTicket(String sb) {
		
		VolleyMyListener myCityListener = new VolleyMyListener() {
			
			@Override
			public void parseJSONObject(JSONObject jsonObject) {
				Map<String, String> map = getReturnSource(jsonObject);
				
				if (Constants.HTTP_STATUS_OK.equals(map.get(Constants.HTTP_STATUS))) {
					ToastUtils.showShortToast(OrderActivity.this, "验证成功");
					Reflesh(null);
				} else {
					ToastUtils.showLongToast(OrderActivity.this, map.get(Constants.HTTP_MESSAGE));
					
				}
				
			}
			
			@Override
			public void getAccessError(String errorLog) {
				ToastUtils.showLongToast(OrderActivity.this, errorLog);
				post(new OrderAction());
			}
		};
		String url_path =
			Constants.URL_PATH + Constants.PATH_SUBMIT_TICKETS + "?tkt_id=" + sb + "&order_no=" + order.getOrder_no();
		volleyTools.getJSONByVolley(Request.Method.GET, url_path, null, myCityListener);
	}
	
	/**
	 * <pre>
	 * 参数说明:@param view
	 * 返回类型:@return void
	 * 方法说明:返回功能
	 * 修改历史:
	 *    修改人员:mac 修改日期:2015年1月29日 下午2:46:22
	 *    修改目的:
	 * </pre>
	 */
	public void Return(View view) {
		setResult(0);
		finish();
	}
	
	/**
	 * <pre>
	 * 参数说明:@param view
	 * 返回类型:@return void
	 * 方法说明:刷新页面
	 * 修改历史:
	 *    修改人员:mac 修改日期:2015年1月29日 下午2:46:33
	 *    修改目的:
	 * </pre>
	 */
	public void Reflesh(View view) {
		orders = null;
		order = null;
		loadData();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_0:
				ToastUtils.showShortToast(this, "将清空选中票");
				// int count_2 = 2;
				// selectTicketByKey(count_2);
				clearTiecketByKey();
				break;
			case KeyEvent.KEYCODE_1:
				ToastUtils.showShortToast(this, "将选中第1张票");
				int count_1 = 1;
				selectTicketByKey(count_1);
				
				break;
			case KeyEvent.KEYCODE_2:
				ToastUtils.showShortToast(this, "将选中前2张票");
				int count_2 = 2;
				selectTicketByKey(count_2);
				break;
			case KeyEvent.KEYCODE_3:
				ToastUtils.showShortToast(this, "将选中前3张票");
				int count_3 = 3;
				selectTicketByKey(count_3);
				
				break;
			case KeyEvent.KEYCODE_4:
				ToastUtils.showShortToast(this, "将选中前4张票");
				int count_4 = 4;
				selectTicketByKey(count_4);
				
				break;
			case KeyEvent.KEYCODE_5:
				ToastUtils.showShortToast(this, "将选中前5张票");
				int count_5 = 5;
				selectTicketByKey(count_5);
				
				break;
			case KeyEvent.KEYCODE_6:
				ToastUtils.showShortToast(this, "将选中前6张票");
				int count_6 = 6;
				selectTicketByKey(count_6);
				
				break;
			case KeyEvent.KEYCODE_7:
				ToastUtils.showShortToast(this, "将选中前7张票");
				int count_7 = 7;
				selectTicketByKey(count_7);
				
				break;
			case KeyEvent.KEYCODE_8:
				ToastUtils.showShortToast(this, "将选中前8张票");
				int count_8 = 8;
				selectTicketByKey(count_8);
				
				break;
			case KeyEvent.KEYCODE_9:
				ToastUtils.showShortToast(this, "将选中前9张票");
				int count_9 = 9;
				selectTicketByKey(count_9);
				
				break;
			
			default:
				break;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	private void selectTicketByKey(int count) {
		clearTiecketByKey();
		for (int i = 0; i < count; i++) {
			if (order.getTicket_info().size() > i) {
				if (!"1".equals(order.getTicket_info().get(i).getIsPrint())) {
					order.getTicket_info().get(i).setSelectd(true);
				}
			}
			
		}
		orderAdapter.notifyDataSetChanged();
	}
	
	private void clearTiecketByKey() {
		for (int i = 0; i < order.getTicket_info().size(); i++) {
			if (order.getTicket_info().size() > i) {
				if (!"1".equals(order.getTicket_info().get(i).getIsPrint())) {
					order.getTicket_info().get(i).setSelectd(false);
				}
			}
			
		}
		orderAdapter.notifyDataSetChanged();
	}
	
	private void sortTickets() {
		if (order.getTicket_info() != null) {
			Collections.sort(order.getTicket_info(), new Comparator<Ticket>() {
				
				@Override
				public int compare(Ticket lhs, Ticket rhs) {
					String isPrint_lhs_String = lhs.getIsPrint();
					String isPrint_rhs_String = rhs.getIsPrint();
					if (TextUtils.isEmpty(isPrint_lhs_String) || !Utils.isNum(isPrint_lhs_String)) {
						isPrint_lhs_String = "0";
					}
					if (TextUtils.isEmpty(isPrint_rhs_String) || !Utils.isNum(isPrint_rhs_String)) {
						isPrint_rhs_String = "0";
					}
					int isPrint_lhs = Integer.parseInt(isPrint_lhs_String);
					int isPrint_rhs = Integer.parseInt(isPrint_rhs_String);
					if (isPrint_lhs > isPrint_rhs) {
						return 1;
						
					} else if (isPrint_lhs < isPrint_rhs) {
						return -1;
					}
					return 0;
				}
				
			});
		}
	}
}
