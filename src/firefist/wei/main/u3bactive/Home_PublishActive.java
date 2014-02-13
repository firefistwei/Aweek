package firefist.wei.main.u3bactive;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import firefist.wei.main.MainActivity;
import firefist.wei.main.MyConstants;
import firefist.wei.main.R;
import firefist.wei.main.service.MyUploadService;

public class Home_PublishActive extends FragmentActivity {

	private Context context;

	private Object widgets[];
	private int widgetsId[];

	File videoFile = null;
	File audioFile = null;
	File photoFile = null;

	String a_title;

	ProgressDialog pd = null;

	public final static int REQUESTCODE_VIDEO = 1;
	public final static int REQUESTCODE_AUDIO = 2;

	String display_time = "72";

	private List<String[]> typeList = null;
	
	private int type_pos = 0; 

	@SuppressLint("NewApi")
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.u3b_publish_active);
		context = this;

		final ActionBar actionBar = getActionBar();

		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.title_bar));
		actionBar.setTitle("发布活动");

		widgetsId = new int[] { R.id.u3b_publish_active_getvideo,// 0
				R.id.u3b_publish_active_getaudio,// 1 LinearLayout
				R.id.u3b_publish_active_title, // 2 et
				R.id.u3b_publish_active_title_layout,// 3 layout
				R.id.u3b_publish_active_typebtn,// 4 tv
				R.id.u3b_publish_active_typetext, // 5 tv
				R.id.u3b_publish_active_videoicon, // 6 iv
				R.id.u3b_publish_active_voiceicon, // 7 iv
				R.id.u3b_publish_active_displaytime, // 8 linearlayout
				R.id.u3b_publish_active_displaytime_tv,// 9 tv
				R.id.u3b_publish_active_type_layout, // 10 layout
				R.id.u3b_publish_active_type_scrollview}; //11 scrollview
		widgets = new Object[widgetsId.length];

		String[] titleStr0 = { "自定义", "我们一起去", "一小时", "来挑战我吧", "帮舍友找对象" };
		String[] titleStr1 = { "过圣诞节","看电影", "唱歌", "喝咖啡", "吃晚饭", "打三国杀","自拍" };
		String[] titleStr2 = { "聊天", "约会", "英语交流","头脑风暴" ,"讲笑话"};
		String[] titleStr3 = { "DOTA", "LOL", "篮球", "羽毛球", "台球","吹牛","腹黑" };
		
		typeList = new ArrayList<String[]>();
		typeList.add(titleStr0);
		typeList.add(titleStr1);
		typeList.add(titleStr2);
		typeList.add(titleStr3);

		clearFile();
		findViewById();
		init();
		setListener();

	}

	private void clearFile() {

		File dir = new File("/sdcard/U2B/Active/");
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

	private void findViewById() {
		for (int i = 0; i < widgetsId.length; i++) {
			widgets[i] = this.findViewById(widgetsId[i]);
		}
	}

	private void init() {
		String action = this.getIntent().getStringExtra("action");
		if (action.equals("type_0")) {
			((LinearLayout) widgets[3]).setVisibility(View.VISIBLE);
			((HorizontalScrollView) widgets[11]).setVisibility(View.GONE);
			((TextView) widgets[4]).setVisibility(View.VISIBLE);
			((TextView) widgets[5]).setVisibility(View.GONE);
			
			type_pos = 0;

		} else if (action.equals("type_1")) {
			((LinearLayout) widgets[3]).setVisibility(View.VISIBLE);
			((HorizontalScrollView) widgets[11]).setVisibility(View.GONE);
			((TextView) widgets[4]).setVisibility(View.GONE);
			((TextView) widgets[5]).setVisibility(View.VISIBLE);

			((TextView) widgets[5]).setText("#我们一起去");
			
			type_pos = 1;

		} else if (action.equals("type_2")) {
			((LinearLayout) widgets[3]).setVisibility(View.VISIBLE);
			((HorizontalScrollView) widgets[11]).setVisibility(View.GONE);
			((TextView) widgets[4]).setVisibility(View.GONE);
			((TextView) widgets[5]).setVisibility(View.VISIBLE);

			((TextView) widgets[5]).setText("#一小时");
			
			type_pos = 2;

		} else if (action.equals("type_3")) {
			((LinearLayout) widgets[3]).setVisibility(View.VISIBLE);
			((HorizontalScrollView) widgets[11]).setVisibility(View.GONE);
			((TextView) widgets[4]).setVisibility(View.GONE);
			((TextView) widgets[5]).setVisibility(View.VISIBLE);

			((TextView) widgets[5]).setText("#来挑战我吧");
			
			type_pos = 3;

		} else if (action.equals("type_4")) {
			((LinearLayout) widgets[3]).setVisibility(View.VISIBLE);
			((HorizontalScrollView) widgets[11]).setVisibility(View.GONE);
			((TextView) widgets[4]).setVisibility(View.GONE);
			((TextView) widgets[5]).setVisibility(View.VISIBLE);

			((TextView) widgets[5]).setText("#帮舍友找对象");
			
			type_pos = 4;
		}

		((TextView) widgets[4]).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				initFirstPage();
			}
		});

		((TextView) widgets[5]).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				initFirstPage();
			}
		});

	}

	public void initFirstPage() {
		((LinearLayout) widgets[3]).setVisibility(View.GONE);
		((HorizontalScrollView) widgets[11]).setVisibility(View.VISIBLE);

		((LinearLayout) widgets[10]).removeAllViews();
		LayoutInflater inflater = LayoutInflater
				.from(((LinearLayout) widgets[10]).getContext());
		View mView = null;

		for (int i = 0; i < typeList.get(0).length; i++) {
			mView = inflater.inflate(R.layout.u3b_active_one_type, null);
			mView.setId(i);
			LinearLayout mViewlayout = (LinearLayout) mView
					.findViewById(R.id.u3b_active_one_type_layout);
			TextView mViewText = (TextView) mView
					.findViewById(R.id.u3b_active_one_type_text);
			ImageView mViewImage = (ImageView) mView
					.findViewById(R.id.u3b_active_one_type_back);

			if (i % 3 == 0) {
				if (i == 0) {
					mViewlayout.setBackgroundDrawable(context.getResources()
							.getDrawable(R.drawable.u3b_vine_blue));
					mViewText.setVisibility(View.GONE);
					mViewImage.setVisibility(View.VISIBLE);

				} else {
					mViewlayout.setBackgroundDrawable(context.getResources()
							.getDrawable(R.drawable.u3b_vine_blue));
					mViewText.setText(typeList.get(0)[i]);

				}
			} else if (i % 3 == 1) {
				mViewlayout.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.u3b_vine_red));
				mViewText.setText(typeList.get(0)[i]);

			} else if (i % 3 == 2) {
				mViewlayout.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.u3b_vine_green));
				mViewText.setText(typeList.get(0)[i]);
			}

			((LinearLayout) widgets[10]).addView(mView, i);

			final int ViewId = mView.getId();
			mView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (ViewId == 0) {
						((LinearLayout) widgets[10]).removeAllViews();

						((LinearLayout) widgets[3]).setVisibility(View.VISIBLE);
						((HorizontalScrollView) widgets[11]).setVisibility(View.GONE);
						((TextView) widgets[4]).setVisibility(View.VISIBLE);
						((TextView) widgets[5]).setVisibility(View.GONE);
						((EditText) widgets[2]).setText("");
						
						type_pos = 0;
						return;
						
						
					} else if(ViewId == 4){
						((LinearLayout) widgets[10]).removeAllViews();

						((LinearLayout) widgets[3]).setVisibility(View.VISIBLE);
						((HorizontalScrollView) widgets[11]).setVisibility(View.GONE);

						((TextView) widgets[4]).setVisibility(View.GONE);
						((TextView) widgets[5]).setVisibility(View.VISIBLE);

						((TextView) widgets[5]).setText("#"
								+ typeList.get(0)[ViewId]);
						((EditText) widgets[2]).setText("");
						
						type_pos = 4;
						return;
					} else{
						((LinearLayout) widgets[10]).removeAllViews();
						initSecondPage(ViewId);
					}

				}
			});
		}
	}

	public void initSecondPage(final int ViewId) {

		((LinearLayout) widgets[3]).setVisibility(View.GONE);
		((HorizontalScrollView) widgets[11]).setVisibility(View.VISIBLE);

		((LinearLayout) widgets[10]).removeAllViews();
		LayoutInflater inflater = LayoutInflater
				.from(((LinearLayout) widgets[10]).getContext());
		View mView = null;

		for (int i = 0; i < typeList.get(ViewId).length; i++) {
			mView = inflater.inflate(R.layout.u3b_active_one_type, null);
			mView.setId(i);
			LinearLayout mViewlayout = (LinearLayout) mView
					.findViewById(R.id.u3b_active_one_type_layout);
			TextView mViewText = (TextView) mView
					.findViewById(R.id.u3b_active_one_type_text);

			if (i % 3 == 0) {
				mViewlayout.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.u3b_vine_blue));
				mViewText.setText(typeList.get(ViewId)[i]);

			} else if (i % 3 == 1) {
				mViewlayout.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.u3b_vine_red));
				mViewText.setText(typeList.get(ViewId)[i]);

			} else if (i % 3 == 2) {
				mViewlayout.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.u3b_vine_green));
				mViewText.setText(typeList.get(ViewId)[i]);
			}

			((LinearLayout) widgets[10]).addView(mView, i);

			final int Id = mView.getId();
			mView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					((LinearLayout) widgets[10]).removeAllViews();

					((LinearLayout) widgets[3]).setVisibility(View.VISIBLE);
					((HorizontalScrollView) widgets[11]).setVisibility(View.GONE);

					((TextView) widgets[4]).setVisibility(View.GONE);
					((TextView) widgets[5]).setVisibility(View.VISIBLE);

					((TextView) widgets[5]).setText("#"
							+ typeList.get(0)[ViewId]);
					((EditText) widgets[2]).setText("#"
							+ typeList.get(ViewId)[Id]);
					
					type_pos = ViewId;
				}
			});
		}
	}

	private void setListener() {
		/**
		 * display time
		 */
		((LinearLayout) widgets[8])
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						v.requestFocus();
						new AlertDialog.Builder(context)
								.setTitle("有效时间")
								.setItems(
										new String[] { "(默认)3天", "1小时", "3小时",
												"24小时", "48小时" },
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												switch (which) {
												case 0:
													display_time = "72";
													((TextView) widgets[9])
															.setText("72 小时");
													break;
												case 1:
													display_time = "1";
													((TextView) widgets[9])
															.setText("1 小时");
													break;
												case 2:
													display_time = "3";
													((TextView) widgets[9])
															.setText("3 小时");
													break;
												case 3:
													display_time = "24";
													((TextView) widgets[9])
															.setText("24 小时");
													break;
												case 4:
													display_time = "48";
													((TextView) widgets[9])
															.setText("48 小时");
													break;
												}
												dialog.dismiss();

											}
										}).create().show();
					}
				});

		/**
		 * video
		 */
		((LinearLayout) widgets[0])
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Home_PublishActive.this,
								MakeVideo.class);
						startActivityForResult(intent, REQUESTCODE_VIDEO);

					}
				});
		/**
		 * audio
		 */
		((LinearLayout) widgets[1])
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Home_PublishActive.this,
								MakeAudio.class);
						startActivityForResult(intent, REQUESTCODE_AUDIO);

					}
				});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem save = menu.add(0, 10005, 0, "Upload");
		save.setIcon(this.getResources().getDrawable(
				R.drawable.ic_navigation_done));
		save.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			/*
			 * Intent upIntent = new Intent(this, MainActivity.class);
			 * upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 * startActivity(upIntent);
			 */
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
			return true;

		case 10005:
			a_title = ((EditText) widgets[2]).getText().toString().trim();
			
			if(!a_title.startsWith("#")){
				a_title = "#"+a_title;
				((EditText) widgets[2]).setText(a_title);
			}
			
			if(type_pos==0){
				
			}else if(type_pos ==1){
				a_title = "#"+ typeList.get(0)[type_pos]+ a_title;
			}else if(type_pos ==2){
				a_title = "#"+ typeList.get(0)[type_pos]+ a_title;
			}else if(type_pos ==3){
				a_title = "#"+ typeList.get(0)[type_pos]+ a_title;
			}else if(type_pos ==4){
				a_title = "#"+ typeList.get(0)[type_pos]+ a_title;
			}
			
			Log.e("POS",a_title);

			if (videoFile == null || audioFile == null) {
				Toast.makeText(context, "短视频和 语音介绍都是必须的哦", 2000).show();

			} else if (a_title.equals("#")||a_title.equals("")) {
				Toast.makeText(context, "标题 不能少了哦", 2000).show();

			} else {
				pd = ProgressDialog.show(context, "请稍后", "正在上传...", true, true);
				uploadActive();
			}

			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUESTCODE_VIDEO:
			if (resultCode == RESULT_OK) {
				String photoPath = data.getStringExtra("photoPath");
				photoFile = new File(photoPath);
				String videoPath = data.getStringExtra("videoPath");
				videoFile = new File(videoPath);

				((ImageView) widgets[6])
						.setImageResource(R.drawable.ic_navigation_done);
				((ImageView) widgets[6]).setVisibility(View.VISIBLE);

			}

			break;
		case REQUESTCODE_AUDIO:
			if (resultCode == RESULT_OK) {
				String filePath = data.getStringExtra("filePath");
				audioFile = new File(filePath);

				((ImageView) widgets[7])
						.setImageResource(R.drawable.ic_navigation_done);
				((ImageView) widgets[7]).setVisibility(View.VISIBLE);
			}

			break;

		}
	}

	@Override
	protected void onDestroy() {
		if (pd != null) {
			pd.dismiss();
		}
		super.onDestroy();
	}

	// ///////////////////////////////////////////
	private void uploadActive() {
		new Thread() {
			public void run() {
				Looper.prepare();

				String keys[] = new String[] { "owner", "start_time",
						"display_time", "location", "title", "latitude",
						"longitude" };
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("owner", MyConstants.UserUid);
				params.put("start_time", "0");
				params.put("display_time", display_time);
				params.put("location", "0");
				params.put("title", a_title);
				params.put("latitude", MyConstants.Latitude + "");
				params.put("longitude", MyConstants.Longitude + "");

				try {
					String result = MyUploadService.uploadActive(photoFile,
							audioFile, videoFile, params);
					JSONObject jsonObject = new JSONObject(result);
					int flag = Integer.valueOf(jsonObject.get("result")
							.toString());
					if (flag > 0) { // 上传成功
						pd.dismiss();
						Toast.makeText(context, "上传成功！", Toast.LENGTH_LONG)
								.show();
						Looper.loop();
					} else {
						pd.dismiss();
						Toast.makeText(context, "上传失败！", Toast.LENGTH_LONG)
								.show();
						Looper.loop();
					}
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
			this.finish();
			/*
			 * Intent upIntent = new Intent(this, MainActivity.class);
			 * upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 * startActivity(upIntent);
			 */
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
		return super.dispatchKeyEvent(event);

	}
}
