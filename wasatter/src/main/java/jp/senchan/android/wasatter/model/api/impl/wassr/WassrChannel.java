package jp.senchan.android.wasatter.model.api.impl.wassr;

import org.json.JSONException;
import org.json.JSONObject;

import jp.senchan.android.wasatter.model.api.WasatterStatus;
import jp.senchan.android.wasatter.model.api.WasatterUser;

/**
 * Wassrのチャンネルクラス
 * インターフェイスこれでいいのか怪しいけどメソッドの共通化のために実装してる
 * @author takuji
 *
 */
public class WassrChannel implements WasatterStatus {

	private static final long serialVersionUID = 1L;
	public String mName;
	public String mNameEn;
	public String mImageUrl;
	public long mLastMessagedAt;
	
	public WassrChannel(JSONObject json) throws JSONException {
		mName = json.getString("title");
		mNameEn = json.getString("name_en");
		mImageUrl = json.getString("image_url");
		mLastMessagedAt = json.getLong("last_messaged_at");
	}
	
	@Override
	public String getServiceName() {
		return null;
	}

	@Override
	public String getBody() {
		return mName;
	}

	@Override
	public String getStatusId() {
		return mNameEn;
	}
	
	public String getImageUrl() {
		return mImageUrl;
	}

	@Override
	public WasatterUser getUser() {
		return null;
	}

	@Override
	public long getTime() {
		return mLastMessagedAt * 1000;
	}

	@Override
	public boolean isRetweet() {
		return false;
	}

	@Override
	public WasatterUser getRetweetUser() {
		return null;
	}

}
