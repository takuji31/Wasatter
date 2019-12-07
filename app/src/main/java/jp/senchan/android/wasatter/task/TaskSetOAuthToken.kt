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
class TaskSetOAuthToken(var target: OAuthToken) : AsyncTask<String?, Void?, Boolean?>() {
    override fun onPreExecute() {
        target.finish()
    }

    protected override fun doInBackground(vararg params: String): Boolean? {
        try {
            val access_token = target.twitter!!.getOAuthAccessToken(
                    target.request, params[0])
            Setting.Companion.setTwitterToken(access_token.token)
            Setting.Companion.setTwitterTokenSecret(access_token.tokenSecret)
        } catch (e: TwitterException) { // TODO 自動生成された catch ブロック
            e.printStackTrace()
        }
        return null
    }

    override fun onPostExecute(result: Boolean?) { // TODO OAuthToken更新完了の処理を書く
    }

}