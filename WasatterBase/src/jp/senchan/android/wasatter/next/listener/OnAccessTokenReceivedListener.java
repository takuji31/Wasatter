package jp.senchan.android.wasatter.next.listener;

import twitter4j.auth.AccessToken;

public interface OnAccessTokenReceivedListener {
	public void onAccessTokenReceived(AccessToken token);
	public void onAccessTokenReceiveFailure();
}
