package jp.senchan.android.wasatter.client;

import java.io.IOException;

import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.item.DataStore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BaseClient {
	public static final int TIMELINE = 1;
	public static final int REPLY = 2;
	public static final int MYPOST = 3;
	public static final int ODAI = 4;
	public static final int TODO = 5;
	public static final int CHANNEL_LIST = 6;
	public static final int CHANNEL = 7;
	public static final String[] msg = new String[] { "Timeline", "Reply",
		"My post", "Odai", "TODO", "Channel list", "Channel status" };

	
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean getImageWithCache(Wasatter c,String url) {
		Bitmap bmp = null;
		try {
			String name = (String) DataStore.icon.get(url);
			if(name != null){
				bmp = c.getImage(name);
			}else{
				bmp = getImage(url);
				String filename = c.makeImageFileName();
				c.saveImage(filename, bmp);
				DataStore.icon.put(url, filename);
				DataStore.commit();
			}
			Wasatter.images.put(url, bmp);
			return true;
		} catch (IOException e1) {
			//まず通らない気がするけど一応
		}
		return false;
	}

}
