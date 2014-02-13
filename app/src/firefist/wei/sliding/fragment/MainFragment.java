package firefist.wei.sliding.fragment;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import firefist.wei.main.MainActivity;
import firefist.wei.main.MyBD;
import firefist.wei.main.R;
import firefist.wei.main.up.UpActivity3.MyFragmentPagerAdapter;
import firefist.wei.sliding.adapter.ScrollingTabsAdapter;
import firefist.wei.sliding.view.ScrollableTabView;


public class MainFragment extends Fragment implements ViewPager.OnPageChangeListener{
	private static final String TAG = "NewsFragment";

	private View showLeft;
	
	private ImageView mTopBackView;
	private MyAdapter mAdapter;
	private ViewPager mPager;
	public static ArrayList<Fragment> pagerItemList = null;

	private Activity mActivity=null;

	private ScrollableTabView mScrollableTabView;
	private ScrollingTabsAdapter mScrollingTabsAdapter;
	
	public static Fragment pageFragment1 = null;
	public static Fragment pageFragment2 = null;
	public static Fragment pageFragment3 = null;
	
	public static int page_position = 0;

	public MainFragment() {
	}

	public MainFragment(Activity activity) {
		this.mActivity = activity;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG, "onCreate");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "onDestroy");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e(TAG, "onCreateView");
		View mView = inflater.inflate(R.layout.view_pager, null);

		showLeft = (View) mView.findViewById(R.id.head_layout_showLeft);

		mTopBackView = (ImageView) showLeft.findViewById(R.id.head_layout_back);

		mPager = (ViewPager) mView.findViewById(R.id.vp_list);

		pagerItemList = new ArrayList<Fragment>();
		
		pageFragment1 = new PageFragment1();
		pageFragment2 = new PageFragment2();
		pageFragment3 = new PageFragment3();

		pagerItemList.add(pageFragment1);

		pagerItemList.add(pageFragment2);

		pagerItemList.add(pageFragment3);


		mAdapter = new MyAdapter(getFragmentManager());
		mPager.setAdapter(mAdapter);

		mPager.setOnPageChangeListener(this);
		initScrollableTabs(mView, mPager);

		// menu = (SatelliteMenu) mView.findViewById(R.id.sate_menu);

		return mView;
	}

	private void initScrollableTabs(View view, ViewPager mViewPager) {
		mScrollableTabView = (ScrollableTabView) view
				.findViewById(R.id.scrollabletabview);
		mScrollingTabsAdapter = new ScrollingTabsAdapter(mActivity);
		mScrollableTabView.setAdapter(mScrollingTabsAdapter);
		mScrollableTabView.setViewPage(mViewPager);
	}

	public ViewPager getViewPage() {
		return mPager;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		showLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((MainActivity) getActivity()).showLeft();
			}
		});

	
	}

	public boolean isFirst() {

		if (mPager.getCurrentItem() == 0)
			return true;
		else
			return false;
	}

	public boolean isEnd() {

		if (mPager.getCurrentItem() == pagerItemList.size() - 1)
			return true;
		else
			return false;
	}

	public class MyAdapter extends FragmentPagerAdapter {
		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getCount() {
			return pagerItemList.size();
		}

		@Override
		public Fragment getItem(int position) {

			Fragment fragment = null;
			
			if (position < pagerItemList.size()){ 
				fragment = pagerItemList.get(position);
			}else{
				fragment =pagerItemList.get(0);
			 }
			if(fragment==null){
				if(position ==0){
					fragment = new PageFragment1();
				}else if(position==1){
					fragment = new PageFragment2();
					
				}else if(position == 2){
					fragment = new PageFragment3();
				}
			}

			return fragment;

		}
	}

	private MyPageChangeListener myPageChangeListener;

	public void setMyPageChangeListener(MyPageChangeListener l) {

		myPageChangeListener = l;

	}

	public interface MyPageChangeListener {
		public void onPageSelected(int position);
		
	}


	@Override
	public void onPageScrollStateChanged(int position) {

	}

	@Override
	public void onPageScrolled(int position, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		
		if (myPageChangeListener != null) {
			myPageChangeListener.onPageSelected(position);

			mPager.setCurrentItem(position);
			page_position = position;
		}
		if (mScrollableTabView != null) {
			mScrollableTabView.selectTab(position);

			mPager.setCurrentItem(position);
			page_position = position;
		}

	}

	@Override
	public void onDestroyView() {
		if(MyBD.mLocationClient!=null){
			MyBD.mLocationClient=null;
		}
		
		Log.e(TAG, "onDestroyView");
		/*pagerItemList.clear();
		pagerItemList = null;
		mScrollableTabView = null;
		mScrollingTabsAdapter = null;
		mActivity = null;*/
		super.onDestroyView();
	}

	@Override
	public void onResume() {
		mPager.setCurrentItem(page_position);
		if(pagerItemList.size()<3){
			pagerItemList = new ArrayList<Fragment>();
			
			pageFragment1 = new PageFragment1();
			pageFragment2 = new PageFragment2();
			pageFragment3 = new PageFragment3();

			pagerItemList.add(pageFragment1);

			pagerItemList.add(pageFragment2);

			pagerItemList.add(pageFragment3);
		}
		super.onResume();
	}
	


}
