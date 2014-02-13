package firefist.wei.sliding.fragment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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
import firefist.wei.main.u3bactive.Home_MyActive;
import firefist.wei.main.u3bactive.Home_PublishActive;
import firefist.wei.main.u3bactive.Introduction_Active;
import firefist.wei.main.u3bactivity.FriendUserInfo;
import firefist.wei.main.u3bactivity.ShowComments;
import firefist.wei.main.u3bactivity.ShowJoin;
import firefist.wei.main.widget.RefreshListView;
import firefist.wei.sliding.fragment.PageFragment1.MyListViewAdapter.ViewHolder;
import firefist.wei.utils.Utils;

/**
 * @活动
 * 
 */
public class PageFragment2 extends Fragment implements
		RefreshListView.IOnRefreshListener, RefreshListView.IOnLoadMoreListener {

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public ImageLoader headLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	private View headView;
	private Button page2_header_publish_btn;
	private Button page2_header_my_btn;

	private Context mContext = null;

	private RefreshListView mListView;
	private MyAdapter mAdapter;
	private RefreshDataAsynTask mRefreshAsynTask;
	private LoadMoreDataAsynTask mLoadMoreAsynTask;

	public static int scrollTop;
	public static int scrollPos;
	/**
	 * 网络数据
	 */
	private static int page_offset = 1; // 查看 附近活动 的 offset
	private ArrayList<HashMap<String, String>> mapList = null;
	public static HashMap<String, Integer> statusLikeMap = new HashMap<String, Integer>(); // 0
	private int more_pos = 0;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.page2_fragment, null);

		mContext = this.getActivity();

		headView = LayoutInflater.from(mContext).inflate(R.layout.page2_header,
				null);
		mListView = (RefreshListView) view
				.findViewById(R.id.page2_fragment_refreshlist);
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		options = new DisplayImageOptions.Builder().resetViewBeforeLoading()
				.cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		initImageLoader(mContext);

	}

	@Override
	public void onResume() {
		// initHead(); 在initList()中
		initList();
		super.onResume();

	}

	private void initHead() {

		page2_header_my_btn = (Button) headView
				.findViewById(R.id.page2_header_my_btn);
		page2_header_publish_btn = (Button) headView
				.findViewById(R.id.page2_header_publish_btn);

		page2_header_my_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, Introduction_Active.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.left_in,
						R.anim.left_out);

			}
		});

		page2_header_publish_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, Home_PublishActive.class);

				intent.putExtra("action", "type_0");
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.left_in,
						R.anim.left_out);
			}
		});

	}

	private void initList() {

		if (headView != null) {
			mListView.removeHeaderView(headView);
			headView = LayoutInflater.from(mContext).inflate(
					R.layout.page2_header, null);
		}

		initHead();
		// addHeaderView 必须在 setAdapter前面
		mListView.addHeaderView(headView);

		mapList = new ArrayList<HashMap<String, String>>();
		mapList = MyConstants.page2HashMapList;

		mAdapter = new MyAdapter(mContext, mapList);
		mListView.setAdapter(mAdapter);
		setListViewListener();

		if (mapList.size() == 0) {
			getNearActive(page_offset);
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
				if (headView != null) {
					mListView.removeHeaderView(headView);
					headView = LayoutInflater.from(mContext).inflate(
							R.layout.page2_header, null);
					initHead();
					mListView.addHeaderView(headView);
				}

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

				mAdapter = new MyAdapter(mContext, mapList);
				mListView.setAdapter(mAdapter);
				setListViewListener();
				break;
			case 1:
				if (headView != null) {
					mListView.removeHeaderView(headView);
					headView = LayoutInflater.from(mContext).inflate(
							R.layout.page2_header, null);
					initHead();
					mListView.addHeaderView(headView);
				}
				for (int i = 0; i < mapList.size(); i++) {
					String aidKey = mapList.get(i).get("aid");
					if (!statusLikeMap.containsKey(aidKey)) {
						statusLikeMap.put(aidKey, 0);
					}
				}
				if (sadView != null) {
					mListView.removeHeaderView(sadView);
				}

				mAdapter.refreshData(mapList);
				break;
			}
		};
	};

	public void setListViewListener() {
		mListView.setOnRefreshListener(this);
		mListView.setOnLoadMoreListener(this);
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

	private class MyAdapter extends BaseAdapter {

		private ArrayList<HashMap<String, String>> mapList;

		private Context mContext;

		private ViewHolder holder = null;
		private MediaPlayer mediaPlayer = null;

		private AnimationDrawable anim = null;

		// private SurfaceView surfaceView = null;

		public MyAdapter(Context context,
				ArrayList<HashMap<String, String>> activeList) {
			this.mContext = context;
			this.mapList = activeList;
		}

		public void refreshData(ArrayList<HashMap<String, String>> activeList) {
			this.mapList = activeList;
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
						R.layout.page2_list_item, null);
				holder = new ViewHolder();

				holder.page2_list_item_name = (TextView) view
						.findViewById(R.id.page2_list_item_name);
				holder.page2_list_item_head = (ImageView) view
						.findViewById(R.id.page2_list_item_head);
				holder.page2_list_item_timedistance = (TextView) view
						.findViewById(R.id.page2_list_item_timedistance);

				holder.page2_list_item_photo = (ImageView) view
						.findViewById(R.id.page2_list_item_photo);
				holder.page2_list_item_progressbar = (ProgressBar) view
						.findViewById(R.id.page2_list_item_progressbar);

				holder.page2_list_item_start = (ImageView) view
						.findViewById(R.id.page2_list_item_start);
				holder.page2_list_item_type = (ImageView) view
						.findViewById(R.id.page2_list_item_type);

				holder.page2_list_item_audiolayout = (LinearLayout) view
						.findViewById(R.id.page2_list_item_audiolayout);
				holder.page2_list_item_audioicon = (ImageView) view
						.findViewById(R.id.page2_list_item_audioicon);

				holder.page2_list_item_title = (TextView) view
						.findViewById(R.id.page2_list_item_title);

				holder.page2_list_item_morecomments = (TextView) view
						.findViewById(R.id.page2_list_item_morecomments);

				holder.page2_list_item_likebtn = (Button) view
						.findViewById(R.id.page2_list_item_likebtn);
				holder.page2_list_item_commentbtn = (Button) view
						.findViewById(R.id.page2_list_item_commentbtn);
				holder.page2_list_item_joinbtn = (Button) view
						.findViewById(R.id.page2_list_item_joinbtn);
				holder.page2_list_item_morebtn = (Button) view
						.findViewById(R.id.page2_list_item_morebtn);

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			/*
			 * holder.page2_list_item_photo
			 * .setMinimumWidth(MainActivity.mScreenWidth - 10);
			 * holder.page2_list_item_photo
			 * .setMinimumHeight(MainActivity.mScreenWidth - 10);
			 * holder.page2_list_item_videoview .setLayoutParams(new
			 * LinearLayout.LayoutParams( MainActivity.mScreenWidth - 10,
			 * MainActivity.mScreenWidth - 10));
			 */

			final Button likebtn;
			final ImageView startbtn;
			likebtn = holder.page2_list_item_likebtn;
			startbtn = holder.page2_list_item_start;
			final ProgressBar progressBar;
			progressBar = holder.page2_list_item_progressbar;

			final ImageView audioIcon;
			audioIcon = holder.page2_list_item_audioicon;

			final SurfaceView surfaceView;
			surfaceView = (SurfaceView) view
					.findViewById(R.id.page2_list_item_surfaceview);
			final ImageView photoView;
			photoView = (ImageView) view
					.findViewById(R.id.page2_list_item_photo);

			surfaceView.setVisibility(View.GONE);
			photoView.setVisibility(View.VISIBLE);

			final int _pos = pos;

			if (!mapList.get(_pos).get("head_URL").equals("")) {
				headLoader.displayImage(mapList.get(_pos).get("head_URL"),
						holder.page2_list_item_head, options, null);
			}

			holder.page2_list_item_title
					.setText(mapList.get(_pos).get("title"));

			holder.page2_list_item_morecomments.setText("查看更多评论  "
					+ mapList.get(_pos).get("current_comment_num") + "条");

			holder.page2_list_item_name.setText(mapList.get(_pos).get(
					"user_name"));
			holder.page2_list_item_timedistance.setText(mapList.get(_pos).get(
					"distance")
					+ " | " + mapList.get(_pos).get("create_time"));

			// 喜欢 按钮
			int likeFlag = statusLikeMap.get(mapList.get(_pos).get("aid"));
			if (likeFlag == 1) {
				likebtn.setBackgroundResource(R.drawable.btn_like_pressed);
			} else {
				likebtn.setBackgroundResource(R.drawable.btn_like_default);
			}

			imageLoader.displayImage(mapList.get(_pos).get("photo_URL"),
					photoView, options, new SimpleImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {
							progressBar.setVisibility(View.VISIBLE);
							startbtn.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							progressBar.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							progressBar.setVisibility(View.GONE);
							startbtn.setVisibility(View.VISIBLE);
						}
					});
			/*
			 * (non-Javadoc)
			 * 
			 * @ 播放视频
			 */
			startbtn.setVisibility(View.VISIBLE);
			startbtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus == false) {
						if (mediaPlayer != null) {
							if (mediaPlayer.isPlaying())
								mediaPlayer.stop();
							mediaPlayer.setOnCompletionListener(null);
							mediaPlayer.setOnPreparedListener(null);
							mediaPlayer = null;
						}
						// 控件
						surfaceView.setVisibility(View.GONE);
						photoView.setVisibility(View.VISIBLE);
						startbtn.setVisibility(View.VISIBLE);
					}
				}
			});
			startbtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(final View v) {
					Log.e("START_BTN", mapList.get(_pos).get("aid") + "  "
							+ _pos);
					// 控件
					startbtn.setVisibility(View.INVISIBLE);
					photoView.setVisibility(View.VISIBLE);
					surfaceView.setVisibility(View.INVISIBLE);
					progressBar.setVisibility(View.VISIBLE);

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

						Log.e("TAG2_path", path);

						mediaPlayer.prepareAsync();

					} catch (Exception e) {

					}

					surfaceView.setOnTouchListener(new View.OnTouchListener() {
						@Override
						public boolean onTouch(View v, MotionEvent event) {
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

					mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

						public void onPrepared(MediaPlayer mp) {
							progressBar.setVisibility(View.INVISIBLE);
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

			/*
			 * (non-Javadoc)
			 * 
			 * @ 播放audio
			 */
			holder.page2_list_item_audiolayout
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							if (mediaPlayer != null) {
								if (mediaPlayer.isPlaying()) {
									mediaPlayer.stop();

									if (anim != null) {
										if (anim.isRunning())
											anim.stop();
										anim = null;
									}
									audioIcon
											.setBackgroundResource(R.drawable.ic_play);
								}
								mediaPlayer.setOnCompletionListener(null);
								mediaPlayer.setOnPreparedListener(null);
								mediaPlayer = null;
							}
							mediaPlayer = new MediaPlayer();

							try {
								mediaPlayer.setDataSource(mapList.get(_pos)
										.get("audio_URL"));
								mediaPlayer.prepareAsync();
							} catch (Exception e) {

							}
							mediaPlayer
									.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

										@Override
										public void onCompletion(MediaPlayer mp) {
											if (anim != null) {
												if (anim.isRunning())
													anim.stop();
												anim = null;
											}
											audioIcon
													.setBackgroundResource(R.drawable.ic_play);

										}
									});
							mediaPlayer
									.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

										@Override
										public void onPrepared(MediaPlayer mp) {

											mp.start();
											audioIcon
													.setBackgroundResource(R.drawable.u3b_play_anim);
											Object obj = audioIcon
													.getBackground();
											anim = (AnimationDrawable) obj;
											anim.stop();
											anim.start();
										}

									});
						}
					});

			// ////////////////////上面的语音

			holder.page2_list_item_head
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext,
									FriendUserInfo.class);
							intent.putExtra("uid",
									mapList.get(_pos).get("owner"));
							startActivity(intent);

						}
					});

			holder.page2_list_item_name
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext,
									FriendUserInfo.class);
							intent.putExtra("uid",
									mapList.get(_pos).get("owner"));
							startActivity(intent);
						}
					});

			holder.page2_list_item_morecomments
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(),
									ShowComments.class);
							intent.putExtra("action", "aid");
							intent.putExtra("aid", mapList.get(_pos).get("aid"));
							startActivity(intent);

						}
					});

			holder.page2_list_item_commentbtn
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(),
									ShowComments.class);
							intent.putExtra("action", "aid");
							intent.putExtra("aid", mapList.get(_pos).get("aid"));
							startActivity(intent);

						}
					});

			likebtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.e("LIKE_BTN", mapList.get(_pos).get("sid") + "  "
							+ _pos);

					int likeFlag = statusLikeMap.get(mapList.get(_pos).get(
							"aid"));
					if (likeFlag == 0) {// 按下喜欢
						likebtn.setBackgroundResource(R.drawable.btn_like_pressed);
						statusLikeMap.put(mapList.get(_pos).get("aid"), 1);
						Log.e("pressed", 1 + "");
					} else {// 取消喜欢
						likebtn.setBackgroundResource(R.drawable.btn_like_default);
						statusLikeMap.put(mapList.get(_pos).get("aid"), 0);
						Log.e("pressed", 0 + "");
					}

				}
			});

			holder.page2_list_item_joinbtn
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(),
									ShowJoin.class);
							intent.putExtra("aid", mapList.get(_pos).get("aid"));
							intent.putExtra("uid",
									mapList.get(_pos).get("owner"));
							startActivity(intent);
						}
					});

			holder.page2_list_item_morebtn
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
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

			ImageView page2_list_item_head;
			TextView page2_list_item_name;
			TextView page2_list_item_timedistance;

			ImageView page2_list_item_photo;
			ProgressBar page2_list_item_progressbar;
			ImageView page2_list_item_start;
			ImageView page2_list_item_type;

			LinearLayout page2_list_item_audiolayout;
			ImageView page2_list_item_audioicon;

			TextView page2_list_item_title;

			TextView page2_list_item_morecomments;
			Button page2_list_item_likebtn;
			Button page2_list_item_commentbtn;
			Button page2_list_item_joinbtn;
			Button page2_list_item_morebtn;

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
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
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

	class RefreshDataAsynTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			page_offset = 1;
			getNearActive(page_offset);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

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
				e.printStackTrace();
			}

			page_offset = page_offset + 10;
			getNearActive(page_offset);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			mAdapter.refreshData(mapList);

			if (more_pos > 0) {
				mListView.onLoadMoreComplete(false);
			} else {
				// 数据加载完了
				mListView.onLoadMoreComplete(true);
			}
		}
	}

	// /////////////////////////////////////////////////
	/**
	 * 获取数据
	 */

	private void getNearActive(final int offset) {
		new Thread() {

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
					inputStream = MyService.getNearActive(offset);
					if (inputStream == null)
						return;
					String jsonString = Utils.readInputStream(inputStream);
					if (mapList != null && jsonString.length() < 15)
						return;

					Log.e("Page2-TAG", jsonString);

					JSONObject jsonObject = new JSONObject(jsonString);
					JSONArray jsonArray = jsonObject.getJSONArray("result");

					mapList = new ArrayList<HashMap<String, String>>();

					if (offset == 1) {
						MyConstants.page2HashMapList = new ArrayList<HashMap<String, String>>();
					}
					HashMap<String, String> map = null;

					String keys[] = new String[] { "aid", "owner", "user_name",
							"head_URL", "create_time", "start_time",
							"location", "title", "photo_URL", "audio_URL",
							"video_URL", "current_support_num",
							"current_join_num", "current_comment_num",
							"distance", "remain_time" }; // 15
					for (int i = 0; i < jsonArray.length(); i++) {

						map = new HashMap<String, String>();

						for (int j = 0; j < keys.length; j++) {
							String value = jsonArray.getJSONObject(i)
									.get(keys[j]).toString();
							if (null == value || "null".equals(value)) {
								value = "";
							}
							map.put(keys[j], value);
						}

						MyConstants.page2HashMapList.add(map);
					}
					if (offset > 1) {
						more_pos = (jsonArray.length() > 0) ? 1 : 0;
					}

				} catch (Exception e) {
				}
				mapList = MyConstants.page2HashMapList;

				if (offset == 1) {
					handler.sendEmptyMessage(mapList.size() > 0 ? 1 : 0);
				}
				Looper.loop();
			}
		}.start();
	}

}
