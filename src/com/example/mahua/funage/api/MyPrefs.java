package com.example.mahua.funage.api;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface MyPrefs {
	
	@DefaultBoolean(false)
	boolean personCardScan();
	
	@DefaultString("")
	String city_id();
	
	@DefaultString("")
	String theater_id();
	
	@DefaultString("")
	String drama_id();

	/**
	 * <pre>
	 * 参数说明:@return
	 * 返回类型:@return boolean
	 * 方法说明:pad是否检查过
	 * 修改历史:
	 *    修改人员:mac 修改日期:2015年2月9日 下午6:43:16
	 *    修改目的:
	 *</pre>
	 */
	@DefaultBoolean(false)
	boolean checkValidated();
	
	/**
	 * <pre>
	 * 参数说明:@return
	 * 返回类型:@return boolean
	 * 方法说明:pad是否验证通过
	 * 修改历史:
	 *    修改人员:mac 修改日期:2015年2月9日 下午6:43:39
	 *    修改目的:
	 *</pre>
	 */
	@DefaultBoolean(false)
	boolean padValidate();
	
	
}
