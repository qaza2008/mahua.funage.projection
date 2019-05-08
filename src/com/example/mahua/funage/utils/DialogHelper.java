package com.example.mahua.funage.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.example.mahua.funage.projection.R;

/**
 * 
 * 应用名称: stock_take 包名称: com.midong.utils 类名称: DialogHelper.java 描述信息:
 * Dialog的帮助类
 */
public class DialogHelper {
	Context context;

	public DialogHelper(Context context) {
		super();
		this.context = context;
	}

	public interface OnClickYesListener {
		void onClickYes();
	}

	public interface onClickNoListener {
		void onClickNo();
	}

	Builder builder;
	Dialog dialog;
	ProgressDialog pDialog;
	/**
	 * 
	 * 类名称:DialogHelper.java 方法名称:showDialog 参数说明:
	 * 
	 * @param title
	 *            :dialog 的标题
	 * @param message
	 *            :dialoag 的内容
	 * @param cancelable
	 *            :dialoag 是否可以back键取消
	 * @param yesListener
	 *            :用于传递"确定"键的监听,不为null时显示此键,当只有一个按键是请使用此按键.
	 * @param yesText
	 *            :默认:"确定",用于修改"确定"键的名字,例如"知道了"
	 * @param noListener
	 *            :用于传递"取消"键的监听,部位null是显示此键.
	 * @param noText
	 *            :默认:"取消",用于修改"取消"键的名字 返回类型:
	 * @return void 业务处理描述: 用于动态显示Dialog,根据传递的Listener确定按钮的个数
	 * 
	 */

	private Dialog waitDialog;

	public Dialog getWaitDialog() {
		waitDialog = new Dialog(context,R.style.dialog);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_wait,
				null);
		ImageView iv = (ImageView) view.findViewById(R.id.iv);
		Animation animation = AnimationUtils.loadAnimation(context, R.drawable.ani_rotate);
		LinearInterpolator lin = new LinearInterpolator();  
		animation.setInterpolator(lin);  
		animation.setDuration(1000);
		iv.setAnimation(animation);
		waitDialog.setContentView(view);
		waitDialog.setCanceledOnTouchOutside(false);

		return waitDialog;
	}

	public void showDialog(String title, String message, boolean cancelable,
			final OnClickYesListener yesListener, String yesText,
			final onClickNoListener noListener, String noText) {
		builder = new AlertDialog.Builder(context, R.style.dialog);
		View view = LayoutInflater.from(context).inflate(R.layout.dialogview,
				null);
////		Button yesButton = (Button) view.findViewById(R.id.dialog_sure);
////		Button noButton = (Button) view.findViewById(R.id.dialog_cancel);
////		TextView tv1 = (TextView) (view.findViewById(R.id.dialog_title));
////		tv1.setText(title);
////		TextView tv2 = (TextView) view.findViewById(R.id.dialog_message);
////		tv2.setText(message);
////		LinearLayout layout = (LinearLayout) view
////				.findViewById(R.id.dialog_layout);
////
////		if (isBlank(yesText)) {
////			yesText = "确定";
////		}
////		if (isBlank(noText)) {
////			noText = "取消";
////		}
//
//		yesButton.setText(yesText);
//		noButton.setText(noText);
//
//		if (null == yesListener) {
//			yesButton.setVisibility(View.GONE);
//		} else {
//			yesButton.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					yesListener.onClickYes();
//				}
//			});
//
//		}
//		if (null == noListener) {
//			noButton.setVisibility(View.GONE);
//		} else {
//			noButton.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					noListener.onClickNo();
//				}
//			});
//
//		}
		// builder.setView(view);
		// dialog = builder.create();

		dialog = new Dialog(context, R.style.dialog);
		dialog.setContentView(view);
		dialog.setCancelable(cancelable);
		dialog.show();

	}

	public boolean isShowing() {
		if (dialog != null) {
			return dialog.isShowing();
		}
		return false;
	}

	private boolean isBlank(String text) {
		if (text == null || text.trim().length() == 0) {
			return true;
		}
		return false;

	}

	public void dismiss() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	public void showPD(String message) {
		pDialog = new ProgressDialog(context);
		pDialog.setMessage(message);
		pDialog.show();
	}

	public void dismissPD() {
		if (pDialog != null) {
			pDialog.dismiss();
		}
	}

}
