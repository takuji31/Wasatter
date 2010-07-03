package jp.senchan.android.wasatter.client;

public class WassrUrl {
	public static final String FRIEND_TIMELINE = "http://api.wassr.jp/statuses/friends_timeline.json";
	public static final String CHANNEL_TIMELINE = "http://api.wassr.jp/channel_message/list.json?name_en=[name]";
	public static final String CHANNEL_PERMA_LINK = "http://wassr.jp/channel/[name]/messages/[rid]";
	public static final String REPLY = "http://api.wassr.jp/statuses/replies.json";
	public static final String MYPOST = "http://api.wassr.jp/statuses/user_timeline.json";
	public static final String ODAI = "http://api.wassr.jp/statuses/user_timeline.json?id=odai";
	public static final String TODO = "http://api.wassr.jp/todo/list.json";
	public static final String CHANNEL_LIST = "http://api.wassr.jp/channel_user/user_list.json";
	public static final String TODO_STATUS = "http://api.wassr.jp/todo/";
	public static final String UPDATE_TIMELINE = "http://api.wassr.jp/statuses/update.json";
	public static final String UPDATE_CHANNEL = "http://api.wassr.jp/channel_message/update.json?name_en=[channel]";
	public static final String FAVORITE_ADD = "http://api.wassr.jp/favorites/create/[rid].json";
	public static final String FAVORITE_DEL = "http://api.wassr.jp/favorites/destroy/[rid].json";
	public static final String FAVORITE_CHANNEL = "http://api.wassr.jp/channel_favorite/toggle.json?channel_message_rid=[rid]";
	public static final String FAVORITE_ICON = "http://wassr.jp/user/[user]/profile_img.png.16";

}
