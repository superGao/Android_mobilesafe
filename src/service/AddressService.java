package service;

import receiver.BootCompleteReceiver;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.superGao.db.dao.AddressDao;
import com.superGao.mobilesafe.R;
import com.superGao.mobilesafe.utils.PrefUtils;

/**
 * 归属地显示的服务
 * 
 * @author gao
 * 
 */
public class AddressService extends Service {

	private TelephonyManager mTM;
	private MyListener mListener;
	private OutCallReceiver mReceiver;
	private WindowManager mWM;
	private View mView;

	private int startX;
	private int startY;

	private int mScreenWidth;
	private int mScreenHeight;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 监听来电
		mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		mListener = new MyListener();
		mTM.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);

		// 监听去电
		mReceiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(mReceiver, filter);
		
		registService();

	}
	/**
	 * 注册广播
	 */
	private void registService(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_TIME_TICK);
		BootCompleteReceiver receiver=new BootCompleteReceiver();
		registerReceiver(receiver, filter);
	}
	

	class MyListener extends PhoneStateListener {
		// 电话状态发生变化
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 电话铃响
				String address = AddressDao.getAddress(incomingNumber);
				showToast(address);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// 摘机
				break;
			case TelephonyManager.CALL_STATE_IDLE:// 电话空闲
				if (mWM != null && mView != null) {
					mWM.removeView(mView);// 电话挂断后,移除窗口布局
				}
				break;

			default:
				break;
			}
		}
	}

	// 去电广播
	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String number = getResultData();// 获取去电电话号码
			String address = AddressDao.getAddress(number);
			showToast(address);
		}
	}

	/**
	 * 需要权限:android.permission.SYSTEM_ALERT_WINDOW
	 * 
	 * @param text
	 */
	private void showToast(String text) {
		mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

		mScreenWidth = mWM.getDefaultDisplay().getWidth();// 屏幕宽度
		mScreenHeight = mWM.getDefaultDisplay().getHeight();// 屏幕高度

		// 初始化布局参数
		final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		params.gravity = Gravity.CENTER + Gravity.CENTER;
		// 修改窗口布局位置
		int lastX = PrefUtils.getInt("lastX", 0, this);
		int lastY = PrefUtils.getInt("lastY", 0, this);
		// params.x 基于重心的x偏移
		params.x = lastX;
		params.y = lastY;

		mView = View.inflate(this, R.layout.custom_toast, null);
		TextView tvAddress = (TextView) mView.findViewById(R.id.tv_address);
		tvAddress.setText(text);

		// 根据用户设置,更新布局背景
		int which = PrefUtils.getInt("address_style", 0, this);

		// 背景图片id数组
		int[] bgs = new int[] { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };

		tvAddress.setBackgroundResource(bgs[which]);

		mView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					//起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					//移动后坐标
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();

					//偏移量
					int dx = endX - startX;
					int dy = endY - startY;

					// 根据偏移量,更新位置
					params.x = params.x + dx;
					params.y = params.y + dy;

					System.out.println("x=" + params.x);
					System.out.println("y=" + params.y);

					// 防止坐标越界
					if (params.x < 0) {
						params.x = 0;
					}

					// 防止坐标越界
					if (params.x + mView.getWidth() > mScreenWidth) {
						params.x = mScreenWidth - mView.getWidth();
					}

					// 防止坐标越界
					if (params.y < 0) {
						params.y = 0;
					}

					// 防止坐标越界
					if (params.y + mView.getHeight() > mScreenHeight - 20) {
						params.y = mScreenHeight - 20 - mView.getHeight();
					}

					mWM.updateViewLayout(mView, params);// 更新最新位置

					// 重新初始化起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					PrefUtils
							.putInt("lastX", params.x, getApplicationContext());
					PrefUtils
							.putInt("lastY", params.y, getApplicationContext());
					break;

				default:
					break;
				}

				return true;
			}
		});

		// 给窗口添加布局
		mWM.addView(mView, params);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 停止监听来电
		mTM.listen(mListener, PhoneStateListener.LISTEN_NONE);

		// 注销去电广播
		unregisterReceiver(mReceiver);
		mReceiver = null;

	}

}
