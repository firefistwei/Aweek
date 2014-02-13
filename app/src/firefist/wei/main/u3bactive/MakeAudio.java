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
	 * @¼��ģ��
	 * 
	 */
	private Animation mRecordLight_1_Animation;
	private Animation mRecordLight_2_Animation;
	private Animation mRecordLight_3_Animation;

	private MediaPlayer mMediaPlayer;
	private RecordUtil mRecordUtil;
	private static final int MAX_TIME = 60;// �¼��ʱ��
	private static final int MIN_TIME = 2;// ���¼��ʱ��

	private static final int RECORD_NO = 0; // ����¼��
	private static final int RECORD_ING = 1; // ����¼��
	private static final int RECORD_ED = 2; // ���¼��
	private int mRecord_State = 0; // ¼����״̬
	private float mRecord_Time;// ¼����ʱ��
	private double mRecord_Volume;// ��˷��ȡ������ֵ
	private boolean mPlayState; // ����״̬
	private int mPlayCurrentPosition;// ��ǰ���ŵ�ʱ��
	private int mMAXVolume;// ��������߶�
	private int mMINVolume;// ��С�����߶�

	Context mContext = null;

	private Object widgets[];
	private int widgetsId[];

	
	private String PATH = "/sdcard/U2B/Active/";
	public static String mRecordPath= null;// ¼���Ĵ洢����
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.u3b_makeaudio);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.title_bar));
		actionBar.setTitle("���������");
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
		((TextView)widgets[0]).setText("0��");
		((TextView)widgets[1]).setText("60��");
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
				Toast.makeText(mContext, "δ¼��", Toast.LENGTH_SHORT).show();
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
		// ���õ�ǰ����С�������������ֵ
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
						// ��ʼ����Ч��
						startRecordLightAnimation();
						// �޸�¼��״̬
						mRecord_State = RECORD_ING;
						// ����¼������·��
						mRecordPath = PATH + UUID.randomUUID().toString()
								+ ".amr";
						mRecordUtil = new RecordUtil(mRecordPath);
						try {
							// ��ʼ¼��
							mRecordUtil.start();
						} catch (IOException e) {
							e.printStackTrace();
						}
						new Thread(new Runnable() {

							public void run() {
								// ��ʼ��¼��ʱ��
								mRecord_Time = 0;
								while (mRecord_State == RECORD_ING) {
									// �������¼��ʱ����ֹͣ¼��
									if (mRecord_Time >= MAX_TIME) {
										mRecordHandler.sendEmptyMessage(0);
									} else {
										try {
											// ÿ��200����ͻ�ȡ�������������½�����ʾ
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
				
				// ֹͣ¼��
				case MotionEvent.ACTION_UP:
					((RelativeLayout)widgets[3]).setVisibility(View.GONE);
					if (mRecord_State == RECORD_ING) {
						// ֹͣ����Ч��
						stopRecordLightAnimation();
						// �޸�¼��״̬
						mRecord_State = RECORD_ED;
						try {
							// ֹͣ¼��
							mRecordUtil.stop();
							// ��ʼ¼������
							mRecord_Volume = 0;
						} catch (IOException e) {
							e.printStackTrace();
						}
						// ���¼��ʱ��С�����ʱ��
						if (mRecord_Time <= MIN_TIME) {
							// ��ʾ����
							Toast.makeText(mContext, "¼��ʱ�����",
									Toast.LENGTH_SHORT).show();
							// �޸�¼��״̬
							mRecord_State = RECORD_NO;
							// �޸�¼��ʱ��
							mRecord_Time = 0;
							// �޸���ʾ����
							((TextView)widgets[0]).setText("0��");
							((ProgressBar)widgets[2]).setProgress(0);
							// �޸�¼����������
							ViewGroup.LayoutParams params = ((ImageView)widgets[4])
									.getLayoutParams();
							params.height = 0;
							((ImageView)widgets[4]).setLayoutParams(params);
						} else {
							// ¼���ɹ�,����ʾ¼���ɹ���Ľ���
							((RelativeLayout)widgets[3]).setVisibility(View.INVISIBLE);

							((ImageView)widgets[8]).setVisibility(View.VISIBLE);

							((ProgressBar)widgets[2])
									.setMax((int) mRecord_Time);
							((ProgressBar)widgets[2]).setProgress(0);
							((TextView)widgets[0]).setText("0��");
							((TextView)widgets[1])
									.setText((int) mRecord_Time + "��");
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
				// ����¼��
				if (!mPlayState) {
					mMediaPlayer = new MediaPlayer();
					try {
						// ���¼����·��
						mMediaPlayer.setDataSource(mRecordPath);
						// ׼��
						mMediaPlayer.prepare();
						// ����
						mMediaPlayer.start();
						// ����ʱ���޸Ľ���
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
						// �޸Ĳ���״̬
						mPlayState = true;
						// �޸Ĳ���ͼ��
						((ImageView)widgets[9]).setVisibility(View.GONE);

						mMediaPlayer
								.setOnCompletionListener(new OnCompletionListener() {
									// ���Ž��������
									public void onCompletion(MediaPlayer mp) {
										// ֹͣ����
										mMediaPlayer.stop();
										// �޸Ĳ���״̬
										mPlayState = false;
										// �޸Ĳ���ͼ��

										// ��ʼ����������
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
						// ���ݲ���״̬�޸���ʾ����
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
	 * ��ʼ����Ч��
	 */
	private void startRecordLightAnimation() {
		mRecordLightHandler.sendEmptyMessageDelayed(0, 0);
		mRecordLightHandler.sendEmptyMessageDelayed(1, 1000);
		mRecordLightHandler.sendEmptyMessageDelayed(2, 2000);
	}

	/**
	 * ֹͣ����Ч��
	 */
	private void stopRecordLightAnimation() {
		mRecordLightHandler.sendEmptyMessage(3);
	}
	
	/**
	 * �������ƶ���Ч��
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
	 * ��������¼��
	 */
	Handler mRecordHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (mRecord_State == RECORD_ING) {
					// ֹͣ����Ч��
					stopRecordLightAnimation();
					// �޸�¼��״̬
					mRecord_State = RECORD_ED;
					try {
						// ֹͣ¼��
						mRecordUtil.stop();
						// ��ʼ��¼������
						mRecord_Volume = 0;
					} catch (IOException e) {
						e.printStackTrace();
					}
					// ����¼���޸Ľ�����ʾ����
					((RelativeLayout)widgets[3]).setVisibility(View.GONE);
					((ImageView)widgets[9]).setVisibility(View.GONE);

					((ImageView)widgets[8]).setVisibility(View.VISIBLE);
					((ProgressBar)widgets[2]).setMax((int) mRecord_Time);
					((ProgressBar)widgets[2]).setProgress(0);
					((TextView)widgets[0]).setText("0��");
					((TextView)widgets[1]).setText((int) mRecord_Time + "��");
				}
				break;

			case 1:
				// ����¼��ʱ����ʾ������
				((ProgressBar)widgets[2]).setProgress((int) mRecord_Time);
				// ��ʾ¼��ʱ��
				((TextView)widgets[1]).setText((int) mRecord_Time + "��");
				// ����¼��������С��ʾЧ��
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
			// ���ݲ���״̬�޸���ʾ����
			if (mMediaPlayer.isPlaying()) {
				mPlayState = false;
				mMediaPlayer.stop();
			}
		}
	}

}
