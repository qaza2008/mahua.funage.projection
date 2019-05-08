package com.example.qr_codescan;

import java.io.IOException;
import java.util.Vector;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.mahua.funage.projection.BaseActivity;
import com.example.mahua.funage.projection.FunctionActivity_;
import com.example.mahua.funage.projection.MessageActivity_;
import com.example.mahua.funage.projection.OrderActivity_;
import com.example.mahua.funage.projection.R;
import com.example.mahua.funage.utils.LogUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.decoding.CaptureActivityHandler;
import com.mining.app.zxing.decoding.InactivityTimer;
import com.mining.app.zxing.view.ViewfinderView;

/**
 * Initial the camera
 * 
 * @author Ryan.Tang
 */
public class MipcaActivityCapture extends BaseActivity implements Callback {
	
	public static final String TAG = MipcaActivityCapture.class.getSimpleName();
	
	private CaptureActivityHandler handler;
	
	private ViewfinderView viewfinderView;
	
	private boolean hasSurface;
	
	private Vector<BarcodeFormat> decodeFormats;
	
	private String characterSet;
	
	private InactivityTimer inactivityTimer;
	
	private MediaPlayer mediaPlayer;
	
	private boolean playBeep;
	
	private static final float BEEP_VOLUME = 0.10f;
	
	private boolean vibrate;
	
	int i = 0;
	
	Button mButtonflash;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		// ViewUtil.addTopView(getApplicationContext(),
		// this, R.string.scan_card);
		LogUtil.i(TAG, "onCreate");
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView)findViewById(R.id.viewfinder_view);
		
		mButtonflash = (Button)findViewById(R.id.btn_flash);
		
		mButtonflash.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				i++;
				if (i % 2 == 0) {
					mButtonflash.setBackgroundResource(R.drawable.ic_flash_off_scan);
					CameraManager.get().offLight();
					
				} else {
					mButtonflash.setBackgroundResource(R.drawable.ic_flash_on_scan);
					CameraManager.get().openLight();
					
				}
				
			}
		});
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView)findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;
		
		playBeep = true;
		AudioManager audioService = (AudioManager)getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}
	
	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}
	
	/**
	 * 处理扫描结果
	 * 
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		// ToastUtils.showShortToast(this, "qrcode");
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		if (TextUtils.isEmpty(resultString)) {
			Toast.makeText(MipcaActivityCapture.this, "Scan failed!", Toast.LENGTH_SHORT).show();
		} else {
			Intent resultIntent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("result", resultString);
			bundle.putParcelable("bitmap", barcode);
			resultIntent.putExtras(bundle);
			this.setResult(RESULT_OK, resultIntent);
			// ---------
			Intent intent = new Intent();
			intent.putExtra("no", resultString);
			// 票的类型
			// 1：电子票
			// 2：身份证
			// 3：二维码取票
			intent.putExtra("type", "3");
			intent.setClass(this, OrderActivity_.class);
			startActivity(intent);
		}
		MipcaActivityCapture.this.finish();
	}
	
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		}
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
		
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
		
	}
	
	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}
	
	public Handler getHandler() {
		return handler;
	}
	
	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
		
	}
	
	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not
			// adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);
			
			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}
	
	private static final long VIBRATE_DURATION = 200L;
	
	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}
	
	/**
	 * When the beep has finished playing, rewind to queue
	 * up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
	
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
		onPersonCardAction(null);
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
		
	}
	
	public void onPersonCardAction(View view) {
		Intent intent = new Intent(this, FunctionActivity_.class);
		startActivity(intent);
		finish();
	}
	
	public void onMessageAction(View view) {
		Intent intent = new Intent(this, MessageActivity_.class);
		startActivity(intent);
		finish();
		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Return(null);
	}
	
}