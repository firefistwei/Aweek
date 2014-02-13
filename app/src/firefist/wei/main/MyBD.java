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
		option.setOpenGps(true); // ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setAddrType("all"); // ���õ�ַ��Ϣ��������Ϊ��all��ʱ�е�ַ��Ϣ��Ĭ���޵�ַ��Ϣ
		option.setScanSpan(500); // ���ö�λģʽ��С��1����һ�ζ�λ;���ڵ���1����ʱ��λ
		mLocationClient.setLocOption(option);

	}

	public void getLocation() {
		setLocationOption(); // �Զ�����
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
	 * ��������������λ�õ�ʱ�򣬸�ʽ�����ַ������������Ļ��
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
			sb.append("ʱ��: ");
			sb.append(location.getTime());
			sb.append("\nγ�ȣ�  ");
			sb.append(location.getLatitude());
			sb.append("\n���ȣ�  ");
			sb.append(location.getLongitude());
			sb.append("\n�뾶��  ");
			sb.append(location.getRadius());

			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\n�ٶ� : ");
				sb.append(location.getSpeed());
				sb.append("\n������ : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\n��"); // ʡ
				sb.append(location.getProvince());
				sb.append("\n��");// ��
				sb.append(location.getCity());
				sb.append("\n��"); // ��/��
				sb.append(location.getDistrict());
				sb.append("\n�����ַ: ");
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
