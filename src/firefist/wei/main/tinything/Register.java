package firefist.wei.main.tinything;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import firefist.wei.main.Login;
import firefist.wei.main.MainActivity;
import firefist.wei.main.MyConstants;
import firefist.wei.main.R;
import firefist.wei.main.service.MyService;
import firefist.wei.main.u3bdomain.U3B_User;
import firefist.wei.utils.Utils;

public class Register extends Activity {

	private EditText mUser;
	private EditText mPassword;
	private EditText mAgain;
	private ImageView mDone;

	private boolean okay;

	public static String uname = null;
	public static String upasswd = null;

	ProgressDialog pd = null;

	public Context context = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.u3b_register);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.title_bar));
		actionBar.setIcon(R.drawable.app_icon);
		actionBar.setTitle("AWeek注册");
		actionBar.show();

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		context = this;
		okay = true;

		mUser = (EditText) findViewById(R.id.u3b_register_email);
		mPassword = (EditText) findViewById(R.id.u3b_register_passwd);
		mAgain = (EditText) findViewById(R.id.u3b_register_passwd2);
		mDone = (ImageView) findViewById(R.id.u3b_register_done);

		initListener();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void initListener() {
		mDone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				okay = true;

				uname = mUser.getText().toString().trim();
				upasswd = mPassword.getText().toString().trim();

				if (uname.equals("")) {
					okay = false;
					Toast.makeText(Register.this, "用户名不能为空", Toast.LENGTH_SHORT)
							.show();

				} else if (upasswd.equals("")) {
					okay = false;
					Toast.makeText(Register.this, "密码不能为空", Toast.LENGTH_SHORT)
							.show();

				} else if (!((mAgain.getText().toString().trim())
						.equals(upasswd))) {
					okay = false;
					Toast.makeText(Register.this, "两次密码输入不一致，请重新输入",
							Toast.LENGTH_SHORT).show();

				}else if(!uname.contains("@")||!uname.endsWith(".com")){
					okay = false;
					Toast.makeText(Register.this, "请输入正确的邮箱",
							Toast.LENGTH_SHORT).show();         
				}else if(upasswd.length()<6|| upasswd.length()>10){
					okay = false;
					Toast.makeText(Register.this, "请输入6~10位密码",
							Toast.LENGTH_SHORT).show(); 
				}

				if (okay == true) {
                    Intent intent = new Intent(Register.this,Register2.class);
                    startActivity(intent);
					/*pd = ProgressDialog.show(Register.this, "请稍候...",
							"正在注册...", false, true);

					getRegister(uname, upasswd);*/
				}
			}
		});
	}

}
