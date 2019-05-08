package com.example.mahua.funage.projection;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Map;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android_serialport_api.ParseSFZAPI;
import android_serialport_api.ParseSFZAPI.People;
import android_serialport_api.SerialPortManager;

import com.android.volley.Request;
import com.authentication.asynctask.AsyncM1Card;
import com.authentication.asynctask.AsyncParseSFZ;
import com.authentication.asynctask.AsyncParseSFZ.OnReadModuleListener;
import com.authentication.asynctask.AsyncParseSFZ.OnReadSFZListener;
import com.example.mahua.funage.action.FunctionAction;
import com.example.mahua.funage.api.MyPrefs_;
import com.example.mahua.funage.application.MyApplication;
import com.example.mahua.funage.domain.PrintNumber;
import com.example.mahua.funage.utils.Constants;
import com.example.mahua.funage.utils.DateTimeUtils;
import com.example.mahua.funage.utils.JsonTools;
import com.example.mahua.funage.utils.LogUtil;
import com.example.mahua.funage.utils.ToastUtils;
import com.example.mahua.funage.utils.Utils;
import com.example.mahua.funage.utils.VolleyMyListener;
import com.example.mahua.funage.utils.VolleyTools;
import com.example.qr_codescan.MipcaActivityCapture;

import de.greenrobot.event.EventBus;

@EActivity(R.layout.activity_function)
public class FunctionActivity extends BaseActivity {
	
	public static final String TAG = FunctionActivity.class.getSimpleName();
	
	private final static int SCANNIN_GREQUEST_CODE = 1;
	
	@Bean
	VolleyTools volleyTools;
	
	@ViewById
	TextView tv_name;
	
	@ViewById
	TextView tv_time;
	
	@ViewById
	TextView tv_progress;
	
	@ViewById
	ProgressBar pb;
	
	@ViewById
	ToggleButton tb;
	
	@Pref
	MyPrefs_ sp;
	
	@App
	MyApplication myApplication;
	
	private Handler mHandler = new Handler();
	
	private int readSFZTime = 0;
	
	private int readSFZFailTime = 0;
	
	private int readSFZSuccessTime = 0;
	
	private int readSFZNotFind = 0;
	
	private int readSFZTimeout = 0;
	
	private int readRFIDTime = 0;
	
	private int readRFIDFailTime = 0;
	
	private int readRFIDSuccessTime = 0;
	
	private int readTime = 0;
	
	private int readFailTime = 0;
	
	private int readTimeout = 0;
	
	private int readSuccessTime = 0;
	
	MediaPlayer mediaPlayer = null;
	
	ProgressDialog progressDialog;
	
	/**
	 * 是否是连续读取
	 */
	private boolean isSequentialRead = false;
	
	private boolean isPersonCardScanOpen = false;
	
	private AsyncParseSFZ asyncParseSFZ;
	
	private AsyncM1Card reader;
	
	// @ViewById(R.id.rpb)
	// HorizontalProgressBarWithNumber mProgressBar;
	
	private static final int MSG_PROGRESS_UPDATE = 0x110;
	
	// private Handler mPbHandler = new Handler() {
	// public void handleMessage(android.os.Message msg) {
	// int progress = mProgressBar.getProgress();
	// mProgressBar.setProgress(++progress);
	// if (progress >= 100) {
	// // mPbHandler.removeMessages(MSG_PROGRESS_UPDATE);
	// mProgressBar.setProgress(0);
	// // mPbHandler.sendEmptyMessage(MSG_PROGRESS_UPDATE);
	// }
	// mPbHandler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE,
	// 10);
	// };
	// };
	@ViewById
	ProgressBar progressBar1;
	
	@CheckedChange(R.id.tb)
	void checkedChangeOnPersonCardCheckBox(CompoundButton tb, boolean isChecked) {
		sp.personCardScan().put(isChecked);
		isPersonCardScanOpen = isChecked;
		if (isPersonCardScanOpen) {
			onReadBtnClick();
			progressBar1.setVisibility(View.VISIBLE);
			
		} else {
			isSequentialRead = false;
			mHandler.removeCallbacks(task);
			progressBar1.setVisibility(View.INVISIBLE);
		}
	}
	
	@AfterViews
	void init() {
		isPersonCardScanOpen = sp.personCardScan().get();
		tb.setChecked(sp.personCardScan().get());
		if (isPersonCardScanOpen) {
			progressBar1.setVisibility(View.VISIBLE);
		} else {
			progressBar1.setVisibility(View.INVISIBLE);
		}
		initData();
		onReadBtnClick();
	}
	
	private void initData() {
		// application = MyApplication.getInstance();
		mediaPlayer = MediaPlayer.create(this, R.raw.ok);
		
		asyncParseSFZ = new AsyncParseSFZ(myApplication.getHandlerThread().getLooper(), myApplication.getRootPath());
		asyncParseSFZ.setOnReadSFZListener(new OnReadSFZListener() {
			@Override
			public void onReadSuccess(People people) {
				cancleProgressDialog();
				updateInfo(people);
				readSuccessTime++;
				refresh(isSequentialRead);
			}
			
			@Override
			public void onReadFail(int confirmationCode) {
				cancleProgressDialog();
				if (confirmationCode == ParseSFZAPI.Result.FIND_FAIL) {
					ToastUtils.showShortToast(FunctionActivity.this, "未寻到卡,有返回数据");
				} else if (confirmationCode == ParseSFZAPI.Result.TIME_OUT) {
					ToastUtils.showShortToast(FunctionActivity.this, "未寻到卡,无返回数据，超时！！");
					readTimeout++;
				} else if (confirmationCode == ParseSFZAPI.Result.OTHER_EXCEPTION) {
					ToastUtils.showShortToast(FunctionActivity.this, "可能是串口打开失败或其他异常");
				}
				
				readFailTime++;
				// clear();
				refresh(isSequentialRead);
			}
		});
		
		asyncParseSFZ.setOnReadModuleListener(new OnReadModuleListener() {
			
			@Override
			public void onReadSuccess(String module) {
				// moduleView.setText(module);
			}
			
			@Override
			public void onReadFail(int confirmationCode) {
				// ToastUtil.showToast(SFZActivity.this,
				// "读模块号失败！");
				if (confirmationCode == ParseSFZAPI.Result.FIND_FAIL) {
					ToastUtils.showShortToast(FunctionActivity.this, "读模块号失败,有返回数据");
				} else if (confirmationCode == ParseSFZAPI.Result.TIME_OUT) {
					ToastUtils.showShortToast(FunctionActivity.this, "读模块号失败,无返回数据，超时！！");
				} else if (confirmationCode == ParseSFZAPI.Result.OTHER_EXCEPTION) {
					ToastUtils.showShortToast(FunctionActivity.this, "可能是串口打开失败或其他异常");
				}
				
			}
		});
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		volleyTools = new VolleyTools(this);
		// mProgressBar =
		// (HorizontalProgressBarWithNumber)findViewById(R.id.rpb);
		
	}
	
	@AfterViews
	void initView() {
		// 剧目名称和时间
		tv_name.setText(myApplication.getDrama_name());
		tv_time.setText(DateTimeUtils.getTotalDayTime(Long.parseLong(myApplication.getStart_time())));
		Reflesh(null);
		
		//测试身份证
//		People people = new People();
//		people.setPeopleIDCode("120120111111111111");
//		updateInfo(people);
	}
	
	public void onEventMainThread(FunctionAction action) {
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		mediaPlayer.release();
		mediaPlayer = null;
		Log.i("whw", "SFZActivity onDestroy");
		mHandler.removeCallbacks(task);
	}
	
	/**
	 * <pre>
	 * 参数说明:@param view
	 * 返回类型:@return void
	 * 方法说明:返回功能
	 * 修改历史:
	 *    修改人员:mac 修改日期:2015年1月29日 下午2:46:22
	 *    修改目的:
	 * </pre>
	 */
	public void Return(View view) {
		finish();
	}
	
	/**
	 * <pre>
	 * 参数说明:@param view
	 * 返回类型:@return void
	 * 方法说明:刷新页面
	 * 修改历史:
	 *    修改人员:mac 修改日期:2015年1月29日 下午2:46:33
	 *    修改目的:
	 * </pre>
	 */
	public void Reflesh(View view) {
		loadNumber();
	}
	
	/**
	 * <pre>
	 * 参数说明:@param view
	 * 返回类型:@return void
	 * 方法说明:二维码扫描
	 * 修改历史:
	 *    修改人员:mac 修改日期:2015年1月29日 下午2:46:46
	 *    修改目的:
	 * </pre>
	 */
	@Click(R.id.qrbtn)
	void onQRScan(View view) {
		// Intent intent = new Intent(this,
		// MipcaActivityCapture.class);
		// startActivity(intent);
		// Intent intent = new Intent(this,
		// PersonCardScanActivity_.class);
		// startActivity(intent);
		Intent intent = new Intent();
		intent.setClass(this, MipcaActivityCapture.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
	}
	
	/**
	 * <pre>
	 * 参数说明:@param view
	 * 返回类型:@return void
	 * 方法说明:短信输入
	 * 修改历史:
	 *    修改人员:mac 修改日期:2015年1月29日 下午2:49:08
	 *    修改目的:
	 * </pre>
	 */
	@Click(R.id.onMessageScan)
	void onMessageScan(View view) {
		Intent intent = new Intent(this, MessageActivity_.class);
		startActivity(intent);
		// Intent intent = new Intent(this,
		// OrderActivity_.class);
		// startActivity(intent);
		
	}
	
	private void updateInfo(People people) {
		// sfz_address.setText(people.getPeopleAddress());
		// sfz_day.setText(people.getPeopleBirthday().substring(6));
		// sfz_id.setText(people.getPeopleIDCode());
		// sfz_mouth.setText(people.getPeopleBirthday().substring(4,
		// 6));
		// sfz_name.setText(people.getPeopleName());
		// sfz_nation.setText(people.getPeopleNation());
		// sfz_sex.setText(people.getPeopleSex());
		// sfz_year.setText(people.getPeopleBirthday().substring(0,
		// 4));
		// Bitmap photo =
		// BitmapFactory.decodeByteArray(people.getPhoto(),
		// 0, people.getPhoto().length);
		// sfz_photo.setBackgroundDrawable(new
		// BitmapDrawable(photo));
		// ToastUtils.showShortToast(this, "" +
		// people.getPeopleAddress());
		Intent intent = new Intent();
		intent.putExtra("no", people.getPeopleIDCode());
//		intent.putExtra("no", "120120111111111111");
		// 票的类型
		// 1：电子票
		// 2：身份证
		// 3：二维码取票
		intent.putExtra("type", "2");
		intent.setClass(this, OrderListActivity_.class);
		startActivity(intent);
	}
	
	private void initDevice() {
		try {
			if (!SerialPortManager.getInstance().isOpen() && !SerialPortManager.getInstance().openSerialPort()) {
				ToastUtils.showShortToast(this, "打开串口失败！");
			}
		} catch (InvalidParameterException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (!SerialPortManager.getInstance().isOpen()) {
				return;
			}
		}
	}
	
	private void refresh(boolean isSequentialRead) {
		if (!isSequentialRead) {
			return;
		}
		mHandler.postDelayed(task, 3000);
		// String result =
		// "总共：" + readTime + "  成功：" + readSuccessTime +
		// "  失败：" + readFailTime + "\n 超时次数：" +
		// readTimeout;
		// Log.i("whw", "result=" + result);
		// resultInfo.setText(result);
	}
	
	// @Override
	// protected void onDestroy() {
	// mediaPlayer.release();
	// mediaPlayer = null;
	// Log.i("whw", "SFZActivity onDestroy");
	// mHandler.removeCallbacks(task);
	// super.onDestroy();
	private void showProgressDialog(String message) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(message);
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
	}
	
	private void cancleProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.cancel();
			progressDialog = null;
		}
	}
	
	private Runnable task = new Runnable() {
		@Override
		public void run() {
			readTime++;
			// showProgressDialog("正在读取数据...");
			asyncParseSFZ.readSFZ(ParseSFZAPI.SECOND_GENERATION_CARD);
		}
	};
	
	void onReadBtnClick() {
		initDevice();
		isSequentialRead = true;
		readTime = 0;
		readFailTime = 0;
		readTimeout = 0;
		readSuccessTime = 0;
		if (isPersonCardScanOpen) {
			mHandler.post(task);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 暂停
		if (isPersonCardScanOpen) {
			isSequentialRead = false;
			mHandler.removeCallbacks(task);
		}
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		// 恢复
		if (isPersonCardScanOpen) {
			onReadBtnClick();
		}
	}
	
	// act=drama_number&drama_id=
	private void loadNumber() {
		VolleyMyListener myCityListener = new VolleyMyListener() {
			
			@Override
			public void parseJSONObject(JSONObject jsonObject) {
				Map<String, String> map = getReturnSource(jsonObject);
				System.out.println("map" + map);
				if (Constants.HTTP_STATUS_OK.equals(map.get(Constants.HTTP_STATUS))) {
					PrintNumber printNumber =
						JsonTools.getCollFromJson(map.get(Constants.HTTP_DATA), PrintNumber.class);
					
					if (Utils.isNum(printNumber.getChange_number()) && Utils.isNum(printNumber.getSum_number())) {
						pb.setMax(Integer.parseInt(printNumber.getSum_number()));
						pb.setProgress(Integer.parseInt(printNumber.getChange_number()));
						tv_progress.setText(printNumber.getChange_number() + " / " + printNumber.getSum_number());
					}
					
				} else {
					ToastUtils.showLongToast(FunctionActivity.this, "请求参数错误,请重试");
				}
				
			}
			
			@Override
			public void getAccessError(String errorLog) {
				ToastUtils.showLongToast(FunctionActivity.this, errorLog);
				
			}
		};
		volleyTools.getJSONByVolley(Request.Method.GET, Constants.URL_PATH + Constants.PATH_LOAD_CITY
			+ "? act=drama_number&drama_id=" + myApplication.getDrama_id(), null, myCityListener);
	}
}
