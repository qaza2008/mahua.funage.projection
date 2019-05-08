package com.example.mahua.funage.projection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.mahua.funage.action.OrderListAction;
import com.example.mahua.funage.adapter.OrderListAdapter;
import com.example.mahua.funage.application.MyApplication;
import com.example.mahua.funage.domain.Order;
import com.example.mahua.funage.utils.Constants;
import com.example.mahua.funage.utils.JsonTools;
import com.example.mahua.funage.utils.ToastUtils;
import com.example.mahua.funage.utils.VolleyMyListener;
import com.example.mahua.funage.utils.VolleyTools;

import de.greenrobot.event.EventBus;

@EActivity(R.layout.activity_order_list)
public class OrderListActivity extends BaseActivity {
	
	@ViewById
	TextView tv_no_data;
	
	@ViewById
	ListView lv_orderDetail;
	
	@Bean
	VolleyTools volleyTools;
	
	List<Order> orders;
	
	OrderListAdapter adapter;
	
	@App
	MyApplication myApplication;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		
	}
	
	String no;
	
	String type;
	
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
		
		VolleyMyListener myCityListener = new VolleyMyListener() {
			
			@Override
			public void parseJSONObject(JSONObject jsonObject) {
				Map<String, String> map = getReturnSource(jsonObject);
				// 200 成功
				// 201 参数错误
				// 203 找不到此票或此票已打
				// 205 找不到订单信息
				// 206 换票类型错误
				String status = map.get(Constants.HTTP_STATUS);
				if (Constants.HTTP_STATUS_OK.equals(map.get(Constants.HTTP_STATUS))) {
					showData();
					orders = JsonTools.getCollListFromJson(map.get(Constants.HTTP_DATA), Order.class);
					if (orders == null) {
						orders = new ArrayList<Order>();
					}
					
					post(new OrderListAction());
				} else if ("201".equals(status)) {
					ToastUtils.showLongToast(OrderListActivity.this, "参数错误");
					showNoData();
					
				} else if ("203".equals(status)) {
					ToastUtils.showLongToast(OrderListActivity.this, "找不到此票或此票已打");
					showNoData();
					
				} else if ("205".equals(status)) {
					ToastUtils.showLongToast(OrderListActivity.this, "找不到订单信息");
					showNoData();
					
				} else if ("206".equals(status)) {
					ToastUtils.showLongToast(OrderListActivity.this, "换票类型错误");
					showNoData();
					
				}
				
			}
			
			private void showData() {
				tv_no_data.setVisibility(View.GONE);
				lv_orderDetail.setVisibility(View.VISIBLE);
			}
			
			private void showNoData() {
				tv_no_data.setVisibility(View.VISIBLE);
				lv_orderDetail.setVisibility(View.GONE);
			}
			
			@Override
			public void getAccessError(String errorLog) {
				ToastUtils.showLongToast(OrderListActivity.this, errorLog);
				
				post(new OrderListAction());
			}
		};
		String url_path =
			Constants.URL_PATH + Constants.PATH_LOAD_ORDER + "?no=" + no + "&type=" + type + "&drama_id="
				+ myApplication.getDrama_id();
		System.out.println("url_path ::  " + url_path);
		
		volleyTools.getJSONByVolley(Request.Method.GET, url_path, null, myCityListener);
	}
	
	public void onEventMainThread(OrderListAction orderAction) {
		// initview
		if (orders != null) {
			adapter = new OrderListAdapter(this, orders, myApplication.getStart_time());
			lv_orderDetail.setAdapter(adapter);
		}
	}
	
	@ItemClick
	void lv_orderDetailItemClicked(int position) {
		
		Order order = orders.get(position);
		Intent intent = new Intent(getIntent());
		intent.putExtra("order", order);
		intent.putExtra("position", position);
		intent.setClass(this, OrderActivity_.class);
		// startActivity(intent);
		startActivityForResult(intent, 0);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Reflesh(null);
	}
	
	public void Return(View view) {
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
		initData();
	}
}
