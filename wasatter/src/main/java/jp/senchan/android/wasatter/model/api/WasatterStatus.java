package jp.senchan.android.wasatter.model.api;

import java.io.Serializable;

public interface WasatterStatus extends Serializable {
	public String getServiceName();
	public String getBody();
	public String getStatusId();
	public WasatterUser getUser();
	public long getTime();
	public boolean isRetweet();
	public WasatterUser getRetweetUser();
}
