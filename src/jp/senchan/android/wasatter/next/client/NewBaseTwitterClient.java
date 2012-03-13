package jp.senchan.android.wasatter.next.client;

import jp.senchan.android.wasatter.auth.params.OAuthTwitter;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class NewBaseTwitterClient {
	
	protected Configuration conf;

	public NewBaseTwitterClient() {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(OAuthTwitter.CONSUMER_KEY);
		builder.setOAuthConsumerSecret(OAuthTwitter.CONSUMER_SECRET);
		conf = builder.build();
	}
	
	public Twitter getClient() {
		return new TwitterFactory(conf).getInstance();
	}
	
	public Twitter getClient(AccessToken token) {
		return new TwitterFactory(conf).getInstance(token);
	}
}
