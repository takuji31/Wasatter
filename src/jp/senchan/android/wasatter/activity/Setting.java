/**
 *
 */
package jp.senchan.android.wasatter.activity;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO 自動生成されたメソッド・スタブ
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.setting);
        PreferenceScreen pc = (PreferenceScreen) this.getPreferenceScreen()
                .getPreference(1);
        OAuthPreference pf = new OAuthPreference(this);
        pf.setOnPreferenceClickListener(new OAuthGetTokenListener());
        pc.addPreference(pf);
        PreferenceScreen pc2 = (PreferenceScreen) this.getPreferenceScreen()
                .getPreference(2);
        Preference pf2 = new Preference(this);
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

    public static boolean isLoadWassrTimeline() {
        Context con = Wasatter.CONTEXT;
        return PreferenceManager.getDefaultSharedPreferences(con).getBoolean(
                con.getString(R.string.key_setting_wassr_load_timeline), true);
    }

    public static boolean isLoadTwitterTimeline() {
        Context con = Wasatter.CONTEXT;
        return PreferenceManager
                .getDefaultSharedPreferences(con)
                .getBoolean(
                        con.getString(R.string.key_setting_twitter_load_timeline),
                        true);
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

    public static boolean isDisplayButtons() {
        Context con = Wasatter.CONTEXT;
        return PreferenceManager.getDefaultSharedPreferences(con).getBoolean(
                con.getString(R.string.key_setting_display_buttons), true);
    }

    private class OAuthGetTokenListener implements OnPreferenceClickListener {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            // TODO 自動生成されたメソッド・スタブ
            Intent intent = new Intent(Setting.this.getBaseContext(),
                    OAuthToken.class);
            Setting.this.startActivity(intent);
            return true;
            // return false;
        }
    }

    private class CacheClearListener implements OnPreferenceClickListener {
        @Override
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
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // TODO 自動生成されたメソッド・スタブ
            Wasatter.deleteImageCache();
            Wasatter.main.first_load = Setting.isLoadImage();
        }
    }
}
