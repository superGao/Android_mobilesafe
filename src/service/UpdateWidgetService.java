package service;

import java.util.Timer;
import java.util.TimerTask;

import receiver.MyWidget;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.superGao.mobilesafe.HomeActivity;
import com.superGao.mobilesafe.R;

import engine.ProgressInfoProvider;

public class UpdateWidgetService extends Service {

	private Timer mTimer;
	private AppWidgetManager mAWM;

	private InnerScreenReceiver mReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// 初始化窗口小部件管理器
		mAWM = AppWidgetManager.getInstance(this);

		startTimer();

		mReceiver = new InnerScreenReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(mReceiver, filter);
	}

	private void startTimer() {
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				updateWidget();
			}

		}, 0, 5000);
	}

	/**
	 * 更新widget
	 */
	protected void updateWidget() {
		// 初始化widget组件
		ComponentName provider = new ComponentName(this, MyWidget.class);
		// 初始化远程view对象
		RemoteViews views = new RemoteViews(getPackageName(),
				R.layout.process_widget);
		views.setTextViewText(R.id.tv_running_num, "正在运行的软件:"
				+ ProgressInfoProvider.getRunningProcessNum(this));
		views.setTextViewText(
				R.id.tv_avail_mem,
				"可用内存:"
						+ Formatter.formatFileSize(this,
								ProgressInfoProvider.getAvailMemo(this)));

		// 点击后跳转主页面
		Intent intent = new Intent();
		intent.setClass(this, HomeActivity.class);

		// PendingIntent 延时执行的Intent(不确定什么时候被执行)
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.ll_root, pendingIntent);

		// 点击一键清理
		Intent clearIntent = new Intent();
		clearIntent.setAction("com.superGao.mobilesafe.KILL_PROCESS");
		PendingIntent clearPendingIntent = PendingIntent.getBroadcast(this, 0,
				clearIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.btn_clear, clearPendingIntent);

		// 更新远程view
		mAWM.updateAppWidget(provider, views);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mTimer != null) {
			mTimer.cancel();
		}

		unregisterReceiver(mReceiver);
	}

	// 屏幕关闭/开启的广播, 此广播只能动态注册,静态注册没有效果
	class InnerScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				// 屏幕开启
				startTimer();
			} else {
				// 屏幕关闭
				if (mTimer != null) {
					mTimer.cancel();
				}
			}
		}

	}

}
