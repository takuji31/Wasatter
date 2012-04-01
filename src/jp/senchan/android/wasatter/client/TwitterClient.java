/**
 *
 */
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

import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.WasatterItem;
import twitter4j.TwitterException;
import twitter4j.auth.Authorization;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.internal.http.HTMLEntity;
import twitter4j.internal.http.HttpClientWrapper;
import twitter4j.internal.http.HttpResponse;
import android.text.SpannableStringBuilder;

/**
 * @author Senka/Takuji
 *
 */
public class TwitterClient {
    private static final String FRIEND_TIMELINE_URL = "http://twitter.com/statuses/home_timeline.json";
    private static final String REPLY_URL = "http://twitter.com/statuses/replies.json";
    private static final String MYPOST_URL = "http://twitter.com/statuses/user_timeline.json";
    private static final String UPDATE_TIMELINE_URL = "http://twitter.com/statuses/update.json";
    private static final String PERMA_LINK = "http://twitter.com/[id]/status/[rid]";
    private static HttpClientWrapper http = new HttpClientWrapper();

    private Wasatter app;

    public TwitterClient(Wasatter app) {
		// TODO Auto-generated constructor stub
    	this.app = app;
	}

    /*
     * (非 Javadoc)
     *
     * @see jp.senchan.android.wasatter.WasatterClient#getTimeLine(int)
     */
    public  ArrayList<WasatterItem> getTimeLine() throws TwitterException {
        return this.getItems(FRIEND_TIMELINE_URL);
    }

    public  ArrayList<WasatterItem> getReply() throws TwitterException {
        return this.getItems(REPLY_URL);
    }

    public  ArrayList<WasatterItem> getMyPost() throws TwitterException {
        return this.getItems(MYPOST_URL);
    }

    public  ArrayList<WasatterItem> getItems(String url)
            throws TwitterException {
        ArrayList<WasatterItem> ret = new ArrayList<WasatterItem>();
        if (!app.isTwitterEnabled()
                || (!app.isLoadTwitterTimeline() && FRIEND_TIMELINE_URL
                        .equals(url))) {
            return ret;
        }
        HttpResponse res;
        try {
            res = http.get(url, getAuthorization());
            JSONArray result = res.asJSONArray();
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
            int j = result.length();
            for (int i = 0; i < j; i++) {
                JSONObject obj = result.getJSONObject(i);
                WasatterItem ws = new WasatterItem();
                ws.service = Wasatter.SERVICE_TWITTER;
                ws.text = HTMLEntity.unescape(obj.getString("text"));
                ws.name = obj.getJSONObject("user").getString("screen_name");
                ws.id = ws.name;
                ws.rid = obj.getString("id");
                ws.link = PERMA_LINK.replace("[id]", ws.id)
                        .replace("[rid]", ws.rid);
                String profile = obj.getJSONObject("user").getString(
                        "profile_image_url");
                if (Wasatter.downloadWaitUrls.indexOf(profile) == -1
                        && Wasatter.images.get(profile) == null) {
                    Wasatter.downloadWaitUrls.add(profile);
                }
                ws.profileImageUrl = profile;
                ws.replyUserNick = obj.getString("in_reply_to_screen_name");
                ws.epoch = sdf.parse(obj.getString("created_at")).getTime() / 1000;
                ret.add(ws);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
        return ret;

    }

    public  boolean updateTimeLine(String status) {
        return this.updateTimeLine(status, null);
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * jp.senchan.android.wasatter.WasatterClient#updateTimeLine(java.lang.String
     * , java.lang.String)
     */
    public  boolean updateTimeLine(String status, String rid) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(UPDATE_TIMELINE_URL);
        sb.append("?source=");
        try {
            sb.append(URLEncoder.encode(Wasatter.VIA, "UTF-8"));
            sb.append("&status=");
            sb.append(URLEncoder.encode(status, "UTF-8"));
        } catch (UnsupportedEncodingException e2) {
            // TODO 自動生成された catch ブロック
            e2.printStackTrace();
        }
        if (rid != null) {
            sb.append("&in_reply_to_status_id=");
            sb.append(rid);
        }
        HttpResponse response;
        try {
            response = http.post(sb.toString(), getAuthorization());
            JSONObject res = response.asJSONObject();
            return res.getString("text") != null;
        } catch (TwitterException e1) {
            // TODO 自動生成された catch ブロック
            e1.printStackTrace();
        } catch (JSONException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
        return false;
    }

    public  Authorization getAuthorization() {
    	ConfigurationBuilder builder = new ConfigurationBuilder();
    	builder.setOAuthConsumerKey(Wasatter.OAUTH_KEY);
    	builder.setOAuthConsumerSecret(Wasatter.OAUTH_SECRET);
    	builder.setOAuthAccessToken(app.getTwitterToken());
    	builder.setOAuthAccessTokenSecret(app.getTwitterTokenSecret());
        return new OAuthAuthorization(builder.build());
    }

}
