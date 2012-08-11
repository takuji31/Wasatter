package jp.senchan.android.wasatter.loader;

import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.model.api.TimelinePager;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class TimelineLoader extends AsyncTaskLoader<TimelinePager> {
	
	private TimelinePager mPager;

	public TimelineLoader(Wasatter app, TimelinePager pager) {
		super(app);
		mPager = pager;
	}

	@Override
	public TimelinePager loadInBackground() {
		mPager.loadNext();
		return mPager;
	}
	
	@Override
	public void deliverResult(TimelinePager data) {
		//FIXME 本当に必要だろうか？
		if (isReset()) {
			if (mPager != null) {
				mPager = null;
			}
			return;
		}

		if (isStarted()) {
			super.deliverResult(data);
		}
	}
	
	@Override
	protected void onStartLoading() {
		forceLoad();
	}
	
	@Override
	protected void onStopLoading() {
		super.onStopLoading();
		cancelLoad();
	}
	
}
