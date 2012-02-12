package jp.senchan.android.wasatter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	public static ArrayList<String> downloadWaitUrls = new ArrayList<String>();
	public static HashMap<String, Bitmap> images = new HashMap<String, Bitmap>();
	public static SQLiteHelperImageStore imageStore;
	public static final int FILENAME_LENGTH = 5;
	public static ActivityMain main;
	private static String ERROR_AUTH = "401";
	private static String ERROR_TMP = "503";
	public static String MODE_POSTING = "mode_posting";
	public static String MODE_DISPLAY = "mode_display";
	public static String MODE_ERROR = "mode_error";

	public static long cacheExpire() {
		return new Date().getTime() / 1000 - 2 * 24 * 60 * 60;
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

	public static String makeImageFileName() {
		String names = "abcdefghijklmnopqrstuvwxyz0123456789";
		SpannableStringBuilder path = new SpannableStringBuilder();
		path.append(getImagePath());
		SpannableStringBuilder ret = new SpannableStringBuilder();
		int max = 35;
		Random ran = new Random(new Date().getTime());
		for (int i = 0; i < FILENAME_LENGTH; i++) {
			int j = ran.nextInt(max + 1);
			ret.append(names.charAt(j));
		}
		ret.append(".png");
		path.append(ret);
		// 存在しないファイル名になるまで再帰呼び出し
		if (new File(path.toString()).exists()) {
			return makeImageFileName();
		} else {
			return ret.toString();
		}
	}

	public static boolean saveImage(String name, byte[] data) {
		// ファイル名の生成
		SpannableStringBuilder path = new SpannableStringBuilder();
		path.append(getImagePath());
		path.append(name);
		File save = new File(path.toString());
		// 親ディレクトリがなければ生成する
		save.getParentFile().mkdirs();
		OutputStream out = null;
		try {
			// ファイル出力ストリームのオープン
			out = new FileOutputStream(path.toString());

			// バイト配列の書き込み
			out.write(data, 0, data.length);

			// ファイル出力ストリームのクローズ
			out.close();
			return true;
		} catch (Exception e) {
			try {
				if (out != null)
					out.close();
			} catch (Exception e2) {
			}
			return false;
		}
	}

	public static Bitmap getImage(String name) {
		// ファイル名の生成
		SpannableStringBuilder path = new SpannableStringBuilder();
		path.append(getImagePath());
		path.append(name);
		try {
			return BitmapFactory.decodeStream(new FileInputStream(path
					.toString()));
		} catch (Exception e) {
			return null;
		}
	}

	public static void deleteImage(String name) {
		try {
			File file = new File(new SpannableStringBuilder(Wasatter
					.getImagePath()).append(name).toString());
			file.delete();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static String getImagePath() {
		SpannableStringBuilder path = new SpannableStringBuilder();
		path.append("/data/data/");
		path.append(CONTEXT.getPackageName());
		path.append("/imagecache/");
		return path.toString();
	}

	public static void deleteImageCache() {
		Wasatter.images.clear();
		SQLiteDatabase rdb = Wasatter.imageStore.getReadableDatabase();
		SQLiteDatabase wdb = Wasatter.imageStore.getWritableDatabase();
		Cursor c = rdb.rawQuery("select filename from imagestore", null);
		c.moveToFirst();
		int count = c.getCount();
		for (int i = 0; i < count; i++) {
			String imageName = c.getString(0);
			Wasatter.deleteImage(imageName);
			c.moveToNext();
		}
		c.close();
		wdb.execSQL("delete from imagestore");
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
