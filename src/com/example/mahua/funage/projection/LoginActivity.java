package com.example.mahua.funage.projection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Debug;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.mahua.funage.action.FunctionAction;
import com.example.mahua.funage.adapter.CustomerSpinnerAdapter;
import com.example.mahua.funage.api.MyPrefs_;
import com.example.mahua.funage.application.MyApplication;
import com.example.mahua.funage.domain.City;
import com.example.mahua.funage.domain.Drama;
import com.example.mahua.funage.domain.Theater;
import com.example.mahua.funage.event.ItemListEvent;
import com.example.mahua.funage.update.UpdateManager;
import com.example.mahua.funage.utils.AndroidUtil;
import com.example.mahua.funage.utils.Constants;
import com.example.mahua.funage.utils.DateTimeUtils;
import com.example.mahua.funage.utils.JsonTools;
import com.example.mahua.funage.utils.LogUtil;
import com.example.mahua.funage.utils.ToastUtils;
import com.example.mahua.funage.utils.VolleyMyListener;
import com.example.mahua.funage.utils.VolleyTools;

import de.greenrobot.event.EventBus;

@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity {
	
	public static final String TAG = LoginActivity.class.getSimpleName();
	
	public static enum ItemType {
		
		city, theater, drama
	}
	
	@ViewById
	Spinner spin_city;
	
	@ViewById
	Spinner spin_theater;
	
	@ViewById
	Spinner spin_drama;
	
	@Bean
	VolleyTools volleyTools;
	
	@Bean
	AndroidUtil androidUtil;
	
	@App
	MyApplication myApplication;
	
	ArrayAdapter<String> city_adapter;
	
	ArrayAdapter<String> theater_adapter;
	
	ArrayAdapter<String> drama_adapter;
	
	List<City> city_list;
	
	List<Theater> theater_list;
	
	List<Drama> drama_list;
	
	@Pref
	MyPrefs_ sp;
	
	@ViewById
	TextView tv_padno;
	
	@ViewById
	TextView tv_padvalidate;
	
	@ViewById
	TextView tv_name;
	
	@AfterViews
	public void init() {
		// ToastUtils.showLongToast(this, " net work " +
		// androidUtil.isNetworkActived());
		// 手持机验证码
		StringBuffer sb = new StringBuffer();
		String[] propertys = {"ro.serialno"};
		for (String key : propertys) {
			String v = getAndroidOsSystemProperties(key);
			sb.append(v);
		}
		tv_padno.setText("设备编号:" + sb.toString());
		
		// 版本号
		String versionName = getVersionName(this);
		tv_name.setText("检票工具 v" + versionName);
		
		// 先看看是否检查过pad有效性
		// TODO
		// ----
		if (BuildConfig.DEBUG) {
			sb.setLength(0);
			sb.append("H4F6R14321002222");
		}
		// -----
		// initPadValidate(sb.toString());
		// updateVersion() ;
		initPhone();
	}
	
	private void initPhone() {
		updateVersion();
		// 有效的pad
		// 请求城市
		try {
			doPostRequest(ItemType.city);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	UpdateManager updateManager;
	
	void updateVersion() {
		updateManager = new UpdateManager(this);
		updateManager.checkUpdate();
		
	}
	
	/**
	 * 获取软件版本号
	 * 
	 * @param context
	 * @return
	 */
	private String getVersionName(Context context) {
		String versionName = "";
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionName =
				context.getPackageManager().getPackageInfo("com.example.mahua.funage.projection", 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}
	
	private void initPadValidate(String padNo) {
		if (!sp.checkValidated().get()) {
			checkValidated(padNo);
			tv_padvalidate.setText("未验证");
			tv_padvalidate.setTextColor(getResources().getColor(R.color.red));
			
		} else {
			if (sp.padValidate().get()) {
				tv_padvalidate.setText("已验证通过");
				tv_padvalidate.setTextColor(getResources().getColor(R.color.green));
				updateVersion();
				// 有效的pad
				// 请求城市
				try {
					doPostRequest(ItemType.city);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				tv_padvalidate.setText("验证未通过");
				tv_padvalidate.setTextColor(getResources().getColor(R.color.red));
				// 无效的pad
				checkValidated(padNo);
			}
		}
	}
	
	private void doPostRequest(ItemType action) throws JSONException {
		switch (action) {
			case city:
				// 获取spinner 数据 ,比如城市,剧院,时间
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("act", "city");
				VolleyMyListener myCityListener = new VolleyMyListener() {
					
					@Override
					public void parseJSONObject(JSONObject jsonObject) {
						Map<String, String> map = getReturnSource(jsonObject);
						System.out.println("map" + map);
						if (Constants.HTTP_STATUS_OK.equals(map.get(Constants.HTTP_STATUS))) {
							city_list = JsonTools.getCollListFromJson(map.get(Constants.HTTP_DATA), City.class);
							ArrayList<String> cityNames = new ArrayList<String>();
							int position = 0;
							String city_id = sp.city_id().getOr("");
							for (int i = 0; i < city_list.size(); i++) {
								City city = city_list.get(i);
								cityNames.add(city.getRegion_name());
								if (city_id.equals(city.getRegion_id())) {
									position = i;
								}
							}
							
							initCitySpinner(cityNames, position);
							// post(new ItemListEvent(datas,
							// ItemType.city));
						} else {
							if (clearList(city_list)) {
								ArrayList<String> cityNames = new ArrayList<String>();
								for (City city : city_list) {
									cityNames.add(city.getRegion_name());
								}
								initCitySpinner(cityNames, 0);
							} else {
								initCitySpinner(new ArrayList<String>(), 0);
							}
							ToastUtils.showLongToast(LoginActivity.this, "请求参数错误,请重试");
						}
						
					}
					
					@Override
					public void getAccessError(String errorLog) {
						ToastUtils.showLongToast(LoginActivity.this, errorLog);
						
					}
				};
				volleyTools.getJSONByVolley(Request.Method.GET, Constants.URL_PATH + Constants.PATH_LOAD_CITY
					+ "?act=city", null, myCityListener);
				// volleyTools.getJSONByVolley(Request.Method.POST,
				// Constants.URL_PATH +
				// Constants.PATH_LOAD_CITY ,
				// jsonObject,
				// myListener);
				break;
			case theater:
				// JSONObject jsonObject = new JSONObject();
				// jsonObject.put("act", "city");
				VolleyMyListener myTheaterListener = new VolleyMyListener() {
					
					@Override
					public void parseJSONObject(JSONObject jsonObject) {
						Map<String, String> map = getReturnSource(jsonObject);
						System.out.println("map" + map);
						if (Constants.HTTP_STATUS_OK.equals(map.get(Constants.HTTP_STATUS))) {
							theater_list = JsonTools.getCollListFromJson(map.get(Constants.HTTP_DATA), Theater.class);
							if (theater_list == null) {
								theater_list = new ArrayList<Theater>();
								
							}
							int position = 0;
							String theater_id = sp.theater_id().getOr("");
							ArrayList<String> theaterNames = new ArrayList<String>();
							for (int i = 0; i < theater_list.size(); i++) {
								Theater theater = theater_list.get(i);
								theaterNames.add(theater.getName());
								if (theater_id.equals(theater.getId())) {
									position = i;
								}
							}
							initTheaterSpinner(theaterNames, position);
							// post(new ItemListEvent(datas,
							// ItemType.city));
						} else {
							if (clearList(theater_list)) {
								ArrayList<String> theaterNames = new ArrayList<String>();
								for (Theater theater : theater_list) {
									theaterNames.add(theater.getName());
								}
								initTheaterSpinner(theaterNames, 0);
							} else {
								initTheaterSpinner(new ArrayList<String>(), 0);
							}
							ToastUtils.showLongToast(LoginActivity.this, "请求参数错误,请重试");
						}
						
					}
					
					@Override
					public void getAccessError(String errorLog) {
						ToastUtils.showLongToast(LoginActivity.this, errorLog);
						
					}
				};
				int city_position = spin_city.getSelectedItemPosition();
				String city_id = city_list.get(city_position).getRegion_id();
				String theater_path = Constants.URL_PATH + Constants.PATH_LOAD_CITY + "?act=theater&city_id=" + city_id;
				volleyTools.getJSONByVolley(Request.Method.GET, theater_path, null, myTheaterListener);
				break;
			case drama:
				VolleyMyListener myDramaListener = new VolleyMyListener() {
					
					@Override
					public void parseJSONObject(JSONObject jsonObject) {
						Map<String, String> map = getReturnSource(jsonObject);
						System.out.println("map" + map);
						
						if (Constants.HTTP_STATUS_OK.equals(map.get(Constants.HTTP_STATUS))) {
							drama_list = JsonTools.getCollListFromJson(map.get(Constants.HTTP_DATA), Drama.class);
							if (drama_list == null) {
								drama_list = new ArrayList<Drama>();
								
							}
							int position = 0;
							String drama_id = sp.drama_id().getOr("");
							ArrayList<String> dramaNames = new ArrayList<String>();
							for (int i = 0; i < drama_list.size(); i++) {
								Drama drama = drama_list.get(i);
								dramaNames.add(DateTimeUtils.getDateTime(Long.parseLong(drama.getStart_time())) + "\n"
									+ drama.getName());
								if (drama_id.equals(drama.getId())) {
									position = i;
									
								}
							}
							initDramaSpinner(dramaNames, position);
							// post(new ItemListEvent(datas,
							// ItemType.city));
							
						} else if (Constants.HTTP_STATUS_FAIL_201.equals(map.get(Constants.HTTP_STATUS))) {
							if (clearList(drama_list)) {
								ArrayList<String> dramaNames = new ArrayList<String>();
								for (Drama drama : drama_list) {
									dramaNames.add(drama.getName());
								}
								initDramaSpinner(dramaNames, 0);
							} else {
								initDramaSpinner(new ArrayList<String>(), 0);
							}
							ToastUtils.showLongToast(LoginActivity.this, "没有这个场次,请重新选择");
						} else {
							if (clearList(drama_list)) {
								ArrayList<String> dramaNames = new ArrayList<String>();
								for (Drama drama : drama_list) {
									dramaNames.add(drama.getName());
								}
								initDramaSpinner(dramaNames, 0);
							} else {
								initDramaSpinner(new ArrayList<String>(), 0);
							}
							ToastUtils.showLongToast(LoginActivity.this, "请求参数错误,请重试");
						}
					}
					
					@Override
					public void getAccessError(String errorLog) {
						ToastUtils.showLongToast(LoginActivity.this, errorLog);
						
					}
				};
				int city_position_ = spin_city.getSelectedItemPosition();
				String city_id_ = city_list.get(city_position_).getRegion_id();
				int theater_position = spin_theater.getSelectedItemPosition();
				String theater_id = theater_list.get(theater_position).getId();
				String drama_path =
					Constants.URL_PATH + Constants.PATH_LOAD_CITY + "?act=drama&city_id=" + city_id_ + "&theater_id="
						+ theater_id;
				volleyTools.getJSONByVolley(Request.Method.GET, drama_path, null, myDramaListener);
				break;
			
			default:
				break;
		}
	}
	
	private void initCitySpinner(ArrayList<String> cityNames, int index) {
		city_adapter = new ArrayAdapter<String>(LoginActivity.this, R.layout.row_spinner_arrow_filter, cityNames);
		city_adapter.setDropDownViewResource(R.layout.row_spinner);
		spin_city.setAdapter(city_adapter);
		spin_city.setSelection(index);
		spin_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				LogUtil.i(TAG, "spin_city  position=== " + position);
				// 请求剧院
				try {
					doPostRequest(ItemType.theater);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}
	
	private void initTheaterSpinner(ArrayList<String> theaterNames, int index) {
		theater_adapter = new ArrayAdapter<String>(LoginActivity.this, R.layout.row_spinner_arrow_filter, theaterNames);
		theater_adapter.setDropDownViewResource(R.layout.row_spinner);
		spin_theater.setAdapter(theater_adapter);
		spin_theater.setSelection(index);
		spin_theater.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				LogUtil.i(TAG, "spin_theater position=== " + position);
				// 请求剧院
				try {
					doPostRequest(ItemType.drama);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}
	
	private void initDramaSpinner(ArrayList<String> dramaNames, int index) {
		drama_adapter = new ArrayAdapter<String>(LoginActivity.this, R.layout.row_spinner_arrow_filter, dramaNames);
		drama_adapter.setDropDownViewResource(R.layout.row_spinner);
		spin_drama.setAdapter(drama_adapter);
		spin_drama.setSelection(index);
		spin_drama.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				LogUtil.i(TAG, "spin_drama position=== " + position);
				// // 请求场次
				// try {
				// doPostRequest(ItemType.drama);
				// } catch (JSONException e) {
				// e.printStackTrace();
				// }
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}
	
	@Click
	void bt_login(View view) {
		// 判断是否可以登录,当有场次时能登录
		
		if (spin_drama != null) {
			if (spin_drama.getAdapter() != null && spin_drama.getAdapter().getCount() > 0) {
				post(new FunctionAction());
			} else {
				ToastUtils.showLongToast(this, "请选择场次");
			}
		} else {
			ToastUtils.showLongToast(this, "请选择场次");
			
		}
		
	}
	
	public void onEventMainThread(FunctionAction action) {
		int city_position = spin_city.getSelectedItemPosition();
		String city_id = city_list.get(city_position).getRegion_id();
		int theater_position = spin_theater.getSelectedItemPosition();
		String theater_id = theater_list.get(theater_position).getId();
		int drama_position = spin_drama.getSelectedItemPosition();
		Drama drama = drama_list.get(drama_position);
		String start_time = drama.getStart_time();
		String drama_name = drama.getName();
		String drama_id = drama.getId();
		myApplication.setLoginInfo(city_id, theater_id, drama_id, start_time, drama_name);
		
		sp.city_id().put(city_id);
		sp.theater_id().put(theater_id);
		sp.drama_id().put(drama_id);
		
		Intent intent = new Intent(this, FunctionActivity_.class);
		startActivity(intent);
	}
	
	public void onEventMainThread(ItemListEvent event) {
		switch (event.getType()) {
			case city:
				
				CustomerSpinnerAdapter<City> citySpinnerAdapter =
					new CustomerSpinnerAdapter<City>(this, R.layout.row_spinner_arrow_filter, event.getItems(),
						ItemType.city);
				citySpinnerAdapter.setDropDownViewResource(R.layout.row_spinner);
				spin_city.setAdapter(citySpinnerAdapter);
				
				break;
			
			case theater:
				
				break;
			case drama:
				
				break;
			
			default:
				break;
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		volleyTools = new VolleyTools(this);
	}
	
	static Method systemProperties_get = null;
	
	static String getAndroidOsSystemProperties(String key) {
		String ret;
		try {
			systemProperties_get = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
			if ((ret = (String)systemProperties_get.invoke(null, key)) != null)
				return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return "";
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// EventBus.getDefault().unregister(this);
	}
	
	private boolean clearList(List list) {
		if (list != null) {
			list.clear();
			return true;
		} else {
			return false;
		}
	}
	
	private void checkValidated(final String padNo) {
		VolleyMyListener myCityListener = new VolleyMyListener() {
			
			@Override
			public void parseJSONObject(JSONObject jsonObject) {
				Map<String, String> map = getReturnSource(jsonObject);
				System.out.println("map" + map);
				sp.checkValidated().put(true);
				if (Constants.HTTP_STATUS_OK.equals(map.get(Constants.HTTP_STATUS))) {
					sp.padValidate().put(true);
					initPadValidate(padNo);
					
				} else {
					ToastUtils.showLongToast(LoginActivity.this, "pad验证未通过,请联系工作人员");
					sp.padValidate().put(false);
					tv_padvalidate.setText("验证未通过");
					tv_padvalidate.setTextColor(getResources().getColor(R.color.red));
				}
				
			}
			
			@Override
			public void getAccessError(String errorLog) {
				ToastUtils.showLongToast(LoginActivity.this, errorLog);
				
			}
		};
		volleyTools.getJSONByVolley(Request.Method.GET,
			Constants.URL_PATH + Constants.PATH_AUTH + "?code=" + padNo,
			null,
			myCityListener);
	}
	
}
