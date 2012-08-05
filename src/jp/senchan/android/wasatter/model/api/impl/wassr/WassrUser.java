package jp.senchan.android.wasatter.model.api.impl.wassr;

import jp.senchan.android.wasatter.model.api.WasatterUser;

import org.json.JSONException;
import org.json.JSONObject;

public class WassrUser implements WasatterUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//XXX WasssrはTwitterのnameがsceent_nameになってる矛盾仕様
	public static final String KEY_NAME = "screen_name";
	public static final String KEY_PROFILE_IMAGE = "profile_image_url";
	public static final String KEY_PROTECTED = "protected";
	
	private String mName;
	private String mScreenName;
	private String mProfileImageUrl;
	public boolean mProtected;
	
	public WassrUser(JSONObject object, String loginId) throws JSONException {
		mScreenName = loginId;
		mName = object.getString(KEY_NAME);
		mProfileImageUrl = object.getString(KEY_PROFILE_IMAGE);
		mProtected = object.getBoolean(KEY_PROTECTED);
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public String getScreenName() {
		return mScreenName;
	}

	@Override
	public String getProfileImageUrl() {
		return mProfileImageUrl;
	}

	@Override
	public boolean isProtected() {
		return mProtected;
	}
	
}
