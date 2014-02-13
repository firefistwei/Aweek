package firefist.wei.left.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import firefist.wei.main.Exit;
import firefist.wei.main.MainActivity;
import firefist.wei.main.R;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Toast;
import firefist.wei.lib.pla.*;

public class U3B_HomeMessage extends FragmentActivity {

	private Context mContext;

	private PLA_AdapterView<BaseAdapter> mAdapterView = null;
	private MyAdapter mAdapter = null;
	private ArrayList<HashMap<String, String>> mapList = null;

	@SuppressLint("NewApi")
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.u3b_home_message);
		mContext = this;

		final ActionBar actionBar = getActionBar();

		//actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setTitle("我的消息");

		MultiColumnListView.changeColumn(2);
		// MultiColumnListView.setPadding(15);
		mAdapterView = (PLA_AdapterView<BaseAdapter>) findViewById(R.id.home_message_listview);
		// mAdapterView.setRight(10);
		// mAdapterView.setLeft(10);
		// mAdapterView.setTop(10);
		// mAdapterView.setBottom(10);
		
		Toast.makeText(mContext, "该功能暂未完成，敬请关注下一版本!", Toast.LENGTH_LONG).show();

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
	protected void onResume() {
		super.onResume();
		initView();

	}

	private void initView() {
		mapList = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < 11; i++)
			mapList.add(new HashMap<String, String>());

		mAdapter = new MyAdapter(mContext, mapList);
		mAdapterView.setAdapter(mAdapter);

	}

	private class MyAdapter extends BaseAdapter {

		private Context context;
		private ArrayList<HashMap<String, String>> mapList;
		private HashMap<String, String> map;

		public MyAdapter(Context context,
				ArrayList<HashMap<String, String>> mapList) {
			this.context = context;
			this.mapList = mapList;
		}

		@Override
		public int getCount() {
			return mapList.size();
		}

		@Override
		public Object getItem(int pos) {
			return mapList.get(pos);
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.u3b_message_item, null);
				ViewHolder holder = new ViewHolder();
				holder.main_layout = (LinearLayout) convertView
						.findViewById(R.id.u3b_message_item_layout);

				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

				params.bottomMargin = 8;
				params.leftMargin = 4;
				params.rightMargin = 4;

				holder.main_layout.setLayoutParams(params);
				// mAdapterView.getChildAt(0).
			}

			return convertView;
		}

		class ViewHolder {
			LinearLayout main_layout;
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
