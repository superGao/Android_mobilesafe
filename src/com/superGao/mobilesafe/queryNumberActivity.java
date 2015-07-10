package com.superGao.mobilesafe;

import java.util.ArrayList;

import com.superGao.db.dao.CommonNumberDao;
import com.superGao.db.dao.CommonNumberDao.ChildInfo;
import com.superGao.db.dao.CommonNumberDao.GroupInfo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

/**
 * 常用号码查询
 * @author gao
 *
 */
public class queryNumberActivity extends Activity {

	private ExpandableListView elvList;
	private CommonNumberAdapter mAdapter;
	private ArrayList<GroupInfo> mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_number);
		elvList = (ExpandableListView) findViewById(R.id.elv_list);

		mList = CommonNumberDao.getCommonNumberGroups();// 获取数据库信息

		mAdapter = new CommonNumberAdapter();
		elvList.setAdapter(mAdapter);
		//子节点的点击事件监听
		elvList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {

				// 跳转到拨打电话页面
				ChildInfo childInfo = mAdapter.getChild(groupPosition,childPosition);
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + childInfo.number));
				startActivity(intent);

				return false;
			}
		});
	}

	class CommonNumberAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			return mList.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mList.get(groupPosition).children.size();
		}

		@Override
		public GroupInfo getGroup(int groupPosition) {
			return mList.get(groupPosition);
		}

		@Override
		public ChildInfo getChild(int groupPosition, int childPosition) {
			return mList.get(groupPosition).children.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView tvGroup = new TextView(getApplicationContext());
			tvGroup.setTextColor(Color.RED);
			tvGroup.setTextSize(20);
			GroupInfo info = getGroup(groupPosition);
			tvGroup.setText("       " + info.name);
			return tvGroup;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView tvChild = new TextView(getApplicationContext());
			tvChild.setTextColor(Color.BLUE);
			tvChild.setTextSize(18);
			ChildInfo info = getChild(groupPosition, childPosition);
			tvChild.setText(info.name + "\n" + info.number);
			return tvChild;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}
}
