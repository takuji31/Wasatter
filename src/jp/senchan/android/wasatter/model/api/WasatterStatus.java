package jp.senchan.android.wasatter.model.api;

import java.io.Serializable;

import android.text.Spanned;

public interface WasatterStatus extends Serializable {
	public String getServiceName();
	public Spanned getBody();
	public String getStatusId();
	public WasatterUser getUser();
	public long getTime();
	public boolean isRetweet();
	public WasatterUser getRetweetUser();
}
