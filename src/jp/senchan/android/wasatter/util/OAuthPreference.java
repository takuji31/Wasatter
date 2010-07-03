/**
 *
 */
package jp.senchan.android.wasatter.util;

import jp.senchan.android.wasatter.R;
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

/**
 * @author Senka/Takuji
 *
 */
public class OAuthPreference extends Preference {

	public OAuthPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public OAuthPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public OAuthPreference(Context context) {
		// TODO 自動生成されたコンストラクター・スタブ
		super(context);
		this.setTitle(R.string.title_setting_twitter_oauth_token_get);
		this.setSummary(R.string.summary_setting_twitter_oauth_token_get);
	}
}
