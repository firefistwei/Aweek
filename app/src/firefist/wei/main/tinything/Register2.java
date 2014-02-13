package firefist.wei.main.tinything;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONObject;

import firefist.wei.main.Login;
import firefist.wei.main.MyConstants;
import firefist.wei.main.R;
import firefist.wei.main.service.MyService;
import firefist.wei.utils.Utils;

public class Register2 extends Activity {
	//

	Context mContext = null;

	private TextView mSex;
	private TextView mBirthday;
	private ImageView mDone;

	public static String gender = "0";
	public static String birthday = null;

	ProgressDialog pd = null;
	LinearLayout dateTimeLayout = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.u3b_register2);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.title_bar));
		actionBar.setIcon(R.drawable.app_icon);
		actionBar.setTitle("注册Next");
		actionBar.show();

		mContext = this;

		mSex = (TextView) findViewById(R.id.u3b_register2_sex);
		mBirthday = (TextView) findViewById(R.id.u3b_register2_birthday);
		mDone = (ImageView) findViewById(R.id.u3b_register2_done);

		// initDatePicker();
		initListener();

	}

	public void register2_sex_click(View v) {
		if (mSex.getText().equals("Woman")) {
			mSex.setText("Man");
			mSex.setTextColor(Color.argb(255, 20, 144, 255));
			gender = 1 + "";

		} else if (mSex.getText().equals("Man")) {
			mSex.setText("Woman");
			mSex.setTextColor(Color.argb(255, 255, 99, 71));
			gender = 0 + "";
		}
	}

	public void register2_birthday_click(View v) {
		myDateDialog();
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

				pd = ProgressDialog.show(Register2.this, "请稍候...", "正在注册...",
						true, true);

				getRegister();

			}
		});
	}

	public void getRegister() {

		new Thread() {
			public void run() {
				Looper.prepare();
				InputStream inputStream;

				try {
					inputStream = MyService.getRegister();

					String result = Utils.readInputStream(inputStream);
					Log.e("TAG",result);
					
					JSONObject jsonObject = new JSONObject(result);
					int flag = Integer.valueOf(jsonObject.getString("result"));
					
					if (flag>0) {
						pd.dismiss();
						Toast.makeText(Register2.this,
								"注册成功" + "邮箱账号为: " + Register.uname,
								Toast.LENGTH_SHORT).show();
						
						/* (non-Javadoc)
						 * @ 本地存一下
						 */
						SharedPreferences sp = getSharedPreferences("u3b_sp",
								MODE_PRIVATE);
						Editor editor = sp.edit();
						editor.putString("u3b_user_remember", "no");
						editor.putString("u3b_user_email",
								Register.uname);
						editor.putString("u3b_user_password",
								Register.upasswd);
						editor.putString("u3b_user_uid",
								flag+"");
						editor.commit();

						Intent intent = new Intent(Register2.this, Login.class);
						startActivity(intent);
						finish();
						Looper.loop();

					} else {
						pd.dismiss();
						Toast.makeText(Register2.this, "注册失败",
								Toast.LENGTH_SHORT).show();
						Looper.loop();
						
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}.start();
	}

	@Override
	protected void onDestroy() {
		if (pd != null) {
			pd.dismiss();
		}
		super.onDestroy();
	}

	private void myDateDialog() {
		MyDateFragment myDateFragment = new MyDateFragment();
		// myDateFragment.show(getFragmentManager(), "1");
		myDateFragment.show(getFragmentManager(), "BirthDay");

	}

	Calendar c = null;

	class MyDateFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Dialog dialog = null;
			c = Calendar.getInstance();
			dialog = new DatePickerDialog(getActivity(),
					new DatePickerDialog.OnDateSetListener() {

						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {

							mBirthday.setText(year + "-"
									+ (monthOfYear+1) + "-"
									+ dayOfMonth);
							birthday  = mBirthday.getText().toString();

						}
					}, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
					c.get(Calendar.DAY_OF_MONTH));

			return dialog;
		}

	}

}
