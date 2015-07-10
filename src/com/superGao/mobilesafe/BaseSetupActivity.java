package com.superGao.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
/**
 * 实现页面的上下页切换
 * @author gao
 *
 */
public abstract class BaseSetupActivity extends Activity {
	//手势识别器
	private GestureDetector gd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		gd=new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
			//滑动效果
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				//竖直滑
				if(Math.abs(e1.getRawY()-e2.getRawY())>100){
					Toast.makeText(BaseSetupActivity.this,"亲，不能这样滑动哦", 0).show();
					return true;
				}
				
				if(e1.getRawX()-e2.getRawX()>200){
					//下一页
					showNextPage();
					return true;
				}
				
				if(e2.getRawX()-e1.getRawX()>200){
					//上一页
					showPreviousPage();
					return true;
				}
				
				if(Math.abs(velocityX)<150){
					Toast.makeText(BaseSetupActivity.this,"滑动速度稍慢", 0).show();
					return true;
				}
				
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}
	/**
	 * 显示下一页
	 */
	public abstract void showNextPage();
	
	public void nextPage(View view){
		showNextPage();
	}
	
	/**
	 * 显示上一页
	 */
	public abstract void showPreviousPage();
	
	public void previousPage(View view){
		showPreviousPage();
	}
	
	/**
	 * 将触摸Activity的事件赋予手势识别器处理
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gd.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
}
