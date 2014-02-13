package firefist.wei.main.activity;

import com.nostra13.universalimageloader.core.ImageLoader;

import firefist.wei.main.R;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;


public abstract class BaseActivity extends Activity {

	public ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.item_clear_memory_cache:
				imageLoader.clearMemoryCache();
				return true;
			case R.id.item_clear_disc_cache:
				imageLoader.clearDiscCache();
				return true;
			default:
				return false;
		}
	}
}
