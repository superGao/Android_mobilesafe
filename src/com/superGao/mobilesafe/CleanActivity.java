package com.superGao.mobilesafe;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class CleanActivity extends TabActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_tab);
		
		TabHost host = getTabHost();
		TabSpec tab1 = host.newTabSpec("缓存清理").setIndicator("缓存清理");
		tab1.setContent(new Intent(this,CleanCacheActivity.class));
		host.addTab(tab1);
		TabSpec tab2 = host.newTabSpec("内存清理").setIndicator("内存清理");
		tab2.setContent(new Intent(this,CleanSDcardActivity.class));
		host.addTab(tab2);
		
		
		
	}
}
