package firefist.wei.left;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import firefist.wei.main.MyBD;
import firefist.wei.main.MyConstants;
import firefist.wei.main.R;

public class Fun_Game extends Activity {

	Context mContext = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.left_fun_game_main);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		mContext = this;
		/*
		 * headView = LayoutInflater.from(this).inflate(R.layout.ranklist_head,
		 * null);
		 */
		


	}

	public void left_fun_game_back(View v) {
		this.finish();
	}

}
