package jp.senchan.android.wasatter.model.api.impl.twitter;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.model.api.WasatterStatus;
import jp.senchan.android.wasatter.model.api.WasatterUser;
import twitter4j.Status;

public class TwitterStatus implements WasatterStatus {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Status mStatus;
	private Status mRetweetStatus;
	private TwitterUser mUser;
	private TwitterUser mRetweetUser;
	
	public TwitterStatus(Status status) {
		if (status.isRetweet()) {
			mRetweetStatus = status;
			mStatus = status.getRetweetedStatus();
			mRetweetUser = new TwitterUser(mRetweetStatus.getUser());
		} else {
			mStatus = status;
		}
		mUser = new TwitterUser(mStatus.getUser());
	}

	@Override
	public String getServiceName() {
		return Wasatter.SERVICE_TWITTER;
	}

	@Override
	public Spanned getBody() {
		return new SpannableStringBuilder(mStatus.getText());
	}

	@Override
	public String getStatusId() {
		return String.valueOf(mStatus.getId());
	}

	@Override
	public WasatterUser getUser() {
		return mUser;
	}

	@Override
	public long getTime() {
		return mStatus.getCreatedAt().getTime();
	}

	@Override
	public boolean isRetweet() {
		return mRetweetStatus != null;
	}

	@Override
	public WasatterUser getRetweetUser() {
		return mRetweetUser;
	}

}
