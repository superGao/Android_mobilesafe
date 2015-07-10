package com.superGao.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.superGao.mobilesafe.utils.PrefUtils;
import com.superGao.mobilesafe.utils.ToastUtils;
/**
 * 设置向导1
 * @author gao
 *
 */
public class Setup3Activity extends BaseSetupActivity {
	private EditText etPhoneNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		etPhoneNumber = (EditText)findViewById(R.id.et_phone_number);
		
		etPhoneNumber.setText(PrefUtils.getString("safeNumber", "",this));
	}
	
	//下一页
	@Override
	public void showNextPage() {
		String phone=etPhoneNumber.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			ToastUtils.showToast(this,"未设置安全号码");
			return;
		}
		//保存电话号码
		
		PrefUtils.putString("safeNumber", phone, this);
		
		startActivity(new Intent(this,Setup4Activity.class));
		finish();
		// 页面切换动画
		overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
	}
	
	//上一页
	@Override
	public void showPreviousPage() {
		Intent intent=new Intent();
		intent.putExtra("safeNumber", etPhoneNumber.getText());
		
		startActivity(new Intent(this,Setup2Activity.class));
		
		finish();
		// 页面切换动画
		overridePendingTransition(R.anim.anim_previous_in, R.anim.anim_previous_out);
	}
	
	
	//选择联系人的点击事件
	public void selectContact(View view){
		//查询联系人
		Intent intent=new Intent(this,SelectContactActivity.class);
		
		startActivityForResult(intent, 0);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==Activity.RESULT_OK){
			//获取数据
			String phone=data.getStringExtra("phone");
			// 替换"-"
			phone=phone.replace("-","");
			// 替换空格
			phone=phone.replace(" ", "");
			//将用户选择的联系人号码设置到输入框中
			etPhoneNumber.setText(phone);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
