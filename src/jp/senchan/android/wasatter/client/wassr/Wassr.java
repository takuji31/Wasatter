package jp.senchan.android.wasatter.client.wassr;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.client.BaseClient;
import jp.senchan.android.wasatter.item.Item;
import jp.senchan.android.wasatter.setting.Setting;
import jp.senchan.android.wasatter.setting.WassrAccount;
import jp.senchan.android.wasatter.util.wassr.WassrUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
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
				Setting.get(WassrAccount.ID,""), Setting.get(WassrAccount.PASS,""));
		AuthScope scope = new AuthScope("api.wassr.jp", 80);
		client.getCredentialsProvider().setCredentials(scope, cred);

		return client;
	}

	public static boolean enabled() {
		boolean id_ne = !WassrAccount.get(WassrAccount.ID, "").equals("");
		boolean pass_ne = !WassrAccount.get(WassrAccount.PASS, "").equals("");
		return id_ne && pass_ne;
	}

	/**
	 * APIを経由してデータを取得するメソッド イメージとしてはThreadでこれを呼ぶ、このメソッドはHandlerでメインスレッドで処理をやる
	 * 
	 * @param mode
	 *            どのデータを取得するか
	 * @param params
	 *            HTTP通信で使うパラメータ
	 * @return 追加されたリスト
	 */
	public static HttpResponse request(int mode, HashMap<String, String> params) {

		// 取得するURL
		String url;
		// TODO Wassrが無効なら終了
		if (!enabled()) {
			return null;
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
			break;
		// 正しくない値が渡されたら終了
		default:
			return null;
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
		try {
			return client.execute(get);
		} catch (ClientProtocolException e1) {
			// TODO ようわからんけど通信がおかしかったら到達するブロック
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO ネットワークエラー…？
			e1.printStackTrace();
		}
		return null;
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
					item.favorite.remove(Setting.get(WassrAccount.ID,""));
				} else {
					item.favorite.add(Setting.get(WassrAccount.ID,""));
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

	/**
	 * タイムラインのJSONをパースするメソッド
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static ArrayList<Item> parseJSON(String jsonStr, int mode) {
		ArrayList<Item> items = new ArrayList<Item>();
		// 配列が空でない時のみ処理する
		try {
			JSONArray result = new JSONArray(jsonStr);
			boolean channel = (mode == Wassr.CHANNEL);
			if (result.length() != 0) {
				// 配列の長さを代入
				int j = result.length();
				SimpleDateFormat sdf = new SimpleDateFormat(
						"EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
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
						String icon_url = WassrUrl.FAVORITE_ICON.replace(
								"[user]", favorites.getString(k));
						item.favorite.add(favorites.getString(k));
						if (!(mode == Wassr.ODAI)
								&& Wasatter.downloadWaitUrls.indexOf(icon_url) == -1
								&& Wasatter.images.get(icon_url) == null) {
							Wasatter.downloadWaitUrls.add(icon_url);
						}
					}
					item.favorited = item.favorite
							.indexOf(Setting.get(WassrAccount.ID,"")) != -1;
					if (items.indexOf(item) == -1) {
						items.add(0, item);
					}

				}
			}
		} catch (JSONException e) {
			// TODO JSONのパースエラー出すべき？
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return items;
	}

	public static ArrayList<Item> parseJSON(String jsonStr) {
		return parseJSON(jsonStr, Wassr.TIMELINE);
	}

	/**
	 * Wassrにヒトコトを投げるメソッド、通常タイムラインへの投稿と返信はこのメソッドで行う。
	 * 
	 * @param status
	 *            本文
	 * @param rid
	 *            返信先のID
	 * @param image
	 *            添付する画像
	 * @return 結果のHTTPコード、無効な場合は0、不明な場合は-1を返す
	 */
	public static int updateTimeLine(String status, String rid, String image) {
		int resCode = -1;
		if (!enabled()) {
			return 0;
		}
		try {

			// まずはパラメーターを準備する
			MultipartEntity reqEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			// 画像が指定されていれば画像
			if (image != null) {
				File file = new File(image);
				FileBody bin = new FileBody(file);
				reqEntity.addPart("image", bin);
			}

			// 返信の場合はrid
			if (rid != null) {
				StringBody reply_rid = new StringBody(rid);
				reqEntity.addPart("reply_status_rid", reply_rid);
			}

			// その他のパラメータ
			StringBody via = new StringBody(WassrUtils.VIA);
			StringBody st = new StringBody(status);
			reqEntity.addPart("source", via);
			reqEntity.addPart("status", st);

			// HttpClientの準備
			DefaultHttpClient client = getHttpClient();
			HttpPost post = new HttpPost(WassrUrl.UPDATE_TIMELINE);
			post.setEntity(reqEntity);
			org.apache.http.HttpResponse response = client.execute(post);
			resCode = response.getStatusLine().getStatusCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ここまで来ることはまずないけど、きたら失敗
		return resCode;
	}
}
