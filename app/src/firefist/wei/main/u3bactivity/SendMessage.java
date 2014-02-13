package firefist.wei.main.u3bactivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import firefist.wei.main.R;

public class SendMessage extends Activity {

	private LinearLayout sendmessage_text_layout;
	private ImageView sendmessage_tovoice;
	private EditText sendmessage_conmment_content;
	private Button sendmessage_comment_send;

	private LinearLayout sendmessage_voice_layout;
	private ImageView sendmessage_totext;
	private Button sendmessage_start;

	private String uid;
	private String msg;

	Context mContext = null;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.u3b_sendmessage);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		ActionBar actionBar = getActionBar();
		
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		actionBar.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.title_bar));
		actionBar.setTitle("发送消息");
		actionBar.show();

		uid = getIntent().getStringExtra("uid");

		mContext = this;

		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		sendmessage_text_layout = (LinearLayout) findViewById(R.id.u3b_sendmessage_text_layout);
		sendmessage_tovoice = (ImageView) findViewById(R.id.u3b_sendmessage_tovoice);
		sendmessage_conmment_content = (EditText) findViewById(R.id.u3b_sendmessage_conmment_content);
		sendmessage_comment_send = (Button) findViewById(R.id.u3b_sendmessage_comment_send);

		sendmessage_voice_layout = (LinearLayout) findViewById(R.id.u3b_sendmessage_voice_layout);
		sendmessage_totext = (ImageView) findViewById(R.id.u3b_sendmessage_totext);
		sendmessage_start = (Button) findViewById(R.id.u3b_sendmessage_start);

	}

	private void setListener() {
		sendmessage_tovoice.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				sendmessage_text_layout.setVisibility(View.GONE);
				sendmessage_voice_layout.setVisibility(View.VISIBLE);

			}
		});

		sendmessage_totext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				sendmessage_text_layout.setVisibility(View.VISIBLE);
				sendmessage_voice_layout.setVisibility(View.GONE);
			}
		});
		
		sendmessage_comment_send.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		sendmessage_start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, "将在下一个版本推出，敬请期待", Toast.LENGTH_SHORT).show();
				
			}
		});

	}

	private void init() {
		// TODO Auto-generated method stub

	}

}
