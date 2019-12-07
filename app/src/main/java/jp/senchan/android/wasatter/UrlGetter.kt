/**
 *
 */
package jp.senchan.android.wasatter

import twitter4j.HttpClientFactory
import twitter4j.JSONException
import twitter4j.TwitterException

/**
 * bit.ly/j.mpの短縮URL
 *
 * @author Senka/Takuji
 */
object UrlGetter {
    const val JMP = "http://api.j.mp/shorten?version=2.0.1&login=takuji31&apiKey=R_aafe078d678bf3a730cf90340f864135&longUrl=[url]"
    const val BITLY = "http://api.bit.ly/shorten?version=2.0.1&login=takuji31&apiKey=R_aafe078d678bf3a730cf90340f864135&longUrl=[url]"
    fun bitly(original: String?, api_url: String): String {
        try {
            val response = HttpClientFactory.getInstance()[api_url.replace("[url]", original)]
            val res = response.asJSONObject()
            return res.getJSONObject("results").getJSONObject(original)
                    .getString("shortUrl")
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: TwitterException) {
            e.printStackTrace()
        }
        return ""
    }

    fun replace(text: String?, api_url: String?): String {
        return ""
    }
}