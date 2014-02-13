package firefist.wei.sliding.adapter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import firefist.wei.main.MainActivity;
import firefist.wei.main.R;
import firefist.wei.sliding.fragment.MainFragment;
import firefist.wei.sliding.view.TabAdapter;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class ScrollingTabsAdapter implements TabAdapter {

	private final Activity activity;

	public ScrollingTabsAdapter(Activity act) {
		activity = act;
		
		
	}

	public View getView(int position) {
		if(activity==null)
			return null;
		LayoutInflater inflater = activity.getLayoutInflater();
		Button tab = (Button) inflater.inflate(R.layout.tabs, null);
		
		
		tab.setWidth(MainActivity.mScreenWidth/3);
		
		final String[] mTitles = activity.getResources().getStringArray(
				R.array.tab_news_titles);
		Set<String> tab_sets = new HashSet<String>(Arrays.asList(mTitles));
		String[] tabs_new = new String[tab_sets.size()];
		int cnt = 0;
		for (int i = 0; i < mTitles.length; i++) {
			if (tab_sets.contains(mTitles[i])) {
				tabs_new[cnt] = mTitles[i];
				cnt++;
			}
		}
		if (position < tabs_new.length)
			tab.setText(tabs_new[position].toUpperCase());
		return tab;
	}

}
