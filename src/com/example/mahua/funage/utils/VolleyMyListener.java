package com.example.mahua.funage.utils;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

/**
 * <pre>
 * 应用名称:  LoginActivity
 * 包名称:    com.shishike.calm.app.util
 * 类名称:    VolleyMyListener.java  
 * 描述信息:   
 * 版权:      Copyright (c)2014  
 * 公司:     北京时时客科技有限责任公司
 * 作者:      mac
 * 版本:      1.0  
 * 创建时间:  2014年9月16日 下午5:09:03  
 *  
 * 更改历史:   
 * 日期         作者      版本     描述说明  
 * --------------------------------------------
 * 2014年9月16日    mac   1.1      1.1 Version
 * </pre>
 */
public abstract class VolleyMyListener {
	public boolean isVolleyResponsed = false;
	
	public abstract void parseJSONObject(JSONObject jsonObject);
	
	public Map<String, String> getReturnSource(JSONObject jsonObject) {
		System.out.println("jsonObject " + jsonObject.toString());
		Map<String, String> returnSource = new HashMap<String, String>();
		String status = jsonObject.optString(Constants.HTTP_STATUS);
		String message = jsonObject.optString(Constants.HTTP_MESSAGE);
		String data = jsonObject.optString(Constants.HTTP_DATA);
		// JSONArray data =
		// jsonObject.optJSONArray(Constants.HTTP_DATA);
		returnSource.put(Constants.HTTP_STATUS, status);
		returnSource.put(Constants.HTTP_MESSAGE, message);
		returnSource.put(Constants.HTTP_DATA, data);
		return returnSource;
	}
	
	public abstract void getAccessError(String errorLog);
	
}
