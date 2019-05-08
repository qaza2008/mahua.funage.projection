package com.example.mahua.funage.utils;

import java.util.Locale;

public class DateTimeUtils {
	public static String getTotalDayTime(Long unixTimeStamp) {
		return new java.text.SimpleDateFormat("yyyy年MM月dd日 E HH:mm:ss", Locale.getDefault()).format(new java.util.Date(
			unixTimeStamp * 1000));
	}
	
	public static String getDateTime(Long unixTimeStamp) {
		return new java.text.SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault()).format(new java.util.Date(
			unixTimeStamp * 1000));
	}
}
