/**
 *
 */
package jp.senchan.android.wasatter.task

import android.os.AsyncTask
import android.view.View
import android.webkit.WebView
import android.widget.Button
import jp.senchan.android.wasatter.R
import jp.senchan.android.wasatter.activity.OAuthToken
import twitter4j.TwitterException

/**
 * @author takuji
 */
class TaskGetOAuthRequestUrl (private val target: OAuthToken) : AsyncTask<Unit, Unit, Unit>() {
    private var btn: Button? = null

    override fun onPreExecute() { // TODO 自動生成されたメソッド・スタブ
        btn = target.findViewById<View>(R.id.button_set_token) as Button
        btn!!.isClickable = false
    }

    override fun doInBackground(vararg params: Unit) { // TODO 自動生成されたメソッド・スタブ
        try {
            target.request = target.twitter!!.oAuthRequestToken
        } catch (e: TwitterException) { // TODO 自動生成された catch ブロック
            e.printStackTrace()
        }
    }

    override fun onPostExecute(result: Unit) { // TODO 自動生成されたメソッド・スタブ
        val wv = target.findViewById<View>(R.id.web) as WebView
        wv.loadUrl(target.request!!.authenticationURL)
        wv.requestFocus()
        btn!!.isClickable = true
    }

}