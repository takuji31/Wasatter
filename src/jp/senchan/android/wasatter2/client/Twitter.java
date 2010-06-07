package jp.senchan.android.wasatter2.client;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;

import jp.senchan.android.wasatter2.R;
import jp.senchan.android.wasatter2.Setting;
import jp.senchan.android.wasatter2.Wasatter;
import jp.senchan.android.wasatter2.activity.TimelineActivity;
import jp.senchan.android.wasatter2.item.Item;
import jp.senchan.android.wasatter2.setting.TwitterAccount;
import jp.senchan.android.wasatter2.util.ToastUtil;
import jp.senchan.android.wasatter2.util.TwitterClient;
import jp.senchan.android.wasatter2.util.WasatterItem;
import jp.senchan.android.wasatter2.util.WassrClient;
import jp.senchan.android.wasatter2.xauth.XAuth;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.http.HTMLEntity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Html.ImageGetter;

/**
 * Version2からのWassrクライアントクラス
 *
 * @author takuji
 *
 */
public class Twitter extends BaseClient {
	public static final int TIMELINE = 1;
	public static final int REPLY = 2;
	public static final int MYPOST = 3;

	public static DefaultHttpClient getHttpClient() {
		// HttpClientの準備
		// TODO Staticに突っ込んで使い回そうかと思ったけどどっか1つ詰まったらそのまま通信出来なさそうなのでやめた。
		DefaultHttpClient client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		// コネクションタイムアウトを設定：10秒
		HttpConnectionParams.setConnectionTimeout(params, 10000);
		// データ取得タイムアウトを設定：10秒
		HttpConnectionParams.setSoTimeout(params, 10000);
		// 認証のセット
		Credentials cred = new UsernamePasswordCredentials(
				Setting.getWassrId(), Setting.getWassrPass());
		AuthScope scope = new AuthScope("api.wassr.jp", 80);
		client.getCredentialsProvider().setCredentials(scope, cred);

		return client;
	}

	/**
	 * APIを経由してデータを取得するメソッド イメージとしてはThreadでこれを呼ぶ、このメソッドはHandlerでメインスレッドで処理をやる
	 *
	 * @param mode
	 *            どのデータを取得するか
	 * @param clear
	 *            リストの中身をクリアするか否か
	 * @param items
	 *            追加する先のリスト
	 * @param params
	 *            HTTP通信で使うパラメータ
	 * @return 追加されたリスト
	 */
	public static void getItems(int mode, final TimelineActivity target,
			boolean clear, ArrayList<Item> items, HashMap<String, String> params) {

		// 取得するURL
		String url;
		// Twitterが無効なら終了
		if (!TwitterAccount.get(TwitterAccount.LOAD_TL, false)) {
			return;
		}
		//xAuth設定がまだだったらToast出して終了
		if("".equals(TwitterAccount.get(TwitterAccount.TOKEN, ""))){
			target.handler.post(new Runnable() {

				
				public void run() {
					// Toastぶん投げ
					ToastUtil.show("認証の設定がありません");
				}
			});
			return;
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
			return;
		}

		// xAuthリクエストの準備
		XAuth request = new XAuth(url, "GET", params);

		try {
			HttpResponse response = request.request();
			// HTTPレスポンスステータスを取得
			final int errorCode = response.getStatusLine().getStatusCode();
			// 400番台以上の場合、エラー処理
			if (errorCode >= 400) {
				target.handler.post(new Runnable() {

					
					public void run() {
						target.httpError(errorCode, "Twitter");

					}
				});
				return;
			}
			HttpEntity resEntity = response.getEntity();
			JSONArray result = null;
			if (resEntity != null) {
				String resString = EntityUtils.toString(resEntity);
				result = new JSONArray(resString);
			}
			// 配列が空なら終了
			if (result == null || result.length() == 0) {
				return;
			}
			try {
				// 配列の長さを代入
				int j = result.length();

				SimpleDateFormat sdf = new SimpleDateFormat(
						"EEE MMM dd HH:mm:ss Z yyyy", Locale.US);

				// リストをクリアする設定ならクリアする
				if (clear) {
					items.clear();
				}
				for (int i = 0; i < j; i++) {
					JSONObject obj = result.getJSONObject(i);
					Item item = new Item();
					item.service = Wasatter.TWITTER;
					item.html = HTMLEntity.unescape(obj.getString("text"));
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
			} catch (JSONException e) {
				// TODO 自動生成された catch ブロック
			}
			return;
		} catch (ClientProtocolException e1) {
			// TODO ようわからんけど通信がおかしかったら到達するブロック
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO ネットワークエラー…？
			e1.printStackTrace();
		} catch (JSONException e) {
			// TODO JSONデータが破損している場合に到達するブロック
			e.printStackTrace();
		}
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

}
