package com.superGao.mobilesafe;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 缓存清理
 * 
 * @author gao
 * 
 */
public class CleanCacheActivity extends Activity {
	protected static final int SCANNING = 0;
	protected static final int SHOW_CACHE_INFO = 1;
	protected static final int SCANNING_FINISHED = 2;
	private TextView tvStatus;
	private LinearLayout llContainer;
	private ProgressBar pbProgress;
	private PackageManager mPm;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANNING:

				String name = (String) msg.obj;
				tvStatus.setText("正在扫描：" + name);

				break;
			case SHOW_CACHE_INFO:
				final CacheInfo info = (CacheInfo) msg.obj;
				View itemView = View.inflate(getApplicationContext(),
						R.layout.list_item_cache_info, null);

				TextView tvName = (TextView) itemView
						.findViewById(R.id.tv_cache_name);
				TextView tvCache = (TextView) itemView
						.findViewById(R.id.tv_cache);
				ImageView ivDelete = (ImageView) itemView
						.findViewById(R.id.iv_delete);
				ImageView ivIcon = (ImageView) itemView
						.findViewById(R.id.iv_cache_icon);

				tvName.setText(info.name);
				ivIcon.setImageDrawable(info.icon);
				tvCache.setText("缓存大小为:"
						+ Formatter.formatFileSize(getApplicationContext(),
								info.cacheSize));
				tvCache.setTextColor(Color.BLUE);
				tvCache.setTextSize(10);

				ivDelete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 点击清理图标进入应用的设置页面
						Intent intent = new Intent(
								"android.settings.APPLICATION_DETAILS_SETTINGS");
						intent.setData(Uri.parse("package:" + info.packageName));
						startActivity(intent);
					}
				});

				// 在线性布局的第一个位置添加textview
				llContainer.addView(itemView, 0);
				break;
			case SCANNING_FINISHED:
				tvStatus.setText("扫描完毕");

				break;

			default:
				break;

			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);
		
		tvStatus = (TextView) findViewById(R.id.tv_status);
		llContainer = (LinearLayout) findViewById(R.id.ll_container);
		pbProgress = (ProgressBar) findViewById(R.id.pb_cache_progress);

		mPm = getPackageManager();
		// 更新进度条
		new Thread() {

			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				List<PackageInfo> apkInfo = mPm.getInstalledPackages(0);
				pbProgress.setMax(apkInfo.size());

				int progress = 0;
				Random random = new Random();
				for (PackageInfo packageInfo : apkInfo) {

					try {
						Method method = mPm.getClass().getMethod(
								"getPackageSizeInfo", String.class,
								IPackageStatsObserver.class);
						method.invoke(mPm, packageInfo.packageName,
								new MyObserver());

						progress++;
						pbProgress.setProgress(progress);

						// 发送消息
						Message msg = Message.obtain();
						msg.what = SCANNING;
						msg.obj = packageInfo.applicationInfo.loadLabel(mPm)
								.toString();
						mHandler.sendMessage(msg);

						Thread.sleep(50 + random.nextInt(60));

					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				mHandler.sendEmptyMessage(SCANNING_FINISHED);
			};
		}.start();
	}

	/**
	 * 内容观察者
	 */
	class MyObserver extends IPackageStatsObserver.Stub {

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			// 获取缓存大小
			long cacheSize = pStats.cacheSize;
			if (cacheSize > 0) {

				try {
					CacheInfo info = new CacheInfo();
					String packageName = pStats.packageName;
					;
					info.packageName = packageName;

					ApplicationInfo applicationInfo = mPm.getApplicationInfo(
							packageName, 0);
					info.name = applicationInfo.loadLabel(mPm).toString();
					info.icon = applicationInfo.loadIcon(mPm);
					info.cacheSize = cacheSize;

					// 发送应用信息
					Message msg = Message.obtain();
					msg.what = SHOW_CACHE_INFO;
					msg.obj = info;
					mHandler.sendMessage(msg);

				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}

			}

		}

	}

	/**
	 * 封装应用的缓存信息
	 * 
	 * @author gao
	 * 
	 */
	class CacheInfo {
		public String packageName;
		public String name;
		public Drawable icon;
		public long cacheSize;
	}

	/**
	 * 一键清理所有缓存
	 */
	public void cleanAllCache(View view) {
		// 通过反射调用freeStorageAndNotify方法, 向系统申请内存
		try {
			Method method = mPm.getClass().getMethod("freeStorageAndNotify",
					long.class, IPackageDataObserver.class);
			method.invoke(mPm, Long.MAX_VALUE, new IPackageDataObserver.Stub() {

				@Override
				public void onRemoveCompleted(String packageName,
						boolean succeeded) throws RemoteException {
					
				}
			});

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			showAlertDialog();
		}

	}
	
	/**
	 * 一键清理后让用户选择是否选择再次刷新
	 */
	protected void showAlertDialog(){  
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setTitle("温馨提示");
		builder.setMessage("亲，清理完成了。。。是否再次刷新？");
		builder.setPositiveButton("刷新", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//再次进入缓存清理页面
				startActivity(new Intent(getApplicationContext(),CleanCacheActivity.class));
			}
		});
		
		builder.setNegativeButton("不了", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//进入主页面
				startActivity(new Intent(getApplicationContext(),HomeActivity.class));
			}
		});
		builder.show();
	}
}
