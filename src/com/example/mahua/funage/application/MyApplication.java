package com.example.mahua.funage.application;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.SystemService;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiManager;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.google.code.microlog4android.config.PropertyConfigurator;

@EApplication
public class MyApplication extends Application {
	
	private RequestQueue mRequestQueue;
	
	private String rootPath;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		PropertyConfigurator.getConfigurator(this).configure();
		
		handlerThread = new HandlerThread("handlerThread");
		handlerThread.start();
		setRootPath();
	}
	
	@SystemService
	WifiManager wifiManager;
	
	private static MyApplication mInstance;
	
	public static MyApplication getInstance() {
		return mInstance;
	}
	
	private String city_id;
	
	private String theater_id;
	
	private String drama_id;
	
	private String start_time;
	
	private String drama_name;
	
	/**
	 * <pre>
	 * 参数说明:@param city_id
	 * 参数说明:@param theater_id
	 * 参数说明:@param drama_id
	 * 参数说明:@param start_time
	 * 参数说明:@param drama_name
	 * 返回类型:@return void
	 * 方法说明:
	 * 修改历史:
	 *    修改人员:mac 修改日期:2015年2月4日 下午11:27:38
	 *    修改目的:
	 * </pre>
	 */
	public void setLoginInfo(String city_id, String theater_id, String drama_id, String start_time, String drama_name) {
		this.city_id = city_id;
		this.theater_id = theater_id;
		this.drama_id = drama_id;
		this.start_time = start_time;
		this.drama_name = drama_name;
	}
	
	public void clearLoginInfo() {
		this.city_id = "";
		this.theater_id = "";
		this.drama_id = "";
		this.start_time = "";
		this.drama_name = "";
	}
	
	public String getCity_id() {
		return city_id;
	}
	
	public void setCity_id(String city_id) {
		this.city_id = city_id;
	}
	
	public String getTheater_id() {
		return theater_id;
	}
	
	public void setTheater_id(String theater_id) {
		this.theater_id = theater_id;
	}
	
	public String getDrama_id() {
		return drama_id;
	}
	
	public void setDrama_id(String drama_id) {
		this.drama_id = drama_id;
	}
	
	public String getStart_time() {
		return start_time;
	}
	
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	
	public String getDrama_name() {
		return drama_name;
	}

	public void setDrama_name(String drama_name) {
		this.drama_name = drama_name;
	}

	public RequestQueue getRequestQueue() {
		// lazy initialize the request queue, the queue
		// instance will be
		// created when it is accessed for the first time
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}
		System.out.println("getRequestQueue" + mRequestQueue);
		
		return mRequestQueue;
	}
	
	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
	
	/**
	 * Adds the specified request to the global queue, if
	 * tag is specified then it is used else Default TAG is
	 * used.
	 * 
	 * @param req
	 * @param tag
	 */
	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? "der" : tag);
		VolleyLog.d("Adding request to queue: %s", req.getUrl());
		getRequestQueue().add(req);
	}
	
	private HandlerThread handlerThread;
	
	public String getRootPath() {
		return rootPath;
	}
	
	public HandlerThread getHandlerThread() {
		return handlerThread;
	}
	
	private void setRootPath() {
		PackageManager manager = this.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			rootPath = info.applicationInfo.dataDir;
			Log.i("rootPath", "################rootPath=" + rootPath);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
