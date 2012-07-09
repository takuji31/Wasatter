package jp.senchan.android.wasatter.next.model.api;

import org.json.JSONException;
import org.json.JSONObject;

public class WassrStatus {
	public static final String KEY_RID = "rid";
	public static final String KEY_LOGIN_ID = "user_login_id";
	public static final String KEY_USER = "user";
	
	public String rid;
	public WassrUser user;
	
	public WassrStatus(JSONObject json) throws JSONException {
		rid = json.getString(KEY_RID);
		user = new WassrUser(json.getJSONObject(KEY_USER), json.getString(KEY_LOGIN_ID));
		
	}
}
