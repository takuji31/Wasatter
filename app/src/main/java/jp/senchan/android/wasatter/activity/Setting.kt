/**
 *
 */
package jp.senchan.android.wasatter.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.preference.Preference
import android.preference.Preference.OnPreferenceClickListener
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.preference.PreferenceScreen
import jp.senchan.android.wasatter.R
import jp.senchan.android.wasatter.Wasatter

/**
 * アカウント設定
 *
 * @author Senka/Takuji
 */
class Setting : PreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { // TODO 自動生成されたメソッド・スタブ
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.setting)
        val pc = this.preferenceScreen
                .getPreference(1) as PreferenceScreen
        val pf = OAuthPreference(this)
        pf.onPreferenceClickListener = OAuthGetTokenListener()
        pc.addPreference(pf)
        val pc2 = this.preferenceScreen
                .getPreference(2) as PreferenceScreen
        val pf2 = Preference(this)
        pf2.title = "キャッシュの消去"
        pf2.summary = "画像のキャッシュを削除します。"
        pf2.onPreferenceClickListener = CacheClearListener()
        pc2.addPreference(pf2)
    }

    private inner class OAuthGetTokenListener : OnPreferenceClickListener {
        override fun onPreferenceClick(preference: Preference): Boolean { // TODO 自動生成されたメソッド・スタブ
            val intent = Intent(this@Setting.baseContext,
                    OAuthToken::class.java)
            this@Setting.startActivity(intent)
            return true
            // return false;
        }
    }

    private inner class CacheClearListener : OnPreferenceClickListener {
        override fun onPreferenceClick(preference: Preference): Boolean { // TODO 自動生成されたメソッド・スタブ
            val adb = AlertDialog.Builder(this@Setting)
            adb.setMessage("キャッシュを消去します。よろしいですか？")
            adb.setNegativeButton("いいえ", null)
            adb.setPositiveButton("はい", CacheClearDoListener())
            adb.show()
            return true
        }
    }

    private inner class CacheClearDoListener : DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface, which: Int) { // TODO: clear image cache
        }
    }
}