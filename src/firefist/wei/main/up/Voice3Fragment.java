package firefist.wei.main.up;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import firefist.wei.main.MainActivity;
import firefist.wei.main.MyConstants;
import firefist.wei.main.R;
import firefist.wei.main.activity.PublishVoice;
import firefist.wei.main.service.MyUploadService;
import firefist.wei.utils.ActivityForResultUtil;
import firefist.wei.utils.RecordUtil;

/**
 * @author Administrator
 * 
 */

public class Voice3Fragment extends Activity {

	public static final String TAG = "Voice3Fragment";

	/**
	 * @界面控件
	 * 
	 */
	RelativeLayout u3b_voice3_layout_top;
	TextView u3b_voice3_record_time;
	TextView u3b_voice3_record_maxtime;
	ProgressBar u3b_voice3_record_progressbar;

	static ImageView u3b_voice3_photo;
	ImageView u3b_voice3_add;

	LinearLayout u3b_voice3_layout_btns;
	ImageView u3b_voice3_redbtn;
	ImageView u3b_voice3_bluebtn;

	LinearLayout u3b_voice3_layout_texts;
	TextView u3b_voice3_title;

	RelativeLayout u3b_voice3_layout_record;
	ImageView u3b_voice3_recording_volume;
	RelativeLayout u3b_voice3_recordinglight_layout;
	ImageView u3b_voice3_recordinglight_1;
	ImageView u3b_voice3_recordinglight_2;
	ImageView u3b_voice3_recordinglight_3;

	ImageView u3b_voice3_startbtn;
	ImageView u3b_voice3_playbtn;

	/**
	 * @录音模块
	 * 
	 */
	private Animation mRecordLight_1_Animation;
	private Animation mRecordLight_2_Animation;
	private Animation mRecordLight_3_Animation;

	private MediaPlayer mMediaPlayer;
	private RecordUtil mRecordUtil;
	private static final int MAX_TIME = 60;// 最长录音时间
	private static final int MIN_TIME = 2;// 最短录音时间

	private static final int RECORD_NO = 0; // 不在录音
	private static final int RECORD_ING = 1; // 正在录音
	private static final int RECORD_ED = 2; // 完成录音
	private int mRecord_State = 0; // 录音的状态
	private float mRecord_Time;// 录音的时间
	private double mRecord_Volume;// 麦克风获取的音量值
	private boolean mPlayState; // 播放状态
	private int mPlayCurrentPosition;// 当前播放的时间
	private int mMAXVolume;// 最大音量高度
	private int mMINVolume;// 最小音量高度

	/**
	 * @数据模块
	 * 
	 */
	ProgressDialog progressDialog = null;

	private static final String PATH = "/sdcard/U2B/Up/";// 录音存储路径
	private String mRecordPath = null;// 录音的存储名称
	private File mAudioFile = null;

	public Bitmap mPhotoBitmap = null;// 上传的图片
	public File mPhotoFile = null;

	private Context mContext = null;
	
	private String display_time ="168";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.u3b_voice3fragment);

		mContext = this;
		final ActionBar actionBar = getActionBar();

		actionBar.setIcon(R.drawable.app_icon);
		
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("语音状态");
		
		clearFile();

		findViewById();
		initListener();
		initVolume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.u3b_upactivity_menu_audio, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			/*Intent upIntent = new Intent(this, MainActivity.class);
			upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(upIntent);*/
			overridePendingTransition(R.anim.right_in,
					R.anim.right_out);
			return true;

		case R.id.menu_audio_to_video:
			Intent intent = new Intent(this, Video3Fragment.class);
			startActivity(intent);
			overridePendingTransition(R.anim.right_in,
					R.anim.right_out);
			this.finish();
			return true;
		case R.id.u3b_upactivity_menu_currenttime:
			new AlertDialog.Builder(mContext)
			.setTitle("有效时间")
			.setItems(
					new String[] { "(默认)一周", "1小时",
							"3小时","24小时","48小时","72小时"},
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface dialog,
								int which) {
							switch (which) {
							case 0:
								display_time = "168";
								break;
							case 1:
								display_time = "1";
								break;
							case 2:
								display_time = "3";
								break;
							case 3:
								display_time = "24";
								break;
							case 4:
								display_time = "48";
								break;
							case 5:
								display_time = "72";
								break;
							}
							dialog.dismiss();

						}
					}).create().show();
			return true;
		}
		return super.onOptionsItemSelected(item);

	}

	private void findViewById() {
		u3b_voice3_layout_top = (RelativeLayout) this
				.findViewById(R.id.u3b_voice3_layout_top);
		u3b_voice3_record_time = (TextView) this
				.findViewById(R.id.u3b_voice3_record_time);
		u3b_voice3_record_maxtime = (TextView) this
				.findViewById(R.id.u3b_voice3_record_maxtime);
		u3b_voice3_record_progressbar = (ProgressBar) this
				.findViewById(R.id.u3b_voice3_record_progressbar);

		u3b_voice3_photo = (ImageView) this.findViewById(R.id.u3b_voice3_photo);
		u3b_voice3_add = (ImageView) this.findViewById(R.id.u3b_voice3_add);
		u3b_voice3_layout_btns = (LinearLayout) this
				.findViewById(R.id.u3b_voice3_layout_btns);
		u3b_voice3_redbtn = (ImageView) this
				.findViewById(R.id.u3b_voice3_redbtn);
		u3b_voice3_bluebtn = (ImageView) this
				.findViewById(R.id.u3b_voice3_bluebtn);
		u3b_voice3_layout_texts = (LinearLayout) this
				.findViewById(R.id.u3b_voice3_layout_texts);
		u3b_voice3_title = (TextView) this.findViewById(R.id.u3b_voice3_title);
		u3b_voice3_layout_record = (RelativeLayout) this
				.findViewById(R.id.u3b_voice3_layout_record);
		u3b_voice3_recording_volume = (ImageView) this
				.findViewById(R.id.u3b_voice3_recording_volume);
		u3b_voice3_recordinglight_layout = (RelativeLayout) this
				.findViewById(R.id.u3b_voice3_recordinglight_layout);
		u3b_voice3_recordinglight_1 = (ImageView) this
				.findViewById(R.id.u3b_voice3_recordinglight_1);
		u3b_voice3_recordinglight_2 = (ImageView) this
				.findViewById(R.id.u3b_voice3_recordinglight_2);
		u3b_voice3_recordinglight_3 = (ImageView) this
				.findViewById(R.id.u3b_voice3_recordinglight_3);

		u3b_voice3_playbtn = (ImageView) this
				.findViewById(R.id.u3b_voice3_playbtn);
		u3b_voice3_startbtn = (ImageView) this
				.findViewById(R.id.u3b_voice3_startbtn);

	}

	private void clearFile() {
		File dir = new File(PATH);
		if (!dir.exists()) {
			dir.mkdir();
		}
		if (dir.isDirectory()) {
			// 处理目录
			File files[] = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (!file.isDirectory()) {// 如果是文件，删除
					file.delete();
				}
			}
		}
	}

	private void initVolume() {
		// 设置当前的最小声音和最大声音值
		mMINVolume = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4.5f, getResources()
						.getDisplayMetrics());
		mMAXVolume = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 65f, getResources()
						.getDisplayMetrics());

	}

	/**
	 * 
	 */
	private void initListener() {

		u3b_voice3_startbtn.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				// 开始录音
				case MotionEvent.ACTION_DOWN:
					u3b_voice3_layout_record.setVisibility(View.VISIBLE);
					if (mRecord_State != RECORD_ING) {
						// 开始动画效果
						startRecordLightAnimation();
						// 修改录音状态
						mRecord_State = RECORD_ING;
						// 设置录音保存路径
						mRecordPath = PATH + UUID.randomUUID().toString()
								+ ".amr";
						// 实例化录音工具类
						mRecordUtil = new RecordUtil(mRecordPath);
						try {
							// 开始录音
							mRecordUtil.start();
						} catch (IOException e) {
							e.printStackTrace();
						}
						new Thread(new Runnable() {

							public void run() {
								// 初始化录音时间
								mRecord_Time = 0;
								while (mRecord_State == RECORD_ING) {
									// 大于最大录音时间则停止录音
									if (mRecord_Time >= MAX_TIME) {
										mRecordHandler.sendEmptyMessage(0);
									} else {
										try {
											// 每隔200毫秒就获取声音音量并更新界面显示
											Thread.sleep(200);
											mRecord_Time += 0.2;
											if (mRecord_State == RECORD_ING) {
												mRecord_Volume = mRecordUtil
														.getAmplitude();
												mRecordHandler
														.sendEmptyMessage(1);
											}
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}).start();
					}
					break;
				// 停止录音
				case MotionEvent.ACTION_UP:
					u3b_voice3_layout_record.setVisibility(View.GONE);
					if (mRecord_State == RECORD_ING) {
						// 停止动画效果
						stopRecordLightAnimation();
						// 修改录音状态
						mRecord_State = RECORD_ED;
						try {
							// 停止录音
							mRecordUtil.stop();
							// 初始录音音量
							mRecord_Volume = 0;
						} catch (IOException e) {
							e.printStackTrace();
						}
						// 如果录音时间小于最短时间
						if (mRecord_Time <= MIN_TIME) {
							// 显示提醒
							Toast.makeText(mContext, "录音时间过短",
									Toast.LENGTH_SHORT).show();
							// 修改录音状态
							mRecord_State = RECORD_NO;
							// 修改录音时间
							mRecord_Time = 0;
							// 修改显示界面
							u3b_voice3_record_time.setText("0″");
							u3b_voice3_record_progressbar.setProgress(0);
							// 修改录音声音界面
							ViewGroup.LayoutParams params = u3b_voice3_recording_volume
									.getLayoutParams();
							params.height = 0;
							u3b_voice3_recording_volume.setLayoutParams(params);
						} else {
							// 录音成功,则显示录音成功后的界面
							u3b_voice3_layout_record.setVisibility(View.GONE);
							u3b_voice3_startbtn.setVisibility(View.GONE);
							u3b_voice3_layout_btns.setVisibility(View.VISIBLE);
							u3b_voice3_layout_texts.setVisibility(View.VISIBLE);

							u3b_voice3_playbtn.setVisibility(View.VISIBLE);

							u3b_voice3_record_progressbar
									.setMax((int) mRecord_Time);
							u3b_voice3_record_progressbar.setProgress(0);
							u3b_voice3_record_time.setText("0″");
							u3b_voice3_record_maxtime
									.setText((int) mRecord_Time + "″");
						}
					}
					break;
				}
				return false;
			}
		});
		u3b_voice3_playbtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 播放录音
				if (!mPlayState) {
					mMediaPlayer = new MediaPlayer();
					try {
						// 添加录音的路径
						mMediaPlayer.setDataSource(mRecordPath);
						// 准备
						mMediaPlayer.prepare();
						// 播放
						mMediaPlayer.start();
						// 根据时间修改界面
						new Thread(new Runnable() {

							public void run() {

								u3b_voice3_record_progressbar
										.setMax((int) mRecord_Time);
								mPlayCurrentPosition = 0;
								while (mMediaPlayer.isPlaying()) {
									mPlayCurrentPosition = mMediaPlayer
											.getCurrentPosition() / 1000;
									u3b_voice3_record_progressbar
											.setProgress(mPlayCurrentPosition);
								}
							}
						}).start();
						// 修改播放状态
						mPlayState = true;
						// 修改播放图标
						u3b_voice3_playbtn.setVisibility(View.GONE);

						mMediaPlayer
								.setOnCompletionListener(new OnCompletionListener() {
									// 播放结束后调用
									public void onCompletion(MediaPlayer mp) {
										// 停止播放
										mMediaPlayer.stop();
										// 修改播放状态
										mPlayState = false;
										// 修改播放图标

										// 初始化播放数据
										mPlayCurrentPosition = 0;
										u3b_voice3_record_progressbar
												.setProgress(mPlayCurrentPosition);
										u3b_voice3_playbtn
												.setVisibility(View.VISIBLE);
									}
								});

					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					if (mMediaPlayer != null) {
						// 根据播放状态修改显示内容
						if (mMediaPlayer.isPlaying()) {
							mPlayState = false;
							mMediaPlayer.stop();
							u3b_voice3_playbtn.setVisibility(View.VISIBLE);
							mPlayCurrentPosition = 0;
							u3b_voice3_record_progressbar
									.setProgress(mPlayCurrentPosition);
						} else {
							mPlayState = false;
							u3b_voice3_playbtn.setVisibility(View.VISIBLE);
							mPlayCurrentPosition = 0;
							u3b_voice3_record_progressbar
									.setProgress(mPlayCurrentPosition);
						}
					}
				}
			}
		});

		u3b_voice3_redbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 录音成功,则显示录音成功后的界面
				u3b_voice3_startbtn.setVisibility(View.VISIBLE);
				u3b_voice3_layout_btns.setVisibility(View.GONE);
				u3b_voice3_layout_texts.setVisibility(View.GONE);

				mPlayState = false;
				u3b_voice3_playbtn.setVisibility(View.GONE);
				mPlayCurrentPosition = 0;
				u3b_voice3_record_progressbar.setProgress(mPlayCurrentPosition);
				u3b_voice3_record_time.setText("0″");
				u3b_voice3_record_maxtime.setText("60″");
				u3b_voice3_record_progressbar.setMax(MAX_TIME);

				if (mMediaPlayer != null) {
					// 根据播放状态修改显示内容
					if (mMediaPlayer.isPlaying()) {
						mPlayState = false;
						mMediaPlayer.stop();
					}
				}

				clearFile();

			}
		});

		/**
		 * 蓝色上传按钮
		 * 
		 */

		u3b_voice3_bluebtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mRecordPath != null)
					mAudioFile = new File(mRecordPath);
				if (mPhotoFile != null && mAudioFile != null
						&& u3b_voice3_title.getText().toString().trim() != "") {
					// ！！此时 title 和description 很可能为空，后面还得加上
					String title = u3b_voice3_title.getText().toString();

					progressDialog = ProgressDialog.show(mContext, "请稍候",
							"正在上传...", true, true);

					upload(title, mPhotoFile, mAudioFile);

				} else {
					Toast.makeText(mContext, "上传之前，确认图片、录音、描述不为空哦", 1).show();
				}

			}
		});

		u3b_voice3_add.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, 1);
				// 从相册中选取图片
				// Intent intent = new Intent(Intent.ACTION_PICK,
				// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				// startActivityForResult(intent, 1);
			}
		});

	} // end initListener()

	/**
	 * 用来控制动画效果
	 */
	Handler mRecordLightHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (mRecord_State == RECORD_ING) {
					u3b_voice3_recordinglight_1.setVisibility(View.VISIBLE);
					mRecordLight_1_Animation = AnimationUtils.loadAnimation(
							mContext, R.anim.voice_anim);
					u3b_voice3_recordinglight_1
							.setAnimation(mRecordLight_1_Animation);
					mRecordLight_1_Animation.startNow();
				}
				break;

			case 1:
				if (mRecord_State == RECORD_ING) {
					u3b_voice3_recordinglight_2.setVisibility(View.VISIBLE);
					mRecordLight_2_Animation = AnimationUtils.loadAnimation(
							mContext, R.anim.voice_anim);
					u3b_voice3_recordinglight_2
							.setAnimation(mRecordLight_2_Animation);
					mRecordLight_2_Animation.startNow();
				}
				break;
			case 2:
				if (mRecord_State == RECORD_ING) {
					u3b_voice3_recordinglight_3.setVisibility(View.VISIBLE);
					mRecordLight_3_Animation = AnimationUtils.loadAnimation(
							mContext, R.anim.voice_anim);
					u3b_voice3_recordinglight_3
							.setAnimation(mRecordLight_3_Animation);
					mRecordLight_3_Animation.startNow();
				}
				break;
			case 3:
				if (mRecordLight_1_Animation != null) {
					u3b_voice3_recordinglight_1.clearAnimation();
					mRecordLight_1_Animation.cancel();
					u3b_voice3_recordinglight_1.setVisibility(View.GONE);

				}
				if (mRecordLight_2_Animation != null) {
					u3b_voice3_recordinglight_2.clearAnimation();
					mRecordLight_2_Animation.cancel();
					u3b_voice3_recordinglight_2.setVisibility(View.GONE);
				}
				if (mRecordLight_3_Animation != null) {
					u3b_voice3_recordinglight_3.clearAnimation();
					mRecordLight_3_Animation.cancel();
					u3b_voice3_recordinglight_3.setVisibility(View.GONE);
				}

				break;
			}
		}
	};
	/**
	 * 用来控制录音
	 */
	Handler mRecordHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (mRecord_State == RECORD_ING) {
					// 停止动画效果
					stopRecordLightAnimation();
					// 修改录音状态
					mRecord_State = RECORD_ED;
					try {
						// 停止录音
						mRecordUtil.stop();
						// 初始化录音音量
						mRecord_Volume = 0;
					} catch (IOException e) {
						e.printStackTrace();
					}
					// 根据录音修改界面显示内容
					u3b_voice3_layout_record.setVisibility(View.GONE);
					u3b_voice3_startbtn.setVisibility(View.GONE);
					u3b_voice3_layout_btns.setVisibility(View.VISIBLE);
					u3b_voice3_layout_texts.setVisibility(View.VISIBLE);

					u3b_voice3_playbtn.setVisibility(View.VISIBLE);
					u3b_voice3_record_progressbar.setMax((int) mRecord_Time);
					u3b_voice3_record_progressbar.setProgress(0);
					u3b_voice3_record_time.setText("0″");
					u3b_voice3_record_maxtime.setText((int) mRecord_Time + "″");
				}
				break;

			case 1:
				// 根据录音时间显示进度条
				u3b_voice3_record_progressbar.setProgress((int) mRecord_Time);
				// 显示录音时间
				u3b_voice3_record_time.setText((int) mRecord_Time + "″");
				// 根据录音声音大小显示效果
				ViewGroup.LayoutParams params = u3b_voice3_recording_volume
						.getLayoutParams();
				if (mRecord_Volume < 200.0) {
					params.height = mMINVolume;
				} else if (mRecord_Volume > 200.0 && mRecord_Volume < 400) {
					params.height = mMINVolume * 2;
				} else if (mRecord_Volume > 400.0 && mRecord_Volume < 800) {
					params.height = mMINVolume * 3;
				} else if (mRecord_Volume > 800.0 && mRecord_Volume < 1600) {
					params.height = mMINVolume * 4;
				} else if (mRecord_Volume > 1600.0 && mRecord_Volume < 3200) {
					params.height = mMINVolume * 5;
				} else if (mRecord_Volume > 3200.0 && mRecord_Volume < 5000) {
					params.height = mMINVolume * 6;
				} else if (mRecord_Volume > 5000.0 && mRecord_Volume < 7000) {
					params.height = mMINVolume * 7;
				} else if (mRecord_Volume > 7000.0 && mRecord_Volume < 10000.0) {
					params.height = mMINVolume * 8;
				} else if (mRecord_Volume > 10000.0 && mRecord_Volume < 14000.0) {
					params.height = mMINVolume * 9;
				} else if (mRecord_Volume > 14000.0 && mRecord_Volume < 17000.0) {
					params.height = mMINVolume * 10;
				} else if (mRecord_Volume > 17000.0 && mRecord_Volume < 20000.0) {
					params.height = mMINVolume * 11;
				} else if (mRecord_Volume > 20000.0 && mRecord_Volume < 24000.0) {
					params.height = mMINVolume * 12;
				} else if (mRecord_Volume > 24000.0 && mRecord_Volume < 28000.0) {
					params.height = mMINVolume * 13;
				} else if (mRecord_Volume > 28000.0) {
					params.height = mMAXVolume;
				}
				u3b_voice3_recording_volume.setLayoutParams(params);
				break;
			}
		}

	};

	/**
	 * 开始动画效果
	 */
	private void startRecordLightAnimation() {
		mRecordLightHandler.sendEmptyMessageDelayed(0, 0);
		mRecordLightHandler.sendEmptyMessageDelayed(1, 1000);
		mRecordLightHandler.sendEmptyMessageDelayed(2, 2000);
	}

	/**
	 * 停止动画效果
	 */
	private void stopRecordLightAnimation() {
		mRecordLightHandler.sendEmptyMessage(3);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mMediaPlayer != null) {
			// 根据播放状态修改显示内容
			if (mMediaPlayer.isPlaying()) {
				mPlayState = false;
				mMediaPlayer.stop();
			}
		}

		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/**
	 * 从相册中获取图片
	 */
	private byte[] bytePic;

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			try {

				// 获得图片的uri
				Uri originalUri = data.getData();
				ContentResolver resolver = mContext.getContentResolver();

				// 将图片显示在UI上
				bytePic = readStream(resolver.openInputStream(Uri
						.parse(originalUri.toString())));

				if (bytePic != null) {
					mPhotoBitmap = BitmapFactory.decodeByteArray(bytePic, 0,
							bytePic.length);
					u3b_voice3_photo.setImageBitmap(mPhotoBitmap);

				} else {
					Toast.makeText(mContext, "照片数据为空", Toast.LENGTH_SHORT)
							.show();
				}

				/* 获取图片的物理地址，遍历cursor，其中_data字段 就是图片的物理路径 */
				/*
				 * Cursor c = resolver.query(originalUri, null, null, null,
				 * null); c.moveToFirst(); String pathname =
				 * c.getString(c.getColumnIndex("_data"));
				 */

				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor cursor = resolver.query(originalUri, proj, null, null,
						null);
				// 按我个人理解 这个是获得用户选择的图片的索引值
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				// 最后根据索引值获取图片路径
				String pathname = cursor.getString(column_index);

				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)
						|| Environment.getExternalStorageState().equals(
								Environment.MEDIA_MOUNTED_READ_ONLY)) {

					File uploadFile = new File(pathname);
					if (!uploadFile.exists()) {
						uploadFile.mkdir();
					}
					if (uploadFile.exists()) {
						mPhotoFile = uploadFile;
					}
				}
			} catch (Exception e) {
			}
		}

	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getRepeatCount() == 0) {
			/*Intent upIntent = new Intent(this, MainActivity.class);
			upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(upIntent);*/
			this.finish();
			overridePendingTransition(R.anim.right_in,
					R.anim.right_out);
			}
		return super.dispatchKeyEvent(event);
		
	}
	
	//////////////////////////////////////////////////////////////////////////

	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;
	}

	public void upload(final String title, final File photoFile,
			final File audioFile) {
		Log.e("TAG", "upload voice before");

		new Thread() {
			public void run() {
				Looper.prepare(); // 为该线程初始化一个消息队列

				Map<String, String> params = new HashMap<String, String>();

				params.put("owner", MyConstants.User_Map.get("uid"));
				params.put("extra_message", title);
				params.put("latitude", String.valueOf(MyConstants.Latitude));
				params.put("longitude", String.valueOf(MyConstants.Longitude));
				params.put("display_time", display_time);

				try {
					Log.e("TAG", "upload put params");
					String result = MyUploadService.uploadVoice(params,
							photoFile, audioFile);
					Log.e("TAG", result);

					JSONObject jsonObject = new JSONObject(result);
					int flag = Integer.valueOf(jsonObject.get("result")
							.toString());

					if (flag > 0) { // 上传成功
						progressDialog.dismiss();
						Toast.makeText(mContext, "上传成功！", Toast.LENGTH_LONG)
								.show();
						Looper.loop();
					} else {
						progressDialog.dismiss();
						Toast.makeText(mContext, "上传失败！", Toast.LENGTH_LONG)
								.show();
						Looper.loop();
					}
				} catch (Exception e) {

					e.printStackTrace();
				}

				// return false;
			}
		}.start();
	}

}
