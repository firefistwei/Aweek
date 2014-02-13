package firefist.wei.main;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class MyBD {

	private static final int MODE_PRIVATE = 0x00000000;
	/**
	 * @param args
	 */
	private Context context;
	public static LocationClient mLocationClient = null; // //baidu api
	public MyLocationListener myListener = new MyLocationListener();
	public String locationData = "";
	public static double longitude = 0;
	public static double latitude = 0;

	public MyBD(Context context) {
		this.context = context;
//		openGPS();
		initLocation();
	}
	
	
	public void saveLocation(){
		SharedPreferences sp = context.getSharedPreferences("u3b_sp",
				MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("u3b_location_longitude", longitude+"");
		editor.putString("u3b_location_latitude", latitude+"");
		
		editor.commit();
		
		
	}

	private void openGPS() {
		boolean gpsEnabled = Settings.Secure.isLocationProviderEnabled(
				context.getContentResolver(), LocationManager.GPS_PROVIDER);
		if (gpsEnabled == false) {
			/*
			 * Settings.Secure.setLocationProviderEnabled(context.getContentResolver
			 * (), LocationManager.GPS_PROVIDER, true);
			 */
			Intent gpsIntent = new Intent();
			gpsIntent.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
			gpsIntent.setData(Uri.parse("custom:3"));
			try {
				PendingIntent.getBroadcast(context, 0, gpsIntent, 0).send();
			} catch (CanceledException e) {
				e.printStackTrace();
			}
		}

	}

	private void initLocation() {
		mLocationClient = new LocationClient(context);
		mLocationClient.registerLocationListener(myListener);

	}

	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setAddrType("all"); // 设置地址信息，仅设置为“all”时有地址信息，默认无地址信息
		option.setScanSpan(500); // 设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
		mLocationClient.setLocOption(option);

	}

	public void getLocation() {
		setLocationOption(); // 自定方法
		mLocationClient.start();

		if (mLocationClient != null) {
			setLocationOption();
			mLocationClient.requestLocation();
		} else
			Log.d("boot", "locClient is null or not started");
		
		Log.e("longtitude",MyConstants.Longitude+"");
		Log.e("latitude",MyConstants.Latitude+"");
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	/**
	 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			longitude = location.getLongitude();
			latitude = location.getLatitude();
			
			saveLocation();

			MyConstants.Longitude = longitude;
			MyConstants.Latitude = latitude;			
		
//			Toast.makeText(context, longitude +"  "+ latitude, 2000).show();

			StringBuffer sb = new StringBuffer(256);
			sb.append("时间: ");
			sb.append(location.getTime());
			sb.append("\n纬度：  ");
			sb.append(location.getLatitude());
			sb.append("\n经度：  ");
			sb.append(location.getLongitude());
			sb.append("\n半径：  ");
			sb.append(location.getRadius());

			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\n速度 : ");
				sb.append(location.getSpeed());
				sb.append("\n卫星数 : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\n："); // 省
				sb.append(location.getProvince());
				sb.append("\n：");// 市
				sb.append(location.getCity());
				sb.append("\n："); // 区/县
				sb.append(location.getDistrict());
				sb.append("\n具体地址: ");
				sb.append(location.getAddrStr());
				sb.append("\nsdk version : ");
				sb.append(mLocationClient.getVersion());
			}
			locationData = sb.toString();
			
		}

		@Override
		public void onReceivePoi(BDLocation position) {

		}

	}

	@Override
	protected void finalize() throws Throwable {
		mLocationClient.stop();

		super.finalize();
	}
	
	
}
