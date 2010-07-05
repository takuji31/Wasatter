package jp.senchan.android.wasatter.client;

import java.io.IOException;
import java.util.Date;

import jp.senchan.android.wasatter.Wasatter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BaseClient {
	public static DefaultHttpClient getHttpClient() {
		// HttpClientの準備
		// TODO Staticに突っ込んで使い回そうかと思ったけどどっか1つ詰まったらそのまま通信出来なさそうなのでやめた。
		DefaultHttpClient client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		// コネクションタイムアウトを設定：10秒
		HttpConnectionParams.setConnectionTimeout(params, 10000);
		// データ取得タイムアウトを設定：10秒
		HttpConnectionParams.setSoTimeout(params, 10000);

		return client;
	}

	public static Bitmap getImage(String url) {

		DefaultHttpClient client = getHttpClient();
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse res = client.execute(get);
			HttpEntity entity = res.getEntity();
			byte[] data = EntityUtils.toByteArray(entity);
			return BitmapFactory.decodeByteArray(data, 0, data.length);

		} catch (ClientProtocolException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean getImageWithCache(String url) {
		SQLiteDatabase db = Wasatter.db.getWritableDatabase();
		SQLiteStatement st;
		Bitmap bmp = Wassr.getImage(url);
		if (bmp != null) {
			String filename = Wasatter.makeImageFileName();
			if (Wasatter.saveImage(filename, bmp)) {
				try {
					st = db
							.compileStatement("insert into imagestore(url,filename,created) values(?,?,?)");
					st.bindString(1, url);
					st.bindString(2, filename);
					// 取得した時間（秒単位）を入れておく
					st.bindLong(3, new Date().getTime() / 1000);
					st.executeInsert();
					Wasatter.images.put(url, bmp);
					return true;
				} catch (SQLiteConstraintException e) {
					//FIXME とりあえずその場しのぎ
				}
			}
		}
		return false;
	}

}
