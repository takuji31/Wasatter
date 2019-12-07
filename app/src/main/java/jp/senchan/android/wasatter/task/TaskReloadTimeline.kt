package jp.senchan.android.wasatter.task

import android.os.AsyncTask
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.ListView
import jp.senchan.android.wasatter.R
import jp.senchan.android.wasatter.StatusItemComparator
import jp.senchan.android.wasatter.Wasatter
import jp.senchan.android.wasatter.WasatterItem
import jp.senchan.android.wasatter.activity.Setting
import jp.senchan.android.wasatter.adapter.Timeline
import twitter4j.TwitterException
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder
import java.util.*

class TaskReloadTimeline // コンストラクタ
(protected var listview: ListView?, protected var mode: Int) : AsyncTask<String?, String?, ArrayList<WasatterItem?>>() {
    // バックグラウンドで実行する処理
    protected override fun doInBackground(vararg param: String): ArrayList<WasatterItem?> {
        val result = ArrayList<WasatterItem?>()
        val twitter = TwitterFactory(
                ConfigurationBuilder()
                        .setOAuthConsumerKey(Wasatter.OAUTH_KEY)
                        .setOAuthConsumerSecret(Wasatter.OAUTH_SECRET)
                        .setOAuthAccessToken(Setting.Companion.getTwitterToken())
                        .setOAuthAccessTokenSecret(Setting.Companion.getTwitterTokenSecret())
                        .build()
        ).instance
        when (mode) {
            MODE_TIMELINE -> try {
                val homeTimeline = twitter.homeTimeline
                for (status in homeTimeline) {
                    result.add(WasatterItem(status))
                }
            } catch (e: TwitterException) {
                publishProgress(Wasatter.SERVICE_TWITTER, e
                        .statusCode.toString())
            }
            MODE_REPLY -> try {
                val mentionsTimeline = twitter.mentionsTimeline
                for (status in mentionsTimeline) {
                    result.add(WasatterItem(status))
                }
            } catch (e: TwitterException) {
                publishProgress(Wasatter.SERVICE_TWITTER, e
                        .statusCode.toString())
            }
            MODE_MYPOST -> try {
                val userTimeline = twitter.userTimeline
                for (status in userTimeline) {
                    result.add(WasatterItem(status))
                }
            } catch (e: TwitterException) {
                publishProgress(Wasatter.SERVICE_TWITTER, e
                        .statusCode.toString())
            }
        }
        return result
    }

    protected override fun onProgressUpdate(vararg values: String) { // まず、何が起こってここに飛んできたか判定
        val service = values[0]
        val error = values[1]
        Wasatter.displayHttpError(error, service)
    }

    // 進行中に出す処理
    override fun onPreExecute() {
        val sb = SpannableStringBuilder()
        sb.append("Loading ")
        sb.append(msg[mode - 1])
        sb.append("...")
        Wasatter.main!!.loading_timeline_text!!.text = sb.toString()
        Wasatter.main!!.layout_progress_timeline!!.visibility = View.VISIBLE
    }

    // メインスレッドで実行する処理
    override fun onPostExecute(result: ArrayList<WasatterItem?>) { // 取得結果の代入
        var set = false
        when (mode) {
            MODE_TIMELINE -> {
                Wasatter.main!!.list_timeline = result
                set = Wasatter.main!!.button_timeline!!.isChecked
            }
            MODE_REPLY -> {
                Wasatter.main!!.list_reply = result
                set = Wasatter.main!!.button_reply!!.isChecked
            }
            MODE_MYPOST -> {
                Wasatter.main!!.list_mypost = result
                set = Wasatter.main!!.button_mypost!!.isChecked
            }
            MODE_ODAI -> {
                Wasatter.main!!.list_odai = result
                set = Wasatter.main!!.button_odai!!.isChecked
            }
            MODE_CHANNEL_LIST -> {
                Wasatter.main!!.list_channel_list = result
                set = Wasatter.main!!.button_channel!!.isChecked
            }
            MODE_CHANNEL -> {
                Wasatter.main!!.list_channel = result
                set = Wasatter.main!!.button_channel!!.isChecked
            }
        }
        // ListViewへの挿入
        if (set) {
            val channel = mode == MODE_CHANNEL
            val adapter = Timeline(listview
                    .getContext(), R.layout.timeline_row, result, channel)
            if (!channel) {
                adapter.sort(StatusItemComparator())
            }
            listview!!.adapter = adapter
            listview!!.requestFocus()
        }
        Wasatter.main!!.layout_progress_timeline!!.visibility = View.GONE
    }

    companion object {
        const val MODE_TIMELINE = 1
        const val MODE_REPLY = 2
        const val MODE_MYPOST = 3
        const val MODE_ODAI = 4
        const val MODE_CHANNEL_LIST = 6
        const val MODE_CHANNEL = 7
        val msg = arrayOf("Timeline", "Reply",
                "My post", "Odai", "TODO", "Channel list", "Channel status")
    }

}