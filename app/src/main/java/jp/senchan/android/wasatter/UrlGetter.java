/**
 *
 */
package jp.senchan.android.wasatter;

import twitter4j.HttpClientFactory;
import twitter4j.HttpResponse;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.TwitterException;

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
			HttpResponse response = HttpClientFactory.getInstance()
					.get(api_url.replace("[url]", original));
			JSONObject res = response.asJSONObject();
			return res.getJSONObject("results").getJSONObject(original)
					.getString("shortUrl");
		} catch (JSONException | TwitterException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String replace(String text, String api_url) {

		return "";
	}
}
