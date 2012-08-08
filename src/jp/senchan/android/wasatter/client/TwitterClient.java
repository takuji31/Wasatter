package jp.senchan.android.wasatter.client;

import jp.senchan.android.wasatter.auth.params.OAuthTwitter;
import android.net.Uri;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterClient {
	
	private RequestToken mRequestToken;
	private OAuthAuthorization mOAuth;
	protected Configuration conf;

	public TwitterClient() {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(OAuthTwitter.CONSUMER_KEY);
		builder.setOAuthConsumerSecret(OAuthTwitter.CONSUMER_SECRET);
		conf = builder.build();
		mOAuth = new OAuthAuthorization(conf);
	}
	
	public Twitter getClient() {
		return new TwitterFactory(conf).getInstance();
	}
	
	public Twitter getClient(AccessToken token) {
		return new TwitterFactory(conf).getInstance(token);
	}

	public String getAuthorizationURL () throws TwitterException {
		mRequestToken = mOAuth.getOAuthRequestToken();
		return mRequestToken.getAuthorizationURL();
	}
	
	public AccessToken getAccessTokenFromURL (Uri uri) throws TwitterException {
		String verifier = uri.getQueryParameter("oauth_verifier");
		return mOAuth.getOAuthAccessToken(mRequestToken, verifier);
	}
}
