package jp.senchan.android.wasatter.client;

public class TwitterUrl {
	public static final String FRIEND_TIMELINE = "http://api.twitter.com/statuses/home_timeline.json";
	public static final String REPLY = "http://api.twitter.com/1/statuses/mentions.json";
	public static final String MYPOST = "http://api.twitter.com/1/statuses/user_timeline.json";
	public static final String UPDATE_TIMELINE = "http://api.twitter.com/1/statuses/update.json";
	public static final String RETWEET = "http://api.twitter.com/1/statuses/retweet.json";
	public static final String FAVORITE_ADD = "http://api.twitter.com/1/favorites/create/[rid].json";
	public static final String FAVORITE_DEL = "http://api.twitter.com/1/favorites/destroy/[rid].json";
	public static final String PERMA_LINK = "http://twitter.com/[id]/status/[rid]";


}
