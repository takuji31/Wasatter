/**
 *
 */
package jp.senchan.android.wasatter.task

import android.os.AsyncTask
import android.view.View
import android.widget.Button
import android.widget.TextView
import jp.senchan.android.wasatter.R
import jp.senchan.android.wasatter.Wasatter
import jp.senchan.android.wasatter.WasatterItem
import jp.senchan.android.wasatter.activity.Detail
import jp.senchan.android.wasatter.activity.Setting
import twitter4j.Twitter
import twitter4j.TwitterException
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken

/**
 * @author takuji
 */
    class TaskToggleFavorite(private val detail: Detail) : AsyncTask<WasatterItem, Unit, Boolean>() {
    private var item: WasatterItem? = null

    override fun doInBackground(vararg params: WasatterItem): Boolean {
        item = params[0]
        if (Wasatter.SERVICE_TWITTER == item!!.service) {
            val tw: Twitter = TwitterFactory().instance
            tw.setOAuthConsumer(Wasatter.OAUTH_KEY, Wasatter.OAUTH_SECRET)
            tw.oAuthAccessToken = AccessToken(Setting.twitterToken, Setting.twitterTokenSecret)
            return try {
                val st = tw.createFavorite(item!!.rid.toLong())
                st.text != null
            } catch (e: NumberFormatException) {
                false
            } catch (e: TwitterException) {
                false
            }
        }
        return false
    }

    override fun onPostExecute(result: Boolean) {
        val text_result = detail.findViewById<View>(R.id.text_result) as TextView
        val button = detail.findViewById<View>(R.id.button_favorite) as Button
        val favorited = item!!.favorited
        val isWassr = Wasatter.SERVICE_TWITTER != item!!.service
        val text: String
        if (!isWassr) {
            text = if (result) "お気に入りに追加しました。" else "お気に入りに追加できませんでした。"
            if (favorited) {
                button.setText(Detail.DEL_TWITTER)
            } else {
                button.setText(Detail.ADD_TWITTER)
            }
        } else if (favorited) {
            text = if (result) "イイネ！しました。" else "イイネ！を取り消しできませんでした。"
        } else {
            text = if (result) "イイネ！を取り消しました。" else "イイネ！できませんでした。"
        }
        if (favorited && isWassr) {
            button.setText(Detail.DEL_WASSR)
        } else if (isWassr) {
            button.setText(Detail.ADD_WASSR)
        }
        text_result.text = text
        detail.favoriteButton!!.isClickable = true
    }

    init {
        detail.favoriteButton!!.isClickable = false
    }
}