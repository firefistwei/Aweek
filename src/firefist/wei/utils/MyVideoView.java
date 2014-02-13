package firefist.wei.utils;

import firefist.wei.main.MainActivity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class MyVideoView extends VideoView {

	public MyVideoView(Context context) {
		super(context);
	}

	public MyVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(MainActivity.mScreenWidth-10, widthMeasureSpec);
		int height = getDefaultSize(MainActivity.mScreenWidth-10, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

}
