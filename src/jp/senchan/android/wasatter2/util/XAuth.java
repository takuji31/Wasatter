package jp.senchan.android.wasatter2.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.text.SpannableStringBuilder;
import android.util.Log;

import jp.senchan.android.wasatter.auth.params.XAuthTwitter;
import jp.senchan.android.wasatter2.Setting;
import jp.senchan.android.wasatter2.Wasatter;

public class XAuth {

	private String consumerKey;
	private String consumerSecret;
	private String token;
	private String tokenSecret;
	private String signature;
	private String userId;
	private String password;

	private static final String oauthVersion = "1,0";
	private static final String oauthSignatureMethod = "HMAC-SHA1";
	private static final String tokenRequestUrl = "https://api.twitter.com/oauth/access_token";

	public XAuth(String userId,String password) {

		this.userId = userId;
		this.password = password;
		this.consumerKey = XAuthTwitter.CONSUMER_KEY;
		this.consumerSecret = XAuthTwitter.CONSUMER_SECRET;
	}

	public void request(){
		//タイムスタンプ
		String timestamp = Long.toString(System.currentTimeMillis());
		//nonceの生成
		String nonce = RandomStringUtils.randomAscii(8);


		try {
		//HttpClientの準備
		DefaultHttpClient client = new DefaultHttpClient();
		//認証のセット
		Credentials cred = new UsernamePasswordCredentials(Setting.getWassrId(), Setting.getWassrPass());
		AuthScope scope = new AuthScope("api.wassr.jp", 80);
		client.getCredentialsProvider().setCredentials(scope, cred);
		HttpPost post = new HttpPost(tokenRequestUrl);
		org.apache.http.HttpResponse response = client.execute(post);
		HttpEntity resEntity = response.getEntity();
		if (resEntity != null) {
			Log.i("RESPONSE", EntityUtils.toString(resEntity));
			return;
		}
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
	}
	public String getSignatureBaseString(String method,String url,String params){
		String amp = "&";
		SpannableStringBuilder sb = new SpannableStringBuilder(method);
		sb.append(amp);
		sb.append(URLEncoder.encode(url));
		sb.append(amp);
		sb.append(URLEncoder.encode(params));
		return sb.toString();
	}
	private String getRequestParameters() {
		SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append("oauth_consumer_key=");
		sb.append(consumerKey);
		sb.append("&oauth_nonce=");
		sb.append(URLEncoder.encode(RandomStringUtils.randomAscii(8)));
		sb.append("&oauth_signature_method=");
		sb.append(oauthSignatureMethod);
		sb.append("&oauth_timestamp=");
		sb.append(Long.toString(System.currentTimeMillis()));
		sb.append("&oauth_version=");
		sb.append(oauthVersion);

		return sb.toString();
	}

}
