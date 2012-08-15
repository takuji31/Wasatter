package jp.senchan.android.wasatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.auth.AccessToken;

import jp.senchan.android.wasatter.app.TimelineActivity;
import jp.senchan.android.wasatter.client.OldTwitterClient;
import jp.senchan.android.wasatter.client.OldWassrClient;
import jp.senchan.android.wasatter.next.PrefKey;
import jp.senchan.android.wasatter.R;
import jp.senchan.lib.app.BaseApp;

import android.app.AlertDialog;
import android.text.TextUtils;

public class Wasatter extends BaseApp {
	public static final String AGENT = "Wasatter for Android";
	public static final String VIA = "Wasatter";
	public static final String ITEM_DETAIL = "item_detail";
	public static final String SERVICE_WASSR = "Wassr";
	public static final String SERVICE_TWITTER = "Twitter";
	private static final String REGEX_URL = "https?://[^\\s]+";
	public static final String ODAI_DATE_FORMAT = "yyyy/MM/dd";
	public static final String REPLY = "reply";
	public static final int FILENAME_LENGTH = 5;
	private static String ERROR_AUTH = "401";
	private static String ERROR_TMP = "503";
	public static String MODE_POSTING = "mode_posting";
	public static String MODE_DISPLAY = "mode_display";
	public static String MODE_ERROR = "mode_error";
	public static TimelineActivity main;

	private static final int PREF_VERSION = 1;

	public OldWassrClient wassrClient;
	public OldTwitterClient twitterClient;
	public WasatterItem selected;
	
	@Override
	public void onCreate() {
		super.onCreate();
		wassrClient = new OldWassrClient(this);
		twitterClient = new OldTwitterClient(this);
		
		if(getPrefVersion() == 0) {
		    //TODO バージョンアップに必要な処理
		    //イメージストアのDBファイル削除
		    //画像キャッシュ全部削除
		    //全設定削除
			//clearPref();
		    //セットアップウィザード表示
		    
		    //updatePrefVersion();
		}
	}

	public static String getUrl(String text) {
		Pattern pt = Pattern.compile(Wasatter.REGEX_URL,
				Pattern.CASE_INSENSITIVE);
		Matcher mc = pt.matcher(text);
		while (mc.find()) {
			return mc.group();
		}
		return "";
	}

	// TODO リソース外出し、というかそもそもこんなにエラー詳しくないほうがよいのではないか
	public void displayHttpError(String error, String service) {
		String message;
		if (ERROR_TMP.equals(error) && Wasatter.SERVICE_WASSR.equals(service)) {
			message = "エラーが発生しました（Wassr,503）Wassrが一時的に不安定になっている可能性があります。";
		} else if (ERROR_TMP.equals(error)
				&& Wasatter.SERVICE_TWITTER.equals(service)) {
			message = "エラーが発生しました（Twitter,503）API制限を超えた可能性があります。";
		} else if (ERROR_AUTH.equals(error)
				&& Wasatter.SERVICE_WASSR.equals(service)) {
			message = "エラーが発生しました（Wassr,401）IDかパスワードが間違っている、もしくはAPI制限を超えた可能性があります。";
		} else if (ERROR_AUTH.equals(error)
				&& Wasatter.SERVICE_TWITTER.equals(service)) {
			message = "エラーが発生しました（Twitter,401）IDかパスワードが間違っている可能性があります。";
		} else if ("JSON".equals(error)) {
			message = "取得データが破損しています、リロードしてください。";
		} else {
			message = "ネットワークエラーが発生しました。リトライしてください。";
		}
		AlertDialog.Builder ad = new AlertDialog.Builder(main);
		ad.setMessage(message);
		ad.setPositiveButton("閉じる", null);
		ad.show();
	}

	@Override
	public int getDefaultPrefVersion() {
		return PREF_VERSION;
	}
	
	public void showErrorToast() {
		toast(R.string.message_something_wrong).show();
	}

	// アカウント設定
	public boolean isWassrEnabled() {
		return getPref("enable_wassr", false);
	}

	public boolean isTwitterEnabled() {
        return getPref("enable_twitter", false);
    }

	public boolean isDisplayBodyMultiLine() {
        return getPref("display_body_multi_line", false);
    }

	public boolean isLoadWassrTimeline() {
        return getPref(getString(R.string.key_setting_wassr_load_timeline), true);
    }

	public boolean isLoadTwitterTimeline() {
		return getPref(getString(R.string.key_setting_twitter_load_timeline),
				true);
	}

	public void setTwitterToken(String token) {
		setPref("twitter_token", token);
	}

	public void setTwitterTokenSecret(String token) {
		setPref("twitter_token_secret", token);
	}

	// 表示設定
	public boolean isLoadImage() {
		return getPref("display_load_image", true);
	}

	public boolean isLoadFavoriteImage() {
		return getPref("display_load_favorite_image", false);
	}

	public boolean isDisplayButtons() {
		return getPref(getString(R.string.key_setting_display_buttons), true);
	}

	/*
	 * ここから下は次期バージョンで使うよ
	 */
	public String getWassrId() {
		return getPref(PrefKey.WASSR_ID, "");
	}

	public void setWassrId(String value) {
		setPref(PrefKey.WASSR_ID, value);
	}
	
	public String getWassrPass() {
		return getPref(PrefKey.WASSR_PASS, "");
	}

	public void setWassrPass(String value) {
		setPref(PrefKey.WASSR_PASS, value);
	}
	
	public String getTwitterToken() {
		return getPref(PrefKey.TWITTER_TOKEN, "");
	}

	public String getTwitterTokenSecret() {
		return getPref(PrefKey.TWITTER_TOKEN_SECRET, "");
	}
	
	public void setTwitterAccessToken(AccessToken token) {
		setPref(PrefKey.TWITTER_TOKEN, token.getToken());
		setPref(PrefKey.TWITTER_TOKEN_SECRET, token.getTokenSecret());
	}
	
	public boolean isImageLoadEnabled() {
		return getPref(PrefKey.IMAGE_LOAD_ENABLED, true);
	}
	
	public boolean canLoadWassrTimeline() {
		return isLoadWassrTimeline() && !TextUtils.isEmpty(getWassrId()) && !TextUtils.isEmpty(getWassrPass());
	}

	public boolean canLoadTwitterTimeline() {
		return isLoadTwitterTimeline() && !TextUtils.isEmpty(getTwitterToken()) && !TextUtils.isEmpty(getTwitterTokenSecret());
	}


}
