package firefist.wei.main.u3bactivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import firefist.wei.main.MainActivity;
import firefist.wei.main.MyConstants;
import firefist.wei.main.R;
import firefist.wei.main.service.MyService;
import firefist.wei.main.service.MyUploadService;
import firefist.wei.utils.ActivityForResultUtil;
import firefist.wei.utils.PhotoUtil;
import firefist.wei.utils.Utils;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfo_U3bActivity extends Activity {

	private ImageView u3b_userinfo_avatar;
	private ImageView u3b_userinfo_ownvideo;
	private TextView u3b_userinfo_name, u3b_userinfo_sig, u3b_userinfo_gender,
			u3b_userinfo_birthday, u3b_userinfo_job, u3b_userinfo_school,
			u3b_userinfo_goodat, u3b_userinfo_registerday,
			u3b_userinfo_introduction, u3b_userinfo_email, u3b_userinfo_phone,
			u3b_userinfo_password, u3b_userinfo_privatesset;
	private ImageView u3b_userinfo_renrenicon, u3b_userinfo_connectweibo;
	private Button u3b_userinfo_logout;

	Context mContext = null;

	ProgressDialog pd = null;

	private Handler handler;
	private File mHeadFile = null;
	private String mHeadPath = "/sdcard/U2B/My/myhead.jpg";
	private Bitmap mHeadBitmap = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.u3b_userinfo);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.title_bar));
		actionBar.setTitle("个人资料");
		actionBar.show();

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		mContext = this;

		findViewById();
		setListener();
		init();
		// init()在onResume()里
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuItem save = menu.add(0, 10001, 0, "SAVE");
		save.setIcon(this.getResources().getDrawable(
				R.drawable.ic_navigation_done));
		save.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 10001:
			pd = ProgressDialog.show(UserInfo_U3bActivity.this, null,
					"正在更新信息...", true, true);
			if (mHeadBitmap != null) {
				uploadPhoto(mHeadBitmap);
			}
			setUserInfo();
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

	private void findViewById() {

		u3b_userinfo_avatar = (ImageView) findViewById(R.id.u3b_userinfo_avatar);
		u3b_userinfo_ownvideo = (ImageView) findViewById(R.id.u3b_userinfo_ownvideo);

		u3b_userinfo_name = (TextView) findViewById(R.id.u3b_userinfo_name);
		u3b_userinfo_sig = (TextView) findViewById(R.id.u3b_userinfo_sig);

		u3b_userinfo_gender = (TextView) findViewById(R.id.u3b_userinfo_gender);
		u3b_userinfo_birthday = (TextView) findViewById(R.id.u3b_userinfo_birthday);
		u3b_userinfo_job = (TextView) findViewById(R.id.u3b_userinfo_job);
		u3b_userinfo_school = (TextView) findViewById(R.id.u3b_userinfo_school);
		u3b_userinfo_goodat = (TextView) findViewById(R.id.u3b_userinfo_goodat);
		u3b_userinfo_registerday = (TextView) findViewById(R.id.u3b_userinfo_registerday);
		u3b_userinfo_introduction = (TextView) findViewById(R.id.u3b_userinfo_introduction);

		u3b_userinfo_email = (TextView) findViewById(R.id.u3b_userinfo_email);
		u3b_userinfo_phone = (TextView) findViewById(R.id.u3b_userinfo_phone);
		u3b_userinfo_password = (TextView) findViewById(R.id.u3b_userinfo_password);
		u3b_userinfo_privatesset = (TextView) findViewById(R.id.u3b_userinfo_privatesset);

		u3b_userinfo_renrenicon = (ImageView) findViewById(R.id.u3b_userinfo_renrenicon);
		u3b_userinfo_connectweibo = (ImageView) findViewById(R.id.u3b_userinfo_sinaicon);

		u3b_userinfo_logout = (Button) findViewById(R.id.u3b_userinfo_logout);

	}

	private void init() {
		if (mHeadBitmap != null) {
			u3b_userinfo_avatar.setImageBitmap(mHeadBitmap);
		} else {
			File file = new File(mHeadPath);
			if (file.exists()) {
				u3b_userinfo_avatar.setImageBitmap(BitmapFactory
						.decodeFile(mHeadPath));
			} else {
				try {
					byte[] photoBytes = MyService.getImage(MyConstants.User_Map
							.get("head_URL"));
					if (photoBytes != null) {
						u3b_userinfo_avatar.setImageBitmap(BitmapFactory
								.decodeByteArray(photoBytes, 0,
										photoBytes.length));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (MyConstants.User_Map == null)
			return;

		u3b_userinfo_name.setText(MyConstants.User_Map.get("user_name"));

		u3b_userinfo_sig.setText(MyConstants.User_Map.get("signature"));
		if(MyConstants.User_Map.get("gender")!=null){
			if (MyConstants.User_Map.get("gender").equals("1")) {
				u3b_userinfo_gender.setText("Man");
			} else {
				u3b_userinfo_gender.setText("Woman");
			}
		}
		
		u3b_userinfo_birthday.setText(MyConstants.User_Map.get("birthday"));
		u3b_userinfo_school.setText(MyConstants.User_Map.get("school"));
		u3b_userinfo_job.setText(MyConstants.User_Map.get("job"));
		u3b_userinfo_introduction.setText(MyConstants.User_Map.get("present"));
		u3b_userinfo_goodat.setText(MyConstants.User_Map.get("hobby"));
		u3b_userinfo_registerday.setText(MyConstants.User_Map
				.get("create_time"));

	}

	private void setListener() {

		/*
		 * u3b_userinfo_registerday,u3b_userinfo_email,u3b_userinfo_phone,
		 * u3b_userinfo_password,
		 * u3b_userinfo_privatesset,u3b_userinfo_connectrenren
		 * ,u3b_userinfo_connectweibo;
		 */

		u3b_userinfo_name.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserInfo_U3bActivity.this,
						ChangeUserInfo_U3bActivity.class);
				intent.putExtra("text_action", "name");
				startActivity(intent);

			}
		});
		u3b_userinfo_sig.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserInfo_U3bActivity.this,
						ChangeUserInfo_U3bActivity.class);
				intent.putExtra("text_action", "sig");
				startActivity(intent);

			}
		});

		u3b_userinfo_school.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserInfo_U3bActivity.this,
						ChangeUserInfo_U3bActivity.class);
				intent.putExtra("text_action", "school");
				startActivity(intent);

			}
		});
		u3b_userinfo_goodat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserInfo_U3bActivity.this,
						ChangeUserInfo_U3bActivity.class);
				intent.putExtra("text_action", "goodat");
				startActivity(intent);

			}
		});

		u3b_userinfo_job.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserInfo_U3bActivity.this,
						ChangeUserInfo_U3bActivity.class);
				intent.putExtra("text_action", "job");
				startActivity(intent);

			}
		});

		u3b_userinfo_introduction
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(UserInfo_U3bActivity.this,
								ChangeUserInfo_U3bActivity.class);
						intent.putExtra("text_action", "introduction");
						startActivity(intent);

					}
				});

		u3b_userinfo_avatar.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				new AlertDialog.Builder(mContext)
						.setTitle("修改头像")
						.setItems(new String[] { "拍照上传", "上传相册中的照片" },
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent intent = null;
										switch (which) {
										case 0:
											intent = new Intent(
													MediaStore.ACTION_IMAGE_CAPTURE);

											File file = new File(mHeadPath);

											if (!file.exists()) {
												try {
													file.createNewFile();
												} catch (IOException e) {

												}
											}
											intent.putExtra(
													MediaStore.EXTRA_OUTPUT,
													Uri.fromFile(file));
											((Activity) mContext)
													.startActivityForResult(
															intent,
															ActivityForResultUtil.REQUESTCODE_UPLOADAVATAR_CAMERA);
											break;
										case 1:
											intent = new Intent(
													Intent.ACTION_PICK, null);
											intent.setDataAndType(
													MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
													"image/*");
											((Activity) mContext)
													.startActivityForResult(
															intent,
															ActivityForResultUtil.REQUESTCODE_UPLOADAVATAR_LOCATION);
											break;
										}
									}
								}).create().show();
			}
		});

		u3b_userinfo_logout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences sharedPreferences = getSharedPreferences(
						"u3b_sp", Context.MODE_PRIVATE);
				Editor editor = sharedPreferences.edit();
				editor.putString("u3b_user_remember", "no");
				editor.commit();

				Toast.makeText(mContext, "用户账号已退出，下次需要重新登录", 1500).show();

				MainActivity.instance.finish();
				finish();
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		/**
		 * 通过照相修改头像
		 */
		case ActivityForResultUtil.REQUESTCODE_UPLOADAVATAR_CAMERA:
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					Toast.makeText(this, "SD不可用", Toast.LENGTH_SHORT).show();
					return;
				}
				File file = new File(mHeadPath);
				startPhotoZoom(Uri.fromFile(file));
			} else {
				Toast.makeText(this, "取消上传", Toast.LENGTH_SHORT).show();
			}
			break;
		/**
		 * 通过本地修改头像
		 */
		case ActivityForResultUtil.REQUESTCODE_UPLOADAVATAR_LOCATION:
			Uri uri = null;
			if (data == null) {
				Toast.makeText(this, "取消上传", Toast.LENGTH_SHORT).show();
				return;
			}
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					Toast.makeText(this, "SD不可用", Toast.LENGTH_SHORT).show();
					return;
				}
				uri = data.getData();
				startPhotoZoom(uri);
			} else {
				Toast.makeText(this, "照片获取失败", Toast.LENGTH_SHORT).show();
			}
			break;
		/**
		 * 裁剪修改的头像
		 */
		case ActivityForResultUtil.REQUESTCODE_UPLOADAVATAR_CROP:
			if (data == null) {
				Toast.makeText(this, "取消上传", Toast.LENGTH_SHORT).show();
				return;
			} else {
				saveCropPhoto(data);
			}
			break;
		}
	}

	/**
	 * 系统裁剪照片
	 * 
	 * @param uri
	 */
	private void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 200);
		intent.putExtra("scale", true);
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("return-data", true);
		startActivityForResult(intent,
				ActivityForResultUtil.REQUESTCODE_UPLOADAVATAR_CROP);
	}

	/**
	 * 保存裁剪的照片
	 * 
	 * @param data
	 */
	private void saveCropPhoto(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			// bitmap = PhotoUtil.toRoundCorner(bitmap, 15);
			if (bitmap != null) {
				u3b_userinfo_avatar.setImageBitmap(bitmap);
				mHeadBitmap = bitmap;

			}
		} else {
			Toast.makeText(this, "获取裁剪照片错误", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 更新头像
	 */
	private void uploadPhoto(Bitmap bitmap) {

		File file = new File(mHeadPath);
		if (file.exists()) {
			file.delete();
		}

		CompressFormat format = Bitmap.CompressFormat.JPEG;
		int quality = 100;
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(mHeadPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (bitmap.compress(format, quality, stream)) {
			mHeadFile = new File(mHeadPath);

		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		init();
	}

	@Override
	protected void onDestroy() {
		if (pd != null) {
			pd.dismiss();
		}
		super.onDestroy();
	}

	// ////////////////////////////////////////////////////
	private void setUserInfo() {
		new Thread() {
			public void run() {
				Looper.prepare();
				try {

					String result = MyUploadService.uploadUserInfo(mHeadFile);
					Log.e("TAG", result);
					JSONObject jsonObject = new JSONObject(result);
					int flag = Integer.valueOf(jsonObject.getString("result"));
					if (flag > 0) {
						pd.dismiss();
						Toast.makeText(mContext, "更新成功", Toast.LENGTH_SHORT)
								.show();
						finish();

					} else {
						pd.dismiss();
						Toast.makeText(mContext, "更新失败", Toast.LENGTH_SHORT)
								.show();

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
