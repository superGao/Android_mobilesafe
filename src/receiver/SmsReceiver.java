package receiver;

import service.CleanDataService;
import service.LocationService;
import service.LockScreenService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.superGao.mobilesafe.R;
import com.superGao.mobilesafe.utils.PrefUtils;
/**
 * 短信接收者
 * 拦截短信，发送指令
 * @author gao
 *
 */
public class SmsReceiver extends BroadcastReceiver {

	
	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] objs=(Object[])intent.getExtras().get("pdus");  
		
		for(Object object:objs){
			SmsMessage msg=SmsMessage.createFromPdu((byte[]) object);
			
			String originatingAddress = msg.getOriginatingAddress();
			String safeNumber=PrefUtils.getString("safeNumber","", context);  
			String messageBody = msg.getMessageBody();
			if(originatingAddress.contains(safeNumber)){
				//播放报警音乐
				if("#*alarm*#".equals(messageBody)){
					MediaPlayer player=MediaPlayer.create(context, R.raw.ylzs);
					//当前音量的最大音量
					player.setVolume(1f,1f);
					//单曲循环
					player.setLooping(true);
					player.start();
					
					//拦截短信
					abortBroadcast();
				}else if("#*location*#".equals(messageBody)){
					//地理位置追踪
					Intent location=new Intent(context,LocationService.class);  
					context.startService(location);
					
					String lastLocation=PrefUtils.getString("config", null, context);
					if(TextUtils.isEmpty(lastLocation)){
						//未获取到位置
						SmsManager.getDefault().sendTextMessage(originatingAddress, null, "geting loaction.....", null, null);
					}else{
						SmsManager.getDefault().sendTextMessage(originatingAddress, null, lastLocation, null,null);
					}
					//拦截短信
					abortBroadcast();
				}else if("#*wipedata*#".equals(messageBody)){
					//清除数据
					Intent location=new Intent(context,CleanDataService.class);
					context.startService(location);
					//拦截短信
					abortBroadcast();
				}else if("#*lockscreen*#".equals(messageBody)){
					//远程锁屏
					Intent location=new Intent(context,LockScreenService.class);
					context.startService(location);
					//拦截短信
					abortBroadcast();
				}
			}
			
		}
	}

}
