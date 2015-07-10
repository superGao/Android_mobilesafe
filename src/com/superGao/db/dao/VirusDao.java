package com.superGao.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * 封装查询病毒的数据库
 * 
 * @author gao
 * 
 */
public class VirusDao {

	public static final String PATH = "data/data/com.superGao.mobilesafe/files/antivirus.db";

	/**
	 * 判断是否是病毒
	 * @param md5
	 * @return
	 */
	public static boolean isVirus(String md5) {
		SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = database.rawQuery(
				"select desc from datable where md5=?", new String[] { md5 });

		boolean isVirus = false;
		if (cursor.moveToFirst()) {
			isVirus = true;
		}

		cursor.close();
		database.close();

		return isVirus;
	}
}
