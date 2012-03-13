package jp.senchan.android.wasatter.next.client;

import android.net.Uri;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;

public class NewTwitterOAuthClient  extends NewBaseTwitterClient {
	
	private RequestToken mRequestToken;
	private OAuthAuthorization mOAuth;

	public NewTwitterOAuthClient() {
		super();
		mOAuth = new OAuthAuthorization(conf);
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
