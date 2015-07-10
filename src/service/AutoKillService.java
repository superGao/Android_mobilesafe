package service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import engine.ProgressInfoProvider;

/**
 * 锁屏清理的服务
 * 
 * @author gao
 * 
 */
public class AutoKillService extends Service {

	private InnerScreenOffReceiver mReceiver;
	private Timer mTimer;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mReceiver = new InnerScreenOffReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);

		// 定时清理
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				System.out.println("定时清理啦...");
			}

		}, 0, 5000);// 每隔5秒运行一次
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);

		mTimer.cancel();// 停止定时器
	}

	// 屏幕关闭的广播, 此广播只能动态注册,静态注册没有效果
	class InnerScreenOffReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("锁屏清理后台进程...");
			ProgressInfoProvider.killAllProgress(context);
		}

	}

}
