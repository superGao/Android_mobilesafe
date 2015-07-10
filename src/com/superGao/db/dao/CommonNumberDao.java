package com.superGao.db.dao;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 常用号码查询数据库封装
 * 
 * @author gao
 * 
 */
public class CommonNumberDao {

	private static final String PATH = "/data/data/com.superGao.mobilesafe/files/commonnum.db";


	public static ArrayList<GroupInfo> getCommonNumberGroups() {
		SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,SQLiteDatabase.OPEN_READONLY);

		Cursor cursor = database.rawQuery("select name,idx from classlist",null);

		ArrayList<GroupInfo> list = new ArrayList<GroupInfo>();
		while (cursor.moveToNext()) {
			GroupInfo info = new GroupInfo();
			String name = cursor.getString(0);
			String idx = cursor.getString(1);
			info.name = name;
			info.idx = idx;
			info.children = getCommonNumberChildren(idx, database);
			list.add(info);
		}

		cursor.close();
		database.close();// 关闭数据库

		return list;
	}


	public static ArrayList<ChildInfo> getCommonNumberChildren(String idx,
			SQLiteDatabase database) {
		Cursor cursor = database.rawQuery("select number,name from table" + idx, null);

		ArrayList<ChildInfo> list = new ArrayList<CommonNumberDao.ChildInfo>();
		while (cursor.moveToNext()) {
			ChildInfo info = new ChildInfo();
			String name = cursor.getString(1);
			String number = cursor.getString(0);
			info.name = name;
			info.number = number;

			list.add(info);
		}

		return list;
	}

	public static class GroupInfo {
		public String name;
		public String idx;
		public ArrayList<ChildInfo> children;
	}

	public static class ChildInfo {
		public String name;
		public String number;
	}
}
