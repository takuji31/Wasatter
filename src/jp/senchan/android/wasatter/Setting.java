/**
 *
 */
package jp.senchan.android.wasatter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;

/**
 * アカウント設定
 *
 * @author Senka/Takuji
 *
 */
public class Setting extends PreferenceActivity {
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		PreferenceScreen pc2 = (PreferenceScreen) this.getPreferenceScreen()
				.getPreference(2);
		Preference pf2 = new Preference(Wasatter.CONTEXT);
		pf2.setTitle("キャッシュの消去");
		pf2.setSummary("画像のキャッシュを削除します。");
		pf2.setOnPreferenceClickListener(new CacheClearListener());
		pc2.addPreference(pf2);
	}

	// アカウント設定
	public static boolean isWassrEnabled() {
		Context con = Wasatter.CONTEXT;
		return PreferenceManager.getDefaultSharedPreferences(con).getBoolean(
				"enable_wassr", false);
	}

	public static boolean isTwitterEnabled() {
		Context con = Wasatter.CONTEXT;
		return PreferenceManager.getDefaultSharedPreferences(con).getBoolean(
				"enable_twitter", false);
	}

	public static boolean isDisplayBodyMultiLine() {
		Context con = Wasatter.CONTEXT;
		return PreferenceManager.getDefaultSharedPreferences(con).getBoolean(
				"display_body_multi_line", false);
	}

	public static String getWassrId() {
		Context con = Wasatter.CONTEXT;
		return PreferenceManager.getDefaultSharedPreferences(con).getString(
				"wassr_id", "");
	}

	public static String getWassrPass() {
		Context con = Wasatter.CONTEXT;
		return PreferenceManager.getDefaultSharedPreferences(con).getString(
				"wassr_pass", "");
	}

	public static boolean isTwitterOAuthEnable() {
		Context con = Wasatter.CONTEXT;
		return PreferenceManager.getDefaultSharedPreferences(con).getBoolean(
				"twitter_oauth_enable", false);
	}

	public static String getTwitterId() {
		Context con = Wasatter.CONTEXT;
		return PreferenceManager.getDefaultSharedPreferences(con).getString(
				"twitter_id", "");
	}

	public static String getTwitterPass() {
		Context con = Wasatter.CONTEXT;
		return PreferenceManager.getDefaultSharedPreferences(con).getString(
				"twitter_pass", "");
	}

	public static boolean setTwitterToken(String token) {
		Context con = Wasatter.CONTEXT;
		return PreferenceManager.getDefaultSharedPreferences(con).edit()
				.putString("twitter_token", token).commit();
	}

	public static String getTwitterToken() {
		Context con = Wasatter.CONTEXT;
		return PreferenceManager.getDefaultSharedPreferences(con).getString(
				"twitter_token", "");
	}

	public static boolean setTwitterTokenSecret(String token) {
		Context con = Wasatter.CONTEXT;
		return PreferenceManager.getDefaultSharedPreferences(con).edit()
				.putString("twitter_token_secret", token).commit();
	}

	public static String getTwitterTokenSecret() {
		Context con = Wasatter.CONTEXT;
		return PreferenceManager.getDefaultSharedPreferences(con).getString(
				"twitter_token_secret", "");
	}

	// 表示設定
	public static boolean isLoadImage() {
		Context con = Wasatter.CONTEXT;
		return PreferenceManager.getDefaultSharedPreferences(con).getBoolean(
				"display_load_image", true);
	}

	public static boolean isLoadFavoriteImage() {
		Context con = Wasatter.CONTEXT;
		return PreferenceManager.getDefaultSharedPreferences(con).getBoolean(
				"display_load_favorite_image", false);
	}

	private class CacheClearListener implements OnPreferenceClickListener {
		public boolean onPreferenceClick(Preference preference) {
			// TODO 自動生成されたメソッド・スタブ
			AlertDialog.Builder adb = new AlertDialog.Builder(Setting.this);
			adb.setMessage("キャッシュを消去します。よろしいですか？");
			adb.setNegativeButton("いいえ", null);
			adb.setPositiveButton("はい", new CacheClearDoListener());
			adb.show();
			return true;
		}
	}

	private class CacheClearDoListener implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			// TODO 自動生成されたメソッド・スタブ
			Wasatter.deleteImageCache();
		}
	}
}