package jp.senchan.android.wasatter.model.api.impl.twitter;

import java.util.ArrayList;

import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.app.fragment.TimelineFragment;
import jp.senchan.android.wasatter.client.TwitterClient;
import jp.senchan.android.wasatter.model.api.TimelinePager;
import jp.senchan.android.wasatter.model.api.WasatterStatus;

public class TwitterTimelinePager extends TimelinePager {

	private static final long serialVersionUID = 1L;
	
	private TwitterClient mClient;
	private long mMaxId = 0;
	
	public TwitterTimelinePager() {
	}
	
	public TwitterTimelinePager(Wasatter app,int mode) {
		mClient = new TwitterClient(app);
		mMode = mode;
	}

	@Override
	public void loadNext() {
		ArrayList<WasatterStatus> result = null;
		
		switch (mMode) {
			case TimelineFragment.MODE_TIMELINE:
				result = mClient.getHomeTimeline(mMaxId);
			break;
			case TimelineFragment.MODE_MENSION:
				result = mClient.getMension(mMaxId);
			break;
		}
		
		if (result != null) {
			addAll(result);
			mMaxId = Long.parseLong(result.get(result.size() - 1).getStatusId());
			setLastResultCode(SUCCESS);
		} else {
			setLastResultCode(FAILURE);
		}
	}

}
