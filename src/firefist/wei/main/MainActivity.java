package firefist.wei.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.nostra13.universalimageloader.utils.L;

import static firefist.wei.main.MyConstants.IMAGES;
import firefist.wei.main.R;
import firefist.wei.main.u3bactive.Home_PublishActive;
import firefist.wei.main.up.UpActivity3;
import firefist.wei.main.up.Video3Fragment;
import firefist.wei.satellite.SatelliteMenu;
import firefist.wei.satellite.SatelliteMenuItem;
import firefist.wei.satellite.SatelliteMenu.SateliteClickedListener;
import firefist.wei.sliding.fragment.*;
import firefist.wei.sliding.fragment.MainFragment.MyPageChangeListener;

import firefist.wei.sliding.fragment.LeftFragment;
import firefist.wei.sliding.utils.IChangeFragment;
import firefist.wei.sliding.view.SlidingMenu;
import firefist.wei.utils.ActivityForResultUtil;
import firefist.wei.utils.PhotoUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;

public class MainActivity extends FragmentActivity implements IChangeFragment {

	private static final String TAG = "SlidingActivity";

	SlidingMenu mSlidingMenu;
	LeftFragment leftFragment;
	MainFragment mainFragment;

	public static UApplication mKXApplication;
	public UActivity kxActivity;

	public static MainActivity instance = null;

	/**
	 * 屏幕的宽度和高度
	 */
	public static int mScreenWidth;
	public static int mScreenHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		instance = this;

		kxActivity = new UActivity();
		mKXApplication = UActivity.mKXApplication;

		init();
		initListener(mainFragment);

		/**
		 * 获取屏幕宽度和高度
		 */
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;

	}

	private void init() {

		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);

		mSlidingMenu.setLeftView(getLayoutInflater().inflate(
				R.layout.left_frame, null));
		mSlidingMenu.setRightView(getLayoutInflater().inflate(
				R.layout.right_frame, null));
		mSlidingMenu.setCenterView(getLayoutInflater().inflate(
				R.layout.center_frame, null));

		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();

		leftFragment = new LeftFragment(this.getSupportFragmentManager());
		leftFragment.setChangeFragmentListener(this);
		t.replace(R.id.left_frame, leftFragment);

		/*
		 * rightFragment = new RightFragment(this.getSupportFragmentManager());
		 * t.replace(R.id.right_frame, rightFragment);
		 */

		mainFragment = new MainFragment(this);
		t.replace(R.id.center_frame, mainFragment);
		t.commit();

	}

	private void initListener(final MainFragment fragment) {
		fragment.setMyPageChangeListener(new MyPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				Log.e(TAG, "onPageSelected : " + position);
				if (fragment.isFirst()) {
					mSlidingMenu.setCanSliding(true, false);

				} else if (fragment.isEnd()) {
					mSlidingMenu.setCanSliding(false, false);
					// mSlidingMenu.setCanSliding(false, true);
				} else {
					mSlidingMenu.setCanSliding(false, false);
				}
			}
		});
	}

	public void showLeft() {
		mSlidingMenu.showLeftView();
	}

	/*
	 * public void showRight() { mSlidingMenu.showRightView(); }
	 */

	@Override
	public void changeFragment(int position) {
		/*
		 * FragmentTransaction t = this.getSupportFragmentManager()
		 * .beginTransaction(); Fragment fragment = null; switch(position){ case
		 * 0: // fragment = new MainFragment(this); //
		 * initListener((MainFragment) fragment); fragment = new
		 * PageFragment1(); break; case 1: fragment = new
		 * PageFragment2(this,this,KXActivity.mKXApplication); break; case 2:
		 * fragment = new Fragment(); break;
		 * 
		 * } t.replace(R.id.center_frame, fragment); t.commit();
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.activity_main_exit:
			SharedPreferences sharedPreferences = getSharedPreferences(
					"u3b_sp", Context.MODE_PRIVATE);
			Editor editor = sharedPreferences.edit();
			editor.putString("u3b_user_remember", "no");
			editor.commit();

			Toast.makeText(this, "用户账号已退出，下次需要重新登录", 1500).show();

			MainActivity.instance.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();

		mSlidingMenu.getCenterView().scrollTo(0, 0);

		mSlidingMenu.getLeftView().setVisibility(View.INVISIBLE);
		mSlidingMenu.getRightView().setVisibility(View.INVISIBLE);
		mSlidingMenu.setCanSliding(true, false);

		/*
		 * WindowManager windowManager = ((Activity)
		 * getApplicationContext()).getWindow() .getWindowManager(); Display
		 * display = windowManager.getDefaultDisplay(); int screenWidth =
		 * display.getWidth();
		 * 
		 * int screenHeight = display.getHeight();
		 * 
		 * LayoutParams bgParams = new LayoutParams(screenWidth, screenHeight);
		 * bgParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		 * mSlidingMenu.getCenterView().bringToFront();
		 */

	}

	public void head_up_photo(View v) {
		// PhotoDialog();
		Intent intent = new Intent(MainActivity.this, Video3Fragment.class);
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	public void head_up_active(View v) {
		Intent intent = new Intent(MainActivity.this, Home_PublishActive.class);
		intent.putExtra("action", "type_0");
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getRepeatCount() == 0) {

			Intent intent = new Intent();
			intent.setClass(MainActivity.this, Exit.class);
			startActivity(intent);
		}
		return false;

	}

}
