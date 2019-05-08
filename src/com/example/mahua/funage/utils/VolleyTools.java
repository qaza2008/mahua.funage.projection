package com.example.mahua.funage.utils;

import java.util.Timer;
import java.util.TimerTask;

import org.androidannotations.annotations.EBean;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.NetworkImageView;
import com.example.mahua.funage.application.MyApplication;

/**
 * <pre>
 * 应用名称:  LoginActivity
 * 包名称:    com.shishike.calm.app.util
 * 类名称:    VolleyTools.java  
 * 描述信息:   
 * 版权:      Copyright (c)2014  
 * 公司:     北京时时客科技有限责任公司
 * 作者:      wangs
 * 版本:      1.0  
 * 创建时间:  2014年9月16日 下午4:31:50  
 *  
 * 更改历史:  
 * 日期         作者      版本     描述说明  
 * --------------------------------------------
 * 2014年9月16日    wangs  1.1      1.1 Version
 * </pre>
 */
@EBean
public class VolleyTools {
	
	private Context mContext;
	
	MyApplication myApplication;
	
	private RequestQueue requestQueue;
	
	public static final String DEFAULT_TITLE = "正在请求数据";
	
	public static final String DEFAULT_MESSAGE = "正在提交打印数据到打印服务器,请等待...";
	
	private DialogHelper dialogHelper;
	public VolleyTools(Context context) {
		this.mContext = context;
		myApplication = MyApplication.getInstance();
		requestQueue = myApplication.getRequestQueue();
		dialogHelper = new DialogHelper(context);
		// System.out.println("myApplication " +
		// myApplication.getPackageName());
		
	}
	
	// @AfterInject
	// void init() {
	// requestQueue = myApplication.getRequestQueue();
	// System.out.println("requestQueue init" + requestQueue
	// == null);
	// LogUtil.i("volley ", "requestQueue init" +
	// (requestQueue == null));
	// }
	
	private String title = DEFAULT_TITLE;
	
	private String message = DEFAULT_MESSAGE;
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	private Handler tagHandler;
	
	/**
	 * 利用Volley获取JSON数据
	 * 
	 */
	/**
	 * <pre>
	 * 参数说明:@param requestMethod   Request.Method.GET  Request.Method.Post
	 * 参数说明:@param JSONDataUrl  
	 * 参数说明:@param jsonObject Post  	jsonRequest A JSONObject to post with the request. Null is allowed and indicates no parameters will be posted along with request.
	 * 返回类型:@return void
	 * 方法说明:
	 * 修改历史:
	 *    修改人员:wangs 修改日期:2014年9月6日 上午11:31:18
	 *    修改目的:
	 * </pre>
	 */
	public void getJSONByVolley(int requestMethod, String JSONDataUrl, JSONObject jsonObject,
		final VolleyMyListener mVolleyMyListener) {
//		LogUtil.i("volley", "JSONDataUrl" + JSONDataUrl + "   " + jsonObject.toString());
		// requestQueue = myApplication.getRequestQueue();
		System.out.println(JSONDataUrl);
//		final ProgressDialog waitDialog = ProgressDialog.show(mContext, title, message);
//		waitDialog.setCancelable(false);
//		waitDialog.setCanceledOnTouchOutside(false);
		final Dialog waitDialog = dialogHelper.getWaitDialog();
		//
		final Timer timer = new Timer();
		
		final CustomerJsonObjectRequest jsonObjectRequest =
			new CustomerJsonObjectRequest(requestMethod, JSONDataUrl, jsonObject, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					LogUtil.i("VolleyTools", "response=" + response.toString());
					mVolleyMyListener.parseJSONObject(response);
					if (timer != null) {
						timer.cancel();
					}
					if (waitDialog != null && waitDialog.isShowing()) {
						waitDialog.dismiss();
					}
				}
				
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError arg0) {
					if (waitDialog != null && waitDialog.isShowing()) {
						waitDialog.dismiss();
					}
					if (timer != null) {
						timer.cancel();
					}
					mVolleyMyListener.getAccessError( "服务器访问失败,请重试");
//					ToastUtils.showShortToast(mContext, "服务器访问失败,请重试");
					tagHandler.sendEmptyMessage(0);
					System.out.println("sorry,Error" + arg0.getMessage());
				}
			}
			
			);
		tagHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				System.out.println("retryCount" + jsonObjectRequest.getRetryPolicy().getCurrentRetryCount());
			}
			
		};
		waitDialog.show();
		jsonObjectRequest.setTag("printerServer");
		final Handler mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (0 == msg.what) {
					if (mContext != null && !mVolleyMyListener.isVolleyResponsed) {
						LogUtil.i("VolleyTools", "打印超时 5s,取消服务器打印,自己打印");
						jsonObjectRequest.deliverError(new VolleyError("连接超时,请重试"));
						if (requestQueue != null) {
							requestQueue.cancelAll("printerServer");
						}
					}
				}
			};
			
		};
		
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				mHandler.sendEmptyMessage(0);
			}
		};
		timer.schedule(task, 10 * 1000);
		System.out.println("requestQueue" + (requestQueue == null));
		requestQueue.add(jsonObjectRequest);
		
	}
	
	/**
	 * 利用Volley异步加载图片
	 * 
	 * 注意方法参数: getImageListener(ImageView view, int
	 * defaultImageResId, int errorImageResId)
	 * 第一个参数:显示图片的ImageView 第二个参数:默认显示的图片资源
	 * 第三个参数:加载错误时显示的图片资源
	 */
	public void loadImageByVolley(String imageUrl, ImageView mImageView, int defaultImageResId, int errorImageResId) {
		final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(20);
		ImageCache imageCache = new ImageCache() {
			@Override
			public void putBitmap(String key, Bitmap value) {
				lruCache.put(key, value);
				
			}
			
			@Override
			public Bitmap getBitmap(String key) {
				return lruCache.get(key);
			}
		};
		ImageLoader imageLoader = new ImageLoader(requestQueue, imageCache);
		ImageListener listener = ImageLoader.getImageListener(mImageView, defaultImageResId, errorImageResId);
		imageLoader.get(imageUrl, listener);
	}
	
	/**
	 * 利用NetworkImageView显示网络图片
	 */
	public void showImageByNetworkImageView(String imageUrl, NetworkImageView mNetworkImageView) {
		final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(20);
		ImageCache imageCache = new ImageCache() {
			@Override
			public void putBitmap(String key, Bitmap value) {
				lruCache.put(key, value);
			}
			
			@Override
			public Bitmap getBitmap(String key) {
				return lruCache.get(key);
			}
		};
		ImageLoader imageLoader = new ImageLoader(requestQueue, imageCache);
		mNetworkImageView.setTag("url");
		mNetworkImageView.setImageUrl(imageUrl, imageLoader);
	}
}
