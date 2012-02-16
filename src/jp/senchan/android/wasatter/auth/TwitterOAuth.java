package jp.senchan.android.wasatter.auth;

import twitter4j.http.OAuthAuthorization;
import twitter4j.http.RequestToken;

public class TwitterOAuth {

	public static final String OAUTH_CALLBACK_URL = "wasatter://callback.twitter/";
	public static OAuthAuthorization auth;
	public static RequestToken       req;

}
