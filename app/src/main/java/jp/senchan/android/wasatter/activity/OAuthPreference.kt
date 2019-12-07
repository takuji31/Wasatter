/**
 *
 */
package jp.senchan.android.wasatter.activity

import android.content.Context
import android.preference.Preference
import android.util.AttributeSet
import jp.senchan.android.wasatter.R

/**
 * @author Senka/Takuji
 */
class OAuthPreference : Preference {
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) { // TODO 自動生成されたコンストラクター・スタブ
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { // TODO 自動生成されたコンストラクター・スタブ
    }

    constructor(context: Context?) : super(context) { // TODO 自動生成されたコンストラクター・スタブ
        this.setTitle(R.string.title_setting_twitter_oauth_token_get)
        this.setSummary(R.string.summary_setting_twitter_oauth_token_get)
    }
}