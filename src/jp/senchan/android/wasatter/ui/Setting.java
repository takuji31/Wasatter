/**
 *
 */
package jp.senchan.android.wasatter.ui;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;

/**
 * アカウント設定
 *
 * @author Senka/Takuji
 *
 */
public class Setting extends PreferenceActivity {
    @SuppressWarnings("deprecation")
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
            Wasatter app = (Wasatter) getApplication();
            app.deleteImageCache();
            Wasatter.main.first_load = app.isLoadImage();
        }
    }
}
