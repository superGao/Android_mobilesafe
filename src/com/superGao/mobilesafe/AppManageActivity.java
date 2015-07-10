package com.superGao.mobilesafe;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.superGao.domain.AppInfo;

import engine.AppInfoProvider;

/**
 * 应用管理
 * 
 * @author gao
 * 
 */
public class AppManageActivity extends Activity implements OnClickListener {

	private TextView tvHeander;
	private ListView lvList;
	private LinearLayout llLoading;
	private ArrayList<AppInfo> mList;
	private ArrayList<AppInfo> mUserList;
	private ArrayList<AppInfo> mSystemList;
	private PopupWindow mPopupWindow;
	private View mPopupView;
	private AnimationSet mPopupAnimSet;
	private AppInfo mClickApp;
	private AppInfoAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appmanage);

		TextView tvRom = (TextView) findViewById(R.id.tv_rom);
		TextView tvSdCard = (TextView) findViewById(R.id.tv_sdcard);

		tvRom.setText("手机内存可用："
				+ getAvailSpace(Environment.getDataDirectory()
						.getAbsolutePath()));
		tvSdCard.setText("SD卡可用："
				+ getAvailSpace(Environment.getExternalStorageDirectory

				().getAbsolutePath()));

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
						tvHeander.setText("系统应用(" + mSystemList.size() + ")");
					} else {
						tvHeander.setText("用户应用(" + mUserList.size() + ")");
					}
				}

			}
		});
		// 点击应用
		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				AppInfo info = mAdapter.getItem(position);

				if (info != null) {
					mClickApp = info;
					//显示对话框
					showPopupWindow(view);
				}

			}
		});

		// 初始化应用数据
		initData();
	}

	/**
	 * 获取可用空间
	 */
	private String getAvailSpace(String path) {
		StatFs sf = new StatFs(path);
		// 获取可用存储块数量
		long blocks = sf.getAvailableBlocks();
		// 获取每个存储块的大小
		long blockSize = sf.getBlockSize();
		// 计算可用空间
		long availSpace = blocks * blockSize;
		// 将可用空间字节转为相应带单位的大小
		return Formatter.formatFileSize(this, availSpace);
	}

	/**
	 * 初始化数据
	 */
	public void initData() {
		llLoading.setVisibility(View.VISIBLE);

		new Thread() {
			public void run() {
			}

			{
				// 所有应用
				mList = AppInfoProvider.getInstallApp(getApplicationContext());
				// 用户应用集合
				mUserList = new ArrayList<AppInfo>();
				mSystemList = new ArrayList<AppInfo>();

				for (AppInfo info : mList) {
					if (info.isUserApp) {
						mUserList.add(info);
					} else {
						mSystemList.add(info);
					}
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mAdapter = new AppInfoAdapter();
						lvList.setAdapter(mAdapter);
						llLoading.setVisibility(View.GONE);
					}
				});
			}
		}.start();
	}

	class AppInfoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mUserList.size() + mSystemList.size() + 2;// 增加两个标题栏
		}

		@Override
		public AppInfo getItem(int position) {
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

		// 返回布局类型的个数, 就会缓存两种convertView
		@Override
		public int getViewTypeCount() {
			return 2;
		}

		// 根据当前位置,返回相应布局类型, 必须从0开始计算
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
			// 首先要判断当前布局类型
			int type = getItemViewType(position);
			switch (type) {
			case 0:
				// 标题栏
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
					headerHolder.tvHeader.setText("用户应用(" + mUserList.size()
							+ ")");
				} else {
					headerHolder.tvHeader.setText("系统应用(" + mSystemList.size()
							+ ")");
				}

				break;
			case 1:
				// 显示应用列表
				ViewHolder holder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.list_item_appinfo, null);
					holder = new ViewHolder();
					holder.tvName = (TextView) convertView
							.findViewById(R.id.tv_name);
					holder.tvLocation = (TextView) convertView
							.findViewById(R.id.tv_location);
					holder.ivIcon = (ImageView) convertView
							.findViewById(R.id.iv_icon);

					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}

				AppInfo info = getItem(position);
				holder.tvName.setText(info.name);
				holder.ivIcon.setImageDrawable(info.icon);

				if (info.isRom) {
					holder.tvLocation.setText("手机内存");
				} else {
					holder.tvLocation.setText("外置存储器");
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
		public TextView tvLocation;
		public ImageView ivIcon;
	}

	/**
	 * 每一条应用的点击事件
	 */
	@Override
	public void onClick(View v) {
		// 消失弹窗
		mPopupWindow.dismiss();
		switch (v.getId()) {
		case R.id.tv_uninstall:
			// 卸载应用
			uninstallApp();
			break;
		case R.id.tv_launch:
			// 启动应用
			launchApp();
			break;
		case R.id.tv_share:
			// 分享应用
			shareApp();
			break;
		default:
			break;
		}
	}

	/**
	 * 分享应用
	 */
	private void shareApp() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT,
				"自从用了这个应用，整个人都精神了! 快来下载吧:https://play.google.com/store/apps/details?id="
						+ mClickApp.packageName);
		startActivity(intent);

	}

	/**
	 * 启动应用
	 */
	private void launchApp() {
		PackageManager pm = getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(mClickApp.packageName);
		startActivity(intent);

	}

	/**
	 * 卸载应用
	 */
	private void uninstallApp() {
		Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setData(Uri.parse("package:" + mClickApp.packageName));
		startActivityForResult(intent, 0);

	}

	/**
	 * 卸载完成后重新加载数据
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		initData();
	}

	/**
	 * 点击应用的小弹窗
	 */
	protected void showPopupWindow(View view) {
		if (mPopupWindow == null) {
			mPopupView = View.inflate(this, R.layout.popup_appinfo, null);
			mPopupWindow = new PopupWindow(mPopupView,
					LayoutParams.WRAP_CONTENT,

					LayoutParams.WRAP_CONTENT, true);
			mPopupWindow.setBackgroundDrawable(new ColorDrawable());

			TextView tvUninstall = (TextView) mPopupView
					.findViewById(R.id.tv_uninstall);
			TextView tvLaunch = (TextView) mPopupView
					.findViewById(R.id.tv_launch);
			TextView tvShare = (TextView) mPopupView
					.findViewById(R.id.tv_share);

			tvUninstall.setOnClickListener(this);
			tvLaunch.setOnClickListener(this);
			tvShare.setOnClickListener(this);

			// 渐变动画
			AlphaAnimation animAlpha = new AlphaAnimation(0, 1);
			animAlpha.setDuration(500);

			// 缩放
			ScaleAnimation animScale = new ScaleAnimation(0, 1, 0, 1,
					Animation.RELATIVE_TO_SELF, 0,

					Animation.RELATIVE_TO_SELF, 0.5f);
			animScale.setDuration(500);

			// 动画集合
			mPopupAnimSet = new AnimationSet(true);
			mPopupAnimSet.addAnimation(animAlpha);
			mPopupAnimSet.addAnimation(animScale);
		}

		// 启动动画
		mPopupView.startAnimation(mPopupAnimSet);
		mPopupWindow.showAsDropDown(view, 60, -view.getHeight());

	}

}
