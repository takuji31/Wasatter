package jp.senchan.android.wasatter.next.model.api;

import org.json.JSONException;
import org.json.JSONObject;

public class WassrUser {

	public static final String KEY_NAME = "screen_name";
	public static final String KEY_PROFILE_IMAGE = "profile_image_url";
	public static final String KEY_PROTECTED = "protected";
	
	public String screenName;
	public String id;
	public String profileImageUrl;
	public boolean isProtected;
	
	public WassrUser(JSONObject object, String loginId) throws JSONException {
		id = loginId;
		screenName = object.getString(KEY_NAME);
		profileImageUrl = object.getString(KEY_PROFILE_IMAGE);
		isProtected = object.getBoolean(KEY_PROTECTED);
	}
}
