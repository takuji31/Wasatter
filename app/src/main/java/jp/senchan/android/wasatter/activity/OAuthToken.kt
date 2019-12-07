/**
 *
 */
package jp.senchan.android.wasatter.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import jp.senchan.android.wasatter.R
import jp.senchan.android.wasatter.Wasatter
import jp.senchan.android.wasatter.task.TaskGetOAuthRequestUrl
import jp.senchan.android.wasatter.task.TaskSetOAuthToken
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.auth.RequestToken

/**
 * @author Senka/Takuji
 */
class OAuthToken : Activity() {
    lateinit var twitter: Twitter
    var request: RequestToken? = null
    override fun onCreate(savedInstanceState: Bundle?) { // TODO 自動生成されたメソッド・スタブ
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.oauth_dialog)
        val cancel_button = findViewById<View>(R.id.button_cancel) as Button
        cancel_button.setOnClickListener(CancelButtonClickListener())
        val set_button = findViewById<View>(R.id.button_set_token) as Button
        set_button.setOnClickListener(SetTokenClickListener())
        val clear_button = findViewById<View>(R.id.button_clear_token) as Button
        clear_button.setOnClickListener(ClearTokenClickListener())
        twitter = TwitterFactory().instance
        twitter
                .setOAuthConsumer(Wasatter.OAUTH_KEY, Wasatter.OAUTH_SECRET)
        val webview = findViewById<View>(R.id.web) as WebView
        webview.webViewClient = WebViewClient()
        TaskGetOAuthRequestUrl(this).execute()
    }

    private inner class CancelButtonClickListener : View.OnClickListener {
        override fun onClick(v: View) { // TODO 自動生成されたメソッド・スタブ
            finish()
        }
    }

    private inner class SetTokenClickListener : View.OnClickListener {
        override fun onClick(v: View) { // TODO 自動生成されたメソッド・スタブ
            val pin = findViewById<View>(R.id.text_token) as TextView
            TaskSetOAuthToken(this@OAuthToken).execute(pin
                    .text.toString())
        }
    }

    private inner class ClearTokenClickListener : View.OnClickListener {
        override fun onClick(v: View) { // TODO 自動生成されたメソッド・スタブ
            val ad = AlertDialog.Builder(
                    this@OAuthToken)
            ad.setMessage(R.string.message_confirm_clear_oauth_token)
            ad.setPositiveButton("OK", ClearTokenOkButtonClickListener())
            ad.setNegativeButton("Cancel", null)
            ad.show()
        }
    }

    private inner class ClearTokenOkButtonClickListener : DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface, which: Int) { // TODO 自動生成されたメソッド・スタブ
            Setting.Companion.setTwitterToken("")
            Setting.Companion.setTwitterTokenSecret("")
            // Wasatter.makeToast("OAuthトークンをクリアしました。");
            val ad = AlertDialog.Builder(
                    this@OAuthToken)
            ad.setMessage("OAuthトークンを削除しました。")
            ad.setPositiveButton("OK", null)
            ad.show()
        }
    }
}