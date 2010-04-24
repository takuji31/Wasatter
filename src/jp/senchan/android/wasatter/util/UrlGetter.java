/**
 *
 */
package jp.senchan.android.wasatter.util;

import twitter4j.TwitterException;
import twitter4j.http.HttpClientWrapper;
import twitter4j.http.HttpResponse;
import twitter4j.org.json.JSONException;
import twitter4j.org.json.JSONObject;

/**
 * bit.ly/j.mpの短縮URL
 * 
 * @author Senka/Takuji
 * 
 */
public class UrlGetter {
	public static final String JMP = "http://api.j.mp/shorten?version=2.0.1&login=takuji31&apiKey=R_aafe078d678bf3a730cf90340f864135&longUrl=[url]";
	public static final String BITLY = "http://api.bit.ly/shorten?version=2.0.1&login=takuji31&apiKey=R_aafe078d678bf3a730cf90340f864135&longUrl=[url]";

	public static String bitly(String original, String api_url) {
		try {
			HttpClientWrapper http = new HttpClientWrapper();
			HttpResponse response = http
					.get(api_url.replace("[url]", original));
			JSONObject res = response.asJSONObject();
			return res.getJSONObject("results").getJSONObject(original)
					.getString("shortUrl");
		} catch (JSONException e) {
		} catch (TwitterException e) {
		}
		return "";
	}

	public static String replace(String text, String api_url) {

		return "";
	}
}
