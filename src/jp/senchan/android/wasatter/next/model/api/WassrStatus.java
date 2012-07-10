package jp.senchan.android.wasatter.next.model.api;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spannable;
import android.text.Spanned;

public class WassrStatus {
	public static final String KEY_RID = "rid";
	public static final String KEY_LOGIN_ID = "user_login_id";
	public static final String KEY_USER = "user";
	public static final String KEY_EPOCH = "epoch";
	public static final String KEY_HTML = "html";
	
	public String rid;
	public WassrUser user;
	public long epoch;
	public Spanned body;
	
	public WassrStatus(JSONObject json) throws JSONException {
		rid = json.getString(KEY_RID);
		user = new WassrUser(json.getJSONObject(KEY_USER), json.getString(KEY_LOGIN_ID));
		epoch = json.getLong(KEY_EPOCH);
		body = Html.fromHtml(json.getString(KEY_HTML), new ImageGetter() {
			
			@Override
			public Drawable getDrawable(String source) {
				//TODO ネットワーク経由で絵文字取得、キャッシュあったらそれ使う
				return null;
			}
		}, null);
	}
}
