package com.superGao.mobilesafe;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.superGao.db.dao.AppLockDao;
import com.superGao.domain.AppInfo;

import engine.AppInfoProvider;

/**
 * 程序锁
 * 
 * @author gao
 * 
 */
public class AppLockActivity extends Activity implements OnClickListener {

	private ArrayList<AppInfo> mList;
	private Button mbtnUnLock;
	private Button mbtnLock;
	private TextView mTvUnlock;
	private TextView mTvLock;
	private ListView mLvUnlock;
	private ListView mLvLock;
	private LinearLayout mLlUnlock;
	private LinearLayout mLlLock;
	private AppLockDao mdao;
	private ArrayList<AppInfo> mUnlockList;
	private ArrayList<AppInfo> mLockList;
	private LinearLayout llLoading;

	private AppLockAdapter mUnlockAdapter;
	private AppLockAdapter mLockAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);

		mbtnUnLock = (Button) findViewById(R.id.btn_unLock);
		mbtnLock = (Button) findViewById(R.id.btn_lock);
		mLvUnlock = (ListView) findViewById(R.id.lv_unLock);
		mLvLock = (ListView) findViewById(R.id.lv_lock);
		mTvUnlock = (TextView) findViewById(R.id.tv_unLock);
		mTvLock = (TextView) findViewById(R.id.tv_lock);
		mLlUnlock = (LinearLayout) findViewById(R.id.ll_unLock);
		mLlLock = (LinearLayout) findViewById(R.id.ll_lock);

		mbtnLock.setOnClickListener(this);
		mbtnUnLock.setOnClickListener(this);

		mdao = AppLockDao.getInstance(this);
		llLoading = (LinearLayout) findViewById(R.id.ll_loading);

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
				// 所有应用
				mList = AppInfoProvider.getInstallApp(getApplicationContext());
				// 未加锁应用集合
				mUnlockList = new ArrayList<AppInfo>();
				// 已加锁应用集合
				mLockList = new ArrayList<AppInfo>();

				for (AppInfo info : mList) {
					if (mdao.findAppLock(info.packageName)) {
						mLockList.add(info);
					} else {
						mUnlockList.add(info);
					}
				}
				// 在主线程刷新ui
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// 设置未加锁程序数据
						mUnlockAdapter = new AppLockAdapter(false);
						mLvUnlock.setAdapter(mUnlockAdapter);
						// 设置已加锁程序数据
						mLockAdapter = new AppLockAdapter(true);
						mLvLock.setAdapter(mLockAdapter);
						
						llLoading.setVisibility(View.GONE);
					}
				});
			}

		}.start();
	}

	/**
	 * 更新已加锁/未加锁数量显示
	 */
	private void updateAppLockNum() {
		mTvUnlock.setText(String.format("未加锁软件%d个", mUnlockList.size()));
		mTvLock.setText(String.format("已加锁软件%d个", mLockList.size()));
	}

	class AppLockAdapter extends BaseAdapter {

		private boolean isLock;
		private TranslateAnimation animLeft;
		private TranslateAnimation animRight;

		public AppLockAdapter(boolean isLock) {
			this.isLock = isLock;

			// 左移动画
			animLeft = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
					Animation.RELATIVE_TO_SELF, -1f,
					Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
					0);
			animLeft.setDuration(400);

			// 右移动画
			animRight = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
					Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF,
					0, Animation.RELATIVE_TO_SELF, 0);
			animRight.setDuration(400);

		}

		@Override
		public int getCount() {
			// 更新程序锁数量
			updateAppLockNum();

			if (isLock) {
				return mLockList.size();
			} else {
				return mUnlockList.size();
			}
		}

		@Override
		public AppInfo getItem(int position) {
			if (isLock) {
				return mLockList.get(position);
			} else {
				return mUnlockList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			if (convertView == null) {
				convertView = View.inflate(getBaseContext(),
						R.layout.list_item_app_lock, null);
				holder=new ViewHolder();
				holder.tvName = (TextView) convertView
						.findViewById(R.id.tv_name);
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.ivLock = (ImageView) convertView
						.findViewById(R.id.iv_lock);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final View view = convertView;
			final AppInfo info = getItem(position);

			holder.tvName.setText(info.name);
			holder.ivIcon.setImageDrawable(info.icon);

			if (isLock) {
				holder.ivLock.setImageResource(R.drawable.lock);
			} else {
				holder.ivLock.setImageResource(R.drawable.unlock);
			}

			holder.ivLock.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isLock) {
						// 程序已加锁，动画左移
						view.startAnimation(animLeft);
						animLeft.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationRepeat(Animation animation) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationEnd(Animation animation) {
								// 从数据库中移除加锁程序
								mdao.delete(info.packageName);
								// 从加锁列表中移除该程序
								mLockList.remove(info);
								// 将该程序加入未加锁程序列表
								mUnlockList.add(info);
								
								//刷新ListView
								mUnlockAdapter.notifyDataSetChanged();
								mLockAdapter.notifyDataSetChanged();
							}
						});
					} else {
						// 程序未加锁，动画右移
						view.startAnimation(animRight);
						animRight.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationRepeat(Animation animation) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationEnd(Animation animation) {
								// 向数据库中添加加锁程序数据
								mdao.addAppLock(info.packageName);
								// 从未加锁列表中移除该程序
								mUnlockList.remove(info);
								// 将该程序加入已加锁程序列表
								mLockList.add(info);
								//刷新ListView
								mUnlockAdapter.notifyDataSetChanged();
								mLockAdapter.notifyDataSetChanged();
							}
						});
					}

				}
			});

			return convertView;
		}

	}

	static class ViewHolder {
		public TextView tvName;
		public ImageView ivIcon;
		public ImageView ivLock;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_unLock:
			mLlUnlock.setVisibility(View.VISIBLE);
			mLlLock.setVisibility(View.GONE);

			mbtnUnLock.setBackgroundResource(R.drawable.tab_left_pressed);
			mbtnLock.setBackgroundResource(R.drawable.tab_right_default);
			break;
		case R.id.btn_lock:
			mLlUnlock.setVisibility(View.GONE);
			mLlLock.setVisibility(View.VISIBLE);

			mbtnUnLock.setBackgroundResource(R.drawable.tab_left_default);
			mbtnLock.setBackgroundResource(R.drawable.tab_right_pressed);
			break;

		default:
			break;
		}

	}

}
