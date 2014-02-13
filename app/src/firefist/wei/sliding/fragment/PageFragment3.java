package firefist.wei.sliding.fragment;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import firefist.wei.main.MyBD;
import firefist.wei.main.MyConstants;
import firefist.wei.main.R;
import firefist.wei.main.service.MyService;
import firefist.wei.main.u3bactivity.FriendUserInfo;
import firefist.wei.main.u3bactivity.ShowComments;
import firefist.wei.main.u3bactivity.ShowJoin;
import firefist.wei.main.u3bactivity.UserInfo_U3bActivity;
import firefist.wei.main.u3bactivity.UserVideoShow;
import firefist.wei.main.widget.RefreshListView;
import firefist.wei.utils.Utils;

/**
 * 个人
 * 
 */
public class PageFragment3 extends Fragment implements
		RefreshListView.IOnLoadMoreListener, RefreshListView.IOnRefreshListener {

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public ImageLoader headLoader = ImageLoader.getInstance();


	private boolean flag_choosen_status = true; // 判断 status 和 active 的 btn 切换

	private Context mContext = null;
	private RefreshListView mListView;
	private MyAdapter mAdapter;
	private LoadMoreDataAsynTask mLoadMoreAsynTask;
	private RefreshDataAsynTask mRefreshAsynTask;

	private String mHeadPath = "/sdcard/U2B/My/myhead.jpg";
	private ArrayList<HashMap<String, String>> mapList = null;

	int more_pos = 0;
	DisplayImageOptions options;

	public static int scrollTop;
	public static int scrollPos;

	private ProgressDialog pd = null;
	private int deleteStatusPos;
	private int deleteActivePos;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.page3_fragment, null);

		mContext = this.getActivity();
		headView = LayoutInflater.from(mContext).inflate(R.layout.page3_header,
				null);

		mListView = (RefreshListView) view.findViewById(R.id.page3_listview);
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
		// initHeadView(); 在initView()中
		initView();
		// setListener();
		super.onResume();
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: // to UI showUserInfo
				showUserInfo();
				break;
			case 1: // list 0
				if (headView != null) {
					mListView.removeHeaderView(headView);
					headView = LayoutInflater.from(mContext).inflate(
							R.layout.page3_header, null);
					initHeadView();
					mListView.addHeaderView(headView);
				}

				if (flag_choosen_status == true) {
					mAdapter = new MyAdapter(mContext, mapList, 1);
				} else {
					mAdapter = new MyAdapter(mContext, mapList, 2);
				}
				mListView.setAdapter(mAdapter);
				setListViewListener();
				setListener();
				break;
			case 2: // list not 0
				if (headView != null) {
					mListView.removeHeaderView(headView);
					headView = LayoutInflater.from(mContext).inflate(
							R.layout.page3_header, null);
					initHeadView();
					mListView.addHeaderView(headView);
				}
				
				/*if (MyConstants.page3List_Status != null) {
					header_status_btn.setText(MyConstants.page3List_Status.size()
							+ " 状态");
				}
				if (MyConstants.page3List_Active != null) {
					header_active_btn.setText(MyConstants.page3List_Active.size()
							+ " 活动");
				}*/
				
				mAdapter.refreshData(mapList);
				setListener();
				break;
			}
		};
	};

	public void setListViewListener() {
		mListView.setOnRefreshListener(this);
		mListView.setOnLoadMoreListener(this);
	}

	private View headView;
	private ImageView header_avatar_img;
	private ImageView page3_header_ownvideo;
	private TextView header_name_tv;
	private TextView header_sig_tv;
	private Button header_wall_btn;

	private void initHeadView() {
		header_avatar_img = (ImageView) headView
				.findViewById(R.id.page3_header_avatar);
		page3_header_ownvideo = (ImageView) headView
				.findViewById(R.id.page3_header_ownvideo);
		header_name_tv = (TextView) headView
				.findViewById(R.id.page3_header_name_tv);
		header_sig_tv = (TextView) headView
				.findViewById(R.id.page3_header_sig);
		header_wall_btn = (Button) headView
				.findViewById(R.id.page3_header_wall_btn);

		if (MyConstants.User_Map != null) {
			if (MyConstants.User_Map.get("uid") != null)
				showUserInfo();
		} else {
			getUserInfo();
		}
	}

	private void setListener() {
		header_avatar_img.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!MyConstants.UserUid.equals("0")) {
					Intent intent = new Intent(getActivity(),
							UserInfo_U3bActivity.class);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.left_in,
							R.anim.left_out);
				}
			}
		});

		//个人记录
		header_wall_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, "该功能未完成，尽在下一版本！", Toast.LENGTH_SHORT).show();

			}
		});

		page3_header_ownvideo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, UserVideoShow.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.left_in,
						R.anim.left_out);
			}

		});

		/*header_status_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//记住此时滚动位置
				int Pos = scrollPos;
				int Top = scrollTop;
				if (flag_choosen_status != true) {
					flag_choosen_status = true;
					header_status_btn.setBackgroundResource(R.drawable.u3b_vine_blue_normal);
					header_active_btn.setBackgroundResource(R.drawable.u3b_vine_blue_pressed);
					
					mapList = MyConstants.page3List_Status;
					// Log.e("P3",MyConstants.page3List_Status.size()+"");
					mAdapter = new MyAdapter(mContext, mapList, 1);
					mListView.setAdapter(mAdapter);
					if (MyConstants.page3List_Status != null) {
						header_status_btn.setText(MyConstants.page3List_Status
								.size() + " 状态");
					}
				}
				mListView.setSelectionFromTop(Pos, Top);
			}
		});*/
		/*header_active_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//记住此时滚动位置
				int Pos = scrollPos;
				int Top = scrollTop;
				if (flag_choosen_status == true) {
					flag_choosen_status = false;
					header_status_btn.setBackgroundResource(R.drawable.u3b_vine_blue_pressed);
					header_active_btn.setBackgroundResource(R.drawable.u3b_vine_blue_normal);
					mapList = MyConstants.page3List_Active;
					// Log.e("P3",MyConstants.page3List_Active.size()+"");
					mAdapter = new MyAdapter(mContext, mapList, 2);
					mListView.setAdapter(mAdapter);
					if (MyConstants.page3List_Active != null) {
						header_active_btn.setText(MyConstants.page3List_Active
								.size() + " 活动");
					}
				}
				mListView.setSelectionFromTop(Pos, Top);
			}
		});*/

	}

	private void showUserInfo() {

		header_name_tv.setText(MyConstants.User_Map.get("user_name"));

		File file = new File(mHeadPath);
		if (file.exists()) {
			header_avatar_img.setImageBitmap(BitmapFactory
					.decodeFile(mHeadPath));
		} else {
			headLoader.displayImage(MyConstants.User_Map.get("head_URL"),
					header_avatar_img, options, null);
		}

		if (MyConstants.User_Map.get("video_URL") != null
				&& !MyConstants.User_Map.get("video_URL").equals("")) {
			page3_header_ownvideo
					.setBackgroundResource(R.drawable.my_video_have);
		}

		/*if (MyConstants.page3List_Status != null) {
			header_status_btn.setText(MyConstants.page3List_Status.size()
					+ " 状态");
		}
		if (MyConstants.page3List_Active != null) {
			header_active_btn.setText(MyConstants.page3List_Active.size()
					+ " 活动");
		}*/

	}

	private void initView() {
		if (headView != null) {
			mListView.removeHeaderView(headView);
			headView = LayoutInflater.from(mContext).inflate(
					R.layout.page3_header, null);
		}

		initHeadView();
		// addHeaderView 必须在 setAdapter前面
		mListView.addHeaderView(headView);

		mapList = new ArrayList<HashMap<String, String>>();

		if (flag_choosen_status == true) {
			mapList = MyConstants.page3List_Status;
			mAdapter = new MyAdapter(mContext, mapList, 1);
		} else {
			mapList = MyConstants.page3List_Active;
			mAdapter = new MyAdapter(mContext, mapList, 2);
		}

		mListView.setAdapter(mAdapter);
		mListView.setOnRefreshListener(this);
		mListView.setOnLoadMoreListener(this);
		setListener();

		if (mapList.size() == 0) {
			getMyZone();
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

	class MyAdapter extends BaseAdapter {

		private ArrayList<HashMap<String, String>> mapList;

		private Context mContext;

		private ViewHolder holder = null;
		private MediaPlayer mediaPlayer = null;
		// private SurfaceView surfaceView = null;

		int type = 1;// 1 status 2 active

		public MyAdapter(Context context,
				ArrayList<HashMap<String, String>> twoList, int type) {
			this.mContext = context;
			this.mapList = twoList;
			this.type = type;

		}

		public void refreshData(ArrayList<HashMap<String, String>> twoList) {
			this.mapList = twoList;
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
			if (type == 1) {

				if (view == null) {

					view = LayoutInflater.from(mContext).inflate(
							R.layout.page3_mystatus_item, null);
					holder = new ViewHolder();

					holder.surfaceView = (SurfaceView) view
							.findViewById(R.id.page3_mystatus_item_surfaceview);
					holder.photoView = (ImageView) view
							.findViewById(R.id.page3_mystatus_item_photoview);
					holder.progressBar = (ProgressBar) view
							.findViewById(R.id.page3_mystatus_item_progressbar);
					holder.startImage = (ImageView) view
							.findViewById(R.id.page3_mystatus_item_start);
					holder.commentImage = (ImageView) view
							.findViewById(R.id.page3_mystatus_item_comment_icon);
					holder.commentText = (TextView) view
							.findViewById(R.id.page3_mystatus_item_comment_text);
					holder.likeImage = (ImageView) view
							.findViewById(R.id.page3_mystatus_item_like_icon);
					holder.likeText = (TextView) view
							.findViewById(R.id.page3_mystatus_item_like_text);
					holder.deleteImage = (ImageView) view
							.findViewById(R.id.page3_mystatus_item_delete_icon);
					holder.leftTime = (TextView) view
							.findViewById(R.id.page3_mystatus_item_delete_text);
					view.setTag(holder);

				} else {
					holder = (ViewHolder) view.getTag();
				}

				final ImageView startbtn;
				startbtn = holder.startImage;

				final SurfaceView surfaceView;
				surfaceView = holder.surfaceView;
				final ImageView photoView;
				photoView = holder.photoView;

				surfaceView.setVisibility(View.GONE);
				photoView.setVisibility(View.VISIBLE);

				final int _pos = pos;

				holder.commentText.setText(mapList.get(_pos).get(
						"current_comment_num")
						+ " words");
				holder.likeText.setText(mapList.get(_pos).get(
						"current_like_num")
						+ " likes");
				String time_remain = mapList.get(_pos).get("remain_time");
				time_remain = Integer.valueOf(time_remain) / 3600 + "";
				holder.leftTime.setText(time_remain + " h");

				imageLoader.displayImage(mapList.get(_pos).get("photo_URL"),
						photoView, options, new SimpleImageLoadingListener() {

							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
								holder.progressBar.setVisibility(View.VISIBLE);
								startbtn.setVisibility(View.GONE);
							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {
								holder.progressBar.setVisibility(View.GONE);
							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								holder.progressBar.setVisibility(View.GONE);
								startbtn.setVisibility(View.VISIBLE);
							}
						});

				if ((mapList.get(_pos).get("audio_URL").length() > 5)) {
					// 是语音
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
							holder.progressBar.setVisibility(View.VISIBLE);

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
								holder.progressBar.setVisibility(View.VISIBLE);
								mediaPlayer.setDataSource(mapList.get(_pos)
										.get("audio_URL"));
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
											holder.progressBar
													.setVisibility(View.INVISIBLE);
											startbtn.setVisibility(View.INVISIBLE);

										}
									});
						}

					});

				} else {
					// 视频
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
							}
						}
					});

					startbtn.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(final View v) {
							// 控件
							startbtn.setVisibility(View.INVISIBLE);
							photoView.setVisibility(View.VISIBLE);
							surfaceView.setVisibility(View.INVISIBLE);
							holder.progressBar.setVisibility(View.VISIBLE);

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
								surfaceView.getHolder().setFixedSize(600, 600);
								surfaceView.getHolder().setKeepScreenOn(true);
								surfaceView.getHolder().addCallback(
										new SurfaceCallback());

								String path = mapList.get(_pos)
										.get("video_URL");
								mediaPlayer.setDataSource(path);
								Log.e("TAG3333", path);

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
														& mediaPlayer
																.isPlaying() != true) {
													mediaPlayer.seekTo(0);
													mediaPlayer.start();

												} else if (mediaPlayer != null
														& mediaPlayer
																.isPlaying() == true) {
													mediaPlayer.pause();
												}

												return false;
											}
										});
							}

							mediaPlayer
									.setOnPreparedListener(new OnPreparedListener() {

										public void onPrepared(MediaPlayer mp) {
											Log.e("TAG3333", "Prepared");
											holder.progressBar
													.setVisibility(View.INVISIBLE);
											photoView
													.setVisibility(View.INVISIBLE);
											surfaceView
													.setVisibility(View.VISIBLE);

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
				}

				holder.commentImage
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(mContext,
										ShowComments.class);
								intent.putExtra("action", "sid");
								intent.putExtra("sid",
										mapList.get(_pos).get("sid"));
								mContext.startActivity(intent);

							}
						});
				holder.likeImage.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Toast.makeText(mContext, "暂未完成，下一版本推出",
								Toast.LENGTH_SHORT).show();
					}
				});

				holder.deleteImage
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {

								new AlertDialog.Builder(mContext)
										.setTitle("你确定要删除吗")
										.setIcon(
												android.R.drawable.ic_menu_delete)
										.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
														dialog.dismiss();
														pd = ProgressDialog
																.show(mContext,
																		null,
																		"正在删除状态",
																		true,
																		true);
														deleteStatus(mapList
																.get(_pos).get(
																		"sid"));
														Log.e("DELETE　Status",
																mapList.get(
																		_pos)
																		.get("sid"));
														deleteStatusPos = _pos;
													}
												})
										.setNegativeButton(
												"取消",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
														dialog.dismiss();
													}
												}).show();
							}
						});
				holder.leftTime.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Toast.makeText(mContext, "延时功能 将在下一版本推出",
								Toast.LENGTH_SHORT).show();
					}
				});

			} else if (type == 2) {

				if (view == null) {
					view = LayoutInflater.from(mContext).inflate(
							R.layout.page3_myactive_item, null);
					holder = new ViewHolder();

					holder.surfaceView = (SurfaceView) view
							.findViewById(R.id.page3_myactive_item_surfaceview);
					holder.photoView = (ImageView) view
							.findViewById(R.id.page3_myactive_item_photoview);
					holder.progressBar = (ProgressBar) view
							.findViewById(R.id.page3_myactive_item_progressbar);
					holder.startImage = (ImageView) view
							.findViewById(R.id.page3_myactive_item_start);
					holder.commentImage = (ImageView) view
							.findViewById(R.id.page3_myactive_item_comment_icon);
					holder.commentText = (TextView) view
							.findViewById(R.id.page3_myactive_item_comment_text);
					holder.likeImage = (ImageView) view
							.findViewById(R.id.page3_myactive_item_like_icon);
					holder.likeText = (TextView) view
							.findViewById(R.id.page3_myactive_item_like_text);
					holder.deleteImage = (ImageView) view
							.findViewById(R.id.page3_myactive_item_delete_icon);
					holder.leftTime = (TextView) view
							.findViewById(R.id.page3_myactive_item_delete_text);
					
					holder.joinImage = (ImageView) view
							.findViewById(R.id.page3_myactive_item_join_icon);
					holder.joinText = (TextView) view
							.findViewById(R.id.page3_myactive_item_join_text);

					view.setTag(holder);
				} else {
					holder = (ViewHolder) view.getTag();
				}

				final ImageView startbtn;
				startbtn = holder.startImage;

				final SurfaceView surfaceView;
				surfaceView = holder.surfaceView;
				final ImageView photoView;
				photoView = holder.photoView;

				surfaceView.setVisibility(View.GONE);
				photoView.setVisibility(View.VISIBLE);

				final int _pos = pos;

				holder.commentText.setText(mapList.get(_pos).get(
						"current_comment_num")
						+ " words");
				holder.likeText.setText(mapList.get(_pos).get(
						"current_like_num")
						+ " likes");
				holder.joinText.setText(mapList.get(_pos).get("current_join_num")
						+" num");
				String time_remain = mapList.get(_pos).get("remain_time");
				time_remain = Integer.valueOf(time_remain) / 3600 + "";
				holder.leftTime.setText( time_remain + " h");

				imageLoader.displayImage(mapList.get(_pos).get("photo_URL"),
						photoView, options, new SimpleImageLoadingListener() {

							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
								holder.progressBar.setVisibility(View.VISIBLE);
								startbtn.setVisibility(View.GONE);
							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {
								holder.progressBar.setVisibility(View.GONE);
							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								holder.progressBar.setVisibility(View.GONE);
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
						}
					}
				});
				startbtn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(final View v) {
						// 控件
						startbtn.setVisibility(View.INVISIBLE);
						photoView.setVisibility(View.VISIBLE);
						surfaceView.setVisibility(View.INVISIBLE);
						holder.progressBar.setVisibility(View.VISIBLE);

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
							surfaceView.getHolder().setFixedSize(600, 600);
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
										holder.progressBar
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

				holder.commentImage
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(mContext,
										ShowComments.class);
								intent.putExtra("action", "aid");
								intent.putExtra("aid",
										mapList.get(_pos).get("aid"));
								mContext.startActivity(intent);

							}
						});
				
				holder.joinImage
						.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(mContext,
										ShowJoin.class);
								intent.putExtra("uid", MyConstants.UserUid);
								intent.putExtra("aid",
										mapList.get(_pos).get("aid"));
								mContext.startActivity(intent);
								
							}
						});
				
				holder.likeImage.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Toast.makeText(mContext, "暂未完成，下一版本推出",
								Toast.LENGTH_SHORT).show();
					}
				});

				holder.leftTime.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Toast.makeText(mContext, "延时功能 将在下一版本推出",
								Toast.LENGTH_SHORT).show();
					}
				});

				holder.deleteImage
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								new AlertDialog.Builder(mContext)
										.setTitle("你确定要删除吗")
										.setIcon(
												android.R.drawable.ic_menu_delete)
										.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
														dialog.dismiss();
														pd = ProgressDialog
																.show(mContext,
																		null,
																		"正在删除活动",
																		true,
																		true);
														deleteActive(mapList
																.get(_pos).get(
																		"aid"));
														Log.e("DELETE　Active",
																mapList.get(
																		_pos)
																		.get("aid"));
														deleteActivePos = _pos;
													}
												})
										.setNegativeButton(
												"取消",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
														dialog.dismiss();
													}
												}).show();

							}
						});
			}
			return view;
		}

		class ViewHolder {
			ImageView photoView;
			SurfaceView surfaceView;
			ProgressBar progressBar;
			ImageView startImage;
			ImageView commentImage;
			TextView commentText;
			ImageView likeImage;
			TextView likeText;
			ImageView deleteImage;
			TextView leftTime;
			
			ImageView joinImage;
			TextView joinText;
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (MyConstants.User_Map==null && MyConstants.User_Map.get("uid")==null) {
				getUserInfo();
			}
			getMyZone();

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

	// /////////////////////////////////////////////////////////////

	/**
	 * 数据部分
	 */

	private void getUserInfo() {
		new Thread() {
			public void run() {
				Looper.prepare();
				InputStream inputStream = null;

				while (MyConstants.Latitude == 0) {
					try {
						MyBD myBD = new MyBD(mContext);
						myBD.getLocation();
						sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				try {
					inputStream = MyService.getUserInfo(MyConstants.UserUid);
					if (inputStream == null)
						return;
					String result = Utils.readInputStream(inputStream);
					if (mapList != null && result.length() < 20)
						return;

					if (result.length() > 0) {
						JSONObject jsonObject = new JSONObject(result);
						JSONObject userObject = jsonObject
								.getJSONObject("result");

						String[] keys = new String[] { "uid", "distance",
								"birthday", "gender", "create_time", "school",
								"user_name", "signature", "head_URL",
								"video_URL", "job", "hobby", "present" };

						HashMap<String, String> user_map = new HashMap<String, String>();
						for (int i = 0; i < keys.length; i++) {
							String value = userObject.getString(keys[i]);
							if (null == value || "null".equals(value)) {
								value = "";
							}
							user_map.put(keys[i], value);
						}

						// MyConstants.UserUid = user_map.get("uid");
						MyConstants.User_Map = user_map;

						handler.sendEmptyMessage(0);

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				Looper.loop();
			}
		}.start();
	}

	private void getMyZone() {

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
					String uid = MyConstants.UserUid;
					Log.e("TAG3 Uid", uid);
					inputStream = MyService.getMyZone(uid);
					String jsonString = Utils.readInputStream(inputStream);
					Log.e("TAG3", jsonString);

					JSONObject jsonObject = new JSONObject(jsonString);
					JSONArray jsonArray = jsonObject.getJSONArray("result");

					mapList = new ArrayList<HashMap<String, String>>();
					MyConstants.page3List_Status = new ArrayList<HashMap<String, String>>();
					MyConstants.page3List_Active = new ArrayList<HashMap<String, String>>();
					HashMap<String, String> map = null;

					String keys1[] = new String[] { "sid", "owner",
							"photo_URL", "audio_URL", "video_URL",
							"extra_message", "create_time", "distance",
							"current_comment_num", "remain_time" };// length=9
					String keys2[] = new String[] { "aid", "owner",
							"create_time", "start_time", "location", "title",
							"photo_URL", "audio_URL", "video_URL",
							"current_support_num", "current_join_num",
							"current_comment_num", "distance", "remain_time" }; // 13
					for (int i = 0; i < jsonArray.length(); i++) {

						map = new HashMap<String, String>();
						if (jsonArray.getJSONObject(i).toString()
								.contains("sid")) {
							for (int j = 0; j < keys1.length; j++) {
								String value = jsonArray.getJSONObject(i)
										.get(keys1[j]).toString();
								if (null == value || "null".equals(value)) {
									value = "";
								}
								map.put(keys1[j], value);
							}
							MyConstants.page3List_Status.add(map);
						} else {
							for (int j = 0; j < keys2.length; j++) {
								String value = jsonArray.getJSONObject(i)
										.get(keys2[j]).toString();
								if (null == value || "null".equals(value)) {
									value = "";
								}
								map.put(keys2[j], value);

							}
							MyConstants.page3List_Active.add(map);
						}

					}
				} catch (Exception e) {
				}

				if (flag_choosen_status = true) {
					mapList = MyConstants.page3List_Status;
				} else {
					mapList = MyConstants.page3List_Active;
				}

				handler.sendEmptyMessage(mapList.size() > 0 ? 2 : 1);

				Looper.loop();
			}
		}.start();

	}

	private void deleteStatus(final String sid) {
		new Thread() {
			public void run() {
				Looper.prepare();
				InputStream inputStream;
				try {

					inputStream = MyService.deleteStatus(sid);
					String jsonString = Utils.readInputStream(inputStream);
					Log.e("TAG3", jsonString);

					JSONObject jsonObject = new JSONObject(jsonString);
					int flag = Integer.valueOf(jsonObject.getString("result"));

					if (flag > 0) {
						pd.dismiss();
						Toast.makeText(mContext, "删除成功", Toast.LENGTH_LONG)
								.show();
						if (mAdapter != null) {
							mapList.remove(deleteStatusPos);
							MyConstants.page3List_Status.remove(deleteStatusPos);
							mAdapter.refreshData(mapList);
							
							handler.sendEmptyMessage(3);
						}
						

					} else {
						pd.dismiss();
						Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT)
								.show();

					}
					Looper.loop();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

	}

	private void deleteActive(final String aid) {
		new Thread() {
			public void run() {
				Looper.prepare();
				InputStream inputStream;
				try {

					inputStream = MyService.deleteActive(aid);
					String jsonString = Utils.readInputStream(inputStream);
					Log.e("TAG3", jsonString);

					JSONObject jsonObject = new JSONObject(jsonString);
					int flag = Integer.valueOf(jsonObject.getString("result"));

					if (flag > 0) {
						pd.dismiss();
						Toast.makeText(mContext, "删除成功", Toast.LENGTH_LONG)
								.show();
						if (mAdapter != null) {
							mapList.remove(deleteActivePos);
							MyConstants.page3List_Active.remove(deleteActivePos);
							mAdapter.refreshData(mapList);
							
							handler.sendEmptyMessage(4);
						}

					} else {
						pd.dismiss();
						Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT)
								.show();

					}
					Looper.loop();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

	}

}