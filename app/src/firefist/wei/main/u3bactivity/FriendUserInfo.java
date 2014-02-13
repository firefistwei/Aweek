package firefist.wei.main.u3bactivity;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

import firefist.wei.main.R;
import firefist.wei.main.service.MyService;
import firefist.wei.sliding.fragment.PageFragment1.MyListViewAdapter;
import firefist.wei.utils.Utils;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FriendUserInfo extends Activity {
	private View headView;
	private ImageView header_avatar;
	private ImageView header_ownvideo;
	private TextView header_distance;
	private TextView header_name;
	private TextView header_follows;
	private Button header_sendmessage;

	private Button header_status_btn;
	private Button header_active_btn;

	private boolean flag_choosen_status = true; // 判断 status 和 active 的 btn 切换

	private Context mContext = null;

	private ListView mListView;
	private MyAdapter mAdapter;

	// 用户信息
	public static HashMap<String, String> map_user = new HashMap<String, String>();

	public static ArrayList<HashMap<String, String>> mapList;
	public static ArrayList<HashMap<String, String>> statusList;
	public static ArrayList<HashMap<String, String>> activeList;

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public ImageLoader headLoader = ImageLoader.getInstance();
	public ImageLoader userLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	private String friend_uid;

	private ActionBar actionBar = null;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.u3b_friend_userinfo);

		actionBar = getActionBar();

		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.title_bar));
		// 后面根据用户名
		actionBar.setTitle("个人资料");
		actionBar.show();

		mContext = this;

		friend_uid = getIntent().getStringExtra("uid");

		options = new DisplayImageOptions.Builder().resetViewBeforeLoading()
				.cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300)).build();
		initImageLoader(mContext);

		statusList = new ArrayList<HashMap<String, String>>();
		activeList = new ArrayList<HashMap<String, String>>();

		getFriendUserInfo();

	}

	@Override
	public void onResume() {
		initList();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.u3b_friendinfo_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;

		case R.id.u3b_friendinfo_menu_guanzhu:

			break;
		case R.id.u3b_friendinfo_menu_lahei:

			break;
		case R.id.u3b_friendinfo_menu_jubao:

			break;
		}
		return super.onOptionsItemSelected(item);
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: // to UI showUserInfo
				showFriendUserInfo();
				break;
			case 1: // list 0
				if (headView != null) {
					mListView.removeHeaderView(headView);
					headView = LayoutInflater.from(mContext).inflate(
							R.layout.u3b_friend_userinfo_header, null);
					initHead();
					showFriendUserInfo();
					mListView.addHeaderView(headView);
				}

				if (flag_choosen_status == true) {
					mAdapter = new MyAdapter(mContext, mapList, 1);
				} else {
					mAdapter = new MyAdapter(mContext, mapList, 2);
				}

				mListView.setAdapter(mAdapter);
				setListener();
				break;
			case 2: // list not 0

				if (headView != null) {
					mListView.removeHeaderView(headView);
					headView = LayoutInflater.from(mContext).inflate(
							R.layout.u3b_friend_userinfo_header, null);
					initHead();
					showFriendUserInfo();
					mListView.addHeaderView(headView);
				}

				if (statusList != null) {
					header_status_btn.setText(statusList.size() + " 状态");
				}
				if (activeList != null) {
					header_active_btn.setText(activeList.size() + " 活动");
				}

				mAdapter.refreshData(mapList);
				setListener();
				break;
			}
		};
	};

	private void initHead() {

		mListView = (ListView) findViewById(R.id.u3b_friend_userinfo_listview);
		headView = LayoutInflater.from(mContext).inflate(
				R.layout.u3b_friend_userinfo_header, null);

		header_avatar = (ImageView) headView
				.findViewById(R.id.friend_userinfo_header_avatar);
		header_ownvideo = (ImageView) headView
				.findViewById(R.id.friend_userinfo_header_ownvideo);
		header_distance = (TextView) headView
				.findViewById(R.id.friend_userinfo_header_distance);
		header_name = (TextView) headView
				.findViewById(R.id.friend_userinfo_header_name);
		header_follows = (TextView) headView
				.findViewById(R.id.friend_userinfo_header_follows);
		header_sendmessage = (Button) headView
				.findViewById(R.id.friend_userinfo_header_sendmessage);
		header_status_btn = (Button) headView
				.findViewById(R.id.friend_userinfo_header_postbtn);
		header_active_btn = (Button) headView
				.findViewById(R.id.friend_userinfo_header_likebtn);

	}

	// handler to UI method
	private void showFriendUserInfo() {
		actionBar.setTitle(map_user.get("user_name") + "的个人资料");

		header_name.setText(map_user.get("user_name"));
		header_distance.setText(map_user.get("distance"));

		userLoader.displayImage(map_user.get("head_URL"), header_avatar,
				options, null);
		/*
		 * @ 用户头像
		 */
		/*
		 * new Thread() {
		 * 
		 * @Override public void run() { Looper.prepare(); try {
		 * 
		 * byte[] photoBytes = MyService.getImage(map_user.get("head_URL")); if
		 * (photoBytes != null) {
		 * header_avatar.setImageBitmap(BitmapFactory.decodeByteArray(
		 * photoBytes, 0, photoBytes.length)); } } catch (Exception e) {
		 * e.printStackTrace(); } Looper.loop(); } }.start();
		 */

		if (map_user.get("video_URL") != null
				&& !map_user.get("video_URL").equals("")) {
			header_ownvideo.setBackgroundResource(R.drawable.my_video_have);
		}

	}

	private void setListener() {

		header_avatar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		header_follows.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		header_ownvideo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			}

		});

		header_sendmessage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(FriendUserInfo.this,
						SendMessage.class);
				intent.putExtra("uid", friend_uid);
				startActivity(intent);
			}
		});

		header_status_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (flag_choosen_status != true) {
					flag_choosen_status = true;
					header_status_btn.setBackgroundColor(Color.rgb(255, 255,
							255));
					header_active_btn.setBackgroundColor(Color.rgb(211, 211,
							211));

					mapList = statusList;
					mAdapter = new MyAdapter(mContext, mapList, 1);
					mListView.setAdapter(mAdapter);
					if (statusList != null) {
						header_status_btn.setText(statusList.size() + " 状态");
					}
				}
			}
		});

		header_active_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (flag_choosen_status == true) {
					flag_choosen_status = false;
					header_status_btn.setBackgroundColor(Color.rgb(211, 211,
							211));
					header_active_btn.setBackgroundColor(Color.rgb(255, 255,
							255));

					mapList = activeList;
					mAdapter = new MyAdapter(mContext, mapList, 2);
					mListView.setAdapter(mAdapter);
					if (activeList != null) {
						header_active_btn.setText(activeList.size() + " 活动");
					}
				}
			}
		});

	}

	private void initList() {
		if (headView != null) {
			mListView.removeHeaderView(headView);
			headView = LayoutInflater.from(mContext).inflate(
					R.layout.u3b_friend_userinfo_header, null);
		}
		initHead();
		// addHeaderView 必须在 setAdapter前面
		mListView.addHeaderView(headView);

		if (mapList == null)
			mapList = new ArrayList<HashMap<String, String>>();

		if (flag_choosen_status == true) {
			mapList = statusList;
			mAdapter = new MyAdapter(mContext, mapList, 1);
		} else {
			mapList = activeList;
			mAdapter = new MyAdapter(mContext, mapList, 2);
		}
		mListView.setAdapter(mAdapter);
		setListener();

		if (mapList.size() == 0) {
			getFriendZone();
		}

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

		private AnimationDrawable anim = null;

		int type = 1;// 1 status 2 active

		public MyAdapter(Context context,
				ArrayList<HashMap<String, String>> twoList, int type) {
			this.mContext = context;
			this.mapList = twoList;
			this.type = type;

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
			if (type == 1) {

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

				if (map_user.get("head_URL") == null) {
					headLoader.displayImage(map_user.get("head_URL"),
							holder.page1_list_item_head, options, null);
				}
				holder.page1_list_item_title.setText(mapList.get(_pos).get(
						"extra_message"));

				holder.page1_list_item_timeanddistance.setText(mapList
						.get(_pos).get("distance")
						+ " | "
						+ mapList.get(_pos).get("create_time"));

				holder.page1_list_item_name.setText(map_user.get("user_name"));
				holder.page1_list_item_morecomments.setText("查看更多评论  "
						+ mapList.get(_pos).get("current_comment_num") + "条");

				imageLoader.displayImage(mapList.get(_pos).get("photo_URL"),
						photoView, options, new SimpleImageLoadingListener() {

							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
								holder.page1_list_item_progressbar
										.setVisibility(View.VISIBLE);
								startbtn.setVisibility(View.INVISIBLE);
							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {
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
							.getResources().getDrawable(
									R.drawable.ic_type_audio));
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
							.getResources().getDrawable(
									R.drawable.ic_type_video));

					startbtn.setVisibility(View.VISIBLE);

					startbtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {

						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							if (hasFocus == false) {
								Log.e("TAG_VIEW_Focuse_false", mapList
										.get(_pos).get("sid") + "  " + _pos);
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
							Log.e("START_BTN", mapList.get(_pos).get("sid")
									+ "  " + _pos);

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

								String path = mapList.get(_pos)
										.get("video_URL");
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
											Log.e("TAG_video", mapList
													.get(_pos).get("sid")
													+ "  " + _pos + "prepared");
											holder.page1_list_item_progressbar
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
								intent.putExtra("sid",
										mapList.get(_pos).get("sid"));
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
								intent.putExtra("sid",
										mapList.get(_pos).get("sid"));
								mContext.startActivity(intent);

							}
						});

				likebtn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						likebtn.setBackgroundResource(R.drawable.btn_like_pressed);

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

			} else if (type == 2) {
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

				if (map_user.get("head_URL") != null) {
					headLoader.displayImage(map_user.get("head_URL"),
							holder.page2_list_item_head, options, null);
				}

				holder.page2_list_item_title.setText(mapList.get(_pos).get(
						"title"));

				holder.page2_list_item_morecomments.setText("查看更多评论  "
						+ mapList.get(_pos).get("current_comment_num") + "条");

				holder.page2_list_item_name.setText(map_user.get("user_name"));
				holder.page2_list_item_timedistance.setText(mapList.get(_pos)
						.get("distance")
						+ " | "
						+ mapList.get(_pos).get("create_time"));

				imageLoader.displayImage(mapList.get(_pos).get("photo_URL"),
						photoView, options, new SimpleImageLoadingListener() {

							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
								progressBar.setVisibility(View.VISIBLE);
								startbtn.setVisibility(View.GONE);
							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {
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

						mediaPlayer
								.setOnPreparedListener(new OnPreparedListener() {

									public void onPrepared(MediaPlayer mp) {
										progressBar
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
											public void onCompletion(
													MediaPlayer mp) {
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
											public void onPrepared(
													MediaPlayer mp) {

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
								Intent intent = new Intent(mContext,
										ShowComments.class);
								intent.putExtra("action", "aid");
								intent.putExtra("aid",
										mapList.get(_pos).get("aid"));
								startActivity(intent);

							}
						});

				holder.page2_list_item_commentbtn
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(mContext,
										ShowComments.class);
								intent.putExtra("action", "aid");
								intent.putExtra("aid",
										mapList.get(_pos).get("aid"));
								startActivity(intent);

							}
						});

				likebtn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						likebtn.setBackgroundResource(R.drawable.btn_like_pressed);

					}
				});

				holder.page2_list_item_joinbtn
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(mContext,
										ShowJoin.class);
								intent.putExtra("aid",
										mapList.get(_pos).get("aid"));
								intent.putExtra("uid",
										mapList.get(_pos).get("uid"));
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

			}
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

	// /////////////////////////////////////////////////////////////

	/**
	 * 数据部分
	 */

	private void getFriendUserInfo() {
		new Thread() {
			public void run() {
				Looper.prepare();
				InputStream inputStream = null;

				try {
					inputStream = MyService.getUserInfo(friend_uid);
					if (inputStream == null)
						return;
					String result = Utils.readInputStream(inputStream);

					Log.e("TAG", result);

					if (result.length() > 0) {
						JSONObject jsonObject = new JSONObject(result)
								.getJSONObject("result");

						String keys[] = new String[] { "uid", "distance",
								"birthday", "gender", "create_time", "school",
								"user_name", "signature", "head_URL",
								"video_URL", "job", "hobby", "present" };

						map_user = new HashMap<String, String>();
						for (int i = 0; i < keys.length; i++) {
							String value = jsonObject.getString(keys[i]);
							if (null == value || "null".equals(value)) {
								value = "";
							}
							map_user.put(keys[i], value);
						}
						handler.sendEmptyMessage(0);
						Log.e("TAG", "handler 0");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				Looper.loop();
			}

		}.start();

	}

	private void getFriendZone() {

		new Thread() {
			@SuppressWarnings("unchecked")
			public void run() {
				Looper.prepare();
				InputStream inputStream;

				String test_uid = map_user.get("uid");
				while (test_uid == null) {
					try {
						sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				try {

					inputStream = MyService.getMyZone(friend_uid);
					if (inputStream == null)
						return;
					String jsonString = Utils.readInputStream(inputStream);

					Log.e("TAG_FriendUserInfo", jsonString);

					JSONObject jsonObject = new JSONObject(jsonString);
					JSONArray jsonArray = jsonObject.getJSONArray("result");

					mapList = new ArrayList<HashMap<String, String>>();
					statusList = new ArrayList<HashMap<String, String>>();
					activeList = new ArrayList<HashMap<String, String>>();
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
							statusList.add(map);
						} else {
							for (int j = 0; j < keys2.length; j++) {
								String value = jsonArray.getJSONObject(i)
										.get(keys2[j]).toString();
								if (null == value || "null".equals(value)) {
									value = "";
								}
								map.put(keys2[j], value);
							}
							activeList.add(map);
						}
					}
				} catch (Exception e) {
				}
				if (flag_choosen_status = true) {
					mapList = statusList;
				} else {
					mapList = activeList;
				}

				if (mapList != null) {
					handler.sendEmptyMessage(mapList.size() > 0 ? 2 : 1);
					Log.e("TAG", "handler 2:1");
				}

				Looper.loop();
			}
		}.start();

	}

}
