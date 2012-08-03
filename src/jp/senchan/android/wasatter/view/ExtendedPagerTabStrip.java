package jp.senchan.android.wasatter.view;

//source: http://blog.svpino.com/2011/08/disabling-pagingswiping-on-android.html

import android.content.Context;
import android.support.v4.view.PagerTabStrip;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ExtendedPagerTabStrip extends PagerTabStrip {

	private boolean enabled;

	public ExtendedPagerTabStrip(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.enabled = true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (this.enabled) {
			return super.onTouchEvent(event);
		}

		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (this.enabled) {
			return super.onInterceptTouchEvent(event);
		}

		return false;
	}

	public void setNavEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}