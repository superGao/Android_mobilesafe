package service;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.superGao.db.dao.BlackNumberDao;
/**
 * 黑名单服务
 * 拦截黑名单短信，电话，删除通话记录
 * @author gao
 *
 */
public class BlackNumberService extends Service {

	private BlackNumberDao mdao;
	private msgReceiver mReceiver;
	private TelephonyManager mTm;
	private phoneListener mphListener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mdao = BlackNumberDao.getInstance(this);
		
		//动态注册广播接受者（优先级高于静态注册）
		mReceiver = new msgReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(mReceiver, filter);
		
		//拦截电话
		mphListener = new phoneListener();
		mTm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
		mTm.listen(mphListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	/**
	 * 短信的广播接受者
	 * 拦截短信
	 */
	class msgReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] objs=(Object[])intent.getExtras().get("pdus");
			for(Object obj:objs){
				SmsMessage msg=SmsMessage.createFromPdu((byte[])obj);
				//来短信的号码
				String phoneNumber=msg.getOriginatingAddress();
				//短信内容
				String content=msg.getMessageBody();
				
				//判断来短信的号码是否是黑名单号码
				boolean blackNumber=mdao.find(phoneNumber);
				if(blackNumber){
					//判断拦截模式，1：电话，2：短信，3：全部
					int mode=mdao.findMode(phoneNumber);
					
					if(mode>1){
						//拦截短信
						abortBroadcast();
					}
					
				}
				
			}
		}
		
	}
	
	/**
	 * 监听电话状态
	 * 拦截电话
	 */
	class phoneListener extends PhoneStateListener{
		 private callObserver mObserver;

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {//电话状态改变
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			//响铃
			case  TelephonyManager.CALL_STATE_RINGING:
				//判断来电号码是否是黑名单中的号码
				boolean number=mdao.find(incomingNumber);
				
				if(number){
					//拦截模式
					int mode=mdao.findMode(incomingNumber);
					if(mode==1||mode==3){
						//拦截电话
						stopPhone();
						
						mObserver = new callObserver(new Handler(), incomingNumber);
						getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true,
								mObserver);
					}
				}
				break;

			default:
				break;
			}
			
			
		}
	}
	/**
	 * 挂断电话
	 */
	public void stopPhone() {
		//通过反射调用被隐藏的serviceManager
		try {
			Class clazz=Class.forName("android.os.ServiceManager");
			Method mothod= clazz.getMethod("getService", String.class);
			IBinder b = (IBinder) mothod.invoke(null, TELEPHONY_SERVICE);
			ITelephony service = ITelephony.Stub.asInterface(b);
			//挂断电话
			service.endCall();
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
	
	/**
	 * 内容观察者
	 * 观察通话记录
	 * 
	 */
	class callObserver extends ContentObserver{
		private String phone;
		
		public callObserver(Handler handler,String number) {
			super(handler);
			this.phone=number;
		}


		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			//删除通话记录
			deleteCallRecord(phone);
			//注册内容观察者
			
		}
		
	}
	
	/**
	 * 删除通话记录
	 * @param phone
	 */
	public void deleteCallRecord(String phone) {
		getContentResolver().delete(Uri.parse("content://call_log/calls"), "number=?", new String[]{phone});
	}
	
}
