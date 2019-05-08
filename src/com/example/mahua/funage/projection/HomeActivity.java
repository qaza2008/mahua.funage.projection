package com.example.mahua.funage.projection;

import java.util.Timer;
import java.util.TimerTask;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.mahua.funage.action.LoginAction;
import com.example.mahua.funage.update.UpdateManager;

import de.greenrobot.event.EventBus;

@EActivity(R.layout.activity_home)
public class HomeActivity extends BaseActivity {
	public static final int delay = 2 * 1000;
	
	Timer timer;
	
	UpdateManager updateManager;
	@AfterViews
	public void init() {
		timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				post(new LoginAction());
				
			}
		};
		timer.schedule(task, delay);
//		updateVersion();
	
	}


//	void updateVersion() {
//			updateManager = new UpdateManager(this);
//			updateManager.checkUpdate();
//	
//	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}
	
	public void onEventMainThread(LoginAction loginAction) {
		
		Intent intent = new Intent(this, LoginActivity_.class);
		startActivity(intent);
		finish();
		
	}
	
}
