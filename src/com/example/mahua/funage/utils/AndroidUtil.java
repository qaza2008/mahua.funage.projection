package com.example.mahua.funage.utils;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * android相关工具类
 * 
 * @date 2014-8-22
 * @author mac
 * 
 */
@EBean
public class AndroidUtil {
	
	public static String wifiMacAddress = null;
	
	/**
	 * 隐藏软键盘
	 * 
	 * @param text
	 */
	public static void hiddenSoftKeyboard(EditText text) {
		InputMethodManager imm = (InputMethodManager)text.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
	}
	
	/**
	 * 显示软键盘
	 * 
	 * @param text
	 */
	public static void showSoftKeyboard(EditText text) {
		InputMethodManager imm = (InputMethodManager)text.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(text, InputMethodManager.SHOW_FORCED);
	}
	
	/**
	 * 获取本地版本号
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			LogUtil.e("getVersionCode", e.getMessage());
			return -1;
		}
		return info.versionCode;
	}
	
	/**
	 * 获取本地版本名称
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			LogUtil.e("getVersionName", e.getMessage());
			return "";
		}
		return info.versionName;
	}
	
	/**
	 * 获取WIFI的Mac地址
	 * 
	 * @return
	 */
	public static String getMacAddress(Context context) {
		if (wifiMacAddress != null) {
			return wifiMacAddress;
		}
		
		WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		String deviceID = wifiInfo == null ? null : wifiInfo.getMacAddress();
		
		if (!TextUtils.isEmpty(deviceID)) {
			wifiMacAddress = deviceID;
			return wifiMacAddress;
		}
		return null;
	}
	
	/**
	 * 使用默认的Intent打开一个Uri
	 * 
	 * @param context
	 * @param str
	 */
	public static void openUriWithIntent(Context context, String str) {
		Uri uri = Uri.parse(str);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		context.startActivity(intent);
	}
	
	/**
	 * 安装APK
	 * 
	 * @param context
	 * @param path
	 * @return file:///storage/emulated/0/download/kry.print
	 *         .1410414646626.apk
	 */
	public static boolean installAPK(Context context, String path) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse(path), "application/vnd.android.package-archive");
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
		return true;
	}
	
	/**
	 * <pre>
	 * 参数说明:@param context
	 * 返回类型:@return void
	 * 方法说明:获取crash log
	 * 修改历史:
	 *    修改人员:mac 修改日期:2015年1月29日 上午10:23:24
	 *    修改目的:
	 * </pre>
	 */
	
	public static void getCrashHandler(Context context) {
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(context);
	}
	
	@SystemService
	ConnectivityManager connectivityManager;
	
	public boolean isNetworkActived() {
		
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		return (activeNetInfo != null) && activeNetInfo.isConnected();
	}
	
	@SystemService
	static WifiManager wifiManager;
	
	public boolean isUsedWifi() {
		boolean isUsedWifi = false;// wifiManager.isWifiEnabled();
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			isUsedWifi = true;
		}
		if (isUsedWifi && wifiManager.isWifiEnabled()) {
			return true;
		} else {
			return false;
		}
	}
	
}
