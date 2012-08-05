package jp.senchan.android.wasatter.model.api;

import java.io.Serializable;

public interface WasatterUser extends Serializable {
	public String getName();
	public String getScreenName();
	public String getProfileImageUrl();
	public boolean isProtected();
}
