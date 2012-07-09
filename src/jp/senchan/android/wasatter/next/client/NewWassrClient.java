package jp.senchan.android.wasatter.next.client;

import java.util.ArrayList;

import jp.senchan.android.wasatter.next.exception.WassrException;
import jp.senchan.android.wasatter.next.listener.APICallback;
import jp.senchan.android.wasatter.next.model.api.WasatterStatus;
import jp.senchan.android.wasatter.next.model.api.WassrStatus;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

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

	public void friendTimeline(int page, AQuery aq, final APICallback<ArrayList<WassrStatus>> callback) {
		Uri.Builder builder = getRequestUriBuilder(FRIEND_TIMELINE);
		builder.appendQueryParameter("page", String.valueOf(page));
		aq.ajax(builder.build().toString(), JSONArray.class, new AjaxCallback<JSONArray>(){
			@Override
			public void callback(String url, JSONArray object, AjaxStatus status) {
				ArrayList<WassrStatus> results = new ArrayList<WassrStatus>();
				if (object != null) {
					//TODO JSON解析
					int length = object.length();
					for (int i = 0; i < length; i++) {
						try {
							WassrStatus s = new WassrStatus(object.getJSONObject(i));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
				callback.callback(url, results, status);
			}
		});
	}
}
