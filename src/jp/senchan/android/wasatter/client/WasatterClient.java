package jp.senchan.android.wasatter.client;

import jp.senchan.android.wasatter.model.api.TimelinePager;

public interface WasatterClient {
	public TimelinePager getHomeTimeline(int page);
	public TimelinePager getReply(int page);
	public TimelinePager getMyPost(int page);
	public TimelinePager getOdai(int page);
	public TimelinePager getChannelList(int page);
	public TimelinePager getChannelTimeline(int page, String channel);
	
	//TODO 要検討
	public TimelinePager getDirectMessage(int page);
	public TimelinePager getProfile();
}
