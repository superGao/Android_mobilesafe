package com.superGao.mobilesafe;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * 选择联系人
 * 
 * @author gao
 * 
 */
public class SelectContactActivity extends Activity {
	private ListView lvContact;
	private ArrayList<HashMap<String, String>> contacts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);

		lvContact = (ListView) findViewById(R.id.lv_contact);
		contacts = readContacts();

		SimpleAdapter adapter = new SimpleAdapter(this, contacts,
				R.layout.list_contact_item, new String[] { "name", "phone" },
				new int[] { R.id.tv_name, R.id.tv_phone });
		lvContact.setAdapter(adapter);

		// 每一条联系人的点击事件
		lvContact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HashMap<String, String> map = contacts.get(position);
				String phone = map.get("phone");

				// Intent跳转时传递数据
				Intent intent = new Intent();
				intent.putExtra("phone", phone);

				setResult(Activity.RESULT_OK, intent);

				finish();

			}

		});
	}

	/**
	 * 读取联系人
	 * 
	 * @return
	 */
	private ArrayList<HashMap<String, String>> readContacts() {
		ArrayList<HashMap<String, String>> contacts = new ArrayList<HashMap<String, String>>();

		// 读取数据库中联系人相关表中的数据
		// 内容解析器
		ContentResolver resolver = getContentResolver();
		// raw_contacts表的uri
		Uri uriRaw = Uri.parse("content://com.android.contacts/raw_contacts");
		// date表的uri
		Uri uriData = Uri.parse("content://com.android.contacts/data");
		// 获取raw_contacts表中的contact_id这一列的数据
		Cursor cursor = resolver.query(uriRaw, new String[] { "contact_id" },
				null, null, null);

		if (cursor != null) {
			while (cursor.moveToNext()) {
				String id = cursor.getString(0);
				// 获取data表中指定列的数据
				Cursor dataCursor = resolver.query(uriData, new String[] {
						"data1", "mimetype" }, "raw_contact_id=?",
						new String[] { id }, null);
				if (dataCursor != null) {
					// 将查询到的数据存入HashMap集合中
					HashMap<String, String> map = new HashMap<String, String>();
					while (dataCursor.moveToNext()) {
						String data = dataCursor.getString(0);
						String mimeType = dataCursor.getString(1);

						if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) {
							// 存入电话号码
							map.put("phone", data);
						} else if ("vnd.android.cursor.item/name"
								.equals(mimeType)) {
							// 存入联系人姓名
							map.put("name", data);
						}
					}
					//释放指针资源
					dataCursor.close();
					//清洗数据,去掉空数据
					if(!TextUtils.isEmpty(map.get("name"))&&!TextUtils.isEmpty(map.get("phone"))){
						// 将map集合存入ArrayList集合中
						contacts.add(map);
					}
				}
			}
		}
		//释放指针资源
		cursor.close();
		
		return contacts;
	}
}
