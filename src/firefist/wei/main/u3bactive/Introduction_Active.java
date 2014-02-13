package firefist.wei.main.u3bactive;

import firefist.wei.main.MainActivity;
import firefist.wei.main.R;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class Introduction_Active extends Activity {
	private Context mContext;
	private ActionBar actionBar;

	private LinearLayout layout1, layout2, layout3, layout4, layout5;

	@SuppressLint("NewApi")
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.u3b_introduce_active);

		mContext = this;

		actionBar = getActionBar();

		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setIcon(R.drawable.app_icon);
		actionBar.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.title_bar));
		actionBar.setTitle("ªÓ∂ØΩÈ…‹");

		initView();

	}

	private void initView() {
		layout1 = (LinearLayout) this
				.findViewById(R.id.u3b_introduce_active_layout1);
		layout2 = (LinearLayout) this
				.findViewById(R.id.u3b_introduce_active_layout2);
		layout3 = (LinearLayout) this
				.findViewById(R.id.u3b_introduce_active_layout3);
		layout4 = (LinearLayout) this
				.findViewById(R.id.u3b_introduce_active_layout4);
		layout5 = (LinearLayout) this
				.findViewById(R.id.u3b_introduce_active_layout5);

		layout1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, Home_PublishActive.class);

				intent.putExtra("action", "type_1");
				startActivity(intent);
				overridePendingTransition(R.anim.left_in,
						R.anim.left_out);

			}
		});
		layout2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, Home_PublishActive.class);

				intent.putExtra("action", "type_1");
				startActivity(intent);
				overridePendingTransition(R.anim.left_in,
						R.anim.left_out);

			}
		});
		layout3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, Home_PublishActive.class);

				intent.putExtra("action", "type_4");
				startActivity(intent);
				overridePendingTransition(R.anim.left_in,
						R.anim.left_out);

			}
		});
		layout4.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, Home_PublishActive.class);

				intent.putExtra("action", "type_2");
				startActivity(intent);
				overridePendingTransition(R.anim.left_in,
						R.anim.left_out);

			}
		});
		layout5.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, Home_PublishActive.class);

				intent.putExtra("action", "type_3");
				startActivity(intent);
				overridePendingTransition(R.anim.left_in,
						R.anim.left_out);

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			/*
			 * Intent upIntent = new Intent(this, MainActivity.class);
			 * upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 * startActivity(upIntent);
			 */
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getRepeatCount() == 0) {
			this.finish();
			/*
			 * Intent upIntent = new Intent(this, MainActivity.class);
			 * upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 * startActivity(upIntent);
			 */
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
		return super.dispatchKeyEvent(event);

	}
}
