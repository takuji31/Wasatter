package jp.senchan.android.wasatter.client;

import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.auth.params.OAuthTwitter;
import android.net.Uri;
import android.text.TextUtils;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterClient {
	
	private TwitterFactory mFactory;
	private RequestToken mRequestToken;
	private AccessToken mToken;
	private Wasatter mApp;

	public TwitterClient(Wasatter app) {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(OAuthTwitter.CONSUMER_KEY);
		builder.setOAuthConsumerSecret(OAuthTwitter.CONSUMER_SECRET);
		mApp = app;
		mFactory = new TwitterFactory(builder.build());
		fetchToken();
	}
	
	public void fetchToken() {
		String token = mApp.getTwitterToken();
		String tokenSecret = mApp.getTwitterTokenSecret();
		if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(tokenSecret)) {
			mToken = new AccessToken(token, tokenSecret);
		}
	}
	
	public Twitter getClient() {
		fetchToken();
		return mFactory.getInstance(mToken);
	}
	

	public String getAuthorizationURL () throws TwitterException {
		mRequestToken = getClient().getOAuthRequestToken();
		return mRequestToken.getAuthorizationURL();
	}
	
	public AccessToken getAccessTokenFromURL (Uri uri) throws TwitterException {
		String verifier = uri.getQueryParameter("oauth_verifier");
		return getClient().getOAuthAccessToken(mRequestToken, verifier);
	}
}