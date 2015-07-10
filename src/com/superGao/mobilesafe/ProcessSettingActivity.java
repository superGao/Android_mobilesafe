package com.superGao.mobilesafe;

import service.AutoKillService;

import com.superGao.mobilesafe.utils.PrefUtils;
import com.superGao.mobilesafe.utils.ServiceStatusUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 进程管理设置页面
 * 
 * @author gao
 * 
 */
public class ProcessSettingActivity extends Activity {

	private CheckBox cbShowSystem;
	private CheckBox cbAutoKill;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_setting);

		cbShowSystem = (CheckBox) findViewById(R.id.cb_show_system);
		cbAutoKill = (CheckBox) findViewById(R.id.cb_auto_kill);

		boolean showSystem = PrefUtils.getBoolean("show_system", true, this);
		if (showSystem) {
			cbShowSystem.setChecked(true);
			cbShowSystem.setText("显示系统进程");
		} else {
			cbShowSystem.setChecked(false);
			cbShowSystem.setText("隐藏系统进程");
		}

		cbShowSystem.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					cbShowSystem.setText("显示系统进程");
					PrefUtils.putBoolean("show_system", true,
							getApplicationContext());
				} else {
					cbShowSystem.setText("隐藏系统进程");
					PrefUtils.putBoolean("show_system", false,
							getApplicationContext());
				}
			}
		});

		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,
				"com.itcast.mobilesafe01.service.AutoKillService");
		if (serviceRunning) {
			cbAutoKill.setChecked(true);
			cbAutoKill.setText("已开启锁屏清理");
		} else {
			cbAutoKill.setChecked(false);
			cbAutoKill.setText("已关闭锁屏清理");
		}

		cbAutoKill.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Intent service = new Intent(getApplicationContext(),
						AutoKillService.class);
				if (isChecked) {
					cbAutoKill.setText("已开启锁屏清理");
					startService(service);
				} else {
					cbAutoKill.setText("已开启锁屏清理");
					stopService(service);
				}
			}
		});
	}
}
