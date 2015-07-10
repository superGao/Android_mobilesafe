package com.superGao.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * 根据号码查询数据库
 * 
 * @author gao
 * 
 */
public class AddressDao {

	public static final String PATH = "data/data/com.superGao.mobilesafe/files/address.db";

	public static String getAddress(String number) {
		String address = "未知号码";

		SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);
		if (number.matches("^1[345678]\\d{9}$")) {
			Cursor cursor = db
					.rawQuery(
							"select location from data2 where id=(select outkey from data1 where id=?)",
							new String[] { number.substring(0, 7) });
			if (cursor != null) {
				if (cursor.moveToNext()) {
					address = cursor.getString(0);
					System.out.println("address:" + address);
				}

				cursor.close();
			}
		} else if(number.matches("^\\d+$")){//是否是数字
			switch (number.length()) {
			case 3:
				address = "警匪";
				break;
			case 4:
				address = "模拟器";
				break;
			case 5:
				address = "客服";
				break;
			case 7:
			case 8:
				address = "本地电话";
				break;
			default:
				if (number.startsWith("0") && number.length() > 10) {
					Cursor cursor = db.rawQuery(
							"select location from data2 where area=?",
							new String[] { number.substring(1, 4) });

					if (cursor.moveToNext()) {
						address = cursor.getString(0);
					}

					cursor.close();

					if (TextUtils.isEmpty(address)) {
						cursor = db.rawQuery(
								"select location from data2 where area=?",
								new String[] { number.substring(1, 3) });
						if (cursor.moveToNext()) {
							address = cursor.getString(0);
						}

						cursor.close();
					}
				}
				break;
			}
		}
		
		db.close();

		return address;
	}
}
