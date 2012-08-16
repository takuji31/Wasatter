package jp.senchan.android.wasatter.client;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.model.api.APICallback;
import jp.senchan.android.wasatter.model.api.WasatterStatus;
import jp.senchan.android.wasatter.model.api.impl.wassr.WassrStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.auth.BasicHandle;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import android.net.Uri;
import android.text.TextUtils;

public class WassrClient implements WasatterApiClient {

	private static final String HOST = "api.wassr.jp";
	private static final String FRIEND_TIMELINE = "/statuses/friends_timeline.json";
	private static final String MENSION = "/statuses/replies.json";
	private static final String USER_TIMELINE = "/statuses/user_timeline.json";
	private static final String UPDATE_STATUS = "/statuses/update.json";
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
		//リトライを禁止する
		client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
		HttpParams params = client.getParams();
		//コネクションのタイムアウトを最適化
		HttpConnectionParams.setConnectionTimeout(params, 20000); 
		HttpConnectionParams.setSoTimeout(params, 20000); 
		return client;
	}
	
	public boolean updateStatus(String body, String imagePath, String replyRid) {
		
		try {
			DefaultHttpClient client = getHttpClient();
			Uri.Builder builder = getRequestUriBuilder(UPDATE_STATUS);
			HttpPost post = new HttpPost(builder.build().toString());
			MultipartEntity entity = new MultipartEntity();
			entity.addPart("status", new StringBody(body, Charset.forName("UTF-8")));
			entity.addPart("source", new StringBody(Wasatter.VIA));
			if (!TextUtils.isEmpty(replyRid)) {
				entity.addPart("reply_status_rid", new StringBody(replyRid));
			}
			if (imagePath != null) {
				entity.addPart("image", new FileBody(new File(imagePath)));
			}
			post.setEntity(entity);
			HttpResponse res = client.execute(post);
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String jsonString = EntityUtils.toString(res.getEntity());
				JSONObject obj = new JSONObject(jsonString);
				obj.length();
				return true;
			}
			
			return false;
		} catch (NullPointerException e) {
			// TODO: handle exception
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public ArrayList<WasatterStatus> retrieveTimeline(String path, int page, HashMap<String, String> params) {
		Uri.Builder builder = getRequestUriBuilder(path);
		builder.appendQueryParameter("page", String.valueOf(page));
		if (params != null) {
			Set<Entry<String, String>> set = params.entrySet();
			for (Entry<String, String> entry : set) {
				builder.appendQueryParameter(entry.getKey(), entry.getValue());
			}
		}
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

	public ArrayList<WasatterStatus> getFriendTimeline(int page) {
		return retrieveTimeline(FRIEND_TIMELINE, page, null);
	}

	public ArrayList<WasatterStatus> getMension(int page) {
		return retrieveTimeline(MENSION, page, null);
	}
	
	public ArrayList<WasatterStatus> getUserTimeline(int page) {
		return retrieveTimeline(USER_TIMELINE, page, null);
	}
	
	public ArrayList<WasatterStatus> getOdaiTimeline(int page) {
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("id", "odai");
		return retrieveTimeline(USER_TIMELINE, page, param);
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
