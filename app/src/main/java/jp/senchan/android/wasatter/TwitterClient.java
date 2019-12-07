/**
 *
 */
package jp.senchan.android.wasatter;

import android.text.SpannableStringBuilder;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import jp.senchan.android.wasatter.activity.Setting;
import twitter4j.HttpClientFactory;
import twitter4j.HttpResponse;
import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.TwitterException;
import twitter4j.auth.Authorization;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.ConfigurationBuilder;

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

    /*
     * (非 Javadoc)
     *
     * @see jp.senchan.android.wasatter.WasatterClient#getTimeLine(int)
     */
    public static ArrayList<WasatterItem> getTimeLine() throws TwitterException {
        return TwitterClient.getItems(TwitterClient.FRIEND_TIMELINE_URL);
    }

    public static ArrayList<WasatterItem> getReply() throws TwitterException {
        return TwitterClient.getItems(TwitterClient.REPLY_URL);
    }

    public static ArrayList<WasatterItem> getMyPost() throws TwitterException {
        return TwitterClient.getItems(TwitterClient.MYPOST_URL);
    }

    public static ArrayList<WasatterItem> getItems(String url)
            throws TwitterException {
        ArrayList<WasatterItem> ret = new ArrayList<WasatterItem>();
        if (!Setting.isTwitterEnabled()
                || (!Setting.isLoadTwitterTimeline() && TwitterClient.FRIEND_TIMELINE_URL
                .equals(url))) {
            return ret;
        }
        HttpResponse res;
        try {
            res = HttpClientFactory.getInstance().get(url, null, getAuthorization(), null);
            JSONArray result = res.asJSONArray();
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
            int j = result.length();
            for (int i = 0; i < j; i++) {
                JSONObject obj = result.getJSONObject(i);
                WasatterItem ws = new WasatterItem();
                ws.service = Wasatter.SERVICE_TWITTER;
                // TODO: unescape html
                ws.text = obj.getString("text");
                ws.name = obj.getJSONObject("user").getString("screen_name");
                ws.id = ws.name;
                ws.rid = obj.getString("id");
                ws.link = TwitterClient.PERMA_LINK.replace("[id]", ws.id)
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
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        return ret;

    }

    public static boolean updateTimeLine(String status) {
        return TwitterClient.updateTimeLine(status, null);
    }

    /*
     * (非 Javadoc)
     *
     * @see
     * jp.senchan.android.wasatter.WasatterClient#updateTimeLine(java.lang.String
     * , java.lang.String)
     */
    public static boolean updateTimeLine(String status, String rid) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(TwitterClient.UPDATE_TIMELINE_URL);
        sb.append("?source=");
        sb.append(URLEncoder.encode(Wasatter.VIA));
        sb.append("&status=");
        sb.append(URLEncoder.encode(status));
        if (rid != null) {
            sb.append("&in_reply_to_status_id=");
            sb.append(rid);
        }
        HttpResponse response;
        try {
            response = HttpClientFactory.getInstance().post(sb.toString(), null, getAuthorization(), null);
            JSONObject res = response.asJSONObject();
            return res.getString("text") != null;
        } catch (TwitterException | JSONException e1) {
            e1.printStackTrace();
        }
        return false;
    }

    public static Authorization getAuthorization() {
        return new OAuthAuthorization(
                new ConfigurationBuilder()
                        .setOAuthAccessToken(Setting.getTwitterToken())
                        .setOAuthAccessTokenSecret(Setting.getTwitterTokenSecret())
                        .setOAuthConsumerKey(Wasatter.OAUTH_KEY)
                        .setOAuthConsumerSecret(Wasatter.OAUTH_SECRET)
                        .build()
        );
    }

}
