package receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.superGao.mobilesafe.utils.PrefUtils;

/**
 * 手机重启广播的接收者
 * @author gao
 * 
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		boolean protect=PrefUtils.getBoolean("protecting", false,context);
		//获取SIM卡序列号
		String bindSim = PrefUtils.getString("bind_card", null, context);
		//检测手机防盗是否开启
		if(!protect){
			return;
		}
		
		if (!TextUtils.isEmpty(bindSim)) {
			// 获取当前最新的SIM卡号
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String sim = tm.getSimSerialNumber() + "xx";
			//比较新的SIM卡序列号和原有SIM卡序列号是否一致
			if (bindSim.equals(sim)) {
				Log.i("superGao", "sim卡未发生变化,安全");
			} else {
				// 发送报警短信
				Log.i("superGao", "sim卡发生变化,危险!");
				
				SmsManager msg=SmsManager.getDefault();
				String phone = PrefUtils.getString("safeNumber", "",context);
				msg.sendTextMessage(phone, null, "SIM card changed!", null, null);
				
			}
		}
		
	}

}
