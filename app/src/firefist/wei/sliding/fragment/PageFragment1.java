package firefist.wei.sliding.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import firefist.wei.main.MainActivity;
import firefist.wei.main.MyBD;
import firefist.wei.main.MyConstants;
import firefist.wei.main.R;
import firefist.wei.main.service.MyService;
import firefist.wei.main.u3bactivity.FriendUserInfo;
import firefist.wei.main.u3bactivity.ShowComments;
import firefist.wei.main.u3bdomain.U3B_Photo;
import firefist.wei.main.widget.FlowIndicator;
import firefist.wei.main.widget.RefreshListView;
import firefist.wei.utils.TextUtil;
import firefist.wei.utils.Utils;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * 附近
 * 
 */
public class PageFragment1 extends Fragment implements
		RefreshListView.IOnRefreshListener, RefreshListView.IOnLoadMoreListener {

	static DisplayImageOptions options;

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public ImageLoader headLoader = ImageLoader.getInstance();

	private RefreshListView mListView;
	private MyListViewAdapter mAdapter;
	private RefreshDataAsynTask mRefreshAsynTask;
	private LoadMoreDataAsynTask mLoadMoreAsynTask;

	private ArrayList<HashMap<String, String>> mapList = null;
	private static int page_offset = 1; // 查看 附近活动 的 offset
	private int more_pos = 0;

	public static HashMap<String, Integer> statusLikeMap = new HashMap<String, Integer>(); // 0

	private Context mContext;

	public static int scrollTop;
	public static int scrollPos;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.page1_fragment, null);
		mListView = (RefreshListView) view.findViewById(R.id.page1_listview);

		mContext = this.getActivity();

		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		BDmap();

		options = new DisplayImageOptions.Builder().resetViewBeforeLoading()
				.cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		initImageLoader(mContext);
		
		if (MyConstants.UserUid.equals("0"))
			getSP_Uid();
	}

	private void getSP_Uid() {
		SharedPreferences sharedPreferences = getActivity()
				.getSharedPreferences("u3b_sp", 0);// 0 (0x00000000)

		MyConstants.UserUid = sharedPreferences.getString("u3b_user_uid", "0");
	}

	@Override
	public void onResume() {
		initList();
		super.onResume();

	}

	private void BDmap() {
		MyBD myBD = new MyBD(mContext);

		myBD.getLocation();

	}

	private void initList() {

		mapList = new ArrayList<HashMap<String, String>>();

		mapList = MyConstants.page1HashMapList;

		mAdapter = new MyListViewAdapter(mContext, mapList);
		mListView.setAdapter(mAdapter);
		setListViewListener();
		if (mapList.size() == 0) {
			getNearStatus(page_offset);
		}
		setListViewPosition();
		mListView.setSelectionFromTop(scrollPos, scrollTop);

	}

	private void setListViewPosition() {

		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					// scrollPos记录当前可见的List顶端的一行的位置
					scrollPos = mListView.getFirstVisiblePosition();

					if (mapList != null) {
						View v = mListView.getChildAt(0);
						scrollTop = (v == null) ? 0 : v.getTop();
					}

				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
			}
		});

	}

	View sadView = null;
	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (sadView != null) {
					mListView.removeHeaderView(sadView);
					sadView = LayoutInflater.from(mContext).inflate(
							R.layout.u3b_sadface_layout, null);
					mListView.addHeaderView(sadView);
				} else {
					sadView = LayoutInflater.from(mContext).inflate(
							R.layout.u3b_sadface_layout, null);
					mListView.addHeaderView(sadView);
				}

				mAdapter = new MyListViewAdapter(mContext, mapList);
				mListView.setAdapter(mAdapter);
				setListViewListener();

				break;
			case 1:
				for (int i = 0; i < mapList.size(); i++) {
					String sidKey = mapList.get(i).get("sid");
					if (!statusLikeMap.containsKey(sidKey)) {
						statusLikeMap.put(sidKey, 0);
					}
				}
				if (sadView != null) {
					mListView.removeHeaderView(sadView);
				}
				mAdapter.refreshData(mapList);
				break;
			}
		}

	};

	public void setListViewListener() {
		mListView.setOnRefreshListener(this);
		mListView.setOnLoadMoreListener(this);
	}

	@Override
	public void OnLoadMore() {

		mLoadMoreAsynTask = new LoadMoreDataAsynTask();
		mLoadMoreAsynTask.execute();
	}

	@Override
	public void OnRefresh() {
		mRefreshAsynTask = new RefreshDataAsynTask();
		mRefreshAsynTask.execute();

	}

	public static void initImageLoader(Context context) {
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

	public class MyListViewAdapter extends BaseAdapter {

		private ArrayList<HashMap<String, String>> mapList;

		private Context mContext;

		private ViewHolder holder = null;
		private MediaPlayer mediaPlayer = null;
		//private SurfaceView surfaceView = null;

		// private ImageView photoImage;
		// private VideoView videoView;

		public MyListViewAdapter(Context context,
				ArrayList<HashMap<String, String>> photoList) {
			mContext = context;
			this.mapList = photoList;

		}

		public void refreshData(ArrayList<HashMap<String, String>> photoList) {
			this.mapList = photoList;
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

			View view = convertView;

			if (view == null) {

				view = LayoutInflater.from(mContext).inflate(
						R.layout.page1_list_item, null);

				holder = new ViewHolder();

				holder.page1_list_item_photo = (ImageView) view
						.findViewById(R.id.page1_list_item_photo);
				holder.page1_list_item_progressbar = (ProgressBar) view
						.findViewById(R.id.page1_list_item_progressbar);

				holder.page1_list_item_start = (ImageView) view
						.findViewById(R.id.page1_list_item_start);

				holder.page1_list_item_type = (ImageView) view
						.findViewById(R.id.page1_list_item_type);
				holder.page1_list_item_title = (TextView) view
						.findViewById(R.id.page1_list_item_title);
				holder.page1_list_item_head = (ImageView) view
						.findViewById(R.id.page1_list_item_head);
				holder.page1_list_item_name = (TextView) view
						.findViewById(R.id.page1_list_item_name);
				holder.page1_list_item_timeanddistance = (TextView) view
						.findViewById(R.id.page1_list_item_timeanddistance);

				holder.page1_list_item_morecomments = (TextView) view
						.findViewById(R.id.page1_list_item_morecomments);

				holder.page1_list_item_likebtn = (Button) view
						.findViewById(R.id.page1_list_item_likebtn);
				holder.page1_list_item_commentbtn = (Button) view
						.findViewById(R.id.page1_list_item_commentbtn);
				holder.page1_list_item_morebtn = (Button) view
						.findViewById(R.id.page1_list_item_morebtn);

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();

			}

			/*
			 * holder.page1_list_item_photo
			 * .setMinimumWidth(MainActivity.mScreenWidth - 10);
			 * holder.page1_list_item_photo
			 * .setMinimumHeight(MainActivity.mScreenWidth - 10);
			 * holder.page1_list_item_videoview .setLayoutParams(new
			 * LinearLayout.LayoutParams( MainActivity.mScreenWidth - 10,
			 * MainActivity.mScreenWidth - 10));
			 */

			final Button likebtn;
			final ImageView startbtn;
			likebtn = holder.page1_list_item_likebtn;
			startbtn = holder.page1_list_item_start;

			final SurfaceView surfaceView;
			surfaceView = (SurfaceView) view
					.findViewById(R.id.page1_list_item_surfaceview);
			final ImageView photoView;
			photoView = (ImageView) view
					.findViewById(R.id.page1_list_item_photo);
			surfaceView.setVisibility(View.GONE);
			photoView.setVisibility(View.VISIBLE);

			final int _pos = pos;

			if(!mapList.get(_pos).get("head_URL").equals("")){
				headLoader.displayImage(mapList.get(_pos).get("head_URL"),
						holder.page1_list_item_head, options, null);
			}
			holder.page1_list_item_title.setText(mapList.get(_pos).get(
					"extra_message"));

			holder.page1_list_item_timeanddistance.setText(mapList.get(_pos)
					.get("distance")
					+ " | "
					+ mapList.get(_pos).get("create_time"));

			holder.page1_list_item_name.setText(mapList.get(_pos).get(
					"user_name"));
			holder.page1_list_item_morecomments.setText("查看更多评论  "
					+ mapList.get(_pos).get("current_comment_num") + "条");

			// 喜欢 按钮
			int likeFlag = statusLikeMap.get(mapList.get(_pos).get("sid"));
			if (likeFlag == 1) {
				likebtn.setBackgroundResource(R.drawable.btn_like_pressed);
			} else {
				likebtn.setBackgroundResource(R.drawable.btn_like_default);
			}

			imageLoader.displayImage(mapList.get(_pos).get("photo_URL"),
					photoView, options, new SimpleImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {
							holder.page1_list_item_progressbar
									.setVisibility(View.VISIBLE);
							startbtn.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							holder.page1_list_item_progressbar
									.setVisibility(View.GONE);
							startbtn.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							holder.page1_list_item_progressbar
									.setVisibility(View.GONE);
							startbtn.setVisibility(View.VISIBLE);
						}
					});

			if ((mapList.get(_pos).get("audio_URL").length() > 5)) {
				// 是语音
				holder.page1_list_item_type.setImageDrawable(mContext
						.getResources().getDrawable(R.drawable.ic_type_audio));
				startbtn.setVisibility(View.VISIBLE);

				startbtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {

						if (mediaPlayer != null) {
							if (mediaPlayer.isPlaying()) {
								mediaPlayer.stop();
							}
							mediaPlayer.setOnCompletionListener(null);
							mediaPlayer.setOnPreparedListener(null);
							mediaPlayer = null;
						}
						startbtn.setVisibility(View.VISIBLE);
					}
				});

				startbtn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						v.requestFocus();

						startbtn.setVisibility(View.INVISIBLE);
						holder.page1_list_item_progressbar
								.setVisibility(View.VISIBLE);

						if (mediaPlayer != null) {
							if (mediaPlayer.isPlaying()) {
								mediaPlayer.stop();
							}
							mediaPlayer.setOnCompletionListener(null);
							mediaPlayer.setOnPreparedListener(null);
							mediaPlayer = null;
						}
						mediaPlayer = new MediaPlayer();

						try {
							holder.page1_list_item_progressbar
									.setVisibility(View.VISIBLE);
							mediaPlayer.setDataSource(mapList.get(_pos).get(
									"audio_URL"));
							mediaPlayer.prepareAsync();
						} catch (Exception e) {

						}

						mediaPlayer
								.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

									@Override
									public void onCompletion(MediaPlayer mp) {
										startbtn.setVisibility(View.VISIBLE);

									}
								});

						mediaPlayer
								.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

									@Override
									public void onPrepared(MediaPlayer mp) {
										mp.start();
										holder.page1_list_item_progressbar
												.setVisibility(View.INVISIBLE);
										startbtn.setVisibility(View.INVISIBLE);

									}

								});

					}

				});

			} else {
				// 视频
				holder.page1_list_item_type.setImageDrawable(mContext
						.getResources().getDrawable(R.drawable.ic_type_video));

				startbtn.setVisibility(View.VISIBLE);

				startbtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus == false) {
							Log.e("TAG_VIEW_Focuse_false", mapList.get(_pos)
									.get("sid") + "  " + _pos);
							if (mediaPlayer != null) {
								if (mediaPlayer.isPlaying()) {
									mediaPlayer.stop();
								}
								mediaPlayer.setOnCompletionListener(null);
								mediaPlayer.setOnPreparedListener(null);
								mediaPlayer = null;
							}
							// 控件
							surfaceView.setVisibility(View.GONE);
							photoView.setVisibility(View.VISIBLE);
							startbtn.setVisibility(View.VISIBLE);

						} else {
							Log.e("TAG_VIEW_Focuse_true", mapList.get(_pos)
									.get("sid") + "  " + _pos);
						}
					}
				});
				startbtn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(final View v) {
						Log.e("START_BTN", mapList.get(_pos).get("sid") + "  "
								+ _pos);

						// 控件
						startbtn.setVisibility(View.INVISIBLE);
						photoView.setVisibility(View.VISIBLE);
						surfaceView.setVisibility(View.INVISIBLE);

						holder.page1_list_item_progressbar
								.setVisibility(View.VISIBLE);

						if (mediaPlayer != null) {
							if (mediaPlayer.isPlaying()) {
								mediaPlayer.stop();
							}
							mediaPlayer.setOnCompletionListener(null);
							mediaPlayer.setOnPreparedListener(null);
							mediaPlayer = null;
						}
						mediaPlayer = new MediaPlayer();

						try {
							surfaceView.getHolder().setFixedSize(800, 800);
							surfaceView.getHolder().setKeepScreenOn(true);
							surfaceView.getHolder().addCallback(
									new SurfaceCallback());

							String path = mapList.get(_pos).get("video_URL");
							mediaPlayer.setDataSource(path);

							mediaPlayer.prepareAsync();

						} catch (Exception e) {

						}

						if (surfaceView != null) {
							surfaceView
									.setOnTouchListener(new View.OnTouchListener() {
										@Override
										public boolean onTouch(View v,
												MotionEvent event) {
											if (mediaPlayer != null
													& mediaPlayer.isPlaying() != true) {
												mediaPlayer.seekTo(0);
												mediaPlayer.start();

											} else if (mediaPlayer != null
													& mediaPlayer.isPlaying() == true) {
												mediaPlayer.pause();
											}

											return false;
										}
									});
						}

						mediaPlayer
								.setOnPreparedListener(new OnPreparedListener() {

									public void onPrepared(MediaPlayer mp) {
										Log.e("TAG_video", mapList.get(_pos)
												.get("sid")
												+ "  "
												+ _pos
												+ "prepared");
										holder.page1_list_item_progressbar
												.setVisibility(View.INVISIBLE);
										photoView.setVisibility(View.INVISIBLE);
										surfaceView.setVisibility(View.VISIBLE);

										mp.seekTo(0);
										mp.start();
									}
								});

						mediaPlayer
								.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

									@Override
									public void onCompletion(MediaPlayer mp) {
										mp.seekTo(0);
										mp.start();

									}
								});
					}

				});

			}// end if 视频

			holder.page1_list_item_head
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext,
									FriendUserInfo.class);
							intent.putExtra("uid",
									mapList.get(_pos).get("owner"));
							mContext.startActivity(intent);

						}
					});

			holder.page1_list_item_name
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext,
									FriendUserInfo.class);
							intent.putExtra("uid",
									mapList.get(_pos).get("owner"));
							mContext.startActivity(intent);
						}
					});

			holder.page1_list_item_morecomments
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext,
									ShowComments.class);
							intent.putExtra("action", "sid");
							intent.putExtra("sid", mapList.get(_pos).get("sid"));
							mContext.startActivity(intent);

						}
					});
			holder.page1_list_item_commentbtn
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							v.requestFocus();
							Intent intent = new Intent(mContext,
									ShowComments.class);
							intent.putExtra("action", "sid");
							intent.putExtra("sid", mapList.get(_pos).get("sid"));
							mContext.startActivity(intent);

						}
					});

			likebtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.e("LIKE_BTN", mapList.get(_pos).get("sid") + "  "
							+ _pos);

					int likeFlag = statusLikeMap.get(mapList.get(_pos).get(
							"sid"));
					Log.e("likeFlag", likeFlag + "");
					if (likeFlag == 0) {// 按下喜欢
						likebtn.setBackgroundResource(R.drawable.btn_like_pressed);
						statusLikeMap.put(mapList.get(_pos).get("sid"), 1);
						Log.e("pressed", 1 + "");
					} else {// 取消喜欢
						likebtn.setBackgroundResource(R.drawable.btn_like_default);
						statusLikeMap.put(mapList.get(_pos).get("sid"), 0);
						Log.e("pressed", 0 + "");
					}

				}
			});

			holder.page1_list_item_morebtn
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							v.requestFocus();
							new AlertDialog.Builder(mContext)
									.setTitle("更多")
									.setItems(
											new String[] { "举报", "过滤该活动",
													"过滤该发布者" },
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													switch (which) {
													case 0:
														break;
													case 1:
														break;
													case 2:
														break;
													}

												}
											}).create().show();
						}
					});

			return view;
		}

		class ViewHolder {
			ImageView page1_list_item_photo;
			ProgressBar page1_list_item_progressbar;
			ImageView page1_list_item_start;

			ImageView page1_list_item_type;
			TextView page1_list_item_title;
			ImageView page1_list_item_head;
			TextView page1_list_item_name;
			TextView page1_list_item_timeanddistance;
			TextView page1_list_item_morecomments;

			Button page1_list_item_likebtn;
			Button page1_list_item_commentbtn;
			Button page1_list_item_morebtn;
		}

		public final class SurfaceCallback implements SurfaceHolder.Callback {

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				if (mediaPlayer != null) {
					mediaPlayer.setDisplay(holder);
				}

			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// if (surfaceView != null) {
				// surfaceView = null;
				// }
				if (mediaPlayer != null) {
					if (mediaPlayer.isPlaying()) {
						mediaPlayer.stop();
					}
					mediaPlayer.setOnCompletionListener(null);
					mediaPlayer.setOnPreparedListener(null);
					mediaPlayer = null;
				}

			}

		}
	}

	class RefreshDataAsynTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// List 的数据
			page_offset = 1;
			getNearStatus(page_offset);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub

			mAdapter.refreshData(mapList);
			mListView.onRefreshComplete();
		}

	}

	class LoadMoreDataAsynTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				Thread.sleep(2000);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// List加载数据
			page_offset = page_offset + 10;
			getNearStatus(page_offset);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub

			mAdapter.refreshData(mapList);

			if (more_pos > 0) {
				mListView.onLoadMoreComplete(false);
			} else {
				// 数据加载完了
				mListView.onLoadMoreComplete(true);
			}

		}

	}

	// //////////////////////////////////////////////////////////////////////////////////

	private void getNearStatus(final int offset) {

		new Thread() {
			@SuppressWarnings("unchecked")
			public void run() {
				Looper.prepare();
				InputStream inputStream;

				while (MyConstants.Latitude == 0) {
					try {
						sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				try {
					inputStream = MyService.getNearStatus(offset);
					if (inputStream == null)
						return;
					String jsonString = Utils.readInputStream(inputStream);
					if (mapList != null && jsonString.length() < 15)
						return;

					Log.e("TAG", jsonString);

					JSONObject jsonObject = new JSONObject(jsonString);
					JSONArray jsonArray = jsonObject.getJSONArray("result");

					mapList = new ArrayList<HashMap<String, String>>();

					if (offset == 1) {
						MyConstants.page1HashMapList = new ArrayList<HashMap<String, String>>();
					}
					HashMap<String, String> map = null;

					String keys[] = new String[] { "sid", "owner", "user_name",
							"photo_URL", "audio_URL", "video_URL",
							"extra_message", "create_time", "distance",
							"head_URL","current_comment_num","remain_time" };// length=12
					for (int i = 0; i < jsonArray.length(); i++) {

						map = new HashMap<String, String>();

						for (int j = 0; j < keys.length; j++) {
							String value = jsonArray.getJSONObject(i).get(keys[j])
									.toString();
							if (null == value || "null".equals(value)) {
								value = "";
							}
							map.put(keys[j],value);
						}

						MyConstants.page1HashMapList.add(map);

					}
					if (offset > 1) {
						more_pos = (jsonArray.length() > 0) ? 1 : 0;
					}

				} catch (Exception e) {
				}
				mapList = MyConstants.page1HashMapList;

				if (offset == 1) {
					handler.sendEmptyMessage(mapList.size() > 0 ? 1 : 0);
				}
				Looper.loop();

			}
		}.start();

	}
}
