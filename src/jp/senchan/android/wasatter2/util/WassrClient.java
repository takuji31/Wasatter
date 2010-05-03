package jp.senchan.android.wasatter2.util;

import java.io.File;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import jp.senchan.android.wasatter2.R;
import jp.senchan.android.wasatter2.Setting;
import jp.senchan.android.wasatter2.Wasatter;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import twitter4j.TwitterException;
import twitter4j.http.Authorization;
import twitter4j.http.BasicAuthorization;
import twitter4j.http.HTMLEntity;
import twitter4j.http.HttpClientWrapper;
import twitter4j.http.HttpResponse;
import twitter4j.org.json.JSONArray;
import twitter4j.org.json.JSONException;
import twitter4j.org.json.JSONObject;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Html.ImageGetter;
import android.util.Log;

/**
 * Wassrクラス<br>
 * Wassrとの通信及び、データの入出力を行う。
 *
 * @author Senka/Takuji
 *
 */
public class WassrClient {
	private static final String FRIEND_TIMELINE_URL = "http://api.wassr.jp/statuses/friends_timeline.json";
	private static final String CHANNEL_TIMELINE_URL = "http://api.wassr.jp/channel_message/list.json?name_en=[name]";
	private static final String CHANNEL_PERMA_LINK = "http://wassr.jp/channel/[name]/messages/[rid]";
	private static final String REPLY_URL = "http://api.wassr.jp/statuses/replies.json";
	private static final String MYPOST_URL = "http://api.wassr.jp/statuses/user_timeline.json";
	private static final String ODAI_URL = "http://api.wassr.jp/statuses/user_timeline.json?id=odai";
	private static final String TODO_URL = "http://api.wassr.jp/todo/list.json";
	private static final String CHANNEL_LIST_URL = "http://api.wassr.jp/channel_user/user_list.json";
	private static final String TODO_STATUS_URL = "http://api.wassr.jp/todo/";
	private static final String UPDATE_TIMELINE_URL = "http://api.wassr.jp/statuses/update.json";
	private static final String UPDATE_CHANNEL_URL = "http://api.wassr.jp/channel_message/update.json?name_en=[channel]";
	private static final String FAVORITE_URL = "http://api.wassr.jp/favorites/create/[rid].json";
	private static final String FAVORITE_DEL_URL = "http://api.wassr.jp/favorites/destroy/[rid].json";
	private static final String FAVORITE_CHANNEL_URL = "http://api.wassr.jp/channel_favorite/toggle.json?channel_message_rid=[rid]";
	public static final String FAVORITE_ICON_URL = "http://wassr.jp/user/[user]/profile_img.png.16";
	private static final String TODO_START = "start";
	private static final String TODO_STOP = "stop";
	private static final String TODO_COMP = "done";
	private static final String TODO_DEL = "delete";
	public static HttpClientWrapper http = new HttpClientWrapper();

	public static ArrayList<WasatterItem> getTimeLine() throws TwitterException {
		return WassrClient.getItems(WassrClient.FRIEND_TIMELINE_URL, false);
	}

	public static ArrayList<WasatterItem> getReply() throws TwitterException {
		return WassrClient.getItems(WassrClient.REPLY_URL, false);
	}

	public static ArrayList<WasatterItem> getMyPost() throws TwitterException {
		return WassrClient.getItems(WassrClient.MYPOST_URL, false);
	}

	public static ArrayList<WasatterItem> getOdai() throws TwitterException {
		return WassrClient.getItems(WassrClient.ODAI_URL, false);
	}

	public static ArrayList<WasatterItem> getChannel(String name)
			throws TwitterException {
		return WassrClient.getItems(CHANNEL_TIMELINE_URL
				.replace("[name]", name), true);
	}

	public static ArrayList<WasatterItem> getItems(String url, boolean channel)
			throws TwitterException {
		ArrayList<WasatterItem> ret = new ArrayList<WasatterItem>();
		if (!Setting.isWassrEnabled()
				|| (!Setting.isLoadWassrTimeline() && WassrClient.FRIEND_TIMELINE_URL
						.equals(url))) {
			return ret;
		}
		JSONArray result;
		try {
			HttpResponse res = http.get(url, getAuthorization());
			result = res.asJSONArray();
			int j = result.length();
			SimpleDateFormat sdf = new SimpleDateFormat(
					"EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
			for (int i = 0; i < j; i++) {
				JSONObject obj = result.getJSONObject(i);
				WasatterItem ws = new WasatterItem();
				ws.service = Wasatter.WASSR;
				ws.rid = obj.getString("rid");
				ws.channel = channel;
				if (channel) {
					JSONObject ch = obj.getJSONObject("channel");
					SpannableStringBuilder sb = new SpannableStringBuilder(ch
							.getString("title"));
					sb.append(" (");
					sb.append(ch.getString("name_en"));
					ws.service = sb.append(")").toString();
					ws.id = obj.getJSONObject("user").getString("login_id");
					ws.name = obj.getJSONObject("user").getString("nick");
					ws.link = CHANNEL_PERMA_LINK.replace("[name]",
							ch.getString("name_en")).replace("[rid]", ws.rid);
					try {
						JSONObject reply = obj.getJSONObject("reply");
						ws.replyUserNick = reply.getJSONObject("user")
								.getString("nick");
						ws.replyMessage = HTMLEntity.unescape(reply
								.getString("body"));
					} catch (JSONException e) {
						// 返信なかったらスルー
					}
					try {
						ws.epoch = sdf.parse(obj.getString("created_on"))
								.getTime() / 1000;
					} catch (ParseException e) {
						ws.epoch = 0;
					}
					ws.text = HTMLEntity.unescape(obj.getString("body"));
				} else {
					ws.id = obj.getString("user_login_id");
					ws.name = obj.getJSONObject("user")
							.getString("screen_name");
					ws.link = obj.getString("link");
					ws.replyUserNick = obj.getString("reply_user_nick");
					ws.replyMessage = HTMLEntity.unescape(obj
							.getString("reply_message"));
					ws.epoch = Long.parseLong(obj.getString("epoch"));

					// 一旦HTMLの解析をして必要な画像をとっておく
					String htmlSrc = HTMLEntity.unescape(obj.getString("html"));
					CharSequence html = Html.fromHtml(htmlSrc,
							new ImageGetter() {

								@Override
								public Drawable getDrawable(String source) {
									// 必要な画像のURLをあらかじめ取得
									Bitmap bmp = Wasatter.images.get(source);
									if (Wasatter.downloadWaitUrls
											.indexOf(source) == -1
											&& bmp == null) {
										Wasatter.downloadWaitUrls.add(source);
									}
									BitmapDrawable bd = new BitmapDrawable(bmp);
									// TODO:解像度ごとにサイズ変えられたらいいなああああ
									Rect bounds = new Rect(0, 0, 20, 20);
									bd.setBounds(bounds);
									return bd;
								}
							}, null);
					ws.html = html;
				}
				String profile = obj.getJSONObject("user").getString(
						"profile_image_url");
				if (Wasatter.downloadWaitUrls.indexOf(profile) == -1
						&& Wasatter.images.get(profile) == null) {
					Wasatter.downloadWaitUrls.add(profile);
				}
				ws.profileImageUrl = profile;
				if ("null".equalsIgnoreCase(ws.replyMessage)) {
					ws.replyMessage = Wasatter.CONTEXT
							.getString(R.string.message_private_message);
				}
				JSONArray favorites = obj.getJSONArray("favorites");
				// お題のイイネは取得しない。
				int fav_count = favorites.length();
				for (int k = 0; k < fav_count; k++) {
					String icon_url = WassrClient.FAVORITE_ICON_URL.replace(
							"[user]", favorites.getString(k));
					ws.favorite.add(favorites.getString(k));
					if (!WassrClient.ODAI_URL.equals(url)
							&& Wasatter.downloadWaitUrls.indexOf(icon_url) == -1
							&& Wasatter.images.get(icon_url) == null) {
						Wasatter.downloadWaitUrls.add(icon_url);
					}
				}
				ws.favorited = ws.favorite.indexOf(Setting.getWassrId()) != -1;
				ret.add(ws);
			}
		} catch (JSONException e) {
			// TODO 自動生成された catch ブロック
		}
		return ret;
	}

	public static ArrayList<WasatterItem> getTodo() {
		ArrayList<WasatterItem> ret = new ArrayList<WasatterItem>();
		if (!Setting.isWassrEnabled()) {
			return ret;
		}
		JSONArray result;
		try {
			result = http.post(WassrClient.TODO_URL, getAuthorization())
					.asJSONArray();
			int j = result.length();
			for (int i = 0; i < j; i++) {
				try {
					JSONObject obj = result.getJSONObject(i);
					WasatterItem ws = new WasatterItem();
					ws.rid = obj.getString("todo_rid");
					ws.text = obj.getString("body");
					ret.add(ws);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (TwitterException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		return ret;
	}

	public static ArrayList<WasatterItem> getChannelList()
			throws TwitterException {
		ArrayList<WasatterItem> ret = new ArrayList<WasatterItem>();
		if (!Setting.isWassrEnabled()) {
			return ret;
		}
		JSONArray result;
		try {
			result = http.get(WassrClient.CHANNEL_LIST_URL, getAuthorization())
					.asJSONObject().getJSONArray("channels");
			int j = result.length();
			for (int i = 0; i < j; i++) {
				try {
					JSONObject obj = result.getJSONObject(i);
					WasatterItem item = new WasatterItem();
					item.id = obj.getString("name_en");
					item.name = obj.getString("title");
					ret.add(item);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (JSONException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return ret;
	}

	public static boolean updateTimeLine(String status, String image)
			throws TwitterException {
		return WassrClient.updateTimeLine(status, null, image);
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
	 * @return
	 */
	public static boolean updateTimeLine(String status, String rid, String image){
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
			StringBody via = new StringBody(Wasatter.VIA);
			StringBody st = new StringBody(status);
			reqEntity.addPart("source", via);
			reqEntity.addPart("status", st);

			//HttpClientの準備
			DefaultHttpClient client = new DefaultHttpClient();
			//認証のセット
			Credentials cred = new UsernamePasswordCredentials(Setting.getWassrId(), Setting.getWassrPass());
			AuthScope scope = new AuthScope("api.wassr.jp", 80);
			client.getCredentialsProvider().setCredentials(scope, cred);
			HttpPost post = new HttpPost(UPDATE_TIMELINE_URL);
			post.setEntity(reqEntity);
			org.apache.http.HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				Log.i("RESPONSE", EntityUtils.toString(resEntity));
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		//ここまで来ることはまずないけど、きたら失敗
		return false;
	}

	public static boolean updateChannel(String channelId, String status,
			String rid) throws TwitterException {
		SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append(UPDATE_CHANNEL_URL.replace("[channel]", channelId));
		sb.append("&body=");
		sb.append(URLEncoder.encode(status));
		if (rid != null) {
			sb.append("&reply_channel_message_rid=");
			sb.append(rid);
		}
		JSONObject res;
		res = http.post(sb.toString(), getAuthorization()).asJSONObject();
		try {
			return res.getString("text") != null
					&& "null".equals(res.getString("error"));
		} catch (JSONException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return false;
	}

	public static boolean favorite(WasatterItem item) {
		JSONObject json;
		try {
			if (!item.favorited) {
				json = http.post(FAVORITE_URL.replace("[rid]", item.rid),
						getAuthorization()).asJSONObject();

			} else {
				json = http.post(FAVORITE_DEL_URL.replace("[rid]", item.rid),
						getAuthorization()).asJSONObject();
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
		} catch (TwitterException e1) {
			e1.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String channelFavorite(WasatterItem item) {
		JSONObject json;
		try {
			json = http.post(FAVORITE_CHANNEL_URL.replace("[rid]", item.rid),
					getAuthorization()).asJSONObject();
			return json.getString("message");
		} catch (TwitterException e1) {
			e1.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "NG";
	}

	public static boolean startTodo(String rid) {
		return WassrClient.todo(rid, WassrClient.TODO_START);
	}

	public static boolean stopTodo(String rid) {
		return WassrClient.todo(rid, WassrClient.TODO_STOP);
	}

	public static boolean completeTodo(String rid) {
		return WassrClient.todo(rid, WassrClient.TODO_COMP);
	}

	public static boolean deleteTodo(String rid) {
		return WassrClient.todo(rid, WassrClient.TODO_DEL);
	}

	public static boolean todo(String rid, String mode) {
		SpannableStringBuilder sb = new SpannableStringBuilder(
				WassrClient.TODO_STATUS_URL);
		sb.append(mode);
		sb.append(".json");
		sb.append("?todo_rid=");
		sb.append(rid);
		try {
			JSONObject json = http.post(sb.toString(), getAuthorization())
					.asJSONObject();
			;
			if (WassrClient.TODO_DEL.equals(mode)) {
				return "ok.removed."
						.equalsIgnoreCase(json.getString("message"));
			}
			return "ok".equalsIgnoreCase(json.getString("message"));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return false;

	}

	public static Authorization getAuthorization() {

		return new BasicAuthorization(Setting.getWassrId(), Setting
				.getWassrPass());

	}
}
