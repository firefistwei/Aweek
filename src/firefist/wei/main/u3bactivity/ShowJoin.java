package firefist.wei.main.u3bactivity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import firefist.wei.main.MainActivity;
import firefist.wei.main.MyConstants;
import firefist.wei.main.R;
import firefist.wei.main.service.MyService;
import firefist.wei.utils.Utils;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowJoin extends Activity{
	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayImageOptions options;

	private String aid;
	private String host_uid;

	Context mContext = null;
	ProgressDialog pd = null;

	private Button joinBtn;
	
	private ListView mListView;
	private MyAdapter mAdapter;

	private ArrayList<HashMap<String, String>> mapList;
	
	public static HashMap<String, Integer> followMap = new HashMap<String, Integer>(); // 0

	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.u3b_show_join);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		ActionBar actionBar = getActionBar();

		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.title_bar));
		actionBar.setIcon(R.drawable.app_icon);
		actionBar.setTitle("参加");
		actionBar.show();

		mContext = this;
		
		aid = getIntent().getStringExtra("aid");
		host_uid = getIntent().getStringExtra("uid");

		options = new DisplayImageOptions.Builder().resetViewBeforeLoading()
				.cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		initImageLoader(mContext);	
	}

	@Override
	protected void onResume() {
		findViewById();
		init();
		setListener();
		super.onResume();
	}

	public void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).enableLogging() // Not
																				// necessary
																				// in
																				// common
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			Intent upIntent = new Intent(this, MainActivity.class);
			upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(upIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void findViewById() {
		joinBtn = (Button)this.findViewById(R.id.u3b_showjoin_join);
	}
	private void setListener() {
		joinBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(MyConstants.myJoinMap.get(aid)==null){
					pd = ProgressDialog.show(mContext, "请稍后", "正在请求中...",
							true, true);
					goJoin();
				}
				
				
			}
		});
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:

				break;
			case 1:
				if (mAdapter != null) {
					mAdapter.refreshData(mapList);
				}
				break;

			}
		};
	};
	
	private void init() {
		if(MyConstants.UserUid.equals(host_uid)){
			joinBtn.setVisibility(View.GONE);
		}
		
		mListView = (ListView) this
				.findViewById(R.id.u3b_showjoin_listview);
		if(mapList==null){
			mapList = new ArrayList<HashMap<String, String>>();
		}
		mAdapter = new MyAdapter(mContext, mapList);
		mListView.setAdapter(mAdapter);
		if(mapList.size()==0){
			getJoinPeople();
		}
		
	}
	
	class MyAdapter extends BaseAdapter {

		private Context context;
		ViewHolder holder = null;

		private ArrayList<HashMap<String, String>> mapList;

		public MyAdapter(Context context,
				ArrayList<HashMap<String, String>> mapList) {
			this.context = context;
			this.mapList = mapList;
		}

		public void refreshData(ArrayList<HashMap<String, String>> mapList) {
			this.mapList = mapList;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mapList.size();
		}

		@Override
		public Object getItem(int pos) {
			return mapList.get(pos);
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.u3b_showsupport_item, null);
				holder = new ViewHolder();

				holder.item_avatar = (ImageView) convertView
						.findViewById(R.id.u3b_showsupport_item_avatar);
				holder.item_name = (TextView) convertView
						.findViewById(R.id.u3b_showsupport_item_name);
				holder.item_sig = (TextView) convertView
						.findViewById(R.id.u3b_showsupport_item__sig);
				holder.item_follow = (ImageView) convertView
						.findViewById(R.id.u3b_showsupport_item_follow);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final ImageView followBtn
				= holder.item_follow;
			final int _pos = pos;
			holder.item_name.setText(mapList.get(_pos).get("user_name"));
			holder.item_sig.setText(" ");

			if (!mapList.get(_pos).get("head_URL").equals("")) {
				imageLoader.displayImage(mapList.get(_pos).get("head_URL"), holder.item_avatar,
						options, null);
			}
			
			int followFlag = followMap.get(mapList.get(_pos).get("uid"));
			if (followFlag == 1) {
				followBtn.setBackgroundResource(R.drawable.btn_following_default);
			} else {
				followBtn.setBackgroundResource(R.drawable.btn_follow_default);
			}
			
			holder.item_avatar.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext,
							FriendUserInfo.class);
					intent.putExtra("uid",
							mapList.get(_pos).get("uid"));
					startActivity(intent);			
				}
			});
			
			
			followBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					int followFlag = followMap.get(mapList.get(_pos).get(
							"uid"));
					if (followFlag == 0) {// 按
						followBtn.setBackgroundResource(R.drawable.btn_following_default);
						followMap.put(mapList.get(_pos).get("uid"), 1);
					} else {// 取消
						followBtn.setBackgroundResource(R.drawable.btn_follow_default);
						followMap.put(mapList.get(_pos).get("uid"), 0);
					}

				}
			});
			
			return convertView;
		}

		class ViewHolder {
			ImageView item_avatar;
			TextView item_name;
			TextView item_sig;
			ImageView item_follow;
		}

	}

	@Override
	protected void onDestroy() {
		if (pd != null) {
			pd.dismiss();
		}
		super.onDestroy();
	}
	
	// //////////////////////////////////////////////////
		private void getJoinPeople() {
			new Thread() {

				public void run() {
					Looper.prepare();
					InputStream inputStream;

					try {
						inputStream = MyService.getJoin(aid);
						

						String jsonString = Utils.readInputStream(inputStream);

						Log.e("ShowJoin-TAG", jsonString);

						JSONObject jsonObject = new JSONObject(jsonString);
						JSONArray jsonArray = jsonObject.getJSONArray("result");

						mapList = new ArrayList<HashMap<String, String>>();
						HashMap<String, String> map = null;

						
						String[] keys = { "aid", "uid", "user_name",
								"head_URL" };

							for (int i = 0; i < jsonArray.length(); i++) {
								map = new HashMap<String, String>();

								for (int j = 0; j < keys.length; j++) {
									String value = jsonArray.getJSONObject(i)
											.getString(keys[j]);
									if (null == value || "null".equals(value)) {
										value = "";
									}
									map.put(keys[j], value);
								}
								mapList.add(map);
							}
						

						mAdapter.refreshData(mapList);

					} catch (Exception e) {
						e.printStackTrace();
					}
					if (mapList.size() > 0) {

						handler.sendEmptyMessage(1);

					} else {
						handler.sendEmptyMessage(0);
					}

					Looper.loop();
				}

			}.start();

		}

		private void goJoin() {
			new Thread() {

				public void run() {
					Looper.prepare();
					InputStream inputStream;

					try {
						
						String[] keys = new String[] { "aid", "uid" };
						HashMap<String, String> params = new HashMap<String, String>();
						params.put(keys[0], aid);
						params.put(keys[1], MyConstants.User_Map.get("uid"));

						inputStream = MyService.goJoin(params);
						
						String result = Utils.readInputStream(inputStream);
						Log.e("TAG",result+"");

						JSONObject jsonObject = new JSONObject(result);
						int flag = Integer.valueOf(jsonObject.get("result")
								.toString());

						if (flag > 0) { // 上传成功
							pd.dismiss();
							Toast.makeText(mContext, "请求发送成功！", Toast.LENGTH_LONG)
									.show();
							joinBtn.setText("已申请");
							MyConstants.myJoinMap.put(aid,1);
							
							//getJoinPeople();
							HashMap<String,String> mMap = new HashMap<String,String>();
							String[] keys2 = { "aid", "uid", "user_name",
							"head_URL" };
							mMap.put(keys2[0],aid);
							mMap.put(keys2[1],MyConstants.User_Map.get("uid"));
							mMap.put(keys2[2],MyConstants.User_Map.get("user_name"));
							mMap.put(keys2[3],MyConstants.User_Map.get("head_URL"));
							mapList.add(mMap);
							mAdapter.refreshData(mapList);
	
						} else {
							pd.dismiss();
							Toast.makeText(mContext, "请求发送失败！", Toast.LENGTH_LONG)
									.show();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					Looper.loop();
				}

			}.start();

		}

		@Override
		public boolean dispatchKeyEvent(KeyEvent event) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
					&& event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getRepeatCount() == 0) {
				Intent upIntent = new Intent(this, MainActivity.class);
				upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(upIntent);
			}
			return super.dispatchKeyEvent(event);

		}
}
