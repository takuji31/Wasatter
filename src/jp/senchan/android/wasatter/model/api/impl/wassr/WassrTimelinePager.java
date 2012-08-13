package jp.senchan.android.wasatter.model.api.impl.wassr;

import java.util.ArrayList;

import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.app.fragment.TimelineFragment;
import jp.senchan.android.wasatter.client.WassrClient;
import jp.senchan.android.wasatter.model.api.TimelinePager;
import jp.senchan.android.wasatter.model.api.WasatterStatus;

public class WassrTimelinePager extends TimelinePager {
	
	private static final long serialVersionUID = 1L;
	
	private int mNextPage = 1;
	private WassrClient mClient;
	
	public WassrTimelinePager() {
	}
	
	public WassrTimelinePager(Wasatter app,int mode) {
		super();
		mClient = new WassrClient(app);
		mMode = mode;
	}

	@Override
	public void loadNext() {
		//TODO 重複削除とかやっておきたい
		ArrayList<WasatterStatus> result = null;
		switch (mMode) {
			case TimelineFragment.MODE_TIMELINE:
				result = mClient.getFriendTimeline(mNextPage);
			break;

			default:
				
			break;
		}
		if (result != null) {
			mNextPage++;
			addAll(result);
			setLastResultCode(SUCCESS);
		} else {
			setLastResultCode(FAILURE);
		}
	}

}
