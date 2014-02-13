package firefist.wei.main.u3bactivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import firefist.wei.main.MainActivity;
import firefist.wei.main.MyConstants;
import firefist.wei.main.R;
import firefist.wei.main.service.MyUploadService;
import firefist.wei.utils.MP4ParserUtil;
import firefist.wei.utils.VideoUtil;

public class UserVideoShow extends Activity implements SurfaceHolder.Callback,
		OnInfoListener, OnErrorListener {

	private Context mContext;

	private SurfaceHolder holder;
	private Camera camera;

	private int widgetsId[];
	private Object[] widgets;
	
	private boolean recording = false;

	private int video_flag = 1;
	// 无视频 0 // 本地 1 // url 2 // 本地 temp 3
	
	private VideoUtil mVideoUtil;

	private ProgressDialog pd = null;

	boolean hasFrontCamera = true;

	private float mRecord_Time = 0;// 录制的时间
	private float mCurrentPosition = 0;// 当前录制的时间
	private static final int MAX_TIME = 10;// 最长录音时间

	private String workingPath = "/sdcard/U2B/My/TEMP/";
	private ArrayList<String> videosPath = new ArrayList<String>();
	private int videoPart = 0;
	
	public static String photoPath = "/sdcard/U2B/My/my_videophoto.jpg";
	public static String videoPath = "/sdcard/U2B/My/my_videoshow.mp4";
	private File videoFile = null;
	private File photoFile = null;
	private String video_URL = null;
	

	//上一个临时文件
	public static String videoTempPath = "/sdcard/U2B/My/temp_video.mp4";
	
	//resume videoview
	int currentVideoViewPosition =0;
	
	

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.u3b_user_videoshow);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.title_bar));
		actionBar.setIcon(R.drawable.app_icon);
		actionBar.setTitle("个人视频介绍");
		actionBar.show();

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		mContext = this;

		widgetsId = new int[] { R.id.user_videoshow_time_tv,// 0
				R.id.user_videoshow_maxtime_tv,// 1
				R.id.user_videoshow_progressbar,// 2
				R.id.user_videoshow_surfaceview,// 3
				R.id.user_videoshow_videoview,// 4
				R.id.user_videoshow_play_iv,// 5
				R.id.user_videoshow_start_iv,// 6
				R.id.user_videoshow_photo_iv, // 7
				R.id.user_videoshow_cancel_iv,// 8
				R.id.user_videoshow_time_relayout, //9
				R.id.user_videoshow_mergebtn_layout, //10
				R.id.user_videoshow_switchbtn}; // 11
		
		widgets = new Object[widgetsId.length];

		File file = new File(videoPath);
		if (file.exists()) {
			video_flag = 1;
		} else {
			video_URL = MyConstants.User_Map.get("video_URL");
			if (!video_URL.equals("")) {
				video_flag = 2;
			} else {
				video_flag = 0;
			}
		}
		
		clearFile();
		findViewById();
		initListener();
		// initView() 在 onResume()
	}

	private void findViewById() {
		for (int i = 0; i < widgetsId.length; i++) {
			widgets[i] = this.findViewById(widgetsId[i]);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem save = menu.add(0, 10004, 0, "SAVE");
		save.setIcon(this.getResources().getDrawable(
				R.drawable.ic_navigation_done));
		save.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 10004:
			if (videoFile != null) {
				pd = ProgressDialog.show(UserVideoShow.this, null, "正在更新信息...",
						true, true);
				if (((VideoView) widgets[4]) != null & ((VideoView) widgets[4]).isPlaying() == true) 
					((VideoView) widgets[4]).pause();
				setUserVideo();
			} else {
				Toast.makeText(mContext, "没有录制，无法上传！", 1500).show();

			}
			return true;

		case android.R.id.home:
			/*Intent upIntent = new Intent(this, MainActivity.class);
			upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(upIntent);*/
			this.finish();
			overridePendingTransition(R.anim.right_in,
					R.anim.right_out);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initView();
	}

	private void initView() {
		if (video_flag > 0) {
			((VideoView) widgets[4]).setVisibility(View.VISIBLE);
			// play btn
			((ImageView) widgets[5]).setVisibility(View.VISIBLE);

			((SurfaceView) widgets[3]).setVisibility(View.GONE);
			// start btn
			((ImageView) widgets[6]).setVisibility(View.GONE);
			//photo view
			((ImageView) widgets[7]).setVisibility(View.VISIBLE);
			// cancel btn
			((ImageView) widgets[8]).setVisibility(View.GONE);
			((RelativeLayout) widgets[9]).setVisibility(View.INVISIBLE);

		} else {

			// 开始录像
			((VideoView) widgets[4]).setVisibility(View.GONE);
			// play btn
			((ImageView) widgets[5]).setVisibility(View.GONE);
			// start btn
			((ImageView) widgets[6]).setVisibility(View.VISIBLE);
			// cancel btn
			((ImageView) widgets[8]).setVisibility(View.GONE);
			((RelativeLayout) widgets[9]).setVisibility(View.VISIBLE);

			((LinearLayout) widgets[10]).setVisibility(View.INVISIBLE);
			((ImageView) widgets[11]).setVisibility(View.INVISIBLE);
			
			((ProgressBar) widgets[2]).setMax((int) MAX_TIME * 1000);
			((ProgressBar) widgets[2]).setProgress(0);
			((TextView) widgets[0]).setText("0″");
			((TextView) widgets[1]).setText((int) MAX_TIME + "″");
			
			holder = ((SurfaceView) widgets[3]).getHolder();
			holder.addCallback(this);
			holder.setKeepScreenOn(true);
			
			/**
			 * 极为重要
			 */
			String tempStr = workingPath + "tempvideo" + videoPart + ".mp4";
			mVideoUtil = new VideoUtil(tempStr, holder);
			camera = mVideoUtil.cameraStart(camera);

			((SurfaceView) widgets[3]).setVisibility(View.VISIBLE);
			
			if(Camera.getNumberOfCameras() >1){
				hasFrontCamera = true;
				((ImageView) widgets[11]).setVisibility(View.VISIBLE);
			}else{
				hasFrontCamera = false;
				((ImageView) widgets[11]).setVisibility(View.GONE);
			}
		}

	}
	
	private void initListener() {
		// start btn
		((ImageView) widgets[6]).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					// 开始录音
					case MotionEvent.ACTION_DOWN:
						((ImageView) widgets[6]).setBackgroundDrawable(mContext
								.getResources().getDrawable(
										R.drawable.amsc_touch_spot_blue));

						mVideoUtil.recorderStart(mContext, camera);
						recording = true;
						((LinearLayout) widgets[10]).setVisibility(View.GONE);
						if(hasFrontCamera){
							((ImageView) widgets[11]).setVisibility(View.GONE);
						}

						new Thread(new Runnable() {

							public void run() {
								// 初始化录音时间
								mRecord_Time = mCurrentPosition;
								while (recording == true) {
									// 大于最大录音时间则停止录音
									if (mRecord_Time >= MAX_TIME) {

										((LinearLayout) widgets[10]).setVisibility(View.GONE);
										mRecordHandler.sendEmptyMessage(0);

									} else {
										try {
											// 每隔20毫秒就获取声音音量并更新界面显示
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
					// 停止录音
					case MotionEvent.ACTION_UP:
						if (recording == true) {
							((ImageView) widgets[6]).setBackgroundDrawable(mContext
									.getResources().getDrawable(
											R.drawable.amsc_cam_glow_ring));
							
							mCurrentPosition = mRecord_Time;
							recording = false;
							mVideoUtil.stopRecording(camera);

							((LinearLayout) widgets[10]).setVisibility(View.VISIBLE);
							if(hasFrontCamera){
								((ImageView) widgets[11]).setVisibility(View.VISIBLE);
							}
							/**
							 * 极为重要
							 */
							/*
							 * String tempStr = "tempvideo"+ videoPart+".mp4";
							 * videosPath.add(tempStr);
							 */
							// 将上一个的视频添加入集合中，在创建下一个视频的路径
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
		
		((LinearLayout) widgets[10]).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				videoPart = videoPart - 1;
				((LinearLayout) widgets[10]).setVisibility(View.GONE);

				mVideoUtil.stopRecording(camera);
				recording = false;

				for (int i = 0; i <= videoPart; i++) {
					videosPath.add("tempvideo" + i + ".mp4");

				}
				
				/*widgetsId = new int[] { R.id.user_videoshow_time_tv,// 0
				R.id.user_videoshow_maxtime_tv,// 1
				R.id.user_videoshow_progressbar,// 2
				R.id.user_videoshow_surfaceview,// 3
				R.id.user_videoshow_videoview,// 4
				R.id.user_videoshow_play_iv,// 5
				R.id.user_videoshow_start_iv,// 6
				R.id.user_videoshow_photo_iv, // 7
				R.id.user_videoshow_cancel_iv,// 8
				R.id.user_videoshow_time_relayout, //9
				R.id.user_videoshow_mergebtn_layout, //10
				R.id.user_videoshow_switchbtn}; // 11*/	
				((ImageView) widgets[11]).setVisibility(View.INVISIBLE);

				//photoview
				((ImageView)widgets[7]).setVisibility(View.INVISIBLE);
				saveVideoBitmap();

				((VideoView) widgets[4]).setVisibility(View.GONE);
				((SurfaceView) widgets[3]).setVisibility(View.GONE);
				//playbtn
				((ImageView)widgets[5]).setVisibility(View.VISIBLE);
				((ImageView)widgets[6]).setVisibility(View.GONE);
				
				((ProgressBar) widgets[2]).setMax((int) MAX_TIME * 1000);
				((ProgressBar) widgets[2]).setProgress(0);
				((TextView) widgets[0]).setText("0″");
				((TextView) widgets[1]).setText((int) MAX_TIME + "″");

				// 合并视频
				mergeVideos();
			}
		});

		// play btn
		((ImageView) widgets[5]).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				((VideoView) widgets[4]).setVisibility(View.VISIBLE);
				((ImageView)widgets[7]).setVisibility(View.VISIBLE);

				if (video_flag == 1) {

					((VideoView) widgets[4]).setVideoPath(videoPath);
					((VideoView) widgets[4]).start();

				} else if (video_flag == 2) {

					Uri videoUri = Uri.parse(video_URL);
					((VideoView) widgets[4]).setVideoURI(videoUri);
					((VideoView) widgets[4]).start();
					
				} else if (video_flag == 3) {
					((VideoView) widgets[4]).setVideoPath(videoTempPath);
					((VideoView) widgets[4]).start();

				}
				((ImageView)widgets[7]).setVisibility(View.INVISIBLE);
				// play btn
				((ImageView) widgets[5]).setVisibility(View.GONE);
				// start_btn
				((ImageView) widgets[6]).setVisibility(View.GONE);
				// cancel btn
				((ImageView) widgets[8]).setVisibility(View.VISIBLE);

			}
		});

		// cancel btn
		((ImageView) widgets[8]).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				videoPart = 0;
				videosPath = new ArrayList<String>();
				mCurrentPosition = 0;
				clearFile();
				video_flag = 0;
				initView();
			}
			
		});
		
		((VideoView) widgets[4]).setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (((VideoView) widgets[4]) != null & ((VideoView) widgets[4]).isPlaying() != true) {
					
					/*((VideoView) widgets[4]).stopPlayback();
					((VideoView) widgets[4]).seekTo(currentVideoViewPosition);
					
					((VideoView) widgets[4]).start();*/
					
					

				} else if (((VideoView) widgets[4]) != null & ((VideoView) widgets[4]).isPlaying() == true) {
					/*((VideoView) widgets[4]).pause();
					currentVideoViewPosition = ((VideoView) widgets[4]).getCurrentPosition();*/
					
				}

				return true;
			}
		});

		((VideoView) widgets[4])
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						((VideoView) widgets[4]).seekTo(0);
						((VideoView) widgets[4]).start();

					}
				});
		if(hasFrontCamera){
			((ImageView) widgets[11]).setOnClickListener(new View.OnClickListener() {

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
	private void clearFile() {
		File dir = new File("/sdcard/U2B/My/TEMP/");
		if (!dir.exists()) {
			dir.mkdirs();
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
	
	private void saveVideoBitmap() {
		String path = workingPath + videosPath.get(0);
		Log.e("TAG", path);
		Bitmap bitmap = mVideoUtil.getVideoThumbnail(path);
		mVideoUtil.BitmapToFile(bitmap, videoTempPath);
		photoFile = new File(videoTempPath);
		//photoview
		((ImageView)widgets[7]).setImageURI(Uri.fromFile(photoFile));
		((ImageView)widgets[7]).setVisibility(View.VISIBLE);
	}
	
	/**
	 * 超给力的 MP4 视频合并
	 */
	public void mergeVideos() {
		new MP4ParserUtil(mContext, workingPath, videosPath, videoTempPath);
		videoFile = new File(videoTempPath);
		video_flag = 3;  //临时文件
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (camera != null) {
			// 相机预览
			try {
				camera.setPreviewDisplay(holder);
				camera.startPreview();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	@Override
	public void onError(MediaRecorder mr, int what, int extra) {
		mVideoUtil.stopRecording(camera);
		Toast.makeText(this, "出现错误，录像停止", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
		if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
			mVideoUtil.stopRecording(camera);
			Toast.makeText(this, "文件已保存", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 用来控制录音
	 */
	Handler mRecordHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0: // 录制时间超过10秒 或 录制结束
				if (recording == true) {

					mVideoUtil.stopRecording(camera);
					recording = false;

					for (int i = 0; i <= videoPart; i++) {
						videosPath.add("tempvideo" + i + ".mp4");

					}

					((ImageView)widgets[7]).setVisibility(View.INVISIBLE);
					saveVideoBitmap();

					((ImageView) widgets[11]).setVisibility(View.INVISIBLE);
					((VideoView) widgets[4]).setVisibility(View.GONE);
					((SurfaceView) widgets[3]).setVisibility(View.GONE);
					//playbtn
					((ImageView)widgets[5]).setVisibility(View.VISIBLE);
					((ImageView)widgets[6]).setVisibility(View.GONE);
					
					((ProgressBar) widgets[2]).setMax((int) MAX_TIME * 1000);
					((ProgressBar) widgets[2]).setProgress(0);
					((TextView) widgets[0]).setText("0″");
					((TextView) widgets[1]).setText((int) MAX_TIME + "″");

					// 合并视频
					mergeVideos();

				}
				break;

			case 1:
				// 根据录音时间显示进度条
				((ProgressBar) widgets[2])
						.setProgress((int) (mRecord_Time * 1000));
				// 显示录音时间
				((TextView) widgets[0]).setText((int) mRecord_Time + "″");

				break;
			}
		}

	};

	@Override
	public void onPause() {
		super.onPause();
		if (camera != null)
			mVideoUtil.stopRecording(camera);
	}

	@Override
	protected void onDestroy() {
		if (pd != null) {
			pd.dismiss();
		}
		if (camera != null) {
			mVideoUtil.stopRecording(camera);
		}
		File tempFile = new File(videoTempPath);
		if (tempFile.exists()) {
			tempFile.delete();
		}
		super.onDestroy();
	}

	// ///////////////////////////////////

	private void setUserVideo() {
		new Thread() {
			public void run() {
				Looper.prepare();
				try {

					String result = MyUploadService.uploadUserVideo(photoFile,videoFile);

					Log.e("TAG_USerVideoShow",result+ photoFile.getAbsolutePath()+videoFile.getAbsolutePath());
					JSONObject jsonObject = new JSONObject(result);
					int flag = Integer.valueOf(jsonObject.getString("result"));

					if (flag > 0) {
						pd.dismiss();
						Toast.makeText(mContext, "上传成功", 1500).show();
						File oldFile = new File(videoPath);
						oldFile.delete();
						File newFile = new File(videoTempPath);
						newFile.renameTo(new File(videoPath));

					} else {
						pd.dismiss();
						Toast.makeText(mContext, "上传失败", 1500).show();

					}
					Looper.loop();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

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

}
