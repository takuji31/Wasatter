/**
 *
 */
package jp.senchan.android.wasatter.app;

import jp.senchan.android.wasatter.R;
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

}
