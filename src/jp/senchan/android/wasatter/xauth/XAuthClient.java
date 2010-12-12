package jp.senchan.android.wasatter.xauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import jp.senchan.android.wasatter.auth.params.OAuthTwitter;
import jp.senchan.android.wasatter.setting.TwitterAccount;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.text.SpannableStringBuilder;

public class XAuthClient extends XAuth {

	public XAuthClient(String url,String method,HashMap<String,String> params){
		this.token = TwitterAccount.get(TwitterAccount.TOKEN, "");
		this.tokenSecret = TwitterAccount.get(TwitterAccount.TOKEN_SECRET, "");
		this.consumerKey = OAuthTwitter.CONSUMER_KEY;
		this.consumerSecret = OAuthTwitter.CONSUMER_SECRET;
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
			SpannableStringBuilder sb = new SpannableStringBuilder(requestUrl);
			sb.append("?");
			sb.append(createParameters());
			if("POST".endsWith(requestMethod)){
				request = new HttpPost(sb.toString());
			}else{
				request = new HttpGet(sb.toString());
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
