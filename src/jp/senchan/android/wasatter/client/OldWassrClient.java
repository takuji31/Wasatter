package jp.senchan.android.wasatter.client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jp.senchan.android.wasatter2.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.WasatterItem;
import twitter4j.TwitterException;
import twitter4j.auth.Authorization;
import twitter4j.auth.BasicAuthorization;
import twitter4j.internal.http.HTMLEntity;
import twitter4j.internal.http.HttpClientWrapper;
import twitter4j.internal.http.HttpResponse;
import android.text.SpannableStringBuilder;

/**
 * Wassrクラス<br>
 * Wassrとの通信及び、データの入出力を行う。
 *
 * @author Senka/Takuji
 *
 */
public class OldWassrClient {
    private static final String FRIEND_TIMELINE_URL = "http://api.wassr.jp/statuses/friends_timeline.json";
    private static final String CHANNEL_TIMELINE_URL = "http://api.wassr.jp/channel_message/list.json?name_en=[name]";
    private static final String CHANNEL_PERMA_LINK = "http://wassr.jp/channel/[name]/messages/[rid]";
    private static final String REPLY_URL = "http://api.wassr.jp/statuses/replies.json";
    private static final String MYPOST_URL = "http://api.wassr.jp/statuses/user_timeline.json";
    private static final String ODAI_URL = "http://api.wassr.jp/statuses/user_timeline.json?id=odai";
    private static final String CHANNEL_LIST_URL = "http://api.wassr.jp/channel_user/user_list.json";
    private static final String UPDATE_TIMELINE_URL = "http://api.wassr.jp/statuses/update.json";
    private static final String UPDATE_CHANNEL_URL = "http://api.wassr.jp/channel_message/update.json?name_en=[channel]";
    private static final String FAVORITE_URL = "http://api.wassr.jp/favorites/create/[rid].json";
    private static final String FAVORITE_DEL_URL = "http://api.wassr.jp/favorites/destroy/[rid].json";
    private static final String FAVORITE_CHANNEL_URL = "http://api.wassr.jp/channel_favorite/toggle.json?channel_message_rid=[rid]";
    public static final String FAVORITE_ICON_URL = "http://wassr.jp/user/[user]/profile_img.png.16";
    private HttpClientWrapper httpClient = new HttpClientWrapper();
    private Wasatter app;

    public OldWassrClient(Wasatter app) {
	    httpClient = new HttpClientWrapper();
	    this.app = app;
	}

    public ArrayList<WasatterItem> getTimeLine() throws TwitterException {
        return getItems(OldWassrClient.FRIEND_TIMELINE_URL, false);
    }

    public ArrayList<WasatterItem> getReply() throws TwitterException {
        return getItems(OldWassrClient.REPLY_URL, false);
    }

    public ArrayList<WasatterItem> getMyPost() throws TwitterException {
        return getItems(OldWassrClient.MYPOST_URL, false);
    }

    public ArrayList<WasatterItem> getOdai() throws TwitterException {
        return getItems(OldWassrClient.ODAI_URL, false);
    }

    public ArrayList<WasatterItem> getChannel(String name)
            throws TwitterException {
        return getItems(
                CHANNEL_TIMELINE_URL.replace("[name]", name), true);
    }

    public ArrayList<WasatterItem> getItems(String url, boolean channel)
            throws TwitterException {
        ArrayList<WasatterItem> ret = new ArrayList<WasatterItem>();
        if (!app.isWassrEnabled()
                || (!app.isLoadWassrTimeline() && OldWassrClient.FRIEND_TIMELINE_URL
                        .equals(url))) {
            return ret;
        }
        JSONArray result;
        try {
            HttpResponse res = httpClient.get(url, getAuthorization());
            result = res.asJSONArray();
            int j = result.length();
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
            for (int i = 0; i < j; i++) {
                JSONObject obj = result.getJSONObject(i);
                WasatterItem ws = new WasatterItem();
                ws.service = Wasatter.SERVICE_WASSR;
                ws.rid = obj.getString("rid");
                ws.channel = channel;
                if (channel) {
                    JSONObject ch = obj.getJSONObject("channel");
                    SpannableStringBuilder sb = new SpannableStringBuilder(
                            ch.getString("title"));
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
                    ws.text = HTMLEntity.unescape(obj.getString("text"));
                }
                String profile = obj.getJSONObject("user").getString(
                        "profile_image_url");
                ws.profileImageUrl = profile;
                if ("null".equalsIgnoreCase(ws.replyMessage)) {
                    ws.replyMessage = this.app.getString(R.string.message_private_message);
                }
                JSONArray favorites = obj.getJSONArray("favorites");
                // お題のイイネは取得しない。
                int fav_count = favorites.length();
                for (int k = 0; k < fav_count; k++) {
                    ws.favorite.add(favorites.getString(k));
                }
                ws.favorited = ws.favorite.indexOf(app.getWassrId()) != -1;
                ret.add(ws);
            }
        } catch (JSONException e) {
            // TODO 自動生成された catch ブロック
        }
        return ret;
    }

    public ArrayList<WasatterItem> getChannelList()
            throws TwitterException {
        ArrayList<WasatterItem> ret = new ArrayList<WasatterItem>();
        if (!app.isWassrEnabled()) {
            return ret;
        }
        JSONArray result;
        try {
            result = httpClient.get(OldWassrClient.CHANNEL_LIST_URL, getAuthorization())
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

    public boolean updateTimeLine(String status) throws TwitterException {
        return updateTimeLine(status, null);
    }

    public boolean updateTimeLine(String status, String rid)
            throws TwitterException {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(UPDATE_TIMELINE_URL);
        sb.append("?source=");
        try {
            sb.append(URLEncoder.encode(Wasatter.VIA, "UTF-8"));
            sb.append("&status=");
            sb.append(URLEncoder.encode(status, "UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            // TODO 自動生成された catch ブロック
            e1.printStackTrace();
        }
        if (rid != null) {
            sb.append("&reply_status_rid=");
            sb.append(rid);
        }
        JSONObject res;
        res = httpClient.post(sb.toString(), getAuthorization()).asJSONObject();
        try {
            return res.getString("text") != null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateChannel(String channelId, String status,
            String rid) throws TwitterException {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(UPDATE_CHANNEL_URL.replace("[channel]", channelId));
        sb.append("&body=");
        try {
            sb.append(URLEncoder.encode(status, "UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            // TODO 自動生成された catch ブロック
            e1.printStackTrace();
        }
        if (rid != null) {
            sb.append("&reply_channel_message_rid=");
            sb.append(rid);
        }
        JSONObject res;
        res = httpClient.post(sb.toString(), getAuthorization()).asJSONObject();
        try {
            return res.getString("text") != null
                    && "null".equals(res.getString("error"));
        } catch (JSONException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
        return false;
    }

    public boolean favorite(WasatterItem item) {
        JSONObject json;
        try {
            if (!item.favorited) {
                json = httpClient.post(FAVORITE_URL.replace("[rid]", item.rid),
                        getAuthorization()).asJSONObject();

            } else {
                json = httpClient.post(FAVORITE_DEL_URL.replace("[rid]", item.rid),
                        getAuthorization()).asJSONObject();
            }
            boolean result = json.getString("status").equalsIgnoreCase("ok");
            if (result) {
                if (item.favorited) {
                    item.favorite.remove(app.getWassrId());
                } else {
                    item.favorite.add(app.getWassrId());
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

    public String channelFavorite(WasatterItem item) {
        JSONObject json;
        try {
            json = httpClient.post(FAVORITE_CHANNEL_URL.replace("[rid]", item.rid),
                    getAuthorization()).asJSONObject();
            return json.getString("message");
        } catch (TwitterException e1) {
            e1.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "NG";
    }

    public Authorization getAuthorization() {

        return new BasicAuthorization(app.getWassrId(),
                app.getWassrPass());

    }
}
