package jp.senchan.android.wasatter;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.senchan.android.wasatter.activity.Main;
import jp.senchan.android.wasatter.item.Item;
import jp.senchan.android.wasatter.setting.Setting;
import jp.senchan.android.wasatter.util.DBHelper;

import org.apache.commons.codec.binary.Base64;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.SpannableStringBuilder;

public class Wasatter extends Application {
	public static final String WASSR = "Wassr";
	public static final String TWITTER = "Twitter";
	//TODO 正規表現見直し
	private static final String REGEX_URL = "(http://|https://){1}[\\w\\.\\-/:]+";
	public static final String REPLY = "reply";
	public static Context CONTEXT;
	public static ArrayList<String> downloadWaitUrls = new ArrayList<String>();
	public static HashMap<String, Bitmap> images = new HashMap<String, Bitmap>();
	public DBHelper db;
	public static final int FILENAME_LENGTH = 5;
	public static Main main;
	private static String ERROR_AUTH = "401";
	private static String ERROR_TMP = "503";
	public static String MODE_POSTING = "mode_posting";
	public static String MODE_DISPLAY = "mode_display";
	public static String MODE_ERROR = "mode_error";
	public static final int TAG_TITLE = 0;
	public static final int TAG_MODE = 1;

	public static ArrayList<String> dlIconUrls = new ArrayList<String>();
	public static HashMap<String, Bitmap> icons = new HashMap<String, Bitmap>();

	public static HashMap<String, Item> userWassr = new HashMap<String, Item>();
	public static HashMap<String, Item> userTwitter = new HashMap<String, Item>();

	public static String getUrl(String text) {
		Pattern pt = Pattern.compile(Wasatter.REGEX_URL,
				Pattern.CASE_INSENSITIVE);
		Matcher mc = pt.matcher(text);
		while (mc.find()) {
			return mc.group();
		}
		return "";
	}

	public static void displayHttpError(String error, String service) {
		String message;
		if (ERROR_TMP.equals(error) && Wasatter.WASSR.equals(service)) {
			message = "エラーが発生しました（Wassr,503）Wassrが一時的に不安定になっている可能性があります。";
		} else if (ERROR_TMP.equals(error) && Wasatter.TWITTER.equals(service)) {
			message = "エラーが発生しました（Twitter,503）API制限を超えた可能性があります。";
		} else if (ERROR_AUTH.equals(error) && Wasatter.WASSR.equals(service)) {
			message = "エラーが発生しました（Wassr,401）IDかパスワードが間違っている、もしくはAPI制限を超えた可能性があります。";
		} else if (ERROR_AUTH.equals(error) && Wasatter.TWITTER.equals(service)) {
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

	public static String base64Encode(byte[] src) {
		try {
			byte[] returnArray = Base64.encodeBase64(src);
			return new String(returnArray, "UTF-8");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	//ここからインスタンスメソッド
	//ここより上は廃止、移動すべき
	
	@Override
	public void onCreate() {
		super.onCreate();
		db = new DBHelper(this);
	}
	public String getDataPath(String dir, String filename,
			boolean external) {
		SpannableStringBuilder sb = new SpannableStringBuilder();
		if (external) {
			sb.append(Environment.getExternalStorageDirectory()
					.getAbsolutePath());
		}
		sb.append(CONTEXT.getFilesDir().getParentFile().getAbsolutePath());
		sb.append("/");
		sb.append(dir);
		sb.append("/");
		sb.append(filename);
		new File(sb.toString()).mkdirs();
		return sb.toString();
	}
	public String getDataPath(String dir, String filename) {
		return getDataPath(dir, filename, Setting.get("use_sd", true));
	}
	public String getDataPath(String dir) {
		return getDataPath(dir, "", Setting.get("use_sd", true));
	}

	

}
