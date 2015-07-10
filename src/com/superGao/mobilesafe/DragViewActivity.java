package com.superGao.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.superGao.mobilesafe.utils.PrefUtils;


/**
 * 归属地位置修改
 * 
 * @author gao
 * 
 */
public class DragViewActivity extends Activity {

	private ImageView ivDrag;

	private int startX;
	private int startY;

	private int mScreenWidth;
	private int mScreenHeight;

	private TextView tvTop;

	private TextView tvBottom;
	
	long[] mHits = new long[2];//点击次数

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_view);

		tvTop = (TextView) findViewById(R.id.tv_top);
		tvBottom = (TextView) findViewById(R.id.tv_bottom);

		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		mScreenWidth = wm.getDefaultDisplay().getWidth();// 屏幕宽度
		mScreenHeight = wm.getDefaultDisplay().getHeight();// 屏幕高度

		ivDrag = (ImageView) findViewById(R.id.iv_drag);

		int lastX = PrefUtils.getInt("lastX", 0, this);// left
		int lastY = PrefUtils.getInt("lastY", 0, this);// top
		// 屏幕下方
		if (lastY > mScreenHeight / 2) {
			tvTop.setVisibility(View.VISIBLE);
			tvBottom.setVisibility(View.INVISIBLE);
		} else {
			tvTop.setVisibility(View.INVISIBLE);
			tvBottom.setVisibility(View.VISIBLE);
		}

		System.out.println("lastX:" + lastX);
		System.out.println("lastY:" + lastY);

		// 获取当前控件的布局参数
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivDrag
				.getLayoutParams();
		params.topMargin = lastY;
		params.leftMargin = lastX;
		
		//双击
		ivDrag.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
				mHits[mHits.length-1]=SystemClock.uptimeMillis();
				
				if(SystemClock.uptimeMillis()-mHits[0]<=500){
					//居中处理
					ivDrag.layout(mScreenWidth/2-ivDrag.getWidth()/2,
							ivDrag.getTop(),
							mScreenWidth/2+ivDrag.getWidth()/2,
							ivDrag.getBottom());
				}
			}
		});
		
		
		// 设置触摸监听
		ivDrag.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:// 按下动作

					// 起点
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:

					// 获取移动后坐标
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();

					int dx = endX - startX;
					int dy = endY - startY;

					// 根据偏移量,更新位置
					int l = ivDrag.getLeft() + dx;
					int t = ivDrag.getTop() + dy;
					int r = ivDrag.getRight() + dx;
					int b = ivDrag.getBottom() + dy;

					if (l < 0 || r > mScreenWidth) {
						return true;
					}
					// 避免布局 超出屏幕边界
					if (t < 0 || b > mScreenHeight - 20) {
						return true;
					}

					//更新提示框位置
					if (t > mScreenHeight / 2) {
						tvTop.setVisibility(View.VISIBLE);
						tvBottom.setVisibility(View.INVISIBLE);
					} else {
						tvTop.setVisibility(View.INVISIBLE);
						tvBottom.setVisibility(View.VISIBLE);
					}

					ivDrag.layout(l, t, r, b);

					//初始化起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					//最终坐标点
					PrefUtils.putInt("lastX", ivDrag.getLeft(),
							getApplicationContext());
					PrefUtils.putInt("lastY", ivDrag.getTop(),
							getApplicationContext());
					break;

				default:
					break;
				}

				return false;// 返回true表示消费此事件, 事件不会再进行传递
								//返回false，表示同时执行onTouch和 onClick事件
			}
		});
	}
}
