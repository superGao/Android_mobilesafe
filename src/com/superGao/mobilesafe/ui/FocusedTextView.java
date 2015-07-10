package com.superGao.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * 自定义TextView
 * @author gao
 *
 */
public class FocusedTextView extends TextView {
	//带有样式时调用
	public FocusedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	//布局初始化时调用
	public FocusedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	//new 对象时调用
	public FocusedTextView(Context context) {
		super(context);
	}
	
	//使当前控件一直处于获取焦点的状态
	public boolean isFocused() {
		return true;
	}
	

}
