package com.superGao.domain;

import android.graphics.drawable.Drawable;

/**
 * 封装进程信息
 * @author gao
 *
 */
public class ProgressInfo {

	public String packageName;
	public String name;
	public Drawable icon;
	public long memory;
	//是否是用户进程
	public boolean isUserProgress;
	//应用是否选中
	public boolean isChecked;
}
