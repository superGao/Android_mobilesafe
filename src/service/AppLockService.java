package service;

import java.util.ArrayList;
import java.util.List;

import com.superGao.db.dao.AppLockDao;
import com.superGao.mobilesafe.EncryptAppActivity;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

/**
 * 程序锁的服务
 * 
 * @author gao
 * 
 */
public class AppLockService extends Service {

	private AppLockDao mdao;
	private ActivityManager mAm;
	private ArrayList<String> mList;
	private boolean isRunning=true;
	// 需要跳过验证的包名
	private String mSkipPackageName;
	private SkipReceiver mReceiver;
	private MyObserver mObserver;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mdao = AppLockDao.getInstance(this);
		mAm = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
		//查询所有加锁的程序
		mList = mdao.findAll();
		
		new Thread(){
			public void run() {
				while(isRunning){
					//获取正在运行的任务栈
					List<RunningTaskInfo> runningTasks=mAm.getRunningTasks(1);
					//最新的任务栈
					RunningTaskInfo runningTaskInfo=runningTasks.get(0);
					//任务栈顶的应用
					String packageName=runningTaskInfo.topActivity.getPackageName();
					
					//判断当前正在运行的程序是否是加锁程序
					if(mList.contains(packageName)&&!packageName.equals(mSkipPackageName)){
						//拦截程序，进入输入密码的页面
						Intent intent=new Intent();
						intent.setClass(getApplicationContext(), EncryptAppActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("packageName", packageName);
						startActivity(intent);
					}
					
					try {
						Thread.sleep(80);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			};
		}.start();
		//注册跳过验证的广播
		mReceiver = new SkipReceiver();
		IntentFilter filter=new IntentFilter("com.superGao.mobilesafe.SKIP_CHECK");
		registerReceiver(mReceiver, filter);
		//注册监听数据变化的内容观察者
		mObserver = new MyObserver(null);
		getContentResolver().registerContentObserver(Uri.parse("content://com.superGao.mobilesafe/change"), true, mObserver);
		
	}
	/**
	 * 停止程序锁检测
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		isRunning=false;
		unregisterReceiver(mReceiver);
		
		//注销数据库监听
		getContentResolver().unregisterContentObserver(mObserver);
		
	}
	
	// 密码成功后,获取需要跳过验证的包名
	class SkipReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			mSkipPackageName = intent.getStringExtra("packageName");
		}
	}
	/**
	 * 监听数据变化
	 * @author gao
	 *
	 */
	class MyObserver extends ContentObserver {

		public MyObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			// 重新获取最新的数据
			mList = mdao.findAll();
		}
	}
}
