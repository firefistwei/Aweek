package firefist.wei.main.up;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import firefist.wei.main.MainActivity;
import firefist.wei.main.MyConstants;
import firefist.wei.main.R;
import firefist.wei.main.service.MyService;
import firefist.wei.main.service.MyUploadService;
import firefist.wei.main.service.NewUploadService;
import firefist.wei.utils.MP4ParserUtil;
import firefist.wei.utils.RecordUtil;
import firefist.wei.utils.VideoUtil;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class Video3Fragment extends Activity implements SurfaceHolder.Callback,
		OnInfoListener, OnErrorListener {

	private static final String TAG = "Video3Fragment";
	/**
	 * @����ؼ�
	 * 
	 */
	RelativeLayout u3b_video3_layout_top;
	TextView u3b_video3_record_time;
	TextView u3b_video3_record_maxtime;
	ProgressBar u3b_video3_record_progressbar;

	LinearLayout u3b_video3_layout_btns;
	ImageView u3b_video3_redbtn;
	ImageView u3b_video3_bluebtn;

	LinearLayout u3b_video3_layout_texts;
	TextView u3b_video3_title;

	ImageView u3b_video3_startbtn;
	ImageView u3b_video3_playbtn;

	ImageView photoView;
	LinearLayout mergebtn_layout;
	ImageView u3b_video3_switchbtn;

	/**
	 * @¼��
	 * 
	 */

	boolean hasFrontCamera = true;

	private float mRecord_Time = 0;// ¼�Ƶ�ʱ��
	private float mCurrentPosition = 0;// ��ǰ¼�Ƶ�ʱ��
	private static final int MAX_TIME = 10;// �¼��ʱ��

	boolean recording = false;
	private SurfaceHolder holder;
	private Camera camera;
	private SurfaceView surfaceView;
	private VideoView videoView;

	private VideoUtil mVideoUtil;

	private String workingPath = "/sdcard/U2B/Up/";
	private ArrayList<String> videosPath = new ArrayList<String>();
	private int videoPart = 0;

	private String video_path = "/sdcard/U2B/Up/up_video.3gp";
	private String photo_path = "/sdcard/U2B/Up/up_videophoto.jpg";
	private File video_file; // ��֧����Ƶ�ļ�·��
	private File photo_file;

	private Context mContext = null;

	private String display_time = "168";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.u3b_video3fragment);

		mContext = this;
		final ActionBar actionBar = getActionBar();

		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(R.drawable.app_icon);
		actionBar.setTitle("��Ƶ״̬");

		clearFile();
		findViewById();
		setListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.u3b_upactivity_menu_video, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			/*
			 * Intent upIntent = new Intent(this, MainActivity.class);
			 * upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 * startActivity(upIntent);
			 */
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
			return true;

		case R.id.menu_video_to_audio:
			Intent intent = new Intent(this, Voice3Fragment.class);
			startActivity(intent);
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
			this.finish();
			return true;
		case R.id.u3b_upactivity_menu_currenttime:
			new AlertDialog.Builder(mContext)
					.setTitle("��Чʱ��")
					.setItems(
							new String[] { "(Ĭ��)һ��", "1Сʱ", "3Сʱ", "24Сʱ",
									"48Сʱ", "72Сʱ" },
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
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
		u3b_video3_layout_top = (RelativeLayout) this
				.findViewById(R.id.u3b_video3_layout_top);
		u3b_video3_record_time = (TextView) this
				.findViewById(R.id.u3b_video3_record_time);
		u3b_video3_record_maxtime = (TextView) this
				.findViewById(R.id.u3b_video3_record_maxtime);
		u3b_video3_record_progressbar = (ProgressBar) this
				.findViewById(R.id.u3b_video3_record_progressbar);

		u3b_video3_layout_btns = (LinearLayout) this
				.findViewById(R.id.u3b_video3_layout_btns);
		u3b_video3_redbtn = (ImageView) this
				.findViewById(R.id.u3b_video3_redbtn);
		u3b_video3_bluebtn = (ImageView) this
				.findViewById(R.id.u3b_video3_bluebtn);
		u3b_video3_layout_texts = (LinearLayout) this
				.findViewById(R.id.u3b_video3_layout_texts);
		u3b_video3_title = (TextView) this.findViewById(R.id.u3b_video3_title);

		u3b_video3_playbtn = (ImageView) this
				.findViewById(R.id.u3b_video3_playbtn);
		u3b_video3_startbtn = (ImageView) this
				.findViewById(R.id.u3b_video3_startbtn);

		surfaceView = (SurfaceView) this
				.findViewById(R.id.u3b_video3_surfaceview);
		videoView = (VideoView) this.findViewById(R.id.u3b_video3_videoview);

		photoView = (ImageView) this.findViewById(R.id.u3b_video3_photoview);

		mergebtn_layout = (LinearLayout) this
				.findViewById(R.id.u3b_video3_mergebtn_layout);
		u3b_video3_switchbtn = (ImageView) this
				.findViewById(R.id.u3b_video3_switchbtn);
	}

	private void clearFile() {

		File dir = new File("/sdcard/U2B/Up/");
		if (!dir.exists()) {
			dir.mkdirs();
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

	@Override
	public void onResume() {
		super.onResume();
		initView();

	}

	private void initView() {
		/**
		 * ����
		 */
		u3b_video3_layout_btns.setVisibility(View.GONE);
		u3b_video3_layout_texts.setVisibility(View.GONE);
		u3b_video3_playbtn.setVisibility(View.INVISIBLE);
		u3b_video3_startbtn.setVisibility(View.VISIBLE);

		videoView.setVisibility(View.VISIBLE);
		videoView
				.setLayoutParams(new LinearLayout.LayoutParams(
						MainActivity.mScreenWidth - 10,
						MainActivity.mScreenWidth - 10));
		videoView.setVisibility(View.GONE);
		photoView.setVisibility(View.GONE);

		u3b_video3_record_progressbar.setMax((int) MAX_TIME * 1000);
		u3b_video3_record_progressbar.setProgress(0);
		u3b_video3_record_time.setText("0��");
		u3b_video3_record_maxtime.setText((int) MAX_TIME + "��");

		holder = surfaceView.getHolder();
		holder.addCallback(this);
		holder.setKeepScreenOn(true);
		/**
		 * ��Ϊ��Ҫ
		 */
		String tempStr = workingPath + "tempvideo" + videoPart + ".mp4";
		mVideoUtil = new VideoUtil(tempStr, holder);
		camera = mVideoUtil.cameraStart(camera);

		surfaceView.setVisibility(View.VISIBLE);

		if (Camera.getNumberOfCameras() > 1) {
			hasFrontCamera = true;
			u3b_video3_switchbtn.setVisibility(View.VISIBLE);
		} else {
			hasFrontCamera = false;
			u3b_video3_switchbtn.setVisibility(View.GONE);
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		if (camera != null)
			mVideoUtil.stopRecording(camera);

	}

	private void setListener() {

		u3b_video3_startbtn.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				// ��ʼ¼��
				case MotionEvent.ACTION_DOWN:
					u3b_video3_startbtn.setBackgroundDrawable(mContext
							.getResources().getDrawable(
									R.drawable.amsc_touch_spot_blue));

					mVideoUtil.recorderStart(mContext, camera);
					recording = true;
					mergebtn_layout.setVisibility(View.GONE);
					if (hasFrontCamera) {
						u3b_video3_switchbtn.setVisibility(View.GONE);
					}

					new Thread(new Runnable() {

						public void run() {
							// ��ʼ��¼��ʱ��
							mRecord_Time = mCurrentPosition;
							while (recording == true) {
								// �������¼��ʱ����ֹͣ¼��
								if (mRecord_Time >= MAX_TIME) {

									mergebtn_layout.setVisibility(View.GONE);
									mRecordHandler.sendEmptyMessage(0);

								} else {
									try {
										// ÿ��20����ͻ�ȡ�������������½�����ʾ
										Thread.sleep(20);
										mRecord_Time += 0.02;
										mRecordHandler.sendEmptyMessage(1);

									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
						}
					}).start();
					break;
				// ֹͣ¼��
				case MotionEvent.ACTION_UP:
					if (recording == true) {
						u3b_video3_startbtn.setBackgroundDrawable(mContext
								.getResources().getDrawable(
										R.drawable.amsc_cam_glow_ring));

						mCurrentPosition = mRecord_Time;
						recording = false;
						mVideoUtil.stopRecording(camera);

						mergebtn_layout.setVisibility(View.VISIBLE);
						if (hasFrontCamera) {
							u3b_video3_switchbtn.setVisibility(View.VISIBLE);
						}
						/**
						 * ��Ϊ��Ҫ
						 */
						/*
						 * String tempStr = "tempvideo"+ videoPart+".mp4";
						 * videosPath.add(tempStr);
						 */
						// ����һ������Ƶ����뼯���У��ڴ�����һ����Ƶ��·��
						videoPart++;
						String tempStr = workingPath + "tempvideo" + videoPart
								+ ".mp4";
						mVideoUtil = new VideoUtil(tempStr, holder);
						camera = mVideoUtil.cameraStart(camera);

					}

					break;
				}
				return false;
			}
		});

		mergebtn_layout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				videoPart = videoPart - 1;
				mergebtn_layout.setVisibility(View.GONE);

				mVideoUtil.stopRecording(camera);
				recording = false;

				for (int i = 0; i <= videoPart; i++) {
					videosPath.add("tempvideo" + i + ".mp4");

				}

				photoView.setVisibility(View.INVISIBLE);
				saveVideoBitmap();

				videoView.setVisibility(View.GONE);
				surfaceView.setVisibility(View.GONE);
				u3b_video3_playbtn.setVisibility(View.VISIBLE);
				u3b_video3_startbtn.setVisibility(View.GONE);
				u3b_video3_layout_btns.setVisibility(View.VISIBLE);
				u3b_video3_layout_texts.setVisibility(View.VISIBLE);

				u3b_video3_record_progressbar.setMax((int) mRecord_Time * 1000);
				u3b_video3_record_progressbar.setProgress(0);
				u3b_video3_record_time.setText("0��");
				u3b_video3_record_maxtime.setText((int) mRecord_Time + "��");

				// �ϲ���Ƶ
				mergeVideos();

			}
		});

		u3b_video3_playbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				photoView.setVisibility(View.INVISIBLE);
				videoView.setVisibility(View.VISIBLE);

				videoView.setVideoPath(video_path);
				videoView.start();

				u3b_video3_playbtn.setVisibility(View.INVISIBLE);
				u3b_video3_startbtn.setVisibility(View.INVISIBLE);

			}
		});

		u3b_video3_redbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				videoPart = 0;
				videosPath = new ArrayList<String>();
				mCurrentPosition = 0;
				clearFile();
				initView();

			}
		});

		u3b_video3_bluebtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (video_file != null
						&& photo_file != null
						&& !u3b_video3_title.getText().toString().trim()
								.equals("")) {
					String title = u3b_video3_title.getText().toString();

					if (videoView != null & videoView.isPlaying() == true) {
						videoView.pause();
					}

					uploadNew(title, photo_file, video_file);
					// upload(title, photo_file, video_file);

				} else {
					Toast.makeText(mContext, "�ϴ�֮ǰ��ȷ����Ƶ��������Ϊ��Ŷ", 1).show();
				}

			}
		});

		videoView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (videoView != null & videoView.isPlaying() != true) {
					// videoView.resume();
					// videoView.start();

				} else if (videoView != null & videoView.isPlaying() == true) {
					// videoView.pause();
				}

				return true;
			}
		});

		videoView
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						videoView.seekTo(0);
						videoView.start();

					}
				});
		if (hasFrontCamera) {
			u3b_video3_switchbtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (camera != null) {

						mVideoUtil.stopRecording(camera);
						String tempStr = workingPath + "tempvideo" + videoPart
								+ ".mp4";
						mVideoUtil = new VideoUtil(tempStr, holder);

						mVideoUtil.switchCam();

						camera = mVideoUtil.cameraStart(camera);
					}
				}
			});

		}

	}

	private void saveVideoBitmap() {
		String path = workingPath + videosPath.get(0);
		Log.e("TAG", path);
		Bitmap bitmap = mVideoUtil.getVideoThumbnail(path);
		mVideoUtil.BitmapToFile(bitmap, photo_path);
		photo_file = new File(photo_path);
		photoView.setImageURI(Uri.fromFile(photo_file));
		photoView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onError(MediaRecorder mr, int what, int extra) {
		mVideoUtil.stopRecording(camera);
		Toast.makeText(mContext, "���ִ���¼��ֹͣ", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
		if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
			mVideoUtil.stopRecording(camera);
			Toast.makeText(mContext, "�ļ��ѱ���", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// ���Ԥ��

		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	/**
	 * ��������¼��
	 */
	Handler mRecordHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0: // ¼��ʱ�䳬��12�� �� ¼�ƽ���
				if (recording == true) {

					mVideoUtil.stopRecording(camera);
					recording = false;

					for (int i = 0; i <= videoPart; i++) {
						videosPath.add("tempvideo" + i + ".mp4");

					}

					photoView.setVisibility(View.INVISIBLE);
					saveVideoBitmap();

					videoView.setVisibility(View.GONE);
					surfaceView.setVisibility(View.GONE);
					u3b_video3_playbtn.setVisibility(View.VISIBLE);
					u3b_video3_startbtn.setVisibility(View.GONE);
					u3b_video3_layout_btns.setVisibility(View.VISIBLE);
					u3b_video3_layout_texts.setVisibility(View.VISIBLE);

					u3b_video3_record_progressbar
							.setMax((int) mRecord_Time * 1000);
					u3b_video3_record_progressbar.setProgress(0);
					u3b_video3_record_time.setText("0��");
					u3b_video3_record_maxtime.setText((int) mRecord_Time + "��");

					// �ϲ���Ƶ
					mergeVideos();

				}
				break;

			case 1:
				// ����¼��ʱ����ʾ������
				u3b_video3_record_progressbar
						.setProgress((int) (mRecord_Time * 1000));
				// ��ʾ¼��ʱ��
				u3b_video3_record_time.setText((int) mRecord_Time + "��");

				break;
			}
		}

	};

	@Override
	public void onDestroy() {
		if (camera != null) {
			mVideoUtil.stopRecording(camera);
		}
		super.onDestroy();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getRepeatCount() == 0) {
			/*
			 * Intent upIntent = new Intent(this, MainActivity.class);
			 * upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 * startActivity(upIntent);
			 */
			this.finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
		return super.dispatchKeyEvent(event);

	}

	/**
	 * �������� MP4 ��Ƶ�ϲ�
	 */
	public void mergeVideos() {
		new MP4ParserUtil(mContext, workingPath, videosPath, video_path);
		video_file = new File(video_path);
	}

	// ///////////////////////////////////////////////////////////////////

	public void upload(final String title, final File photoFile,
			final File videoFile) {

		new Thread() {
			public void run() {
				Looper.prepare(); // Ϊ���̳߳�ʼ��һ����Ϣ����

				Map<String, String> params = new HashMap<String, String>();

				params.put("owner", MyConstants.User_Map.get("uid"));
				params.put("extra_message", title);
				params.put("longitude", String.valueOf(MyConstants.Longitude));
				params.put("latitude", String.valueOf(MyConstants.Latitude));
				params.put("display_time", display_time);

				try {
					String result = MyUploadService.uploadVideo(params,
							photoFile, videoFile);

					Log.e("TAG", result);
					JSONObject jsonObject = new JSONObject(result);
					int flag = Integer.valueOf(jsonObject.get("result")
							.toString());

					if (flag > 0) { // �ϴ��ɹ�
						Toast.makeText(mContext, "�ϴ��ɹ���", Toast.LENGTH_LONG)
								.show();
						Looper.loop();
					} else {
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

	public void uploadNew(final String title, final File photoFile,
			final File videoFile) {
		Map<String, String> params = new HashMap<String, String>();

		params.put("owner", MyConstants.User_Map.get("uid"));
		params.put("extra_message", title);
		params.put("longitude", String.valueOf(MyConstants.Longitude));
		params.put("latitude", String.valueOf(MyConstants.Latitude));

		new NewUploadService.UploadVideoTask(mContext, params, photoFile,
				videoFile).execute();
	}

}
