package firefist.wei.main.u3bactive;

import java.util.ArrayList;
import java.util.List;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import firefist.wei.main.MainActivity;
import firefist.wei.main.R;
import firefist.wei.main.up.UpActivity3.MyFragmentPagerAdapter;

public class Home_MyActive extends FragmentActivity implements
		ActionBar.TabListener {

	MyFragmentPagerAdapter myAdapter;
	ViewPager mViewPager;
	
	private Context mContext;
	private ActionBar actionBar;

	@SuppressLint("NewApi")
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.home_myactive);

		mContext = this;
		
		actionBar = getActionBar();
		
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		actionBar.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.title_bar));
		actionBar.setTitle("我的活动");
		
		myAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.home_myactive_pager);
		mViewPager.setAdapter(myAdapter);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);

					}
				});
		
		actionBar.addTab(actionBar.newTab().setText("我发布的")
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("我参与的")
				.setTabListener(this));
		
		actionBar.setSelectedNavigationItem(0);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
	}
	
	public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			switch(pos){
			case 0:
				return new Fragment1();

			case 1:
				return new Fragment2();
				
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return 2;
		}
		
	}

	/**
	 * @Fragment  我发布的
	 *
	 */
	public class Fragment1 extends Fragment {

		ListView mListView;
		MyAdapter mAdapter;
		Context context;
		
		List<String> data;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.home_myactive_page1, container,
					false);
			mListView = (ListView)rootView.findViewById
					(R.id.home_myactive_page1_listview);
			return rootView;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			context = this.getActivity();
			
			data = new ArrayList<String>();
			for(int i=0; i<5;i++)
			data.add("1");
			
			mAdapter = new MyAdapter();
			mListView.setAdapter(mAdapter);
		}

		
		public class MyAdapter extends BaseAdapter{

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return data.size();
			}

			@Override
			public Object getItem(int pos) {
				// TODO Auto-generated method stub
				return data.get(pos);
			}

			@Override
			public long getItemId(int pos) {
				// TODO Auto-generated method stub
				return pos;
			}

			@Override
			public View getView(int pos, View convertView, ViewGroup parent) {
				if(convertView == null){
					convertView = LayoutInflater.from(context).inflate(
							R.layout.u3b_myactive_item, null);
					
				}else{
					convertView.setTag(pos);
				}
				
				
				return convertView;
			}
			
		}
	

	}
	
	/* 
	 * @Fragment 我参与的
	 */
	public class Fragment2 extends Fragment {
		
		
		ListView mListView;
		MyAdapter mAdapter;
		Context context;
		
		List<String> data;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.home_myactive_page2, container,
					false);
			mListView = (ListView)rootView.findViewById
					(R.id.home_myactive_page2_listview);
			return rootView;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
			context = this.getActivity();			

			data = new ArrayList<String>();
			for(int i=0; i<5;i++)
			data.add("1");
			
			mAdapter = new MyAdapter();
			mListView.setAdapter(mAdapter);
		}

		
		public class MyAdapter extends BaseAdapter{

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return data.size();
			}

			@Override
			public Object getItem(int pos) {
				// TODO Auto-generated method stub
				return data.get(pos);
			}

			@Override
			public long getItemId(int pos) {
				// TODO Auto-generated method stub
				return pos;
			}

			@Override
			public View getView(int pos, View convertView, ViewGroup parent) {
				if(convertView == null){
					convertView = LayoutInflater.from(context).inflate(
							R.layout.u3b_myactive_item, null);
					
				}else{
					convertView.setTag(pos);
				}
				
				
				return convertView;
			}	
		}
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
