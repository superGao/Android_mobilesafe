package com.superGao.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 创建程序锁数据库
 * @author gao
 *
 */
public class AppLockOpenHelper extends SQLiteOpenHelper {

	public AppLockOpenHelper(Context context) {
		super(context,"applock.db",null,1);
	}
	//创建数据库
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql="create table applock (_id integer primary key autoincrement,packagename varchar(50))";
		db.execSQL(sql);
	}
	//数据库更新
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
