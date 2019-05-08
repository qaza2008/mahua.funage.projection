package com.example.mahua.funage.projection;

import org.androidannotations.annotations.EActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.mahua.funage.api.ApiException;
import com.example.mahua.funage.utils.CrashHandler;
import com.example.mahua.funage.utils.ToastUtils;

import de.greenrobot.event.EventBus;

@EActivity
public class BaseActivity extends Activity {
	
	/**
	 * <pre>
	 * 参数说明:@param obj
	 * 返回类型:@return void
	 * 方法说明:通过EventBus 发送一个事件.
	 * 修改历史:
	 *    修改人员:mac 修改日期:2015年1月27日 上午10:15:02
	 *    修改目的:
	 * </pre>
	 */
	public void post(Object obj) {
		EventBus.getDefault().post(obj);
	}
	
	/**
	 * <pre>
	 * 参数说明:@param obj
	 * 返回类型:@return void
	 * 方法说明:通过EventBus 发送一个粘性事件
	 * 修改历史:
	 *    修改人员:mac 修改日期:2015年1月27日 上午10:16:11
	 *    修改目的:
	 * </pre>
	 */
	public void postSticky(Object obj) {
		EventBus.getDefault().postSticky(obj);
		
	}
	
	public void onApiException(ApiException ex) {
		ToastUtils.showShortToast(this, ex.getErrorMessage());
	}
	
	public void getCrashHandler() {
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getCrashHandler();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
			WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	//
	// @Override
	// protected void onDestroy() {
	// super.onDestroy();
	// EventBus.getDefault().unregister(this);
	// }
}
