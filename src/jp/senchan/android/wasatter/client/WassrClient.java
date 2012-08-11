package jp.senchan.android.wasatter.client;

import java.util.ArrayList;

import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.WasatterActivity;
import jp.senchan.android.wasatter.model.api.APICallback;
import jp.senchan.android.wasatter.model.api.WasatterStatus;
import jp.senchan.android.wasatter.model.api.impl.wassr.WassrStatus;
import jp.senchan.android.wasatter.next.exception.WassrException;

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
import com.androidquery.auth.BasicHandle;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import android.net.Uri;

public class WassrClient {

	private static final String HOST = "api.wassr.jp";
	private static final String FRIEND_TIMELINE = "/statuses/friends_timeline.json";
	private static final int PORT = 80;

	private AQuery mAQuery;
	private String mLoginId;
	private String mPassword;
	
	public WassrClient(AQuery aq, String loginId, String password) {
		mLoginId = loginId;
		mPassword = password;
		mAQuery = aq;
		mAQuery.auth(new BasicHandle(loginId, password));
	}

	public WassrClient(Wasatter app) {
		mLoginId = app.getWassrId();
		mPassword = app.getWassrPass();
		mAQuery = new AQuery(app);
		mAQuery.auth(new BasicHandle(mLoginId, mPassword));
	}

	public Uri.Builder getRequestUriBuilder(String path) {
		Uri.Builder builder = new Uri.Builder();
		builder.scheme("http");
		builder.authority(HOST);
		builder.path(path);
		return builder;
	}

	public DefaultHttpClient getHttpClient() {
		DefaultHttpClient client = new DefaultHttpClient();
		client.getCredentialsProvider().setCredentials(
				new AuthScope(HOST, PORT),
				new UsernamePasswordCredentials(mLoginId, mPassword));
		return client;
	}

	public ArrayList<WasatterStatus> getFriendTimeline(int page) {
		Uri.Builder builder = getRequestUriBuilder(FRIEND_TIMELINE);
		builder.appendQueryParameter("page", String.valueOf(page));
		DefaultHttpClient client = getHttpClient();
		HttpGet get = new HttpGet(builder.build().toString());
		try {
			HttpResponse res = client.execute(get);
			HttpEntity entity = res.getEntity();
			String str = EntityUtils.toString(entity);
			JSONArray json = new JSONArray(str);
			ArrayList<WasatterStatus> results = new ArrayList<WasatterStatus>();
			int length = json.length();
			for (int i = 0; i < length; i++) {
				WassrStatus s = new WassrStatus(json.getJSONObject(i));
				results.add(s);
			}
			return results;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void friendTimeline(int page,
			final APICallback<ArrayList<WasatterStatus>> callback) {
		Uri.Builder builder = getRequestUriBuilder(FRIEND_TIMELINE);
		builder.appendQueryParameter("page", String.valueOf(page));
		AjaxCallback<JSONArray> cb = new AjaxCallback<JSONArray>() {
			@Override
			public void callback(String url, JSONArray object, AjaxStatus status) {
				ArrayList<WasatterStatus> results = new ArrayList<WasatterStatus>();
				if (object != null) {
					int length = object.length();
					for (int i = 0; i < length; i++) {
						try {
							WassrStatus s = new WassrStatus(
									object.getJSONObject(i));
							results.add(s);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
				callback.runCallback(results, status.getCode());
			}
		};
		mAQuery.ajax(builder.build().toString(), JSONArray.class, cb);
	}

	public void cancel() {
		mAQuery.ajaxCancel();
	}

}
