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
import jp.senchan.android.wasatter2.setting.WassrAccount;
import jp.senchan.android.wasatter2.util.WassrClient;

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
public class Wassr extends BaseClient {
	public static final int TIMELINE = 1;
	public static final int REPLY = 2;
	public static final int MYPOST = 3;
	public static final int ODAI = 4;
	public static final int TODO = 5;
	public static final int CHANNEL_LIST = 6;
	public static final int CHANNEL = 7;

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

		// 取得するのがチャンネルか否か
		boolean channel = false;
		// 取得するURL
		String url;
		// TODO Wassrが無効なら終了
		if (!WassrAccount.get(WassrAccount.LOAD_TL, false)) {
			return;
		}

		// URLを決定する、チャンネルかどうかも判断する
		switch (mode) {
		case TIMELINE:
			url = WassrUrl.FRIEND_TIMELINE;
			break;
		case REPLY:
			url = WassrUrl.REPLY;
			break;
		case MYPOST:
			url = WassrUrl.MYPOST;
			break;
		case ODAI:
			url = WassrUrl.ODAI;
			break;
		case TODO:
			url = WassrUrl.TODO;
			break;
		case CHANNEL_LIST:
			url = WassrUrl.CHANNEL_LIST;
			break;
		case CHANNEL:
			url = WassrUrl.CHANNEL_TIMELINE;
			channel = true;
			break;
		// 正しくない値が渡されたら終了
		default:
			return;
		}

		// HttpClientの準備
		DefaultHttpClient client = getHttpClient();
		HttpGet get = new HttpGet(url);
		// パラメーターをセットする
		HttpParams param = get.getParams();
		if (params != null) {
			Iterator<Entry<String, String>> it = params.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				param.setParameter(entry.getKey(), entry.getValue());
			}
		}

		// 通信する
		HttpResponse response;
		try {
			response = client.execute(get);
			// HTTPレスポンスステータスを取得
			final int errorCode = response.getStatusLine().getStatusCode();
			// 400番台以上の場合、エラー処理
			if (errorCode >= 400) {
				target.handler.post(new Runnable() {

					
					public void run() {
						target.httpError(errorCode, "Wassr");

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
						"EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

				// リストをクリアする設定ならクリアする
				if (clear) {
					items.clear();
				}
				for (int i = 0; i < j; i++) {
					JSONObject obj = result.getJSONObject(i);
					Item item = new Item();
					item.service = Wasatter.WASSR;
					item.rid = obj.getString("rid");
					item.channel = channel;
					// 一旦HTMLの解析をして必要な画像をとっておく
					String htmlSrc = obj.getString("html");
					Html.fromHtml(htmlSrc, new ImageGetter() {

						
						public Drawable getDrawable(String source) {
							// 必要な画像のURLをあらかじめ取得
							Bitmap bmp = Wasatter.images.get(source);
							if (bmp == null) {
								Wassr.getImageWithCache(source);
							}
							return null;
						}
					}, null);
					item.html = htmlSrc;
					if (channel) {
						JSONObject ch = obj.getJSONObject("channel");
						SpannableStringBuilder sb = new SpannableStringBuilder(
								ch.getString("title"));
						sb.append(" (");
						sb.append(ch.getString("name_en"));
						item.service = sb.append(")").toString();
						item.screenName = obj.getJSONObject("user").getString(
								"login_id");
						item.name = obj.getJSONObject("user").getString("nick");
						item.link = WassrUrl.CHANNEL_PERMA_LINK.replace(
								"[name]", ch.getString("name_en")).replace(
								"[rid]", item.rid);
						try {
							JSONObject reply = obj.getJSONObject("reply");
							item.replyUserNick = reply.getJSONObject("user")
									.getString("nick");
							item.replyMessage = reply.getString("body");
						} catch (JSONException e) {
							// 返信なかったらスルー
						}
						try {
							item.epoch = sdf.parse(obj.getString("created_on"))
									.getTime() / 1000;
						} catch (ParseException e) {
							item.epoch = 0;
						}
						item.text = obj.getString("body");
					} else {
						item.screenName = obj.getString("user_login_id");
						item.name = obj.getJSONObject("user").getString(
								"screen_name");
						item.link = obj.getString("link");
						item.replyUserNick = obj.getString("reply_user_nick");
						item.replyMessage = obj.getString("reply_message");
						item.epoch = Long.parseLong(obj.getString("epoch"));

						item.text = obj.getString("text");
					}
					String profile = obj.getJSONObject("user").getString(
							"profile_image_url");
					if (Wasatter.downloadWaitUrls.indexOf(profile) == -1
							&& Wasatter.images.get(profile) == null) {
						Wasatter.downloadWaitUrls.add(profile);
					}
					item.profileImageUrl = profile;
					if ("null".equalsIgnoreCase(item.replyMessage)) {
						item.replyMessage = Wasatter.CONTEXT
								.getString(R.string.message_private_message);
					}
					JSONArray favorites = obj.getJSONArray("favorites");
					// お題のイイネは取得しない。
					int fav_count = favorites.length();
					for (int k = 0; k < fav_count; k++) {
						String icon_url = WassrClient.FAVORITE_ICON_URL
								.replace("[user]", favorites.getString(k));
						item.favorite.add(favorites.getString(k));
						if (!WassrUrl.ODAI.equals(url)
								&& Wasatter.downloadWaitUrls.indexOf(icon_url) == -1
								&& Wasatter.images.get(icon_url) == null) {
							Wasatter.downloadWaitUrls.add(icon_url);
						}
					}
					item.favorited = item.favorite
							.indexOf(Setting.getWassrId()) != -1;
					if (items.indexOf(item) == -1) {
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

	public static String channelFavorite(Item item) {
		JSONObject json = null;
		try {
			// HttpClientの準備
			DefaultHttpClient client = getHttpClient();
			// URLの設定
			String url = WassrUrl.FAVORITE_CHANNEL.replace("[rid]", item.rid);
			HttpGet get = new HttpGet(url);
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String resString = EntityUtils.toString(entity);
				json = new JSONObject(resString);
			}
			// 配列が空なら終了
			if (json == null || json.length() == 0) {
				return null;
			}
			return json.getString("message");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return "NG";
	}

}
