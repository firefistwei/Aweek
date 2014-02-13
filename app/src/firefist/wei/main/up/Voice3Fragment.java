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
	 * @����ؼ�
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

	/**
	 * @����ģ��
	 * 
	 */
	ProgressDialog progressDialog = null;

	private static final String PATH = "/sdcard/U2B/Up/";// ¼���洢·��
	private String mRecordPath = null;// ¼���Ĵ洢����
	private File mAudioFile = null;

	public Bitmap mPhotoBitmap = null;// �ϴ���ͼƬ
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
		actionBar.setTitle("����״̬");
		
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
			.setTitle("��Чʱ��")
			.setItems(
					new String[] { "(Ĭ��)һ��", "1Сʱ",
							"3Сʱ","24Сʱ","48Сʱ","72Сʱ"},
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
			// ����Ŀ¼
			File files[] = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (!file.isDirectory()) {// ������ļ���ɾ��
					file.delete();
				}
			}
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

		u3b_voice3_startbtn.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				// ��ʼ¼��
				case MotionEvent.ACTION_DOWN:
					u3b_voice3_layout_record.setVisibility(View.VISIBLE);
					if (mRecord_State != RECORD_ING) {
						// ��ʼ����Ч��
						startRecordLightAnimation();
						// �޸�¼��״̬
						mRecord_State = RECORD_ING;
						// ����¼������·��
						mRecordPath = PATH + UUID.randomUUID().toString()
								+ ".amr";
						// ʵ����¼��������
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
					u3b_voice3_layout_record.setVisibility(View.GONE);
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
							u3b_voice3_record_time.setText("0��");
							u3b_voice3_record_progressbar.setProgress(0);
							// �޸�¼����������
							ViewGroup.LayoutParams params = u3b_voice3_recording_volume
									.getLayoutParams();
							params.height = 0;
							u3b_voice3_recording_volume.setLayoutParams(params);
						} else {
							// ¼���ɹ�,����ʾ¼���ɹ���Ľ���
							u3b_voice3_layout_record.setVisibility(View.GONE);
							u3b_voice3_startbtn.setVisibility(View.GONE);
							u3b_voice3_layout_btns.setVisibility(View.VISIBLE);
							u3b_voice3_layout_texts.setVisibility(View.VISIBLE);

							u3b_voice3_playbtn.setVisibility(View.VISIBLE);

							u3b_voice3_record_progressbar
									.setMax((int) mRecord_Time);
							u3b_voice3_record_progressbar.setProgress(0);
							u3b_voice3_record_time.setText("0��");
							u3b_voice3_record_maxtime
									.setText((int) mRecord_Time + "��");
						}
					}
					break;
				}
				return false;
			}
		});
		u3b_voice3_playbtn.setOnClickListener(new OnClickListener() {

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
						// �޸Ĳ���״̬
						mPlayState = true;
						// �޸Ĳ���ͼ��
						u3b_voice3_playbtn.setVisibility(View.GONE);

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
						// ���ݲ���״̬�޸���ʾ����
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
				// ¼���ɹ�,����ʾ¼���ɹ���Ľ���
				u3b_voice3_startbtn.setVisibility(View.VISIBLE);
				u3b_voice3_layout_btns.setVisibility(View.GONE);
				u3b_voice3_layout_texts.setVisibility(View.GONE);

				mPlayState = false;
				u3b_voice3_playbtn.setVisibility(View.GONE);
				mPlayCurrentPosition = 0;
				u3b_voice3_record_progressbar.setProgress(mPlayCurrentPosition);
				u3b_voice3_record_time.setText("0��");
				u3b_voice3_record_maxtime.setText("60��");
				u3b_voice3_record_progressbar.setMax(MAX_TIME);

				if (mMediaPlayer != null) {
					// ���ݲ���״̬�޸���ʾ����
					if (mMediaPlayer.isPlaying()) {
						mPlayState = false;
						mMediaPlayer.stop();
					}
				}

				clearFile();

			}
		});

		/**
		 * ��ɫ�ϴ���ť
		 * 
		 */

		u3b_voice3_bluebtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mRecordPath != null)
					mAudioFile = new File(mRecordPath);
				if (mPhotoFile != null && mAudioFile != null
						&& u3b_voice3_title.getText().toString().trim() != "") {
					// ������ʱ title ��description �ܿ���Ϊ�գ����滹�ü���
					String title = u3b_voice3_title.getText().toString();

					progressDialog = ProgressDialog.show(mContext, "���Ժ�",
							"�����ϴ�...", true, true);

					upload(title, mPhotoFile, mAudioFile);

				} else {
					Toast.makeText(mContext, "�ϴ�֮ǰ��ȷ��ͼƬ��¼����������Ϊ��Ŷ", 1).show();
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
				// �������ѡȡͼƬ
				// Intent intent = new Intent(Intent.ACTION_PICK,
				// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				// startActivityForResult(intent, 1);
			}
		});

	} // end initListener()

	/**
	 * �������ƶ���Ч��
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
					u3b_voice3_layout_record.setVisibility(View.GONE);
					u3b_voice3_startbtn.setVisibility(View.GONE);
					u3b_voice3_layout_btns.setVisibility(View.VISIBLE);
					u3b_voice3_layout_texts.setVisibility(View.VISIBLE);

					u3b_voice3_playbtn.setVisibility(View.VISIBLE);
					u3b_voice3_record_progressbar.setMax((int) mRecord_Time);
					u3b_voice3_record_progressbar.setProgress(0);
					u3b_voice3_record_time.setText("0��");
					u3b_voice3_record_maxtime.setText((int) mRecord_Time + "��");
				}
				break;

			case 1:
				// ����¼��ʱ����ʾ������
				u3b_voice3_record_progressbar.setProgress((int) mRecord_Time);
				// ��ʾ¼��ʱ��
				u3b_voice3_record_time.setText((int) mRecord_Time + "��");
				// ����¼��������С��ʾЧ��
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

		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/**
	 * ������л�ȡͼƬ
	 */
	private byte[] bytePic;

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			try {

				// ���ͼƬ��uri
				Uri originalUri = data.getData();
				ContentResolver resolver = mContext.getContentResolver();

				// ��ͼƬ��ʾ��UI��
				bytePic = readStream(resolver.openInputStream(Uri
						.parse(originalUri.toString())));

				if (bytePic != null) {
					mPhotoBitmap = BitmapFactory.decodeByteArray(bytePic, 0,
							bytePic.length);
					u3b_voice3_photo.setImageBitmap(mPhotoBitmap);

				} else {
					Toast.makeText(mContext, "��Ƭ����Ϊ��", Toast.LENGTH_SHORT)
							.show();
				}

				/* ��ȡͼƬ�������ַ������cursor������_data�ֶ� ����ͼƬ������·�� */
				/*
				 * Cursor c = resolver.query(originalUri, null, null, null,
				 * null); c.moveToFirst(); String pathname =
				 * c.getString(c.getColumnIndex("_data"));
				 */

				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor cursor = resolver.query(originalUri, proj, null, null,
						null);
				// ���Ҹ������ ����ǻ���û�ѡ���ͼƬ������ֵ
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				// ����������ֵ��ȡͼƬ·��
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
				Looper.prepare(); // Ϊ���̳߳�ʼ��һ����Ϣ����

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

					if (flag > 0) { // �ϴ��ɹ�
						progressDialog.dismiss();
						Toast.makeText(mContext, "�ϴ��ɹ���", Toast.LENGTH_LONG)
								.show();
						Looper.loop();
					} else {
						progressDialog.dismiss();
						Toast.makeText(mContext, "�ϴ�ʧ�ܣ�", Toast.LENGTH_LONG)
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
