package jp.senchan.android.wasatter.next.client;

import java.util.ArrayList;

import jp.senchan.android.wasatter.next.exception.WassrException;
import jp.senchan.android.wasatter.next.model.dataobject.WassrStatus;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import twitter4j.internal.org.json.JSONArray;

import android.net.Uri;

public class NewWassrClient {

	private static final String HOST = "api.wassr.jp";
	private static final int PORT = 80;
	
	private static final String FRIEND_TIMELINE = "/statuses/friends_timeline.json";

	private String mLoginId;
	private String mPassword;

	public NewWassrClient(String loginId, String password) {
		mLoginId = loginId;
		mPassword = password;
	}

	public DefaultHttpClient getHttpClient() {
		DefaultHttpClient client = new DefaultHttpClient();

		client.getCredentialsProvider().setCredentials(
				new AuthScope(HOST, PORT),
				new UsernamePasswordCredentials(mLoginId, mPassword));
		return client;
	}
	
	public Uri.Builder getRequestUriBuilder(String path) {
		Uri.Builder builder = new Uri.Builder();
		builder.scheme("http");
		builder.authority(HOST);
		builder.path(path);
		return builder;
	}
	
	public ArrayList<WassrStatus> friendTimeline(int page) throws WassrException {
		Uri.Builder builder = getRequestUriBuilder(FRIEND_TIMELINE);
		builder.appendQueryParameter("page", String.valueOf(page));
		DefaultHttpClient client = getHttpClient();
		HttpGet get = new HttpGet(builder.build().toString());
		try {
			HttpResponse res = client.execute(get);
			HttpEntity entity = res.getEntity();
			String data = EntityUtils.toString(entity);
			JSONArray json = new JSONArray(data);
			//TODO JSON解析
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WassrException();
		}
		return null;
	}
}
