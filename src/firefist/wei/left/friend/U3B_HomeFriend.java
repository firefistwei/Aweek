package firefist.wei.left.friend;

import firefist.wei.main.MainActivity;
import firefist.wei.main.R;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class U3B_HomeFriend extends FragmentActivity implements
		ActionBar.TabListener {

	MyFragmentPagerAdapter myAdapter;

	ViewPager mViewPager;

	private Context context;

	@SuppressLint("NewApi")
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.u3b_upactivity3);
		context = this;

		myAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

		final ActionBar actionBar = getActionBar();

		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		actionBar.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.title_bar));
		actionBar.setTitle("我的好友");

		mViewPager = (ViewPager) findViewById(R.id.u3b_activity3_pager);
		mViewPager.setAdapter(myAdapter);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);

					}
				});

		Tab tab = actionBar.newTab().setText("好友").setTabListener(this);
		actionBar.addTab(tab);

		tab = actionBar.newTab().setText("关注").setTabListener(this);
		actionBar.addTab(tab);

		tab = actionBar.newTab().setText("粉丝").setTabListener(this);
		actionBar.addTab(tab);

		actionBar.setSelectedNavigationItem(0);
		
		Toast.makeText(context, "该功能暂未完成，敬请关注下一版本!", Toast.LENGTH_LONG).show();


	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {

	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());

	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {

	}

	public static class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			switch (i) {
			case 0:
				return new U3B_Friendfrag();
			case 1:
				return new U3B_Followerfrag();
//				return new Voice3Fragment();
			case 2:
				return new U3B_Fansfrag();
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {

			return super.getPageTitle(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.u3b_upactivity_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			Intent upIntent = new Intent(this, MainActivity.class);
			upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(upIntent);
			return true;
			

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getRepeatCount() == 0) {
			Intent upIntent = new Intent(this, MainActivity.class);
			upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(upIntent);
			}
		return false;
		
	}

}
