package jp.senchan.android.wasatter.model.api;

import android.text.Spanned;

public interface WasatterStatus {
	public String getServiceName();
	public Spanned getBody();
	public String getStatusId();
	public WasatterUser getUser();
	public long getTime();
}
