package jp.senchan.android.wasatter.model.api.impl.twitter;

import jp.senchan.android.wasatter.model.api.WasatterUser;

import twitter4j.User;

public class TwitterUser implements WasatterUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public User mUser;

	public TwitterUser(User user) {
		mUser = user;
	}

	@Override
	public String getName() {
		return mUser.getName();
	}

	@Override
	public String getScreenName() {
		return mUser.getScreenName();
	}

	@Override
	public String getProfileImageUrl() {
		return mUser.getProfileImageURL().toString();
	}

	@Override
	public boolean isProtected() {
		return mUser.isProtected();
	}

}
