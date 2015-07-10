package service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * 追踪地理位置的服务
 * 
 * @author gao
 * 
 */
public class LocationService extends Service {

	private LocationManager mLm;
	private MyListener mListener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mLm = (LocationManager) getSystemService(LOCATION_SERVICE);
		mListener = new MyListener();

		Criteria criteria = new Criteria();
		// 获取高精度
		criteria.setAccuracy(Criteria.ACCURACY_HIGH);
		// 联网
		criteria.setCostAllowed(true);
		// 获取当时最好的位置提供者
		String provider = mLm.getBestProvider(criteria, true);
		mLm.requestLocationUpdates(provider, 0, 0, mListener);
	}

	class MyListener implements LocationListener {
		// 位置发生改变
		@Override
		public void onLocationChanged(Location location) {
			String longitude = "j:" + location.getLongitude() + "\n";//获取经度
			String latitude = "w:" + location.getLatitude() + "\n";//获取纬度
			String accuracy = "a" + location.getAccuracy() + "\n";//获取精度
			
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("lastlocation",longitude +latitude+ accuracy);
			editor.commit();
		}
		//位置提供者状态变化
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		mLm.removeUpdates(mListener);
		
		mListener=null;
	}
}
