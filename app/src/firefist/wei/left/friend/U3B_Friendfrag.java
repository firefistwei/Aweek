package firefist.wei.left.friend;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import firefist.wei.lib.pla.MultiColumnListView;
import firefist.wei.lib.pla.PLA_AdapterView;
import firefist.wei.lib.pla.PLA_AdapterView.OnItemClickListener;
import firefist.wei.main.R;


public class U3B_Friendfrag extends Fragment {

	private Context mContext;

	private PLA_AdapterView<BaseAdapter> mAdapterView = null;
	private MyAdapter mAdapter = null;
	private ArrayList<HashMap<String, String>> mapList = null;

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.friend_friendfrag, container,
				false);

		MultiColumnListView.changeColumn(3);
		mAdapterView = (PLA_AdapterView<BaseAdapter>) rootView
				.findViewById(R.id.friend_friendfrag_listview);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mContext = this.getActivity();
		
		
		initView();

	}


	private void initView() {
		mapList = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < 31; i++)
			mapList.add(new HashMap<String, String>());

		mAdapter = new MyAdapter(mContext, mapList);
		mAdapterView.setAdapter(mAdapter);
		
		mAdapterView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(PLA_AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
			}
			
		});

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
						R.layout.friend_friendfrag_item, null);
				ViewHolder holder = new ViewHolder();
				holder.main_layout = (LinearLayout) convertView
						.findViewById(R.id.friend_friendfrag_item_layout);

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


}
