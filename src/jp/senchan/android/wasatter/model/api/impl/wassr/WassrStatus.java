package jp.senchan.android.wasatter.model.api.impl.wassr;

import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.model.api.WasatterStatus;
import jp.senchan.android.wasatter.model.api.WasatterUser;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;

public class WassrStatus implements WasatterStatus {
	public static final String KEY_RID = "rid";
	public static final String KEY_LOGIN_ID = "user_login_id";
	public static final String KEY_USER = "user";
	public static final String KEY_EPOCH = "epoch";
	public static final String KEY_HTML = "html";
	
	private String mRid;
	private WassrUser mUser;
	private long mEpoch;
	private Spanned mBody;
	
	public WassrStatus(JSONObject json) throws JSONException {
		mRid = json.getString(KEY_RID);
		mUser = new WassrUser(json.getJSONObject(KEY_USER), json.getString(KEY_LOGIN_ID));
		mEpoch = json.getLong(KEY_EPOCH);
		mBody = Html.fromHtml(json.getString(KEY_HTML), new ImageGetter() {
			
			@Override
			public Drawable getDrawable(String source) {
				//TODO ネットワーク経由で絵文字取得、キャッシュあったらそれ使う
				return null;
			}
		}, null);
	}

 	@Override
	public String getServiceName() {
		return Wasatter.SERVICE_WASSR;
	}

	@Override
	public Spanned getBody() {
		return mBody;
	}

	@Override
	public String getStatusId() {
		return mRid;
	}

	@Override
	public WasatterUser getUser() {
		return mUser;
	}

	@Override
	public long getTime() {
		return mEpoch * 1000;
	}
}