package com.example.mahua.funage.utils;

import java.io.File;
import java.io.FileOutputStream;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EBean;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import com.example.mahua.funage.application.MyApplication;

@EBean
@SuppressLint("SimpleDateFormat")
public class LogUtil {
	// public static boolean isLogEnable=false;
	public static final String TAG = "LogUtil";
	
	public static boolean isLogEnable = false;
	
	@App
	public static MyApplication instance;
	
	private static String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	
	// 存放log文件的文件夹;
	private final static String LOG_FILE_DIR = "LogInfo";
	
	public static final String PATH_LOGCAT = SD_PATH + File.separator + LOG_FILE_DIR;
	
	private static FileOutputStream out;
	
	// private static File fileTag = new File(PATH_LOGCAT +
	// File.separator + "1");
	
	// 文件名;
	public static String fileName;
	
	private static int FILE_SIZE = 1024 * 1024;
	
	private static String SHOP_INFO;
	
	public static boolean isLogVEnable() {
		return true;
	}
	
	public static boolean isLogIEnable() {
		return true;
	}
	
	public static boolean isLogDEnable() {
		return true;
	}
	
	public static boolean isLogWEnable() {
		return true;
	}
	
	public static boolean isLogEEnable() {
		return true;
	}
	
	public static void v(String tag, String msg) {
		if (isLogVEnable()) {
			Log.v(tag, msg);
			
		}
	}
	
	public static void i(String tag, String msg) {
		if (isLogIEnable()) {
			Log.i(tag, msg);
			
		}
	}
	
	public static void d(String tag, String msg) {
		if (isLogDEnable()) {
			Log.d(tag, msg);
			
		}
	}
	
	public static void w(String tag, String msg) {
		if (isLogWEnable()) {
			Log.w(tag, msg);
			
		}
	}
	
	public static void e(String tag, String msg) {
		if (isLogEEnable()) {
			Log.e(tag, msg);
			
		}
	}
	
	public static void e(String tag, String msg, Exception ex) {
		if (isLogEEnable()) {
			Log.e(tag, msg, ex);
			
		}
	}
	
}
