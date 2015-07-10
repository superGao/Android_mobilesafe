package com.superGao.db.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * 程序锁数据库封装 懒汉模式
 * 
 * @author gao
 * 
 */
public class AppLockDao {
	private static AppLockDao sInstance = null;
	private AppLockOpenHelper mHelper;
	private Context mContext;

	private AppLockDao(Context con) {
		mHelper = new AppLockOpenHelper(con);
		mContext = con;
	}

	public static AppLockDao getInstance(Context con) {
		if (sInstance == null) {
			// 同步锁
			synchronized (AppLockDao.class) {
				if (sInstance == null) {
					sInstance = new AppLockDao(con);
				}
			}
		}
		return sInstance;
	}

	/**
	 * 增加程序锁
	 * 向数据库中添加字段
	 */
	public void addAppLock(String packageName){
		SQLiteDatabase database=mHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("packagename", packageName);
		database.insert("applock", null, values);
		database.close();
		//内容观察者
		mContext.getContentResolver().notifyChange(Uri.parse("content://com.superGao.mobilesafe/change"), null);
		
	}
	/**
	 * 删除程序锁
	 */
	public void delete(String packageName){
		SQLiteDatabase database=mHelper.getWritableDatabase();
		database.delete("applock", "packagename=?", new String[]{packageName});
		database.close();
	}
	
	/**
	 * 查询程序锁
	 */
	public boolean findAppLock(String packageName){
		SQLiteDatabase database=mHelper.getWritableDatabase();
		Cursor cursor=database.query("applock", new String[]{"packagename"}, "packagename=?", new String[]{packageName}, null, null, null);
		
		boolean exist=false;
		if(cursor.moveToFirst()){
			exist=true;
		}
		
		cursor.close();
		database.close();
		return exist;
	}
	
	/**
	 * 查询所有加锁程序
	 */
	public ArrayList<String> findAll(){
		SQLiteDatabase database=mHelper.getWritableDatabase();
		Cursor cursor=database.query("applock", new String[]{"packagename"}, null, null, null, null, null);
		ArrayList<String> list=new ArrayList<String>();
		
		while(cursor.moveToNext()){
			String packageName=cursor.getString(0);
			list.add(packageName);
		}
		
		cursor.close();
		database.close();
		return list;
	}
}
