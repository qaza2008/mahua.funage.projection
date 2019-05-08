package com.example.mahua.funage.projection;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android_serialport_api.ParseSFZAPI;
import android_serialport_api.ParseSFZAPI.People;
import android_serialport_api.SerialPortManager;

import com.authentication.asynctask.AsyncM1Card;
import com.authentication.asynctask.AsyncParseSFZ;
import com.authentication.asynctask.AsyncParseSFZ.OnReadModuleListener;
import com.authentication.asynctask.AsyncParseSFZ.OnReadSFZListener;
import com.example.mahua.funage.application.MyApplication;
import com.example.mahua.funage.utils.ToastUtils;

@EActivity(R.layout.activity_person_card)
public class PersonCardScanActivity extends Activity {
	
	@ViewById
	TextView sfz_name;
	
	@ViewById
	TextView sfz_sex;
	
	@ViewById
	TextView sfz_nation;
	
	@ViewById
	TextView sfz_year;
	
	@ViewById
	TextView sfz_mouth;
	
	@ViewById
	TextView sfz_day;
	
	@ViewById
	TextView sfz_address;
	
	@ViewById
	TextView sfz_id;
	
	@ViewById
	ImageView sfz_photo;
	
	@ViewById(R.id.readCardButton)
	Button read_button;
	
	@ViewById(R.id.stopReadCardButton)
	Button stop_button;
	
	@ViewById(R.id.backButton)
	Button back_button;
	
	@ViewById(R.id.resultInfo2)
	TextView resultInfo;
	
	@ViewById
	TextView rfidNum;
	
	@ViewById(R.id.module)
	TextView moduleView;
	
	ProgressDialog progressDialog;
	
	private AsyncParseSFZ asyncParseSFZ;
	
	private AsyncM1Card reader;
	
	private int timeoutTime = 0;
	
	private int notFindTime = 0;
	
	private int validateFail = 0;
	
	private int readFail = 0;
	
	private static final int READ_SFZ = 1;
	
	private static final int READ_CARD_NUM = 2;
	
	private boolean isStop;
	
	private int readTime = 0;
	
	private int readFailTime = 0;
	
	private int readTimeout = 0;
	
	private int readSuccessTime = 0;
	
	/**
	 * 是否是连续读取
	 */
	private boolean isSequentialRead = false;
	
	// private Handler mHandler = new Handler() {
	//
	// @Override
	// public void handleMessage(Message msg) {
	// super.handleMessage(msg);
	// switch (msg.what) {
	// case READ_SFZ:
	// clear();
	// readSFZTime++;
	// asyncParseSFZ.readSFZ(ParseSFZAPI.SECOND_GENERATION_CARD);
	// break;
	// case READ_CARD_NUM:
	// // clear();
	// readRFIDTime++;
	// // reader.readCardNum();
	// reader.read(12, 1, M1CardAPI.KEY_A, null);
	// break;
	// default:
	// break;
	// }
	// }
	//
	// };
	private Handler mHandler = new Handler();
	
	private int readSFZTime = 0;
	
	private int readSFZFailTime = 0;
	
	private int readSFZSuccessTime = 0;
	
	private int readSFZNotFind = 0;
	
	private int readSFZTimeout = 0;
	
	private int readRFIDTime = 0;
	
	private int readRFIDFailTime = 0;
	
	private int readRFIDSuccessTime = 0;
	
	MyApplication application;
	
	MediaPlayer mediaPlayer = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}
	
	private void initData() {
		application = MyApplication.getInstance();
		mediaPlayer = MediaPlayer.create(this, R.raw.ok);
		
		asyncParseSFZ = new AsyncParseSFZ(application.getHandlerThread().getLooper(), application.getRootPath());
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
					ToastUtils.showShortToast(PersonCardScanActivity.this, "未寻到卡,有返回数据");
				} else if (confirmationCode == ParseSFZAPI.Result.TIME_OUT) {
					ToastUtils.showShortToast(PersonCardScanActivity.this, "未寻到卡,无返回数据，超时！！");
					readTimeout++;
				} else if (confirmationCode == ParseSFZAPI.Result.OTHER_EXCEPTION) {
					ToastUtils.showShortToast(PersonCardScanActivity.this, "可能是串口打开失败或其他异常");
				}
				
				readFailTime++;
				clear();
				refresh(isSequentialRead);
			}
		});
		
		asyncParseSFZ.setOnReadModuleListener(new OnReadModuleListener() {
			
			@Override
			public void onReadSuccess(String module) {
				moduleView.setText(module);
			}
			
			@Override
			public void onReadFail(int confirmationCode) {
				// ToastUtil.showToast(SFZActivity.this,
				// "读模块号失败！");
				if (confirmationCode == ParseSFZAPI.Result.FIND_FAIL) {
					ToastUtils.showShortToast(PersonCardScanActivity.this, "读模块号失败,有返回数据");
				} else if (confirmationCode == ParseSFZAPI.Result.TIME_OUT) {
					ToastUtils.showShortToast(PersonCardScanActivity.this, "读模块号失败,无返回数据，超时！！");
				} else if (confirmationCode == ParseSFZAPI.Result.OTHER_EXCEPTION) {
					ToastUtils.showShortToast(PersonCardScanActivity.this, "可能是串口打开失败或其他异常");
				}
				
			}
		});
	}
	
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
	
	@Click(R.id.readCardButton)
	void onReadBtnClick(View v) {
		initDevice();
		//
		// clear();
		// isStop = false;
		// // readRFIDTime++;
		// // reader.read(12, M1CardAPI.KEY_A, 1,
		// // null);
		// mHandler.sendEmptyMessage(READ_SFZ);
		isSequentialRead = true;
		readTime = 0;
		readFailTime = 0;
		readTimeout = 0;
		readSuccessTime = 0;
		mHandler.post(task);
		
	}
	
	private Runnable task = new Runnable() {
		@Override
		public void run() {
			readTime++;
			showProgressDialog("正在读取数据...");
			asyncParseSFZ.readSFZ(ParseSFZAPI.SECOND_GENERATION_CARD);
		}
	};
	
	@Click(R.id.stopReadCardButton)
	void onStopBtnClick(View v) {
//		initDevice();
//		stopRead();
	}
	
	@Click(R.id.backButton)
	void onBackBtnClick(View v) {
		finish();
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
	
	private void clear() {
		sfz_address.setText("");
		sfz_day.setText("");
		sfz_id.setText("");
		sfz_mouth.setText("");
		sfz_name.setText("");
		sfz_nation.setText("");
		sfz_sex.setText("");
		sfz_year.setText("");
		sfz_photo.setBackgroundColor(0);
		
		rfidNum.setText("");
		
	}
	
//	private void stopRead() {
//		cancleProgressDialog();
//		isStop = true;
//		mHandler.removeMessages(READ_CARD_NUM);
//		mHandler.removeMessages(READ_SFZ);
//	}
	
	private void updateInfo(People people) {
		sfz_address.setText(people.getPeopleAddress());
		sfz_day.setText(people.getPeopleBirthday().substring(6));
		sfz_id.setText(people.getPeopleIDCode());
		sfz_mouth.setText(people.getPeopleBirthday().substring(4, 6));
		sfz_name.setText(people.getPeopleName());
		sfz_nation.setText(people.getPeopleNation());
		sfz_sex.setText(people.getPeopleSex());
		sfz_year.setText(people.getPeopleBirthday().substring(0, 4));
		Bitmap photo = BitmapFactory.decodeByteArray(people.getPhoto(), 0, people.getPhoto().length);
		sfz_photo.setBackgroundDrawable(new BitmapDrawable(photo));
	}
	

	
	private void refresh(boolean isSequentialRead) {
		if (!isSequentialRead) {
			return;
		}
		mHandler.postDelayed(task, 2000);
		String result =
			"总共：" + readTime + "  成功：" + readSuccessTime + "  失败：" + readFailTime + "\n 超时次数：" + readTimeout;
		Log.i("whw", "result=" + result);
		resultInfo.setText(result);
	}
	
	@Override
	protected void onDestroy() {
		mediaPlayer.release();
		mediaPlayer = null;
		Log.i("whw", "SFZActivity onDestroy");
		mHandler.removeCallbacks(task);
		cancleProgressDialog();
		super.onDestroy();
	}
}
