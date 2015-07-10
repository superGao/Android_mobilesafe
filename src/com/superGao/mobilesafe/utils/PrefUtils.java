package com.superGao.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharePreference工具类
 * 
 * @author gao
 * 
 */
public class PrefUtils {

	public static void putBoolean(String key, boolean value, Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		sp.edit().putBoolean(key, value).commit();
	}

	public static boolean getBoolean(String key, boolean defValue, Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		return sp.getBoolean(key, defValue);
	}

	public static void putString(String key, String value, Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		sp.edit().putString(key, value).commit();
	}

	public static String getString(String key, String defValue, Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		return sp.getString(key, defValue);
	}

	public static void putInt(String key, int value, Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		sp.edit().putInt(key, value).commit();
	}

	public static int getInt(String key, int defValue, Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		return sp.getInt(key, defValue);
	}
	
	//当用户取消绑定SIM卡时，删除原有SIM卡的序列号
	public static void removeKey(String key,Context ctx){
		SharedPreferences sp = ctx.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		sp.edit().remove(key).commit();
	}
	
	
}
