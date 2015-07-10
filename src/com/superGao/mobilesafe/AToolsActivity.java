package com.superGao.mobilesafe;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.superGao.mobilesafe.utils.MsgUtil;
import com.superGao.mobilesafe.utils.MsgUtil.MsgCallback;
import com.superGao.mobilesafe.utils.ToastUtils;

/**
 * 高级工具
 * @author gao
 *
 */
public class AToolsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}
	
	
	/**
	 * 归属地查询界面
	 * @param view
	 */
	public void numberAddressQuery(View view) {
		startActivity(new Intent(this,NumberAddressActivity.class));
	}
	
	/**
	 * 短信备份
	 */
	public void backupsMsg(View view){
		//检查SD卡是否挂载
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/superGao.xml";
			final File output = new File(path);

			final ProgressDialog dialog = new ProgressDialog(this);
			dialog.setTitle("备份短信中。。");
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.show();
			
			new Thread() {
				public void run() {
					MsgUtil.backupsMsg(getApplicationContext(), output,
							new MsgCallback(){

								@Override
								public void preSmsBackup(int totalCount) {
									// TODO Auto-generated method stub
									dialog.setMax(totalCount);
								}

								@Override
								public void onSmsBackup(int progress) {
									// TODO Auto-generated method stub
									dialog.setProgress(progress);
								}
							});
					dialog.dismiss();
				};
			}.start();
		} else {
			ToastUtils.showToast(this, "SD卡不存在!");
		}
	}
	
	/**
	 * 常用号码查询
	 */
	public void queryNumber(View view) {
		startActivity(new Intent(this, queryNumberActivity.class));
	}
	
	/**
	 * 程序锁
	 */
	public void appLock(View view){
		//跳转到程序锁界面
		startActivity(new Intent(this, AppLockActivity.class));
	}
	
}
