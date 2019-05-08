package com.example.mahua.funage.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahua.funage.projection.R;

public class ToastUtils extends Toast{
static	Toast toast ;
static	TextView view;
	public ToastUtils(Context context) {
		super(context);
	}
	/**
	 * 
	 * 类名称:M_Toast.java 
	 * 方法名称:init
	 * 参数说明:
	 * 返回类型:
	 *     @return void
	 */
	private static  void init(Context context){
		toast = new Toast(context);
		 view = (TextView) LayoutInflater.from(context).inflate(R.layout.toastview, null);
		 toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setView(view);
		
	}
	
	private static void showToast(CharSequence text){
		view.setText(text);
		toast.show();
		
	}
	
	/**
	 * <pre>
	 * 
	 * 类名称:M_Toast.java 
	 * 方法名称:showLongToast
	 * 参数说明:
	 *     @param text
	 * 返回类型:
	 *     @return void
	 * 业务处理描述: 显示长Toast
	 *	</pre>
	 */
	public static void showLongToast(Context context,CharSequence text){
		init(context);
		toast.setDuration(Toast.LENGTH_LONG);
		showToast(text);
	}
	/**
	 * 
	 * 类名称:M_Toast.java 
	 * 方法名称:showShortToast
	 * 参数说明:
	 *     @param text
	 * 返回类型:
	 *     @return void
	 * 业务处理描述:
	 * 		显示短Toast
	 */
	public static void showShortToast(Context context,CharSequence text){
		init(context);
		toast.setDuration(Toast.LENGTH_SHORT);
		showToast(text);
	}
	
	

}
