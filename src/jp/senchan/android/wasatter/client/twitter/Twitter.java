package jp.senchan.android.wasatter.client.twitter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.client.BaseClient;
import jp.senchan.android.wasatter.item.Item;
import jp.senchan.android.wasatter.setting.TwitterAccount;
import jp.senchan.android.wasatter.xauth.SignatureEncode;
import jp.senchan.android.wasatter.xauth.XAuthClient;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Version2からのTwitterクライアントクラス
 *
 * @author takuji
 *
 */
public class Twitter extends BaseClient {

	public static DefaultHttpClient getHttpClient() {
		// HttpClientの準備
		// TODO Staticに突っ込んで使い回そうかと思ったけどどっか1つ詰まったらそのまま通信出来なさそうなのでやめた。
		DefaultHttpClient client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		// コネクションタイムアウトを設定：10秒
		HttpConnectionParams.setConnectionTimeout(params, 10000);
		// データ取得タイムアウトを設定：10秒
		HttpConnectionParams.setSoTimeout(params, 10000);

		return client;
	}
	
	/**
	 * Twitterが有効かどうか調べるメソッド
	 * @return boolean Twitterが有効かどうか
	 */
	public static boolean enabled(){
		return !"".equals(TwitterAccount.get(TwitterAccount.TOKEN, ""));
	}

	/**
	 * APIを経由してデータを取得するメソッド
	 *
	 * @param mode
	 *            どのデータを取得するか
	 * @param params
	 *            HTTP通信で使うパラメータ
	 */
	public static HttpResponse request(int mode, HashMap<String, String> params) {

		// 取得するURL
		String url;
		// Twitterが無効なら終了
		if (!enabled()) {
			return null;
		}
		// URLを決定する
		switch (mode) {
		case TIMELINE:
			url = TwitterUrl.FRIEND_TIMELINE;
			break;
		case REPLY:
			url = TwitterUrl.REPLY;
			break;
		case MYPOST:
			url = TwitterUrl.MYPOST;
			break;
		// 正しくない値が渡されたら終了
		default:
			return null;
		}

		// xAuthリクエストの準備
		XAuthClient request = new XAuthClient(url, "GET", params);

		return request.request();
	}

	
	public static int updateTimeline(String status, String rid){
		int resCode = -1;
		// Twitterが無効なら終了
		if (!enabled()) {
			return 0;
		}
		// xAuthリクエストの準備
		//TODO 仮にテキストだけ
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("status", SignatureEncode.encode(status));
		if(rid != null){
			params.put("in_reply_to_status_id", rid);
		}
		XAuthClient request = new XAuthClient(TwitterUrl.UPDATE_TIMELINE, "POST", params);

		try {
			HttpResponse response = request.request();
			// HTTPレスポンスステータスを取得
			resCode = response.getStatusLine().getStatusCode();
		} catch (Exception e) {
			// TODO: ぬるぽとか
		}		
		return resCode;
	}
	
	
	public static ArrayList<Item> parseJSON(String jsonStr,int mode){
		
		ArrayList<Item> items = new ArrayList<Item>();
		// 配列が空なら終了
		try {
			JSONArray result = new JSONArray(jsonStr);
			if (result.length() != 0) {
			// 配列の長さを代入
			int j = result.length();

			SimpleDateFormat sdf = new SimpleDateFormat(
					"EEE MMM dd HH:mm:ss Z yyyy", Locale.US);

			for (int i = 0; i < j; i++) {
				JSONObject obj = result.getJSONObject(i);
				String test = obj.toString();
				test.toString();
				Item item = new Item();
				item.service = Wasatter.TWITTER;
				item.html = obj.getString("text");
				item.screenName = obj.getJSONObject("user")
						.getString("screen_name");
				item.name = obj.getJSONObject("user").getString("name");
				item.rid = obj.getString("id");
				item.link = TwitterUrl.PERMA_LINK.replace("[id]", item.screenName)
						.replace("[rid]", item.rid);
				item.text = obj
						.getString("text");
				item.html = item.text;
				String profile = obj.getJSONObject("user").getString(
						"profile_image_url");
				if (Wasatter.downloadWaitUrls.indexOf(profile) == -1
						&& Wasatter.images.get(profile) == null) {
					Wasatter.downloadWaitUrls.add(profile);
				}
				item.profileImageUrl = profile;
				item.replyUserNick = obj
						.getString("in_reply_to_screen_name");
				try {
					item.epoch = sdf.parse(obj.getString("created_at"))
							.getTime() / 1000;
				} catch (ParseException e) {
					// まぁまずないだろうけど一応
					item.epoch = 0;
				}
				item.favorited = obj.getBoolean("favorited");
				if(items.indexOf(item) == -1){
					items.add(0, item);
				}
			}
			}
		} catch (JSONException e) {
			// FIXME ここでcatchするのはいかがなものか
		}
		return items;
		
	}

}
