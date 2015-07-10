package com.superGao.mobilesafe;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.superGao.db.dao.BlackNumberDao;
import com.superGao.domain.BlackNumberInfo;
import com.superGao.mobilesafe.utils.ToastUtils;


/**
 * 
 * @author gao
 * 
 */
public class BlackNumberActivity extends Activity {

	private ListView lvList;
	private BlackNumberDao mDao;
	private ArrayList<BlackNumberInfo> mList = null;
	private BlackNumberAdapter mAdapter;

	private ProgressBar pbLoading;
	// 分页查找的起始位置
	private int mIndex;
	// 是否正在加载下一页数据
	private boolean isLoading = false;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			
			if (mAdapter == null) {
				mAdapter = new BlackNumberAdapter();
				lvList.setAdapter(mAdapter);
			} else {
				mAdapter.notifyDataSetChanged();
			}
			mIndex = mList.size();

			pbLoading.setVisibility(View.GONE);
			isLoading = false;
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacknumber);
		pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

		lvList = (ListView) findViewById(R.id.lv_blackNumber);
		mDao = BlackNumberDao.getInstance(this);

		//滑动
		lvList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == SCROLL_STATE_IDLE) {
					// 判断是否是最后一个
					int lastVisiblePosition = lvList.getLastVisiblePosition();
					
					// 判断是否是最后一页
					if (lastVisiblePosition >= mList.size() - 1 && !isLoading) {
						//获取数据总数
						int totalCount = mDao.getTotalCount();
						if (mList.size() >= totalCount) {
							ToastUtils.showToast(getApplicationContext(),
									"亲，没有更多数据了哦。。。");
							return;
						}
						// 加载下一页
						init();
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});

		init();
	}

	private void init() {
		pbLoading.setVisibility(View.VISIBLE);
		isLoading = true;
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// 加载第一页数据
				if (mList == null) {
					mList = mDao.findPart(mIndex);
				} else {
					ArrayList<BlackNumberInfo> partList = mDao.findPart(mIndex);
					mList.addAll(partList);
				}

				mHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	/**
	 * 添加黑名单
	 * 
	 * @param view
	 */
	public void addBlackNumber(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_add_blacknumber, null);
		dialog.setView(view, 0, 0, 0, 0);

		Button btnAdd = (Button) view.findViewById(R.id.btn_add);
		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

		final EditText etNumber = (EditText) view.findViewById(R.id.et_number);
		final RadioGroup rgGroup = (RadioGroup) view
				.findViewById(R.id.rg_group);

		btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String number = etNumber.getText().toString().trim();
				if (!TextUtils.isEmpty(number)) {
					int checkedRadioButtonId = rgGroup
							.getCheckedRadioButtonId();

					int mode = 1;
					switch (checkedRadioButtonId) {
					case R.id.rb_number:
						mode = 1;
						break;
					case R.id.rb_msg:
						mode = 2;
						break;
					case R.id.rb_all:
						mode = 3;
						break;

					default:
						break;
					}

					mDao.add(number, mode);

					BlackNumberInfo addInfo = new BlackNumberInfo();
					addInfo.number = number;
					addInfo.mode = mode;
					mList.add(0, addInfo);

					mAdapter.notifyDataSetChanged();

					dialog.dismiss();
				} else {
					ToastUtils.showToast(getApplicationContext(), "输入内容不能为空!");
				}
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	class BlackNumberAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public BlackNumberInfo getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		//优化ListView
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_blacknumber, null);

				TextView tvNumber = (TextView) view
						.findViewById(R.id.tv_number);
				TextView tvMode = (TextView) view.findViewById(R.id.tv_mode);
				ImageView ivDelete = (ImageView) view
						.findViewById(R.id.iv_delete);

				holder = new ViewHolder();
				holder.tvNumber = tvNumber;
				holder.tvMode = tvMode;
				holder.ivDelete = ivDelete;

				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			final BlackNumberInfo info = getItem(position);
			holder.tvNumber.setText(info.number);

			switch (info.mode) {
			case 1:
				holder.tvMode.setText("拦截电话");
				break;
			case 2:
				holder.tvMode.setText("拦截短信");
				break;
			case 3:
				holder.tvMode.setText("拦截电话+短信");
				break;
			default:
				break;
			}

			holder.ivDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 从数据库移除
					mDao.delete(info.number);
					// 从集合中移除
					mList.remove(info);
					// 刷新listview
					mAdapter.notifyDataSetChanged();
				}
			});

			return view;
		}

	}

	static class ViewHolder {
		public TextView tvNumber;
		public TextView tvMode;
		public ImageView ivDelete;
	}
}
