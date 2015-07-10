package com.superGao.mobilesafe.utils;

import java.io.File;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

/**
 * 备份短信的工具类
 * 读取短信数据库中的数据
 * 将读到的数据写入xml文件中
 * address: 短信号码
 * date:短信时间
 * type: 类型 1:收到短信, 2表示发出的短信
 * body: 短信内容
 * @author gao
 *
 */
public class MsgUtil{

	public static void backupsMsg(Context ctx, File output, MsgCallback callback) {
		try {
			XmlSerializer xml = Xml.newSerializer();
			xml.setOutput(new FileOutputStream(output), "UTF-8");
			xml.startDocument("UTF-8", true);
			xml.startTag(null, "smss");

			Cursor cursor = ctx.getContentResolver().query(
					Uri.parse("content://sms"),
					new String[] { "address", "date", "type", "body" }, null,
					null, null);
			//获取短信总数
			int totalCount = cursor.getCount();
			//回传短信总数
			callback.preSmsBackup(totalCount);

			int progress = 0;
			while (cursor.moveToNext()) {
				xml.startTag(null, "sms");

				xml.startTag(null, "address");
				//获取短信号码
				String address = cursor.getString(cursor.getColumnIndex("address"));
				xml.text(address);
				xml.endTag(null, "address");

				xml.startTag(null, "date");
				//获取短信收发时间
				long date = cursor.getLong(cursor.getColumnIndex("date"));
				xml.text(date+"");
				xml.endTag(null, "date");

				xml.startTag(null, "type");
				//获取短信收发类型
				int type = cursor.getInt(cursor.getColumnIndex("type"));
				xml.text(type+"");
				xml.endTag(null, "type");

				xml.startTag(null, "body");
				//获取短信内容
				String body = cursor.getString(cursor.getColumnIndex("body"));
				xml.text(body);
				xml.endTag(null, "body");

				xml.endTag(null, "sms");

				// 更新备份进度
				progress++;
				//回传进度
				callback.onSmsBackup(progress);
				// 模拟耗时操作
				Thread.sleep(500);
			}

			xml.endTag(null, "smss");
			xml.endDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 备份短信的回调
	 * @author gao
	 *
	 */
	public interface MsgCallback {
		// 备份短信前
		public void preSmsBackup(int totalCount);

		// 备份过程中
		public void onSmsBackup(int progress);
	}
}
