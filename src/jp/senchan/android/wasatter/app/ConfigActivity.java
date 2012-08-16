package jp.senchan.android.wasatter.app;

import java.util.List;

import jp.senchan.android.wasatter2.R;
import android.annotation.SuppressLint;
import android.os.Build;
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
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			//TODO PreferenceActivity
			addPreferencesFromResource(R.xml.account);
		}
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onBuildHeaders(List<Header> target) {
		super.onBuildHeaders(target);
		loadHeadersFromResource(R.xml.preference_header, target);
	}
}
