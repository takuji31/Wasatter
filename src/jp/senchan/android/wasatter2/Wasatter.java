package jp.senchan.android.wasatter2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;

import jp.senchan.android.wasatter2.activity.Main;
import jp.senchan.android.wasatter2.util.DBHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.text.SpannableStringBuilder;

public class Wasatter {
	public static final String AGENT = "Wasatter for Android";
	public static final String VIA = "Wasatter";
	public static final String VIA_BETA = "Wasatter BETA";
	public static final String WASSR = "Wassr";
	public static final String TWITTER = "Twitter";
	private static final String REGEX_URL = "(http://|https://){1}[\\w\\.\\-/:]+";
	public static final String ODAI_DATE_FORMAT = "yyyy/MM/dd";
	public static final String REPLY = "reply";
	public static Context CONTEXT;
	public static ArrayList<String> downloadWaitUrls = new ArrayList<String>();
	public static HashMap<String, Bitmap> images = new HashMap<String, Bitmap>();
	public static DBHelper db;
	public static final int FILENAME_LENGTH = 5;
	public static Main main;
	private static String ERROR_AUTH = "401";
	private static String ERROR_TMP = "503";
	public static String MODE_POSTING = "mode_posting";
	public static String MODE_DISPLAY = "mode_display";
	public static String MODE_ERROR = "mode_error";

	/**
	 * キャッシュの有効期限を取得する
	 * @return キャッシュの有効期限（秒数）
	 */
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

	public static boolean saveImage(String name, Bitmap image) {
		return saveImage(getImagePath(),name, image,CompressFormat.PNG,80);
	}
	public static boolean saveTempImage(Bitmap image) {
		return saveImage(getTempPath(),"temp.jpg", image,CompressFormat.JPEG,80);
	}

	public static boolean saveImage(String path,String name, Bitmap image,CompressFormat format,int quality) {
		// ファイル名の生成
		SpannableStringBuilder fullPath = new SpannableStringBuilder();
		fullPath.append(path);
		fullPath.append(name);
		// 親ディレクトリがなければ生成する
		File save = new File(fullPath.toString());
		save.getParentFile().mkdirs();
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(fullPath.toString());
		} catch (FileNotFoundException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		try {
			image.compress(format, quality, out);
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
		InputStream in = null;
		ByteArrayOutputStream out = null;
		// ファイル名の生成
		SpannableStringBuilder path = new SpannableStringBuilder();
		path.append(getImagePath());
		path.append(name);
		try {
			return BitmapFactory.decodeStream(new FileInputStream(path
					.toString()));
		} catch (Exception e) {
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (Exception e2) {
			}
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

	public static String getTempPath(){
		String sdPath = Environment.getExternalStorageDirectory().getPath();
		return new SpannableStringBuilder(sdPath).append("/wasatter/").toString();
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
		SQLiteDatabase rdb = Wasatter.db.getReadableDatabase();
		SQLiteDatabase wdb = Wasatter.db.getWritableDatabase();
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

	public static void displayHttpError(String error, String service) {
		String message;
		if (ERROR_TMP.equals(error) && Wasatter.WASSR.equals(service)) {
			message = "エラーが発生しました（Wassr,503）Wassrが一時的に不安定になっている可能性があります。";
		} else if (ERROR_TMP.equals(error)
				&& Wasatter.TWITTER.equals(service)) {
			message = "エラーが発生しました（Twitter,503）API制限を超えた可能性があります。";
		} else if (ERROR_AUTH.equals(error)
				&& Wasatter.WASSR.equals(service)) {
			message = "エラーが発生しました（Wassr,401）IDかパスワードが間違っている、もしくはAPI制限を超えた可能性があります。";
		} else if (ERROR_AUTH.equals(error)
				&& Wasatter.TWITTER.equals(service)) {
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

	public static String base64Encode(String src){
		try {
			byte[] srcArray = src.getBytes("UTF-8");
			byte[] returnArray = Base64.encodeBase64(srcArray);
			return new String(returnArray,"UTF-8");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

 }
