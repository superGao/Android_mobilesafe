package com.superGao.mobilesafe;

import android.content.Intent;
import android.os.Bundle;
/**
 * 设置向导1
 * @author gao
 *
 */
public class Setup1Activity extends BaseSetupActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}

	@Override
	public void showNextPage() {
		startActivity(new Intent(this,Setup2Activity.class));
		finish();
		
		//页面切换动画
		overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
	}

	@Override
	public void showPreviousPage() {
		// TODO Auto-generated method stub
		
	}
}
