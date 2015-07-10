package com.superGao.mobilesafe;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.superGao.domain.ProgressInfo;
import com.superGao.mobilesafe.utils.PrefUtils;
import com.superGao.mobilesafe.utils.ToastUtils;

import engine.ProgressInfoProvider;

/**
 * 进程管理
 * 
 * @author gao
 * 
 */
public class ProgressManageActivity extends Activity {

	private TextView tvHeander;
	private ListView lvList;
	private LinearLayout llLoading;
	private ArrayList<ProgressInfo> mList;
	private ArrayList<ProgressInfo> mUserList;
	private ArrayList<ProgressInfo> mSystemList;
	private ProcessAdapter mAdapter;
	private int mrunningProcessNum;
	private TextView mtvRunningNum;
	private TextView mtvAvailMemo;
	private long mAvailMemo;
	private long mTotalMemo;

	@Override  
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress_manage);

		mrunningProcessNum = ProgressInfoProvider.getRunningProcessNum(this);

		mtvRunningNum = (TextView) findViewById(R.id.tv_run);
		mtvAvailMemo = (TextView) findViewById(R.id.tv_momery);

		mtvRunningNum.setText(String.format("运行中的进程:%d个", mrunningProcessNum));
		mAvailMemo = ProgressInfoProvider.getAvailMemo(this);
		mTotalMemo = ProgressInfoProvider.getTotalMemo(this);
		
		Log.i("superGao", "mAvailMemo"+mAvailMemo);
		Log.i("superGao", "mTotalMemo"+mTotalMemo);  

		mtvAvailMemo.setText(String.format("剩余/总内存:%s/%s",
				Formatter.formatFileSize(this, mAvailMemo),
				Formatter.formatFileSize(this, mTotalMemo)));

		tvHeander = (TextView) findViewById(R.id.tv_header);
		lvList = (ListView) findViewById(R.id.lv_list);
		llLoading = (LinearLayout) findViewById(R.id.ll_loading);

		// 监听滑动
		lvList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			// 标题栏
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (mUserList != null && mSystemList != null) {
					if (firstVisibleItem >= mUserList.size() + 1) {
						tvHeander.setText("系统进程(" + mSystemList.size() + ")");
					} else {
						tvHeander.setText("用户进程(" + mUserList.size() + ")");
					}
				}

			}
		});
		// 点击应用
		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ProgressInfo info = mAdapter.getItem(position);
				if (info != null) {
					if (info.packageName.equals(getPackageName())) {// 跳过手机卫士
						return;
					}

					info.isChecked = !info.isChecked;

					// 更新复选框状态
					CheckBox cbCheck = (CheckBox) view
							.findViewById(R.id.cb_check);
					cbCheck.setChecked(info.isChecked);
				}

			}
		});

		// 初始化应用数据
		initData();
	}


	/**
	 * 初始化数据
	 */
	public void initData() {
		llLoading.setVisibility(View.VISIBLE);

		new Thread() {
			public void run() {
				//所有进程
				mList = ProgressInfoProvider
						.getRunningProgresses(getApplicationContext());
				//用户进程集合
				mUserList = new ArrayList<ProgressInfo>();
				//系统进程集合
				mSystemList = new ArrayList<ProgressInfo>();

				for (ProgressInfo info : mList) {
					if (info.isUserProgress) {
						mUserList.add(info);
					} else {
						mSystemList.add(info);
					}
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mAdapter = new ProcessAdapter();
						lvList.setAdapter(mAdapter);
						llLoading.setVisibility(View.GONE);
					}
				});
			}
		}.start();
	}

	class ProcessAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			boolean showSystem = PrefUtils.getBoolean("show_system", true,
					getApplicationContext());
			//判断是否显示系统进程
			if (showSystem) {
				return mUserList.size() + mSystemList.size() + 2;
			} else {
				return mUserList.size() + 1;
			}
		}

		@Override
		public ProgressInfo getItem(int position) {
			if (position == 0 || position == mUserList.size() + 1) {// 遇到标题栏,直接返回null
				return null;
			}
			if (position < mUserList.size() + 1) {
				return mUserList.get(position - 1);
			} else {
				return mSystemList.get(position - mUserList.size() - 2);// 需要减掉两个标题栏的占位
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		//返回布局类型，从0开始
		@Override
		public int getItemViewType(int position) {
			if (position == 0 || position == mUserList.size() + 1) {// 遇到标题栏
				return 0;
			} else {
				return 1;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			//判断布局类型
			switch (type) {
			//标题
			case 0:
				HeaderHolder headerHolder;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.list_item_header, null);
					headerHolder = new HeaderHolder();
					headerHolder.tvHeader = (TextView) convertView
							.findViewById(R.id.tv_header);
					convertView.setTag(headerHolder);
				} else {
					headerHolder = (HeaderHolder) convertView.getTag();
				}

				if (position == 0) {
					headerHolder.tvHeader.setText("用户进程(" + mUserList.size()
							+ ")");
				} else {
					headerHolder.tvHeader.setText("系统进程(" + mSystemList.size()
							+ ")");
				}
				break;
			//内容
			case 1:
				ViewHolder holder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.list_item_process_info, null);
					holder = new ViewHolder();
					holder.tvName = (TextView) convertView
							.findViewById(R.id.tv_name);
					holder.tvMemory = (TextView) convertView
							.findViewById(R.id.tv_memory);
					holder.ivIcon = (ImageView) convertView
							.findViewById(R.id.iv_icon);
					holder.cbCheck = (CheckBox) convertView
							.findViewById(R.id.cb_check);

					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}

				ProgressInfo info =getItem(position);
				holder.tvName.setText(info.name);
				holder.tvMemory.setText(Formatter.formatFileSize(
						getApplicationContext(), info.memory));
				holder.ivIcon.setImageDrawable(info.icon);

				if (info.packageName.equals(getPackageName())) {// 手机卫士进程不展示checkbox
					holder.cbCheck.setVisibility(View.INVISIBLE);
				} else {
					holder.cbCheck.setVisibility(View.VISIBLE);
					holder.cbCheck.setChecked(info.isChecked);
				}
				break;
			default:
				break;
			}

			return convertView;
		}
	}

	static class HeaderHolder {
		public TextView tvHeader;
		public TextView tvLocation;
		public ImageView ivIcon;
	}

	static class ViewHolder {
		public TextView tvName;
		public TextView tvMemory;
		public ImageView ivIcon;
		public CheckBox cbCheck;
	}

	/**
	 * 全选
	 */
	public void selectAll(View view){
		for(ProgressInfo info:mUserList){
			//跳过手机卫士
			if(info.packageName.equals(getPackageName())){
				continue;
			}
			
			info.isChecked=true;
		}
		
		for(ProgressInfo info: mSystemList){
			info.isChecked=true;
		}
		
		mAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 反选
	 */
	public void reverseSelect(View view) {
		for (ProgressInfo info : mUserList) {
			// 跳过手机卫士
			if (info.packageName.equals(getPackageName())) {
				continue;
			}

			info.isChecked = !info.isChecked;
		}

		for (ProgressInfo info : mSystemList) {
			info.isChecked = !info.isChecked;
		}

		mAdapter.notifyDataSetChanged();
	}
	 
	/**
	 * 一键清理
	 */
	public void killProgress(View view){
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		ArrayList<ProgressInfo> killedList = new ArrayList<ProgressInfo>();

		for (ProgressInfo info : mUserList) {
			if (info.isChecked) {
				am.killBackgroundProcesses(info.packageName);
				killedList.add(info);
			}
		}

		for (ProgressInfo info : mSystemList) {
			if (info.isChecked) {
				am.killBackgroundProcesses(info.packageName);// 杀死后台进程
				killedList.add(info);
			}
		}

		long savedMemory = 0;
		for (ProgressInfo processInfo : killedList) {
			if (processInfo.isUserProgress) {
				mUserList.remove(processInfo);
			} else {
				mSystemList.remove(processInfo);
			}

			savedMemory += processInfo.memory;
		}

		mAdapter.notifyDataSetChanged();

		// 提示用户
		ToastUtils
				.showToast(this, String.format("帮您杀死%d个后台进程,共节省%s空间!",
						killedList.size(),
						Formatter.formatFileSize(this, savedMemory)));

		// 更新数量和剩余内存
		mrunningProcessNum -= killedList.size();
		mAvailMemo += savedMemory;

		mtvRunningNum.setText(String.format("运行中的进程:%d个", mrunningProcessNum));
		mtvAvailMemo.setText(String.format("剩余/总内存:%s/%s",
				Formatter.formatFileSize(this, mAvailMemo),
				Formatter.formatFileSize(this, mTotalMemo)));
	}
	
	/**
	 * 设置
	 * @param view
	 */
	public void setting(View view) {
		Intent intent = new Intent(this, ProcessSettingActivity.class);
		startActivityForResult(intent, 0);
	}

	// 从设置页面跳回来
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 重新刷新listview
		mAdapter.notifyDataSetChanged();
	}
}
