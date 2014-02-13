package firefist.wei.main;

import java.io.InputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import firefist.wei.main.service.MyService;
import firefist.wei.main.tinything.Register;
import firefist.wei.main.u3bactivity.PreUserInfoSetting;
import firefist.wei.main.u3bdomain.U3B_User;
import firefist.wei.utils.Utils;

@SuppressLint("NewApi")
public class Login extends Activity {

	private ImageView welcome;// a path to mainactivity

	private EditText email; // �ʺű༭��
	private EditText password; // ����༭��

	private TextView forgetkey;
	private ImageView done;

	private Handler handler;

	ProgressDialog pd = null;

	Context mContext = null;

	private boolean remember_flag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.u3b_login);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);

		actionBar.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.title_bar));
		actionBar.setIcon(R.drawable.app_icon);
		actionBar.setTitle("AWeek��¼");
		actionBar.show();

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		mContext = this;
		init();
		initListener();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuItem save = menu.add(0, 10001, 0, "ע��");
		// save.setIcon(this.getResources().getDrawable(
		// R.drawable.ic_navigation_done));
		save.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 10001: // ע��
			Intent intent = new Intent(Login.this, Register.class);
			startActivity(intent);
			return true;
		case android.R.id.home:
			Intent intent2 = new Intent(Login.this, MainActivity.class);
			startActivity(intent2);
			this.finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void init() {
		email = (EditText) findViewById(R.id.u3b_login_email);
		password = (EditText) findViewById(R.id.u3b_login_passwd);

		forgetkey = (TextView) findViewById(R.id.u3b_login_forgetkey);
		done = (ImageView) findViewById(R.id.u3b_login_done);

		SharedPreferences sharedPreferences = getSharedPreferences("u3b_sp",
				MODE_PRIVATE);

		String pre_email = sharedPreferences.getString("u3b_user_email", "");
		String pre_pwd = sharedPreferences.getString("u3b_user_password", "");
		String remember_account = sharedPreferences.getString(
				"u3b_user_remember", "no");

		if (remember_account.equals("yes")) {

			remember_flag = true;

			String email = sharedPreferences.getString("u3b_user_email", "");
			String pwd = sharedPreferences.getString("u3b_user_password", "");

			Log.e("TAG_email", email);

			if (!email.equals("")) {
				getLogin(email, pwd);
				Intent intent = new Intent();
				intent.setClass(Login.this, MainActivity.class);
				startActivity(intent);
				this.finish();
			}
		} else {
			remember_flag = false;
		}

		if (Register.uname != null) {
			email.setText(Register.uname);
			password.setText(Register.upasswd);
		} else if (pre_email.equals("")) {
			email.setText("");
			password.setText("");

		} else {
			email.setText(pre_email);
			password.setText(pre_pwd);
		}

	}

	private void initListener() {
		forgetkey.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		done.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String user = email.getText().toString().trim();
				String passwd = password.getText().toString().trim();
				if (user.equals("")) {
					Toast.makeText(mContext, "�û�������Ϊ��Ŷ", Toast.LENGTH_SHORT)
							.show();
				} else if (passwd.equals("")) {
					Toast.makeText(mContext, "���벻��Ϊ��Ŷ", Toast.LENGTH_SHORT)
							.show();
				} else {
					pd = ProgressDialog.show(Login.this, null, "���ڵ�¼...", true,
							true);
					getLogin(user, passwd);
				}
			}
		});

		welcome = (ImageView) findViewById(R.id.u3b_login_welcome);
		welcome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// ��������
				MyConstants.User_Map.put("uid", "10000");

				MyConstants.User_Map.put("school", "������ѧ");
				MyConstants.User_Map.put("user_name", "����");

				MyConstants.User_Map.put("signature", "�ҽ����󴸣�����û�뵽��U2Bû��IOS�档");
				MyConstants.User_Map.put("head_url", "");
				MyConstants.User_Map.put("video_url", "");
				MyConstants.User_Map.put("job", "ѧ��");
				MyConstants.User_Map.put("hobby", "��");
				MyConstants.User_Map.put("introduction", "��");
				MyConstants.User_Map.put("birthday", "2000-01-01");
				MyConstants.User_Map.put("email", "wangdachui@163.com");
				MyConstants.User_Map.put("create_time", "2014-10-21");
				MyConstants.User_Map.put("gender", "1");

				Intent intent = new Intent();
				intent.setClass(Login.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	private void getLogin(final String user, final String passwd) {
		new Thread() {

			public void run() {
				Looper.prepare();
				InputStream inputStream;

				try {
					inputStream = MyService.getLogin(user, passwd);
					if(inputStream==null){
						pd.dismiss();
						Toast.makeText(mContext, "��¼ʧ�ܣ������µ�½", 1000).show();
						Looper.loop();
					}
					String result = Utils.readInputStream(inputStream);
					Log.e("TAG_Login", result);

					if (result.length() > 35) {
						JSONObject jsonObject = new JSONObject(result);
						JSONObject userObject = jsonObject
								.getJSONObject("result");

						String[] keys = new String[] { "uid", "distance",
								"birthday", "gender", "create_time", "school",
								"user_name", "signature", "head_URL",
								"video_URL", "job", "hobby", "present" };

						HashMap<String, String> user_map = new HashMap<String, String>();
						for (int i = 0; i < keys.length; i++) {
							String value = userObject.getString(keys[i]);
							if (null == value || "null".equals(value)) {
								value = "";
							}
							user_map.put(keys[i], value);
						}

						MyConstants.UserUid = user_map.get("uid");
						MyConstants.User_Map = user_map;

					}else{
						pd.dismiss();
						new AlertDialog.Builder(Login.this)
						/*
						 * .setIcon( getResources().getDrawable(
						 * R.drawable.login_error_passwd))
						 */
						.setTitle("��¼ʧ��")
								.setMessage("AWeek�ʺŻ������벻��ȷ��\n���һ�°ɣ�")
								.create().show();
						Looper.loop();
						
					}

					if (remember_flag == false) {

						if (Long.valueOf(MyConstants.User_Map.get("uid")) > 0) {
							pd.dismiss();

							/*
							 * (non-Javadoc)
							 * 
							 * @ ���ش�һ��
							 */
							SharedPreferences sp = getSharedPreferences(
									"u3b_sp", MODE_PRIVATE);
							Editor editor = sp.edit();
							editor.putString("u3b_user_remember", "yes");
							editor.putString("u3b_user_email", user);
							editor.putString("u3b_user_password", passwd);
							editor.putString("u3b_user_uid",
									MyConstants.User_Map.get("uid"));
							editor.commit();

							/*
							 * (non-Javadoc)
							 * 
							 * @ �ж��Ƿ��ǵ�һ�ε�¼
							 */
							if (MyConstants.User_Map.get("user_name") == null
									|| MyConstants.User_Map.get("user_name")
											.equals("")) {
								Intent intent = new Intent();
								intent.setClass(Login.this,
										PreUserInfoSetting.class);
								startActivity(intent);
								finish();
							} else {
								Intent intent = new Intent();
								intent.setClass(Login.this, MainActivity.class);
								startActivity(intent);
								finish();
							}

							Looper.loop();
						} else {
							pd.dismiss();
							new AlertDialog.Builder(Login.this)
							/*
							 * .setIcon( getResources().getDrawable(
							 * R.drawable.login_error_passwd))
							 */
							.setTitle("��¼ʧ��")
									.setMessage("U2b�ʺŻ������벻��ȷ��\n������������룡")
									.create().show();
							Looper.loop();
						}

					}
					Looper.loop();
				} catch (Exception e) {
					Log.e("LOGIN","6");
					e.printStackTrace();
				}
			}
		}.start();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (pd != null) {
			pd.dismiss();
		}
		super.onDestroy();
	}
}
