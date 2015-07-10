package com.superGao.mobilesafe;

import com.superGao.mobilesafe.utils.PrefUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 手机防盗页面
 * 
 * @author gao
 * 
 */
public class LostAndFindActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 判断用户是否设置过密码
		boolean configed = PrefUtils.getBoolean("configed", false, this);
		if (!configed) {
			// 跳转到设置向导页面
			startActivity(new Intent(this, Setup1Activity.class));
			finish();
		} else {
			// 手机防盗主页
			setContentView(R.layout.activity_lost_and_find);
			
			TextView tvSafePhone = (TextView) findViewById(R.id.tv_safePhone);
			ImageView ivSafe = (ImageView) findViewById(R.id.iv_safe);

			// 获取数据
			String phone = PrefUtils.getString("safeNumber", "", this);
			boolean safe = PrefUtils.getBoolean("protecting", false, this);

			if (!TextUtils.isEmpty(phone)) {
				tvSafePhone.setText(phone);
			}
			

			if (safe == true) {
				ivSafe.setImageResource(R.drawable.lock);
			} else {
				ivSafe.setImageResource(R.drawable.unlock);
			}
		}

		
	}

	/**
	 * 重新进入设置向导
	 */
	public void resetSetting(View view) {
		// 进入设置向导第一页
		startActivity(new Intent(this, Setup1Activity.class));

		finish();
		// 页面切换动画
		overridePendingTransition(R.anim.anim_previous_in,
				R.anim.anim_previous_out);
	}

}
