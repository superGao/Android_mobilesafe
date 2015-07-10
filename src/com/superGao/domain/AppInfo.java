package com.superGao.domain;

import android.graphics.drawable.Drawable;

/**
 * 封装应用信息
 * @author gao
 *
 */
public class AppInfo {

	public String packageName;
	public String name;
	public Drawable icon;
	//是否是用户应用
	public boolean isUserApp;
	//应用是否在手机内存中
	public boolean isRom;
}
