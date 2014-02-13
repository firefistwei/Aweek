package firefist.wei.main.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import firefist.wei.main.MyConstants;
import firefist.wei.main.R;

public class FindFriend extends Activity{

	Button find_btn;
	TextView tv2;
	EditText ed;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.findfriend);

		init();
		
		
	}
	
	public void findfriend_back(View v){
		this.finish();
	}

	private void init() {
		
		find_btn  = (Button) this.findViewById(R.id.findfriend_btn);
		find_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
    
			}
		});
		
	}

	
}
