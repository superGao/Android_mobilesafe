package com.superGao.mobilesafe;

import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;
import com.superGao.mobilesafe.utils.MD5Utils;
import com.superGao.mobilesafe.utils.PrefUtils;
import com.superGao.mobilesafe.utils.ToastUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 应用主页
 * 
 * @author gao
 * 
 */
public class HomeActivity extends Activity {
	private StartAppAd startAppAd = new StartAppAd(this);
	
	private long exitTime=System.currentTimeMillis();
	
	private GridView gv;

	private String[] mHomeItems = new String[] { "手机防盗", "通讯卫士", "应用管理",
			"进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心" };
	private int[] mIconIds = new int[] { R.drawable.safe,
			R.drawable.callmsgsafe, R.drawable.app, R.drawable.taskmanager,
			R.drawable.netmanager, R.drawable.trojan, R.drawable.sysoptimize,
			R.drawable.atools, R.drawable.settings };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StartAppSDK.init(this, "106730647", "206392354", true);
		
		setContentView(R.layout.activity_home);

		gv = (GridView) findViewById(R.id.gv_home);
		gv.setAdapter(new HomeAdapter());

		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 8:
					// 进入设置中心
					startActivity(new Intent(HomeActivity.this,
							SettingActivity.class));
					break;

				case 0:
					// 进入手机防盗页面
					showSafeDialog();
					break;
				case 1:
					// 进入通讯卫士页面(黑名单)
					startActivity(new Intent(getApplicationContext(),BlackNumberActivity.class));
					break;
				case 7:
					// 高级工具
					startActivity(new Intent(getApplicationContext(),
							AToolsActivity.class));
					break;
				case 2:
					//应用管理
					startActivity(new Intent(getApplicationContext(),
							AppManageActivity.class));
					break;
					
				case 3:
					// 进程管理
					startActivity(new Intent(getApplicationContext(),
							ProgressManageActivity.class));
					break;
				case 4:
					//流量统计
					startActivity(new Intent(getApplicationContext(),
							TrafficStatActivity.class));
					break;
				case 5:
					//病毒查杀
					startActivity(new Intent(getApplicationContext(),
							AntiVirusActivity.class));
					break;
				case 6:
					//缓存清理
					startActivity(new Intent(getApplicationContext(),
							CleanActivity.class));
					break;
					
				default:
					break;
				}

			}

		});
	}
	

	/**
	 * 手机防盗页面
	 */
	protected void showSafeDialog() {
		// 判断用户是否已经设置过密码
		String password = PrefUtils.getString("password", null, this);
		if (!TextUtils.isEmpty(password)) {
			// 输入密码
			showInputPasswordDialog();
		} else {
			// 设置密码
			showSetPasswordDialog();
		}
	}

	/**
	 * 设置密码的对话框
	 */
	private void showSetPasswordDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_set_password, null);
		// 去除周围边距，兼容低版本
		dialog.setView(view, 0, 0, 0, 0);

		Button btnOk = (Button) view.findViewById(R.id.btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

		final EditText etPassword = (EditText) view
				.findViewById(R.id.et_password);
		final EditText etPasswordConfirm = (EditText) view
				.findViewById(R.id.et_password_confirm);

		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断输入内容是否为空
				String password = etPassword.getText().toString().trim();
				String passConfirm = etPasswordConfirm.getText().toString()
						.trim();
				if (!TextUtils.isEmpty(password)
						&& !TextUtils.isEmpty(passConfirm)) {
					if (password.equals(passConfirm)) {
						// 保存并将密码MD5加密
						PrefUtils.putString("password",
								MD5Utils.encode(password),
								getApplicationContext());

						// 跳到手机防盗页面
						startActivity(new Intent(getApplicationContext(),
								LostAndFindActivity.class));

						dialog.dismiss();
					} else {
						ToastUtils.showToast(getApplicationContext(),
								"两次密码不一致!");
					}
				} else {
					ToastUtils.showToast(getApplicationContext(), "输入内容不能为空!");
				}
			}
		});
		// 取消设置密码
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * 用户输入密码
	 */
	private void showInputPasswordDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_input_password, null);
		// 去除周围边距，兼容低版本
		dialog.setView(view, 0, 0, 0, 0);

		Button btnOk = (Button) view.findViewById(R.id.btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

		final EditText etPassword = (EditText) view
				.findViewById(R.id.et_password);

		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断输入内容是否为空
				String password = etPassword.getText().toString().trim();
				if (!TextUtils.isEmpty(password)) {
					String savedPass = PrefUtils.getString("password", null,
							getApplicationContext());
					// 判断密码是否正确
					if (MD5Utils.encode(password).equals(savedPass)) {
						// 跳到手机防盗页面
						startActivity(new Intent(getApplicationContext(),
								LostAndFindActivity.class));
						dialog.dismiss();
					} else {
						ToastUtils.showToast(getApplicationContext(), "密码错误!");
					}
				} else {
					ToastUtils.showToast(getApplicationContext(), "输入内容不能为空!");
				}
			}
		});

		// 取消输入密码
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();

	}

	/**
	 * 设置主页面的功能选项
	 * 
	 * @author gao
	 * 
	 */
	class HomeAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return mHomeItems.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = View.inflate(getApplicationContext(),
					R.layout.list_item_home, null);

			ImageView image = (ImageView) v.findViewById(R.id.iv_icon);
			TextView name = (TextView) v.findViewById(R.id.tv_name);

			image.setImageResource(mIconIds[position]);
			name.setText(mHomeItems[position]);

			return v;
		}

	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    startAppAd.onResume();
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    startAppAd.onPause();
	}
	
	/**
	 * 用户点击返回键的事件处理
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			long currentTime=System.currentTimeMillis();
			
			if(currentTime-exitTime==0||currentTime-exitTime>2000){
				exitTime=System.currentTimeMillis();
				ToastUtils.showToast(getApplicationContext(), "亲，再按一次将失去我的保护了哦。。。");
				
				return false;
			}else{
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}


}
