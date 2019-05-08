package com.example.mahua.funage.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonTools {
	
	public JsonTools() {
		// TODO Auto-generated constructor stub
	}
	
	// 可以将任意类型的数据转化为json字符串
	public static String getJsonFromGenerics(Object obj) {
		String jsonString = null;
		Gson gson = new Gson();
		jsonString = gson.toJson(obj);
		
		return jsonString;
	}
	
	public static <T> T getCollFromJson(String jsonString, Type type) {
		Gson gson = new Gson();
		return gson.fromJson(jsonString, type);
	}
	
	public static <E> List<E> getCollListFromJson(String jsonString, Type type) {
		List<E> list = new ArrayList<E>();
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(jsonString);
			for (int i = 0; i < jsonArray.length(); i++) {
				list.add((E)(getCollFromJson(jsonArray.optJSONObject(i).toString(), type)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	// 生成json数据 将 Map<String, Object> 生成成json字符串
	public static String getJsonStringFromMap(Map<String, Object> map) {
		
		Gson gson = new Gson();
		String myjsonString = gson.toJson(map, new TypeToken<Map<String, Object>>() {
		}.getType());
		return myjsonString;
		
	}
	
	public static String getURLStringFromMap(Map<String, Object> params) {
		StringBuffer URLString = new StringBuffer("?");
		for (Map.Entry<String, Object> me : params.entrySet()) {
			URLString.append(me.getKey() + "=" + me.getValue() + "&");
		}
		URLString.deleteCharAt(URLString.length() - 1);
		return URLString.toString();
	}
	
	// 生成json数据 将 Map<String, String> 生成成json字符串
	public static String myJsonString(Map<String, String> map) {
		
		Gson gson = new Gson();
		String myjsonString = gson.toJson(map, new TypeToken<Map<String, String>>() {
		}.getType());
		return myjsonString;
		
	}
	
	public static String toJsonFromList_Map(List<Map<String, String>> list) {
		
		Gson gson = new Gson();
		String myjsonString = gson.toJson(list, new TypeToken<List<Map<String, String>>>() {
		}.getType());
		return myjsonString;
	}
	
	public static List<Map<String, String>> toList_MapFromJson(String json) {
		
		Gson gson = new Gson();
		List<Map<String, String>> list = gson.fromJson(json, new TypeToken<List<Map<String, String>>>() {
		}.getType());
		return list;
	}
	
	// 将json数据 解析成Map<String, String>
	public static Map<String, String> myGetJsonString(String jsonString) {
		
		Map<String, String> map = new HashMap<String, String>();
		Gson gson = new Gson();
		
		map = gson.fromJson(jsonString, new TypeToken<Map<String, String>>() {
		}.getType());
		
		return map;
		
	}
	
	private static String parseJsonString(String key, String jsonString) {
		
		String result = "";
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONObject jsonObject2 = jsonObject.getJSONObject(key);
			
			Iterator<String> it = jsonObject2.keys();
			String key1 = "";
			if (it.hasNext()) {
				key1 = it.next();
			}
			result = jsonObject2.getString(key1);
			// result = jsonObject.getString(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
		
	}
	
	// 将json数据解析成List<Map<String, Object>>
	public static List<Map<String, Object>> parseJsonStringList(String jsonString) {
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Gson gson = new Gson();
		list = gson.fromJson(jsonString, new TypeToken<List<Map<String, Object>>>() {
		}.getType());
		
		return list;
		
	}
	
	// 将json数据解析成List<String>
	public static List<String> parseJsonList(String key, String jsonString) {
		
		List<String> list = new ArrayList<String>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			String jsonString2 = jsonObject.getString(key);
			
			Gson gson = new Gson();
			list = gson.fromJson(jsonString2, new TypeToken<List<String>>() {
			}.getType());
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * 
	 * 
	 * 
	 * 格式： { "pageEntry": [ { "responseCode": null,
	 * "repsonseMessages": null }, { "responseCode": null,
	 * "repsonseMessages": null } ] }
	 * 
	 * 
	 * @param cursor
	 * @return
	 */
	public static String parseCursor(Cursor cursor) {
		String jsonString = null;
		JSONArray ja = new JSONArray();
		while (cursor.moveToNext()) {
			Map<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				map.put(cursor.getColumnName(i), cursor.getString(i));
			}
			JSONObject jo = new JSONObject(map);
			ja.put(jo);
			
		}
		JSONObject jo2 = new JSONObject();
		try {
			jo2.put("pageEntry", ja);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jo2.toString();
	}
	
	// cursor 表名
	public static String parseSingleCursor(Cursor cursor, String tableName) {
		JSONObject ja = null;
		while (cursor.moveToNext()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("repsonseMessages", tableName);
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				map.put(cursor.getColumnName(i), cursor.getString(i));
			}
			ja = new JSONObject(map);
		}
		JSONObject jo2 = new JSONObject();
		try {
			jo2.put("pageEntry", ja);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jo2.toString();
		
	}
	
	private static void setter(Object obj, String att, Object value, Class<?> type) {
		try {
			Method method = obj.getClass().getDeclaredMethod("set" + att, type);
			method.invoke(obj, value);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
}
