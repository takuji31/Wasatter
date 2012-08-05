package jp.senchan.android.wasatter.client;

import java.util.ArrayList;

import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.auth.params.OAuthTwitter;
import jp.senchan.android.wasatter.model.api.WasatterStatus;
import jp.senchan.android.wasatter.model.api.impl.twitter.TwitterStatus;
import jp.senchan.android.wasatter.next.listener.APICallback;
import android.text.TextUtils;
import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
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
	
	public AsyncTwitter friendTimeline(int page, final APICallback<ArrayList<WasatterStatus>> callback) {
		AsyncTwitter client = getClient();
		client.addListener(new TwitterAdapter(){
			@Override
			public void gotHomeTimeline(ResponseList<Status> statuses) {
				ArrayList<WasatterStatus> result = new ArrayList<WasatterStatus>();
				for (Status status : statuses) {
					result.add(new TwitterStatus(status));
				}
				callback.callback(null, result, 200);
			}
			
			@Override
			public void onException(TwitterException ex, TwitterMethod method) {
				callback.callback(null, new ArrayList<WasatterStatus>(), ex.getStatusCode());
			}
		});
		Paging paging = new Paging(page);
		client.getHomeTimeline(paging);
		return client;
	}
	
}
