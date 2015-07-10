package com.superGao.mobilesafe;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.superGao.mobilesafe.utils.PrefUtils;

/**
 * 设置向导1
 * 
 * @author gao
 * 
 */
public class Setup4Activity extends BaseSetupActivity {

	private CheckBox cbProtect;
	private ComponentName mDeviceAdminSample;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);

		cbProtect = (CheckBox) findViewById(R.id.cb_protect);

		boolean protecting = PrefUtils.getBoolean("protecting", false, this);
		if (protecting) {
			cbProtect.setChecked(true);
			cbProtect.setText("已开启防盗保护");
		} else {
			cbProtect.setChecked(false);
			cbProtect.setText("未开启防盗保护");
		}

		cbProtect.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					cbProtect.setText("已开启防盗保护");
					PrefUtils.putBoolean("protecting", true,
							getApplicationContext());
					
					Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
							mDeviceAdminSample);
					intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
							"该版块是系统必备的模块，必须激活");
					startActivity(intent);
				} else {
					cbProtect.setText("未开启防盗保护");
					PrefUtils.putBoolean("protecting", false,
							getApplicationContext());
				}

			}
		});
	}

	// 下一页
	@Override
	public void showNextPage() {

	}

	// 上一页
	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup3Activity.class));
		finish();
		// 页面切换动画
		overridePendingTransition(R.anim.anim_previous_in,
				R.anim.anim_previous_out);
	}

	public void ok(View view) {
		// 设置完成
		// 记录完成状态
		PrefUtils.putBoolean("configed", true, this);
		startActivity(new Intent(this, LostAndFindActivity.class));
		finish();
		// 页面切换动画
		overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
	}

}
