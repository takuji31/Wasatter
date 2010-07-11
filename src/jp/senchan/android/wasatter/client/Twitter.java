package jp.senchan.android.wasatter.client;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import jp.senchan.android.wasatter.Setting;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.item.Item;
import jp.senchan.android.wasatter.setting.TwitterAccount;
import jp.senchan.android.wasatter.xauth.SignatureEncode;
import jp.senchan.android.wasatter.xauth.XAuthClient;
import jp.senchan.android.wasatter.xauth.XAuthTokenGetter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Version2からのWassrクライアントクラス
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
	 * APIを経由してデータを取得するメソッド イメージとしてはThreadでこれを呼ぶ、このメソッドはHandlerでメインスレッドで処理をやる
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

	
	public static boolean update(String status,String imagePath){

		// Twitterが無効なら終了
		if (!enabled()) {
			return false;
		}
		// xAuthリクエストの準備
		//TODO 仮にテキストだけ
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("status", SignatureEncode.encode(status));
		XAuthClient request = new XAuthClient(TwitterUrl.UPDATE_TIMELINE, "POST", params);

		try {
			HttpResponse response = request.request();
			// HTTPレスポンスステータスを取得
			final int errorCode = response.getStatusLine().getStatusCode();
			// 400番台以上の場合、falseを返す
			if (errorCode >= 400) {
				return false;
			}
			HttpEntity resEntity = response.getEntity();
			String resString = EntityUtils.toString(resEntity);
			JSONObject result = new JSONObject(resString);
			Log.i("Result", result.toString());
			return true;
		} catch (ClientProtocolException e1) {
			// TODO ようわからんけど通信がおかしかったら到達するブロック
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO ネットワークエラー…？
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO: ぬるぽとか
		}		
		return false;
	}
	
	
	public static boolean favorite(Item item) {
		JSONObject json = null;
		try {
			// HttpClientの準備
			DefaultHttpClient client = getHttpClient();
			// URLの設定
			String url = null;
			if (!item.favorited) {
				url = WassrUrl.FAVORITE_ADD.replace("[rid]", item.rid);

			} else {
				url = WassrUrl.FAVORITE_DEL.replace("[rid]", item.rid);
			}
			HttpPost post = new HttpPost(url);
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String resString = EntityUtils.toString(entity);
				json = new JSONObject(resString);
			}
			// 配列が空なら終了
			if (json == null || json.length() == 0) {
				return false;
			}
			boolean result = json.getString("status").equalsIgnoreCase("ok");
			if (result) {
				if (item.favorited) {
					item.favorite.remove(Setting.getWassrId());
				} else {
					item.favorite.add(Setting.getWassrId());
				}
				item.favorited = !item.favorited;
			}
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return false;
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
			// TODO 自動生成された catch ブロック
		}
		return items;
		
	}

}
