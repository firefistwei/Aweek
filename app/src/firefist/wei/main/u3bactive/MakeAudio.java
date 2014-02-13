package firefist.wei.main.u3bactive;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import firefist.wei.main.R;
import firefist.wei.main.u3bactivity.UserInfo_U3bActivity;
import firefist.wei.utils.RecordUtil;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

public class MakeAudio extends Activity {
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

	Context mContext = null;

	private Object widgets[];
	private int widgetsId[];

	
	private String PATH = "/sdcard/U2B/Active/";
	public static String mRecordPath= null;// 录音的存储名称
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.u3b_makeaudio);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.title_bar));
		actionBar.setTitle("活动语音介绍");
		actionBar.show();

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		mContext = this;

		widgetsId = new int[] { R.id.u3b_makeaudio_time_tv,
				R.id.u3b_makeaudio_maxtime_tv,
				R.id.u3b_makeaudio_progressbar,//2
				R.id.u3b_makeaudio_record_rlayout,
				R.id.u3b_makeaudio_volume_iv,  //4
				R.id.u3b_makeaudio_recordinglight_iv1,//5
				R.id.u3b_makeaudio_recordinglight_iv2,
				R.id.u3b_makeaudio_recordinglight_iv3,
				R.id.u3b_makeaudio_play_iv,
				R.id.u3b_makeaudio_start_iv};
		widgets = new Object[widgetsId.length];

		findViewById();
		initView();
		initVolume();
		initListener();
	}

	private void initView() {
		((ImageView)widgets[9]).setVisibility(View.VISIBLE);

		mPlayState = false;
		((ImageView)widgets[8]).setVisibility(View.GONE);
		mPlayCurrentPosition = 0;
		((ProgressBar)widgets[2]).setProgress(mPlayCurrentPosition);
		((TextView)widgets[0]).setText("0″");
		((TextView)widgets[1]).setText("60″");
		((ProgressBar)widgets[2]).setMax(MAX_TIME);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem save = menu.add(0, 10003, 0, "SAVE");
		save.setIcon(this.getResources().getDrawable(
				R.drawable.ic_navigation_done));
		save.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return true;
	}





	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 10003:
			if(mRecordPath!=null){
				Intent intent = new Intent(MakeAudio.this,Home_PublishActive.class);
				intent.putExtra("filePath", mRecordPath);
				setResult(RESULT_OK, intent);
				this.finish();
			}else{
				Toast.makeText(mContext, "未录音", Toast.LENGTH_SHORT).show();
			}
			
			return true;

		case android.R.id.home:
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	private void findViewById() {
		for (int i = 0; i < widgetsId.length; i++) {
			widgets[i] = this.findViewById(widgetsId[i]);
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
		
		//start Btn
		((ImageView)widgets[9]).setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
				
				case MotionEvent.ACTION_DOWN:
					((RelativeLayout)widgets[3]).setVisibility(View.VISIBLE);
					if(mRecord_State!=RECORD_ING){
						initView();
						// 开始动画效果
						startRecordLightAnimation();
						// 修改录音状态
						mRecord_State = RECORD_ING;
						// 设置录音保存路径
						mRecordPath = PATH + UUID.randomUUID().toString()
								+ ".amr";
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
					((RelativeLayout)widgets[3]).setVisibility(View.GONE);
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
							((TextView)widgets[0]).setText("0″");
							((ProgressBar)widgets[2]).setProgress(0);
							// 修改录音声音界面
							ViewGroup.LayoutParams params = ((ImageView)widgets[4])
									.getLayoutParams();
							params.height = 0;
							((ImageView)widgets[4]).setLayoutParams(params);
						} else {
							// 录音成功,则显示录音成功后的界面
							((RelativeLayout)widgets[3]).setVisibility(View.INVISIBLE);

							((ImageView)widgets[8]).setVisibility(View.VISIBLE);

							((ProgressBar)widgets[2])
									.setMax((int) mRecord_Time);
							((ProgressBar)widgets[2]).setProgress(0);
							((TextView)widgets[0]).setText("0″");
							((TextView)widgets[1])
									.setText((int) mRecord_Time + "″");
						}
					}
					break;
				}
				return false;
			}
		});
		/*widgetsId = new int[] { R.id.u3b_makeaudio_time_tv,
		R.id.u3b_makeaudio_maxtime_tv,
		R.id.u3b_makeaudio_progressbar,
		R.id.u3b_makeaudio_record_rlayout,
		R.id.u3b_makeaudio_volume_iv,  //4
		R.id.u3b_makeaudio_recordinglight_iv1,//5
		R.id.u3b_makeaudio_recordinglight_iv2,
		R.id.u3b_makeaudio_recordinglight_iv3,
		R.id.u3b_makeaudio_play_btn,//8
		R.id.u3b_makeaudio_start_btn  };*/
		//play btn
		((ImageView)widgets[8]).setOnClickListener(new OnClickListener() {

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

								((ProgressBar)widgets[2])
										.setMax((int) mRecord_Time);
								mPlayCurrentPosition = 0;
								while (mMediaPlayer.isPlaying()) {
									mPlayCurrentPosition = mMediaPlayer
											.getCurrentPosition() / 1000;
									((ProgressBar)widgets[2])
											.setProgress(mPlayCurrentPosition);
								}
							}
						}).start();
						// 修改播放状态
						mPlayState = true;
						// 修改播放图标
						((ImageView)widgets[9]).setVisibility(View.GONE);

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
										((ProgressBar)widgets[2])
												.setProgress(mPlayCurrentPosition);
										((ImageView)widgets[9])
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
							((ImageView)widgets[9]).setVisibility(View.VISIBLE);
							mPlayCurrentPosition = 0;
							((ProgressBar)widgets[2])
									.setProgress(mPlayCurrentPosition);
						} else {
							mPlayState = false;
							((ImageView)widgets[9]).setVisibility(View.VISIBLE);
							mPlayCurrentPosition = 0;
							((ProgressBar)widgets[2])
									.setProgress(mPlayCurrentPosition);
						}
					}
				}
			}
		});
		
	
	}
	
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
	
	/**
	 * 用来控制动画效果
	 */
	Handler mRecordLightHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (mRecord_State == RECORD_ING) {
					((ImageView)widgets[5]).setVisibility(View.VISIBLE);
					mRecordLight_1_Animation = AnimationUtils.loadAnimation(
							mContext, R.anim.voice_anim);
					((ImageView)widgets[5])
							.setAnimation(mRecordLight_1_Animation);
					mRecordLight_1_Animation.startNow();
				}
				break;

			case 1:
				if (mRecord_State == RECORD_ING) {
					((ImageView)widgets[6]).setVisibility(View.VISIBLE);
					mRecordLight_2_Animation = AnimationUtils.loadAnimation(
							mContext, R.anim.voice_anim);
					((ImageView)widgets[6])
							.setAnimation(mRecordLight_2_Animation);
					mRecordLight_2_Animation.startNow();
				}
				break;
			case 2:
				if (mRecord_State == RECORD_ING) {
					((ImageView)widgets[7]).setVisibility(View.VISIBLE);
					mRecordLight_3_Animation = AnimationUtils.loadAnimation(
							mContext, R.anim.voice_anim);
					((ImageView)widgets[7])
							.setAnimation(mRecordLight_3_Animation);
					mRecordLight_3_Animation.startNow();
				}
				break;
			case 3:
				if (mRecordLight_1_Animation != null) {
					((ImageView)widgets[5]).clearAnimation();
					mRecordLight_1_Animation.cancel();
					((ImageView)widgets[5]).setVisibility(View.GONE);

				}
				if (mRecordLight_2_Animation != null) {
					((ImageView)widgets[6]).clearAnimation();
					mRecordLight_2_Animation.cancel();
					((ImageView)widgets[6]).setVisibility(View.GONE);
				}
				if (mRecordLight_3_Animation != null) {
					((ImageView)widgets[7]).clearAnimation();
					mRecordLight_3_Animation.cancel();
					((ImageView)widgets[7]).setVisibility(View.GONE);
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
					((RelativeLayout)widgets[3]).setVisibility(View.GONE);
					((ImageView)widgets[9]).setVisibility(View.GONE);

					((ImageView)widgets[8]).setVisibility(View.VISIBLE);
					((ProgressBar)widgets[2]).setMax((int) mRecord_Time);
					((ProgressBar)widgets[2]).setProgress(0);
					((TextView)widgets[0]).setText("0″");
					((TextView)widgets[1]).setText((int) mRecord_Time + "″");
				}
				break;

			case 1:
				// 根据录音时间显示进度条
				((ProgressBar)widgets[2]).setProgress((int) mRecord_Time);
				// 显示录音时间
				((TextView)widgets[1]).setText((int) mRecord_Time + "″");
				// 根据录音声音大小显示效果
				ViewGroup.LayoutParams params = ((ImageView)widgets[4])
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
				((ImageView)widgets[4]).setLayoutParams(params);
				break;
			}
		}

	};
	
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
	}

}
