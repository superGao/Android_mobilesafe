package service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
/**
 * 清除数据
 * @author gao
 *
 */
public class CleanDataService extends Service {
	private DevicePolicyManager mDPM;
	private ComponentName mDeviceAdminSample;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		if (mDPM.isAdminActive(mDeviceAdminSample)) {
			mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);// 清除内部数据和sdcard
		} else {
			Toast.makeText(this, "您需要先激活超级管理员!", Toast.LENGTH_SHORT).show();
		}
	}
}
