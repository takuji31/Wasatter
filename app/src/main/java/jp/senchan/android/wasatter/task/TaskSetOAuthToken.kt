/**
 *
 */
package jp.senchan.android.wasatter.task

import android.os.AsyncTask
import jp.senchan.android.wasatter.activity.OAuthToken
import jp.senchan.android.wasatter.activity.Setting
import twitter4j.TwitterException

/**
 * @author takuji
 */
class TaskSetOAuthToken(var target: OAuthToken) : AsyncTask<String, Unit, Unit>() {
    override fun onPreExecute() {
        target.finish()
    }

    override fun doInBackground(vararg params: String) {
        try {
            val access_token = target.twitter.getOAuthAccessToken(
                    target.request, params[0])
            target.settingsRepository.twitterToken = access_token.token
            target.settingsRepository.twitterTokenSecret = access_token.tokenSecret
        } catch (e: TwitterException) {
            e.printStackTrace()
        }
    }

    override fun onPostExecute(result: Unit) {
        // TODO OAuthToken更新完了の処理を書く
    }

}