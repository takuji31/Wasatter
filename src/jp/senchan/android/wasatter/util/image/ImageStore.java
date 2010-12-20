package jp.senchan.android.wasatter.util.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Random;

import jp.senchan.android.wasatter.Wasatter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.text.SpannableStringBuilder;

public class ImageStore {

	public static void deleteImageCache() {
		// TODO JDBM仕様に書き換える
		Wasatter.images.clear();
		SQLiteDatabase rdb = Wasatter.db.getReadableDatabase();
		Cursor c = rdb.rawQuery("select filename from imagestore", null);
		c.moveToFirst();
		int count = c.getCount();
		for (int i = 0; i < count; i++) {
			String imageName = c.getString(0);
			ImageStore.deleteImage(imageName);
			c.moveToNext();
		}
		c.close();
		SQLiteDatabase wdb = Wasatter.db.getWritableDatabase();
		wdb.execSQL("delete from imagestore");
	}

	public static String getImagePath() {
		return Wasatter.getDataPath("imagecache");
	}

	public static void deleteImage(String name) {
		try {
			File file = new File(new SpannableStringBuilder(getImagePath()).append(name).toString());
			file.delete();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static String getImageTempPath() {
		return Wasatter.getDataPath(".temp_image");
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
			e.printStackTrace();
			return null;
		}
	}

	public static boolean saveImage(String path, String name, Bitmap image,
			CompressFormat format, int quality) {
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

	public static boolean saveTempImage(Bitmap image) {
		return saveImage(getImageTempPath(), "temp.jpg", image,
				CompressFormat.JPEG, 80);
	}

	public static boolean saveImage(String name, Bitmap image) {
		return saveImage(getImagePath(), name, image, CompressFormat.PNG, 80);
	}

	public static String makeImageFileName() {
		String names = "abcdefghijklmnopqrstuvwxyz0123456789";
		SpannableStringBuilder path = new SpannableStringBuilder();
		path.append(getImagePath());
		SpannableStringBuilder ret = new SpannableStringBuilder();
		int max = 35;
		Random ran = new Random(new Date().getTime());
		for (int i = 0; i < Wasatter.FILENAME_LENGTH; i++) {
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

	/**
	 * キャッシュの有効期限を取得する
	 * 
	 * @return キャッシュの有効期限（秒数）
	 */
	public static long cacheExpire() {
		return new Date().getTime() / 1000 - 2 * 24 * 60 * 60;
	}

}
