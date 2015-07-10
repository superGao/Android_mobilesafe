package com.superGao.mobilesafe;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.superGao.mobilesafe.ui.SettingItemView;
import com.superGao.mobilesafe.utils.PrefUtils;
import com.superGao.mobilesafe.utils.ToastUtils;

/**
 * 设置向导1
 * 
 * @author gao
 * 
 */
public class Setup2Activity extends BaseSetupActivity {
	private SettingItemView sivbind;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);

		sivbind = (SettingItemView) findViewById(R.id.siv_sim);

		// 检查SIM卡绑定状态
		String bind = PrefUtils.getString("bind_card", null, this);
		if (TextUtils.isEmpty(bind)) {
			sivbind.setChecked(false);
		} else {
			sivbind.setChecked(true);
		}

		// 设置绑定SIM卡选项的点击事件
		sivbind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sivbind.isChecked()) {
					sivbind.setChecked(false);
					PrefUtils.removeKey("bind_card", getApplicationContext());
				} else {
					sivbind.setChecked(true);  

					// 获取SIM卡的序列号
					TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					String num = tm.getSimSerialNumber();  
					// 保存SIM卡序列号
					PrefUtils.putString("bind_card", num,
							getApplicationContext());
				}
			}
		});

	}

	// 下一页
	@Override
	public void showNextPage() {
		// 判断SIM卡是否绑定，绑定后才能进入下一步
		// 检查SIM卡绑定状态
		String bind = PrefUtils.getString("bind_card", null, this);
		if (TextUtils.isEmpty(bind)) {
			ToastUtils.showToast(this, "SIM卡未绑定");
			return;
		}
		// 切换到下一页
		startActivity(new Intent(this, Setup3Activity.class));
		finish();
		// 页面切换动画
		overridePendingTransition(R.anim.anim_in, R.anim.anim_out);

	}

	// 上一页
	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();
		// 页面切换动画
		overridePendingTransition(R.anim.anim_previous_in,
				R.anim.anim_previous_out);
	}

}
