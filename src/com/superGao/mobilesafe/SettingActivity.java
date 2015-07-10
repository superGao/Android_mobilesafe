package com.superGao.mobilesafe;

import service.AddressService;
import service.AppLockService;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.superGao.mobilesafe.ui.SettingClickView;
import com.superGao.mobilesafe.ui.SettingItemView;
import com.superGao.mobilesafe.utils.PrefUtils;
import com.superGao.mobilesafe.utils.ServiceStatusUtils;

/**
 * 设置中心
 * 
 * @author gao
 * 
 */
public class SettingActivity extends Activity {

	private SettingItemView sivAddress;
	private SettingClickView scvLocation;
	private SettingClickView scvStyle;

	final String[] mItems = new String[] { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };
	private SettingItemView sivblackNumber;
	private SettingItemView sivAppLock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		initUpdate();
		initAddress();
		initAddressStyle();
		initAddressLocation();
		initBlackNumber();
		initAppLock();
	}
	
	/**
	 * 初始化自动更新状态
	 */
	private void initUpdate(){
		final SettingItemView sivUpdate = (SettingItemView) findViewById(R.id.siv_update);

		boolean update = PrefUtils.getBoolean("update", false, this);

		if (update) {
			sivUpdate.setChecked(true);
		} else {
			sivUpdate.setChecked(false);
		}

		/**
		 * 监听自动更新的选择状态
		 */
		sivUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sivUpdate.isChecked()) {
					sivUpdate.setChecked(false);
					PrefUtils.putBoolean("update", false,
							getApplicationContext());
				} else {
					sivUpdate.setChecked(true);
					PrefUtils.putBoolean("update", true,
							getApplicationContext());
				}
			}
		});
	}

	/**
	 * 初始化归属地位置
	 */
	private void initAddressLocation() {
		scvLocation = (SettingClickView) findViewById(R.id.scv_location);
		scvLocation.setTitle("归属地提示框位置");
		scvLocation.setDesc("设置归属地提示框位置");
		scvLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转到修改归属地位置的页面
				startActivity(new Intent(getApplicationContext(),
						DragViewActivity.class));
			}
		});
	}

	/**
	 * 初始化归属地风格
	 */
	private void initAddressStyle() {
		scvStyle = (SettingClickView) findViewById(R.id.scv_style);
		scvStyle.setTitle("归属地提示框颜色");

		int which = PrefUtils.getInt("address_style", 0, this);
		scvStyle.setDesc(mItems[which]);

		scvStyle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showChooseDialog();
			}
		});
	}

	/**
	 * 设置归属地弹窗颜色
	 */
	protected void showChooseDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("归属地提示框颜色");
		builder.setIcon(R.drawable.ic_launcher);

		int which = PrefUtils.getInt("address_style", 0, this);
		builder.setSingleChoiceItems(mItems, which,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.out.println("which=" + which);
						PrefUtils.putInt("address_style", which,
								getApplicationContext());
						scvStyle.setDesc(mItems[which]);
						dialog.dismiss();
					}
				});

		builder.setNegativeButton("取消", null);
		builder.show();
	}

	/**
	 * 初始化归属地
	 */
	private void initAddress() {
		sivAddress = (SettingItemView) findViewById(R.id.siv_showAddress);
		sivAddress.setTitle("电话归属地设置");
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,
				"service.AddressService");
		sivAddress.setChecked(serviceRunning);

		sivAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent service = new Intent(getApplicationContext(),
						AddressService.class);

				if (sivAddress.isChecked()) {
					sivAddress.setChecked(false);
					// 关闭归属地服务
					stopService(service);

				} else {
					sivAddress.setChecked(true);
					// 开启归属地服务
					startService(service);
				}
			}
		});
	}

	/**
	 * 初始化黑名单拦截
	 */
	private void initBlackNumber() {
		sivblackNumber=(SettingItemView)findViewById(R.id.siv_blackNumber);
		sivblackNumber.setTitle("黑名单拦截设置");
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,
				"service.BlackNumberService");
		sivblackNumber.setChecked(serviceRunning);

		sivblackNumber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent service = new Intent(getApplicationContext(),
						AddressService.class);

				if (sivblackNumber.isChecked()) {
					sivblackNumber.setChecked(false);
					// 关闭黑名单拦截服务
					stopService(service);

				} else {
					sivblackNumber.setChecked(true);
					// 开启黑名单拦截服务
					startService(service);
				}
			}
		});
	}
	
	/**
	 * 初始化程序锁设置
	 */
	private void initAppLock() {
		sivAppLock = (SettingItemView)findViewById(R.id.siv_appLock);
		sivAppLock.setTitle("程序锁设置");
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,
				"service.AppLockService");
		sivAppLock.setChecked(serviceRunning);

		sivAppLock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent service = new Intent(getApplicationContext(),
						AppLockService.class);

				if (sivAppLock.isChecked()) {
					sivAppLock.setChecked(false);
					// 关闭程序锁服务
					stopService(service);

				} else {
					sivAppLock.setChecked(true);
					// 开启程序锁服务
					startService(service);
				}
			}
		});
	}
}
