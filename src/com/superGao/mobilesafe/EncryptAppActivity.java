package com.superGao.mobilesafe;

import com.superGao.mobilesafe.utils.ToastUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 加锁程序的密码功能
 * 
 * @author gao
 * 
 */
public class EncryptAppActivity extends Activity {
	private EditText etPassword;
	private Button btnOk;
	private ImageView ivIcon;
	private TextView tvName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_encrypt_app);

		tvName = (TextView) findViewById(R.id.tv_name);
		ivIcon = (ImageView) findViewById(R.id.iv_icon);
		etPassword = (EditText) findViewById(R.id.et_password);
		btnOk = (Button) findViewById(R.id.btn_ok);

		Intent intent = getIntent();
		final String packageName = intent.getStringExtra("packageName");
		PackageManager pm = getPackageManager();
		try {

			ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
			String name = appInfo.loadLabel(pm).toString();
			Drawable icon = appInfo.loadIcon(pm);

			tvName.setText(name);
			ivIcon.setImageDrawable(icon);

		} catch (Exception e) {
			e.printStackTrace();
		}

		//验证用户输入的密码是否正确
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 获取用户输入的密码
				String password = etPassword.getText().toString();
				if (!TextUtils.isEmpty(password)) {
					if (password.equals("666")) {
						// 通知程序锁跳过检测当前应用
						Intent intent = new Intent();
						intent.setAction("com.superGao.mobilesafe.SKIP_CHECK");
						intent.putExtra("packageName", packageName);
						sendBroadcast(intent);

						finish();
					} else {
						ToastUtils.showToast(getApplicationContext(), "密码错误!");
					}
				} else {
					ToastUtils.showToast(getApplicationContext(), "密码不能为空哦！");
				}
			}
		});
	}

	// 点击返回键
	@Override
	public void onBackPressed() {
		// 跳到桌面
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);

		finish();
	}
}
