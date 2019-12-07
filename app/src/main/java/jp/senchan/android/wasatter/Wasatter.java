package jp.senchan.android.wasatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableStringBuilder;

public class Wasatter extends Application {
	public static final String AGENT = "Wasatter for Android";
	public static final String VIA = "Wasatter";
	public static final String ITEM_DETAIL = "item_detail";
	public static final String SERVICE_WASSR = "Wassr";
	public static final String SERVICE_TWITTER = "Twitter";
	private static final String REGEX_URL = "https?://[^\\s]+";
	public static final String ODAI_DATE_FORMAT = "yyyy/MM/dd";
	public static final String REPLY = "reply";
	public static final String OAUTH_KEY = "5WURvsXWy6pwsFyJvR7Yw";
	public static final String OAUTH_SECRET = "Ya9SKf2G0iPDRzrGeFrwTVe4eIgsjO8t4chCQl62vs";
	public static final String WASSR_OAUTH_REQUEST = "http://wassr.jp/auth/?app_key=9jJkQds39nOUjHFPZ6LgpbE8cuXVoJOS&sig=bed07c761de34c88af1a3cf1fd03b60ce1235715";
	public static Context CONTEXT;
	public static HashMap<String, Bitmap> images = new HashMap<String, Bitmap>();
	public static ActivityMain main;
	private static String ERROR_AUTH = "401";
	private static String ERROR_TMP = "503";
	public static String MODE_POSTING = "mode_posting";
	public static String MODE_DISPLAY = "mode_display";
	public static String MODE_ERROR = "mode_error";

	public static String getUrl(String text) {
		Pattern pt = Pattern.compile(Wasatter.REGEX_URL,
				Pattern.CASE_INSENSITIVE);
		Matcher mc = pt.matcher(text);
		while (mc.find()) {
			return mc.group();
		}
		return "";
	}

	//TODO リソース外出し、というかそもそもこんなにエラー詳しくないほうがよいのではないか
	public static void displayHttpError(String error, String service) {
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
		AlertDialog.Builder ad = new AlertDialog.Builder(Wasatter.main);
		ad.setMessage(message);
		ad.setPositiveButton("閉じる", null);
		ad.show();
	}
}
