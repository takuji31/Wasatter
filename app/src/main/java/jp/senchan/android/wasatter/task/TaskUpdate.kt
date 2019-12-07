package jp.senchan.android.wasatter.task

import android.os.AsyncTask
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import jp.senchan.android.wasatter.R
import jp.senchan.android.wasatter.Wasatter
import jp.senchan.android.wasatter.activity.Setting
import twitter4j.Twitter
import twitter4j.TwitterException
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken

class TaskUpdate(vararg params: Boolean) : AsyncTask<String?, String?, Void?>() {
    var reply: Boolean
    var twitter: Boolean
    var wassr: Boolean
    var r_twitter = false
    var channel: Boolean
    protected override fun doInBackground(vararg params: String): Void? {
        if (wassr) {
            publishProgress(Wasatter.MODE_POSTING, Wasatter.SERVICE_WASSR)
        }
        if (twitter) {
            val tw: Twitter
            tw = TwitterFactory().instance
            tw.setOAuthConsumer(Wasatter.OAUTH_KEY, Wasatter.OAUTH_SECRET)
            tw.oAuthAccessToken = AccessToken(Setting.Companion.getTwitterToken(), Setting.Companion.getTwitterTokenSecret())
            try {
                publishProgress(Wasatter.MODE_POSTING, Wasatter.SERVICE_TWITTER)
                tw.updateStatus(params[0])
                r_twitter = true
            } catch (e: TwitterException) {
                publishProgress(Wasatter.MODE_ERROR, Wasatter.SERVICE_TWITTER, e.statusCode.toString())
                r_twitter = false
            }
        }
        return null
    }

    protected override fun onProgressUpdate(vararg values: String) { // まず、何が起こってここに飛んできたか判定
        val service = values[1]
        if (Wasatter.MODE_POSTING == values[0]) {
            val text_posting_service = Wasatter.main
                    .findViewById<View>(R.id.text_update_status_service) as TextView
            val layout = Wasatter.main
                    .findViewById<View>(R.id.layout_update_status) as LinearLayout
            layout.visibility = View.VISIBLE
            text_posting_service.text = service
        } else if (Wasatter.MODE_ERROR == values[0]) {
            val error = values[2]
            Wasatter.displayHttpError(error, service)
        }
    }

    override fun onPostExecute(result: Void?) {
        val layout = Wasatter.main
                .findViewById<View>(R.id.layout_update_status) as LinearLayout
        layout.visibility = View.GONE
    }

    init {
        reply = params[0]
        wassr = params[1]
        twitter = params[2]
        channel = params[3]
    }
}