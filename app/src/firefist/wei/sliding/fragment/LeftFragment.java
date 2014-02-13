/*
 * Copyright (C) 2012 yueyueniao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package firefist.wei.sliding.fragment;

import java.util.ArrayList;
import java.util.List;

import firefist.wei.left.friend.U3B_Friendfrag;
import firefist.wei.left.friend.U3B_HomeFriend;
import firefist.wei.left.message.U3B_HomeMessage;
import firefist.wei.main.MainActivity;
import firefist.wei.main.R;
import firefist.wei.main.activity.Set_About;
import firefist.wei.sliding.utils.IChangeFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LeftFragment extends Fragment implements OnItemClickListener {

	private static final String TAG = "LeftFragment";

	private ListView mListView;

	private MyAdapter myAdapter;
	private List<String> data = new ArrayList<String>();
	private FragmentManager mFragmentManager;
	
	public LeftFragment(){
		
	}

	public LeftFragment(FragmentManager fragmentManager) {
		mFragmentManager = fragmentManager;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		
		View view = inflater.inflate(R.layout.left, null);
		
		mListView = (ListView) view.findViewById(R.id.left_listview);

		data.add("主 页");
		data.add("消 息");
		data.add("好友");
		data.add("设 置");

		myAdapter = new MyAdapter(data);
		mListView.setAdapter(myAdapter);
		mListView.setOnItemClickListener(this);
		myAdapter.setSelectPosition(0);
		

		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);


	}


	private class MyAdapter extends BaseAdapter {

		private List<String> data;

		private int selectPosition;

		MyAdapter(List<String> list) {
			this.data = list;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void setSelectPosition(int position) {
			selectPosition = position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = LayoutInflater.from(getActivity()).inflate(
					R.layout.left_list, null);
			TextView textView = (TextView) row
					.findViewById(R.id.left_list_text);
			textView.setText(data.get(position));
			ImageView img = (ImageView) row.findViewById(R.id.left_list_image);
			switch (position) {
			case 0:
				img.setBackgroundResource(R.drawable.ic_nav_home);
				break;
			case 1:
				img.setBackgroundResource(R.drawable.ic_nav_messenger);
				break;
			case 2:
				img.setBackgroundResource(R.drawable.ic_nav_profile);
				break;
			case 3:
				img.setBackgroundResource(R.drawable.ic_nav_circles);
				break;
			
			default:
				break;
			}
			return row;
		}

	}

	private IChangeFragment iChangeFragment;

	public void setChangeFragmentListener(IChangeFragment listener) {
		iChangeFragment = listener;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = null;
		switch (position) {
		
		case 0: // 主页
			((MainActivity) getActivity()).showLeft();
			break;
		case 1: // 消息
			intent = new Intent(getActivity(),
					U3B_HomeMessage.class);
			startActivity(intent);
			break;
		case 2: // 好友
			intent = new Intent(getActivity(),
					U3B_HomeFriend.class);
			startActivity(intent);
			break;
		case 3: // 设置
			intent = new Intent(getActivity(),
					Set_About.class);
			startActivity(intent);
			break;
		/*case 1: // 语音&视频
			Intent intent1 = new Intent(getActivity(),
					Voice_Video_Activity.class);
			startActivity(intent1);
			// Toast.makeText(getApplicationContext(), "你点了 个人活动 ",
			// 1000).show();
			break;*/
/*		case 1: // U2B活动
			Intent intent2 = new Intent(getActivity(),
					Nearby_ViewPager_Activity.class);
			startActivity(intent2);
			// Toast.makeText(getApplicationContext(), "你点了 公共活动 ",
			// 1000).show();
			break;
		case 4: // Have Fun
			Intent intent5 = new Intent(getActivity(), Fun_Game.class);
			startActivity(intent5);
			break;
		case 5: // 设置
			Intent intent6 = new Intent(getActivity(), Set_Activity.class);
			startActivity(intent6);
			break;
		case 6: // 关于
			Intent intent7 = new Intent(getActivity(), Set_About.class);
			startActivity(intent7);
			break;*/

		}
		// View childView = null;
		// View text = null;
		// int length = mListView.getChildCount();
		// for(int i = 0,pos = 0; i <length; i++,pos++){
		// childView = mListView.getChildAt(i);
		// text = childView.findViewById(R.id.left_list_text);
		// if(pos == position){
		// text.setSelected(true);
		// }
		// }

		// if(iChangeFragment != null){
		// iChangeFragment.changeFragment(position);
		// }

		myAdapter.setSelectPosition(position);
		myAdapter.notifyDataSetChanged();

		// FragmentTransaction t = mFragmentManager
		// .beginTransaction();
		// LocalFragment leftFragment = new LocalFragment();
		// t.replace(R.id.center_frame, leftFragment);
		// t.commit();
	}

}
