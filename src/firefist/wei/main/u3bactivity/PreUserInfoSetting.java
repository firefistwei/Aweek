package firefist.wei.main.u3bactivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import firefist.wei.main.Login;
import firefist.wei.main.MyConstants;
import firefist.wei.main.R;
import firefist.wei.main.service.MyUploadService;
import firefist.wei.utils.ActivityForResultUtil;
import firefist.wei.utils.PhotoUtil;

/**
 * Created by Administrator on 13-10-15.
 */
public class PreUserInfoSetting extends Activity {

	Context mContext = null;
	
	private String headPath ="/sdcard/U2B/My/myhead.jpg";

	private ImageView u3b_preuserinfo_avatar;
	private EditText u3b_preuserinfo_name, u3b_preuserinfo_signature,
			u3b_preuserinfo_job, u3b_preuserinfo_goodat,
			u3b_preuserinfo_school, u3b_preuserinfo_introduction;

	ProgressDialog pd = null;
	private Handler handler;

	private File mHeadFile = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.u3b_preuserinfo);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.title_bar));
		actionBar.setTitle("个人信息初始化");
		actionBar.show();

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		mContext = this;

		findViewById();
		setListener();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuItem save = menu.add(0, 10002, 0, "SAVE");
		save.setIcon(this.getResources().getDrawable(
				R.drawable.ic_navigation_done));
		save.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 10002:

			pd = ProgressDialog.show(PreUserInfoSetting.this, null,
					"正在更新个人信息...", true, true);
			saveUserMapInfo();
			setUserInfo();

			return true;

		case android.R.id.home:
			this.finish();

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void saveUserMapInfo() {
		MyConstants.User_Map.put("user_name", u3b_preuserinfo_name.getText()
				.toString().trim());
		MyConstants.User_Map.put("signature", u3b_preuserinfo_signature
				.getText().toString().trim());
		MyConstants.User_Map.put("job", u3b_preuserinfo_job.getText()
				.toString().trim());
		MyConstants.User_Map.put("hobby", u3b_preuserinfo_goodat.getText()
				.toString().trim());
		MyConstants.User_Map.put("school", u3b_preuserinfo_school.getText()
				.toString().trim());
		MyConstants.User_Map.put("present", u3b_preuserinfo_introduction
				.getText().toString().trim());
	}

	

	private void findViewById() {
		u3b_preuserinfo_avatar = (ImageView) this
				.findViewById(R.id.u3b_preuserinfo_avatar);
		u3b_preuserinfo_name = (EditText) this
				.findViewById(R.id.u3b_preuserinfo_name);
		u3b_preuserinfo_signature = (EditText) this
				.findViewById(R.id.u3b_preuserinfo_signature);
		u3b_preuserinfo_job = (EditText) this
				.findViewById(R.id.u3b_preuserinfo_job);
		u3b_preuserinfo_goodat = (EditText) this
				.findViewById(R.id.u3b_preuserinfo_goodat);
		u3b_preuserinfo_school = (EditText) this
				.findViewById(R.id.u3b_preuserinfo_school);
		u3b_preuserinfo_introduction = (EditText) this
				.findViewById(R.id.u3b_preuserinfo_introduction);
	}

	private void setListener() {

		u3b_preuserinfo_avatar.setOnClickListener(new OnClickListener() {

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
											File dir = new File(
													"/sdcard/U2B/Camera");
											if (!dir.exists()) {
												dir.mkdirs();
											}
											
											File file = new File(
													headPath);

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
				File file = new File(headPath);
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
//			bitmap = PhotoUtil.toRoundCorner(bitmap, 15);
			if (bitmap != null) {
				uploadPhoto(bitmap);
			}
		} else {
			Toast.makeText(this, "获取裁剪照片错误", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 更新头像
	 */
	private void uploadPhoto(Bitmap bitmap) {
		u3b_preuserinfo_avatar.setImageBitmap(bitmap);
		
		File file = new File(headPath);
		if(file.exists()){
			file.delete();
		}

		CompressFormat format = Bitmap.CompressFormat.JPEG;
		int quality = 100;
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(headPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (bitmap.compress(format, quality, stream)) {
			mHeadFile = new File(headPath);

		}

	}

	@Override
	protected void onDestroy() {
		if (pd != null) {
			pd.dismiss();
		}
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void setUserInfo() {
		new Thread() {
			public void run() {
				Looper.prepare();

				try {
					String result = MyUploadService.uploadUserInfo(mHeadFile);

					JSONObject jsonObject = new JSONObject(result);
					int flag = Integer.valueOf(jsonObject.getString("result"));
					if (flag == 0) {
						pd.dismiss();
						Toast.makeText(PreUserInfoSetting.this, "上传失败",
								Toast.LENGTH_SHORT).show();
						Looper.loop();
					} else { // success
						pd.dismiss();
						Toast.makeText(PreUserInfoSetting.this, "上传成功",
								Toast.LENGTH_SHORT).show();
						

						Intent intent = new Intent(PreUserInfoSetting.this,
								Login.class);
						startActivity(intent);
						finish();
						Looper.loop();

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}.start();

	}

}
