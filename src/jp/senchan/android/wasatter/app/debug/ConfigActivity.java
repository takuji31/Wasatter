package jp.senchan.android.wasatter.app.debug;

import jp.senchan.android.wasatter2.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * 設定用のActivity、LintとかDeprecatedなメソッドとかの回避で色々ひどい
 * @author takuji
 *
 */
public class ConfigActivity extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.account);
	}
	
}
