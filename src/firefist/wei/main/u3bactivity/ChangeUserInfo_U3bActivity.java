package firefist.wei.main.u3bactivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import firefist.wei.main.MyConstants;
import firefist.wei.main.R;

public class ChangeUserInfo_U3bActivity extends Activity {

	Button btnok;
	TextView title;
	EditText edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.u3b_change_userinfo);

		findViewById();
		init();

	}
	public void myinfo_item_back(View v) {
		this.finish();
	}
	
	private void findViewById() {
		title = (TextView)findViewById(R.id.u3b_change_userinfo_title);
		edit = (EditText)findViewById(R.id.u3b_change_userinfo_ed);
		btnok = (Button)findViewById(R.id.u3b_change_userinfo_done);
	}

	private void init() {
		
		final String action = getIntent().getStringExtra("text_action");
		
		if(action.equals("name")){
			title.setText("修改昵称");
			edit.setText(MyConstants.User_Map.get("user_name"));
			
		}else if(action.equals("sig")){
			title.setText("修改签名");
			edit.setText(MyConstants.User_Map.get("signature"));
			
		}else if(action.equals("job")){
			title.setText("修改职业");
			edit.setText(MyConstants.User_Map.get("job"));
			
		}else if(action.equals("school")){
			title.setText("修改学校");
			edit.setText(MyConstants.User_Map.get("school"));
			
		}else if(action.equals("goodat")){
			title.setText("修改专长");
			edit.setText(MyConstants.User_Map.get("hobby"));
			
		}else if(action.equals("introduction")){
			title.setText("修改个人说明");
			edit.setText(MyConstants.User_Map.get("present"));
		}

		btnok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(action.equals("name")){
					MyConstants.User_Map.put("user_name",edit.getText().toString().trim()) ;
				}else if(action.equals("sig")){
					MyConstants.User_Map.put("signature",edit.getText().toString().trim()) ;
				}else if(action.equals("job")){
					MyConstants.User_Map.put("job",edit.getText().toString().trim()) ;
				}else if(action.equals("school")){
					MyConstants.User_Map.put("school",edit.getText().toString().trim()) ;
				}else if(action.equals("goodat")){
					MyConstants.User_Map.put("hobby",edit.getText().toString().trim()) ;
				}else if(action.equals("introduction")){
					MyConstants.User_Map.put("present",edit.getText().toString().trim()) ;
				}
				finish();
			}
		});
		
		
	}

}
