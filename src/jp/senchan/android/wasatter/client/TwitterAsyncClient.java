package jp.senchan.android.wasatter.client;

import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.auth.params.OAuthTwitter;
import android.text.TextUtils;
import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterAsyncClient {
	
	private AsyncTwitterFactory mFactory;
	private AccessToken mToken;
	private Wasatter mApp;

	public TwitterAsyncClient(Wasatter app) {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(OAuthTwitter.CONSUMER_KEY);
		builder.setOAuthConsumerSecret(OAuthTwitter.CONSUMER_SECRET);
		Configuration conf = builder.build();
		mApp = app;
		mFactory = new AsyncTwitterFactory(conf);
		fetchToken();
	}
	
	public void fetchToken() {
		String token = mApp.getTwitterToken();
		String tokenSecret = mApp.getTwitterTokenSecret();
		if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(tokenSecret)) {
			mToken = new AccessToken(token, tokenSecret);
		}
	}
	
	public AsyncTwitter getClient() {
		fetchToken();
		return mFactory.getInstance(mToken);
	}
	
	public void shutdown() {
		getClient().shutdown();
	}
	
}
