package service;

import receiver.MyAdminReceiver;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
/**
 * 锁屏功能
 * @author gao
 *
 */
public class LockScreenService extends Service {
	private DevicePolicyManager mDPM;
	private ComponentName mDeviceAdminSample;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		//立即锁屏
		// 设备策略管理器
		mDPM = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
		// 初始化管理员组件
		mDeviceAdminSample = new ComponentName(this, MyAdminReceiver.class);
		
		if (mDPM.isAdminActive(mDeviceAdminSample)) {
			mDPM.lockNow();// 立即锁屏
			mDPM.resetPassword("666666", 0);
		} else {
			Toast.makeText(this, "超级管理员权限未激活!", Toast.LENGTH_SHORT).show();
		}
	}

}
