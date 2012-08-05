package jp.senchan.android.wasatter.model.api;

import jp.senchan.android.wasatter.Wasatter;
import twitter4j.Status;

public class TwitterStatus extends WasatterStatus {
	
	private Status mStatus;
	
	public TwitterStatus(Status status) {
		mStatus = status;
	}

	@Override
	public String name() {
		return mStatus.getUser().getName();
	}

	@Override
	public String screenName() {
		return mStatus.getUser().getScreenName();
	}

	@Override
	public String serviceName() {
		return Wasatter.SERVICE_TWITTER;
	}

}
