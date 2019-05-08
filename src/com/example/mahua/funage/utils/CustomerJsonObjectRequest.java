package com.example.mahua.funage.utils;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

/**
 * <pre>
 * 应用名称:  LoginActivity
 * 包名称:    com.shishike.calm.app.util
 * 类名称:    CustomerJsonObjectRequest.java  
 * 描述信息:   
 * 版权:      Copyright (c)2014  
 * 公司:     北京时时客科技有限责任公司
 * 作者:      mac
 * 版本:      1.0  
 * 创建时间:  2014年9月16日 下午5:08:54  
 *  
 * 更改历史:  
 * 日期         作者      版本     描述说明  
 * --------------------------------------------
 * 2014年9月16日    mac   1.1      1.1 Version
 * </pre>
 */
public class CustomerJsonObjectRequest extends JsonObjectRequest {
	
	private static final int SOCKET_TIMEOUT = 10 * 1000;
	
	public CustomerJsonObjectRequest(int method, String url, JSONObject jsonRequest, Listener<JSONObject> listener,
		ErrorListener errorListener) {
		super(method, url, jsonRequest, listener, errorListener);
	}
	
	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
			// System.out.println("response.data" +
			// response.data);
			// StringBuffer sb = new StringBuffer();
			// for(int i = 0;i<response.data.length;i++){
			// sb.append(i);
			// }
			// System.out.println("sb" + sb.toString());
			//
			String jsonString = new String(response.data, Charset.forName("utf8"));
			// System.out.println("jsonString1 " +
			// jsonString);
			// jsonString = unicode2String(jsonString);
			// System.out.println("jsonString2  " +
			// jsonString);
			return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}
	
	public static String unicode2String(String unicode) {
		
		StringBuffer string = new StringBuffer();
		
		String[] hex = unicode.split("\\\\u");
		
		for (int i = 1; i < hex.length; i++) {
			
			// 转换出每一个代码点
			int data = Integer.parseInt(hex[i], 16);
			
			// 追加成string
			string.append((char)data);
		}
		
		return string.toString();
	}
	
	private void removeBom(NetworkResponse response) {
		byte[] bytes = response.data;
		byte[] strs = new byte[bytes.length - 3];// 去掉该内容的bom标示
		int j = 0;
		if (bytes[0] == -17 && bytes[1] == -69 && bytes[2] == -65) {// bom文件格式头字节
			for (int i = 3; i < bytes.length; i++) {
				strs[j] = bytes[i];
				j++;
			}
		}
	}
	
	public static final String removeBOM(String data) {
		if (TextUtils.isEmpty(data)) {
			return data;
		}
		if (data.startsWith("\ufeff")) {
			return data.substring(1);
		} else {
			return data;
		}
	}
	
	/**
	 * 设置字符集为UTF-8,并采用gzip压缩传输
	 */
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Charset", "UTF-8");
		// headers.put("Content-Type",
		// "application/x-javascript");
		headers.put("Content-Type", "text/html");
		// headers.put("Accept-Encoding", "gzip,deflate");
		
		return headers;
	}
	
	/**
	 * 
	 超时设置：重写getRetryPolicy方法 重试失败的请求，自定义请求超时
	 * Volley中没有指定的方法来设置请求超时时间，可以设置RetryPolicy
	 * 来变通实现。DefaultRetryPolicy类有个initialTimeout参数
	 * ，可以设置超时时间。要确保最大重试次数为1，以保证超时后不重新请求。 Setting Request
	 * Timeout request.setRetryPolicy(new
	 * DefaultRetryPolicy(20 * 1000, 1, 1.0f));
	 * 如果你想失败后重新请求（因超时
	 * ），您可以指定使用上面的代码，增加重试次数。注意最后一个参数，它允许你指定一个退避乘数可以用来实现
	 * “指数退避”来从RESTful服务器请求数据。
	 */
	
	@Override
	public RetryPolicy getRetryPolicy() {
		RetryPolicy retryPolicy = new DefaultRetryPolicy(SOCKET_TIMEOUT, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		return retryPolicy;
	}
	
}
