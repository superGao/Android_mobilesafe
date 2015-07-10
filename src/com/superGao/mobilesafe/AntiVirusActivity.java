package com.superGao.mobilesafe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.superGao.db.dao.VirusDao;
import com.superGao.mobilesafe.utils.MD5Utils;

/**
 * 手机杀毒
 * 
 * @author gao
 * 
 */
public class AntiVirusActivity extends Activity {
	protected static final int UPDATE_STATUS = 0;
	protected static final int SCAN_FINISH = 1;
	private ImageView ivScanning;
	private TextView tvStatus;
	private LinearLayout llContainer;
	private ProgressBar pbProgress;

	
	private ArrayList<ScanInfo> mVirusList=new ArrayList<AntiVirusActivity.ScanInfo>();
	
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg){
			switch (msg.what) {
			case UPDATE_STATUS:
				ScanInfo info=(ScanInfo)msg.obj;
				tvStatus.setText("正在扫描："+info.name);
				
				TextView view=new TextView(getApplicationContext());
				if(info.isVirus){
					view.setText("亲，发现病毒了，请尽快查杀！"+info.name);
					view.setTextColor(Color.RED);
				}else{
					view .setText("恭喜，没有发现病毒："+info.name);
					view.setTextColor(Color.parseColor("#cc00ff"));
				}
				//在线性布局的第一个位置添加textview
				llContainer.addView(view,0);
				break;
			case SCAN_FINISH:
				tvStatus.setText("扫描完毕");
				// 停止扫描动画
				ivScanning.clearAnimation();
				// 判断是否查杀到病毒
				if (mVirusList.isEmpty()) {
					showSafeDialog();
				} else {
					showAlertDialog();
				}
				break;

			default:
				break;

			}
		};
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);
		
		ivScanning = (ImageView) findViewById(R.id.iv_scanning);
		tvStatus = (TextView)findViewById(R.id.tv_status);
		llContainer = (LinearLayout)findViewById(R.id.ll_container);
		pbProgress = (ProgressBar)findViewById(R.id.pb_progress);
		
		
		
		// 扫描的动画
		RotateAnimation ra = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setDuration(1000);
		//无限循环
		ra.setRepeatCount(Animation.INFINITE);
		//匀速
		ra.setInterpolator(new LinearInterpolator());
		//开启动画
		ivScanning.startAnimation(ra);
		
		//更新进度条
		new Thread(){
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//获取apk信息
				PackageManager pm=getPackageManager();
				List<PackageInfo> apkInfo=pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
				pbProgress.setMax(apkInfo.size());
				
				int progress=0;
				Random random=new Random();
				for(PackageInfo packageInfo:apkInfo){
					ScanInfo info=new ScanInfo();
					info.packageName=packageInfo.packageName;
					
					//将apk MD5加密
					//apk路径
					String path=packageInfo.applicationInfo.sourceDir;
					String md5=MD5Utils.encodeFile(path);
					//获取apk名称
					String name=packageInfo.applicationInfo.loadLabel(pm).toString();
					info.name=name;
					
					//判断是否是病毒
					boolean isVirus=VirusDao.isVirus(md5);
					if(isVirus){
						//是病毒
						info.isVirus=true;
						mVirusList.add(info);
					}else{
						info.isVirus=false;
					}
					
					progress++;
					pbProgress.setProgress(progress);
					
					//发送消息
					Message msg=Message.obtain();
					msg.obj=info;
					msg.what=UPDATE_STATUS;
					mHandler.sendMessage(msg);
					
					try {
						Thread.sleep(50+random.nextInt(100));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
				mHandler.sendEmptyMessage(SCAN_FINISH);
			};
		}.start();
	}
	
	/**
	 * 发现病毒时弹出警告弹窗
	 */
	protected void showAlertDialog(){
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setTitle("温馨提示");
		builder.setMessage("发现"+mVirusList.size()+"个病毒，请尽快干掉");
		builder.setPositiveButton("干掉", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				for(ScanInfo info:mVirusList){
					//卸载apk
					Intent intent=new Intent(Intent.ACTION_DELETE);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					intent.setData(Uri.parse("package:"+info.packageName));
					startActivity(intent);
				}
			}
		});
		
		builder.setNegativeButton("我喜欢病毒。。。", null);
		builder.show();
	}
	
	/**
	 * 封装扫描的应用信息
	 * @author gao
	 *
	 */
	class ScanInfo {
		public String packageName;
		public String name;
		public boolean isVirus;
	}
	
	/**
	 * 病毒扫描完毕后后让用户选择是否选择再次杀毒
	 */
	protected void showSafeDialog(){
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setTitle("温馨提示");
		builder.setMessage("亲，扫毒完成了。。。是否再次扫毒？");
		builder.setPositiveButton("再扫一次", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//再次进入缓存清理页面
				startActivity(new Intent(getApplicationContext(),AntiVirusActivity.class));
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
