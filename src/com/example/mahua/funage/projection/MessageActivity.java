package com.example.mahua.funage.projection;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mahua.funage.utils.ToastUtils;
import com.example.mahua.funage.utils.VolleyTools;
import com.example.qr_codescan.MipcaActivityCapture;

@EActivity(R.layout.activity_message)
public class MessageActivity extends BaseActivity {
	
	private final static int SCANNIN_GREQUEST_CODE = 1;
	@ViewById
	EditText et_text;
	
	@ViewById
	Button btn_submit;
	
	@ViewById
	Button btn_clear;
	
	VolleyTools volleyTools;
	
	@AfterTextChange(R.id.et_text)
	void afterEditChange(Editable text, TextView tv_text) {
		if (TextUtils.isEmpty(tv_text.getText().toString())) {
			
			btn_submit.setEnabled(false);
			btn_submit.setTextColor(getResources().getColor(R.color.gray));
			btn_clear.setEnabled(false);
			btn_clear.setTextColor(getResources().getColor(R.color.gray));
		} else {
			btn_submit.setEnabled(true);
			btn_submit.setTextColor(getResources().getColor(R.color.white));
			btn_clear.setEnabled(true);
			btn_clear.setTextColor(getResources().getColor(R.color.white));
		}
	}
	
	@Click(R.id.btn_clear)
	void btnClearClick() {
		et_text.setText("");
	}
	
	@Click(R.id.btn_submit)
	void btnSubmitClick() {
		if (TextUtils.isEmpty(et_text.getText().toString().trim())) {
			ToastUtils.showShortToast(this, "请输入有效的验证码");
		} else {
			Intent intent = new Intent();
			intent.putExtra("no", et_text.getText().toString().trim());
			// 票的类型
			// 1：电子票
			// 2：身份证
			// 3：二维码取票
			intent.putExtra("type", "1");
			intent.setClass(this, OrderActivity_.class);
			startActivity(intent);
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		volleyTools = new VolleyTools(this);
		
	}
	
	public void Return(View view) {
		onPersonCardAction();
	}
	
	public void Reflesh(View view) {
		Intent intent = new Intent();
		intent.setClass(this, MessageActivity_.class);
		startActivity(intent);
		finish();
		
	}
	
	@Click(R.id.btn_personcard)
	void onPersonCardAction(){
		Intent intent = new Intent();
		intent.setClass(this, FunctionActivity_.class);
		startActivity(intent);
		finish();
	}
	@Click(R.id.btn_qrcode)
	void onMipcaAction(){
		Intent intent = new Intent();
		intent.setClass(this, MipcaActivityCapture.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
		finish();
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		 Return(null);
	}
	
}
