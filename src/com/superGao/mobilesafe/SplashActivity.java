package com.superGao.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.superGao.mobilesafe.utils.PrefUtils;
import com.superGao.mobilesafe.utils.StreamTools;
import com.superGao.mobilesafe.utils.ToastUtils;

public class SplashActivity extends Activity {
	protected static final String TAG = "superGao";
	protected static final int ENTER_HOME = 1;
	protected static final int SHOW_UPDATE_DIALOG = 0;
	protected static final int URL_ERROR = 2;
	protected static final int ENTWORK_ERROR = 3;
	protected static final int JSON_ERROR = 4;
	private TextView tv_splash_version;
	private String description;
	private String apkurl;
	private TextView tv_update_info;
	private String mVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("版本号：" + getVersionName());

		// 判断自动更新的复选框是否选中
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean check = sp.getBoolean("update", false);

		copyDB("address.db");// 拷贝归属地数据库
		copyDB("commonnum.db");// 拷贝常用号码数据库
		copyDB("antivirus.db");//拷贝查询病毒的数据库

		if (check) {
			// 检查升级
			checkUpdate();
		} else {
			// 取消自动更新
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// 进入主页
					enterHome();
				}
			}, 2000);
		}

		// 渐变动画
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(600);

		findViewById(R.id.rl_root_splash).startAnimation(aa);

		// 更新进度
		tv_update_info = (TextView) findViewById(R.id.tv_update_info);
		// 创建快捷方式
		createShortCut();

	}

	/**
	 * 拷贝数据库
	 * 
	 * @param string
	 */
	private void copyDB(String dbName) {
		File file = new File(getFilesDir(), dbName);// 目的文件

		if (file.exists()) {
			System.out.println("数据库" + dbName + "已存在,无须拷贝!");
			return;
		}

		FileOutputStream out = null;
		InputStream in = null;
		try {
			out = new FileOutputStream(file);
			in = getAssets().open(dbName);// 源文件

			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case SHOW_UPDATE_DIALOG:
				// 显示对话框
				showUpdateDialog();
				break;
			case ENTER_HOME:
				// 进入主页
				enterHome();
				break;
			case URL_ERROR:
				// URL错误
				enterHome();
				ToastUtils.showToast(getApplicationContext(), "URL错误");
				break;
			case ENTWORK_ERROR:
				// 网络异常
				enterHome();
				ToastUtils.showToast(getApplicationContext(), "网络异常");
				break;
			case JSON_ERROR:
				// JSON解析出错
				enterHome();
				ToastUtils.showToast(getApplicationContext(), "数据解析出错");
				break;

			default:
				break;
			}
		}

	};

	/*
	 * 检查升级
	 */
	private void checkUpdate() {
		new Thread() {

			public void run() {
				Message mes = Message.obtain();

				long startTime = System.currentTimeMillis();

				try {
					URL url = new URL(getString(R.string.serverurl));
					// 联网
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					// 设置相关参数
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(4000);
					int code = conn.getResponseCode();
					if (code == 200) {
						// 联网成功
						InputStream is = conn.getInputStream();
						// 把流转换为string类型
						String result = StreamTools.readFromStream(is);
						Log.i(TAG, "联网成功" + result);
						// json解析
						JSONObject obj = new JSONObject(result);
						mVersion = (String) obj.get("version");

						description = (String) obj.get("description");
						apkurl = (String) obj.get("apkurl");

						// 检查是否有新版本
						if (getVersionName().equals(mVersion)) {
							// 版本一致，没有新版本，进入主页面
							mes.what = ENTER_HOME;
						} else {
							// 有新版本，弹出升级对话框
							mes.what = SHOW_UPDATE_DIALOG;
						}

					}
				} catch (MalformedURLException e) {
					mes.what = URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					mes.what = ENTWORK_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					mes.what = JSON_ERROR;
					e.printStackTrace();
				} finally {
					long endTime = System.currentTimeMillis();
					long dTime = endTime - startTime;
					// 界面休眠2s
					if (dTime < 2000) {
						try {
							Thread.sleep(2000 - dTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					// 发送消息
					handler.sendMessage(mes);
				}

			};

		}.start();
	}

	/**
	 * 升级对话框
	 */
	protected void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(this);

		builder.setTitle("发现新版本" + mVersion);
		// 监听用户点击返回或者对话框以外区域的行为
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// 进入主页
				enterHome();
				dialog.dismiss();
			}

		});
		builder.setMessage(description);
		builder.setPositiveButton("立即升级", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 判断SD卡是否存在
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// SD卡存在,下载新版本
					// 利用第三方的框架实现apk的下载
					FinalHttp finalhttp = new FinalHttp();
					finalhttp.download(apkurl, Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/mobilesafe2.0.apk", new AjaxCallBack<File>() {

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							t.printStackTrace();
							Toast.makeText(getApplicationContext(), "下载失败", 0)
									.show();
							super.onFailure(t, errorNo, strMsg);
						}

						@Override
						public void onLoading(long count, long current) {
							super.onLoading(count, current);
							// 下载进度
							int progress = (int) (current * 100 / count);
							// 显示下载进度文本
							tv_update_info.setVisibility(View.VISIBLE);
							tv_update_info.setText("更新进度为：" + progress + "%");
						}

						@Override
						public void onSuccess(File t) {
							super.onSuccess(t);
							// 下载成功，安装新版本
							installAPK(t);

						}

						/**
						 * 安装新版本
						 * 
						 * @param t
						 */
						private void installAPK(File t) {
							Intent intent = new Intent();
							intent.setAction(Intent.ACTION_VIEW);
							intent.addCategory(Intent.CATEGORY_DEFAULT);
							intent.setDataAndType(Uri.fromFile(t),
									"application/vnd.android.package-archive");

							startActivityForResult(intent, 0);
							;
						}

					});

				} else {
					// SD卡不存在
					Toast.makeText(getApplicationContext(), "SD卡不存在", 0).show();
					return;
				}
			}
		});

		builder.setNegativeButton("暂不升级", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 取消升级，进入主页面
				dialog.dismiss();
				enterHome();
			}
		});
		builder.show();
	}

	/**
	 * 进入主页面
	 */
	protected void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);

		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 进入主页名
		enterHome();
	}

	/**
	 * 动态获取版本信息
	 */
	private String getVersionName() {

		PackageManager pm = getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);

			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();

			return "";
		}
	}
	
	/**
	 *创建快捷方式
	 */
	private void createShortCut() {  
		// 只允许执行一次
		boolean isCreated = PrefUtils.getBoolean("is_shortcut_created", true,this);
		if (isCreated) {
			Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
			// 快捷方式名称
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "superGao手机卫士");
			// 快捷方式图标  
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(),R.drawable.luncher_bg));

			// 跳到闪屏页面  
			Intent actionIntent = new Intent("com.superGao.mobilesafe.Splash");
			actionIntent.addCategory(Intent.CATEGORY_DEFAULT);
			
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);

			sendBroadcast(intent);// 发送广播  
			PrefUtils.putBoolean("is_shortcut_created", false, this);
		}
	}

}
