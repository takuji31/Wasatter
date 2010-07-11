package jp.senchan.android.wasatter.xauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import jp.senchan.android.wasatter.auth.params.XAuthTwitter;
import jp.senchan.android.wasatter.setting.TwitterAccount;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

public class XAuthClient extends XAuth {

	private HashMap<String, String> params;

	public XAuthClient(String url,String method,HashMap<String,String> params){
		this.token = TwitterAccount.get(TwitterAccount.TOKEN, "");
		this.tokenSecret = TwitterAccount.get(TwitterAccount.TOKEN_SECRET, "");
		this.consumerKey = XAuthTwitter.CONSUMER_KEY;
		this.consumerSecret = XAuthTwitter.CONSUMER_SECRET;
		this.requestUrl = url;
		this.requestMethod = method;
		this.params = params;
	}
	/**
	 * APIにリクエストを実行するメソッド
	 * @return HTTPレスポンス
	 */
	public org.apache.http.HttpResponse request() {
		oauthParametersMap = createParametersMap();
		try {
			// HttpClientの準備
			DefaultHttpClient client = new DefaultHttpClient();
			// リクエストの作成
			HttpUriRequest request;
			if("POST".endsWith(requestMethod)){
				request = new HttpPost(requestUrl);
			}else{
				request = new HttpGet(requestUrl);
			}
			// パラメーターをセットする
			if (params != null) {
				HttpParams param = request.getParams();
				Iterator<Entry<String, String>> it = params.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, String> entry = it.next();
					param.setParameter(entry.getKey(), entry.getValue());
				}
			}

			//認証ヘッダーの付加
			request.setHeader("Authorization", createAuthorizationValue());
			return client.execute(request);
		} catch (UnsupportedEncodingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return null;
	}
}
