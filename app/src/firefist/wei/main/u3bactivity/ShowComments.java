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
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
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
import android.graphics.BitmapFactory;
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

public class ShowComments extends Activity {

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayImageOptions options;

	private LinearLayout showcomments_text_layout;
	private ImageView showcomments_tovoice;
	private EditText showcomments_conmment_content;
	private Button showcomments_comment_send;

	private LinearLayout showcomments_voice_layout;
	private ImageView showcomments_totext;
	private Button showcomments_start;

	private String aid;
	private String sid;
	private String action;
	private String msg;

	Context mContext = null;
	ProgressDialog pd = null;

	private ListView mListView;
	private MyAdapter mAdapter;

	private ArrayList<HashMap<String, String>> mapList;

	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.u3b_show_comments);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		ActionBar actionBar = getActionBar();

		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.title_bar));
		actionBar.setIcon(R.drawable.app_icon);
		actionBar.setTitle("评论");
		actionBar.show();

		action = getIntent().getStringExtra("action");
		Log.e("TAG_SHOWCOMMENT", action);
		if (action.equals("aid")) {
			aid = getIntent().getStringExtra("aid");
		} else {
			sid = getIntent().getStringExtra("sid");
		}

		mContext = this;

		options = new DisplayImageOptions.Builder().resetViewBeforeLoading()
				.cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		initImageLoader(mContext);

	}

	@Override
	protected void onResume() {
		findViewById();
		setListener();
		init();
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
		showcomments_text_layout = (LinearLayout) findViewById(R.id.u3b_showcomments_text_layout);
		showcomments_tovoice = (ImageView) findViewById(R.id.u3b_showcomments_tovoice);
		showcomments_conmment_content = (EditText) findViewById(R.id.u3b_showcomments_conmment_content);
		showcomments_comment_send = (Button) findViewById(R.id.u3b_showcomments_comment_send);

		showcomments_voice_layout = (LinearLayout) findViewById(R.id.u3b_showcomments_voice_layout);
		showcomments_totext = (ImageView) findViewById(R.id.u3b_showcomments_totext);
		showcomments_start = (Button) findViewById(R.id.u3b_showcomments_start);

	}

	private void setListener() {
		showcomments_tovoice.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showcomments_text_layout.setVisibility(View.GONE);
				showcomments_voice_layout.setVisibility(View.VISIBLE);

				Toast.makeText(mContext, "语音聊天 将在下一个版本推出，敬请期待",
						Toast.LENGTH_SHORT).show();

			}
		});

		showcomments_totext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showcomments_text_layout.setVisibility(View.VISIBLE);
				showcomments_voice_layout.setVisibility(View.GONE);
			}
		});

		showcomments_comment_send
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						msg = showcomments_conmment_content.getText()
								.toString().trim();
						if (msg.equals("")) {
							Toast.makeText(mContext, "评论内容不能为空", 1500).show();
							return;
						}
						pd = ProgressDialog.show(mContext, "请稍后", "正在发送...",
								true, true);
						sendComment();

					}
				});

		showcomments_start.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

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

		mListView = (ListView) this
				.findViewById(R.id.u3b_showcomments_listview);
		mapList = new ArrayList<HashMap<String, String>>();
		mAdapter = new MyAdapter(mContext, mapList);
		mListView.setAdapter(mAdapter);
		getMessageData();

	}

	/*
	 * public void addHeader(){ View header=
	 * LayoutInflater.from(mContext).inflate( R.layout.u3b_showcomment_item,
	 * null);
	 * 
	 * mListView.addHeaderView(header);
	 * 
	 * mAdapter.refreshData(mapList); }
	 */

	class MyAdapter extends BaseAdapter {

		private Context context;
		ViewHolder holder = null;

		private ArrayList<HashMap<String, String>> mapList;
		private HashMap<String, String> map;

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
						R.layout.u3b_showcomment_item, null);
				holder = new ViewHolder();

				holder.item_avatar = (ImageView) convertView
						.findViewById(R.id.u3b_showcomment_item_avatar);
				holder.item_name = (TextView) convertView
						.findViewById(R.id.u3b_showcomment_item_name);
				holder.item_msg = (TextView) convertView
						.findViewById(R.id.u3b_showcomment_item__msg);
				holder.item_time = (TextView) convertView
						.findViewById(R.id.u3b_showcomment_item_time);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			map = new HashMap<String, String>();
			map = mapList.get(pos);
			holder.item_name.setText(map.get("user_name"));
			holder.item_msg.setText(map.get("comment_content"));
			holder.item_time.setText(map.get("create_time"));

			if (!map.get("head_URL").equals("")) {
				imageLoader.displayImage(map.get("head_URL"), holder.item_avatar,
						options, null);
			}
			
			final int _pos = pos;
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
			/*
			 * @ 用户头像
			 */
			/*
			 * new Thread() {
			 * 
			 * @Override public void run() { Looper.prepare(); try {
			 * 
			 * byte[] data = MyService.getImage(map.get("head_URL"));
			 * 
			 * holder.item_avatar .setImageBitmap(BitmapFactory.decodeByteArray(
			 * data, 0, data.length)); } catch (Exception e) {
			 * e.printStackTrace(); } Looper.loop(); } }.start();
			 */

			return convertView;
		}

		class ViewHolder {
			ImageView item_avatar;
			TextView item_name;
			TextView item_msg;
			TextView item_time;

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
	private void getMessageData() {
		new Thread() {

			public void run() {
				Looper.prepare();
				InputStream inputStream;

				try {
					if (action.equals("aid")) {
						inputStream = MyService.getShowComments(action, aid);
					} else {
						inputStream = MyService.getShowComments(action, sid);
					}

					String jsonString = Utils.readInputStream(inputStream);

					Log.e("ShowComment-TAG", jsonString);

					JSONObject jsonObject = new JSONObject(jsonString);
					JSONArray jsonArray = jsonObject.getJSONArray("result");

					mapList = new ArrayList<HashMap<String, String>>();
					HashMap<String, String> map = null;

					if (action.equals("aid")) {
						String[] keys = { "mid", "aid", "uid", "create_time",
								"comment_content", "refrence_mid", "user_name",
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
					} else {
						String[] keys = { "mid", "sid", "uid", "create_time",
								"comment_content", "refrence_mid", "user_name",
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

	private void sendComment() {
		new Thread() {

			public void run() {
				Looper.prepare();
				InputStream inputStream;

				try {
					if (action.equals("aid")) {
						String[] keys1 = new String[] { "aid", "uid",
								"comment_content", "refrence_mid" };
						HashMap<String, String> params = new HashMap<String, String>();
						params.put(keys1[0], aid);
						params.put(keys1[1], MyConstants.User_Map.get("uid"));
						params.put(keys1[2], msg);
						params.put(keys1[3], "0");

						inputStream = MyService.sendActiveComment(params);
					} else {
						String[] keys2 = new String[] { "sid", "uid",
								"comment_content", "refrence_mid" };
						HashMap<String, String> params = new HashMap<String, String>();
						params.put(keys2[0], sid);
						params.put(keys2[1], MyConstants.User_Map.get("uid"));
						params.put(keys2[2], msg);
						params.put(keys2[3], "0");

						inputStream = MyService.sendStatusComment(params);
					}

					String result = Utils.readInputStream(inputStream);

					JSONObject jsonObject = new JSONObject(result);
					int flag = Integer.valueOf(jsonObject.get("result")
							.toString());

					if (flag > 0) { // 上传成功
						pd.dismiss();
						Toast.makeText(mContext, "评论成功！", Toast.LENGTH_LONG)
								.show();
						getMessageData();
						showcomments_conmment_content.setText("");

					} else {
						pd.dismiss();
						Toast.makeText(mContext, "评论失败！", Toast.LENGTH_LONG)
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