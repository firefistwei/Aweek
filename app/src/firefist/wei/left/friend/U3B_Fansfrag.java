package firefist.wei.left.friend;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import firefist.wei.lib.pla.MultiColumnListView;
import firefist.wei.lib.pla.PLA_AdapterView;
import firefist.wei.main.MyConstants;
import firefist.wei.main.R;
import firefist.wei.main.service.MyUploadService;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;

public class U3B_Fansfrag extends Fragment{
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
		

	}
	
	@Override
	public void onResume() {
		super.onResume();
		initView();

	}

	private void initView() {
		mapList = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < 31; i++)
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
