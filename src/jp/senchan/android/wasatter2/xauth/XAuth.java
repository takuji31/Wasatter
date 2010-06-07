package jp.senchan.android.wasatter2.xauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Map.Entry;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import jp.senchan.android.wasatter.auth.params.XAuthTwitter;
import jp.senchan.android.wasatter2.Wasatter;
import jp.senchan.android.wasatter2.setting.TwitterAccount;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.text.SpannableStringBuilder;

public class XAuth {

	private String consumerKey;
	private String consumerSecret;
	private String token;
	private String tokenSecret;
	private HashMap<String, String> params;
	private SortedMap<String, String> oauthParametersMap;
	private String userId;
	private String password;
	private String requestUrl;
	private String requestMethod;

	private static final String oauthVersion = "1.0";
	private static final String oauthSignatureMethod = "HMAC-SHA1";
	private static final String algorithm = "HmacSHA1";
	private static final String xauthAuthMode = "client_auth";
	private static final String tokenRequestUrl = "https://api.twitter.com/oauth/access_token";

	public XAuth(String userId, String password) {

		this.userId = userId;
		this.password = password;
		this.consumerKey = XAuthTwitter.CONSUMER_KEY;
		this.consumerSecret = XAuthTwitter.CONSUMER_SECRET;
	}
	public XAuth(String url,String method,HashMap<String,String> params){
		this.token = TwitterAccount.get(TwitterAccount.TOKEN, "");
		this.tokenSecret = TwitterAccount.get(TwitterAccount.TOKEN_SECRET, "");
		this.consumerKey = XAuthTwitter.CONSUMER_KEY;
		this.consumerSecret = XAuthTwitter.CONSUMER_SECRET;
		this.requestUrl = url;
		this.requestMethod = method;
		this.params = params;
	}

	/**
	 * トークン取得メソッド
	 * @return
	 */
	public HashMap<String,String> getToken() {
		HashMap<String, String> result = new HashMap<String, String>();
		try {
			// HttpClientの準備
			DefaultHttpClient client = new DefaultHttpClient();
			// パラメータ
			String params = getRequestParametersXAuth();
			// ヘッダー
			String header = getxAuthHeader();

			String signParams = new SpannableStringBuilder(header).append("&")
					.append(params).toString();
			String signBaseString = getSignatureBaseString("POST",
					tokenRequestUrl, signParams);
			String key = getKeyXAuth();
			String sign = getSignatureXAuth(signBaseString, key);
			String requestUrl = new SpannableStringBuilder(tokenRequestUrl)
					.append("?").append(params).toString();
			HttpPost post = new HttpPost(requestUrl);
			String authHeader = new SpannableStringBuilder("OAuth ").append(
					header).append("&oauth_signature=").append(sign).append(
					"\"").toString().replace("&", "\", ").replace("=", "=\"");
			post.setHeader("Authorization", authHeader);
			org.apache.http.HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				//&で文字列を区切る
				List<String> resultArray = Arrays.asList(EntityUtils.toString(resEntity).split("&"));
				//分割結果が少なかったら恐らくエラーなのでnullを返す
				if(resultArray.size() <= 2){
					return result;
				}else{
					//分割結果が3以上なら多分成功してる。
					Iterator<String> it = resultArray.iterator();
					while(it.hasNext()){
						try{
							//結果を=で区切ってキーと値にする
							String[] i = it.next().split("=");
							//ハッシュに値を代入
							result.put(i[0], i[1]);
						}catch(ArrayIndexOutOfBoundsException e){
							e.printStackTrace();
						}
					}
				}
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
		return result;
	}

	/**
	 * 署名のベース文字列（トークンリクエスト用）を取得
	 *
	 * @param method
	 * @param url
	 * @param params
	 * @return
	 */
	public String getSignatureBaseString(String method, String url,
			String params) {
		String amp = "&";
		SpannableStringBuilder sb = new SpannableStringBuilder(method);
		sb.append(amp);
		sb.append(URLEncoder.encode(url));
		sb.append(amp);
		sb.append(URLEncoder.encode(params));
		return sb.toString();
	}

	/**
	 * xAuth暗号キーの作成メソッド（トークンリクエスト用）
	 * @return
	 */
	public String getKeyXAuth() {
		return new SpannableStringBuilder(URLEncoder.encode(consumerSecret))
				.append("&").toString();
	}

	/**
	 * リクエストパラメータの生成
	 *
	 * @return
	 */
	public String getRequestParametersXAuth() {
		SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append("x_auth_mode=");
		sb.append(xauthAuthMode);
		sb.append("&x_auth_password=");
		sb.append(password);
		sb.append("&x_auth_username=");
		sb.append(userId);

		return sb.toString();
	}

	/**
	 * xAuthの認証ヘッダー作成メソッド（トークンリクエスト用）
	 * @return
	 */
	public String getxAuthHeader() {
		SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append("oauth_consumer_key=");
		sb.append(consumerKey);
		sb.append("&oauth_nonce=");
		sb.append(UUID.randomUUID().toString());
		sb.append("&oauth_signature_method=");
		sb.append(oauthSignatureMethod);
		sb.append("&oauth_timestamp=");
		sb.append(Long.toString(System.currentTimeMillis()));
		sb.append("&oauth_version=");
		sb.append(oauthVersion);
		return sb.toString();
	}

	/**
	 * xAuthの署名作成メソッド（トークンリクエスト用）
	 * @param signatureBaseString
	 * @param keyString
	 * @return
	 */
	private String getSignatureXAuth(String signatureBaseString, String keyString) {
		String signature = null;
		try {
			Mac mac = Mac.getInstance(algorithm);
			Key key = new SecretKeySpec(keyString.getBytes(), algorithm);

			mac.init(key);
			byte[] digest = mac.doFinal(signatureBaseString.getBytes());
			String sign = Wasatter.base64Encode(digest);
			signature = URLEncoder.encode(sign);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return signature;
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

	private SortedMap<String, String> createParametersMap() {
		SortedMap<String, String> map = new TreeMap<String, String>();
		map.put("oauth_consumer_key", consumerKey);
		map.put("oauth_nonce", UUID.randomUUID().toString());
		map.put("oauth_signature_method", oauthSignatureMethod);
		map.put("oauth_timestamp", getTimeStamp());
		map.put("oauth_version", oauthVersion);
		map.put("oauth_token", token);
		return map;
	}

	private String createAuthorizationValue(){
		StringBuilder builder = new StringBuilder();
		builder.append("OAuth ");
		for (Map.Entry<String, String> param : oauthParametersMap.entrySet()) {
			builder.append(param.getKey() + "=");
			builder.append("\"" + param.getValue() + "\",");
		}
		builder.append("oauth_signature" + "=");
		builder.append("\"" +
						getSignatureXAuth(getSignatureBaseString(), getKey())
						+ "\"");
		return builder.toString();
	}

	private String getKey() {
		StringBuilder builder = new StringBuilder();
		builder.append(consumerSecret);
		builder.append("&");
		builder.append(tokenSecret);
		return builder.toString();
	}

	private String getRequestParameters() {
		if (params != null && params.size() > 0) {
			for (Map.Entry<String, String> param : params.entrySet()) {
				oauthParametersMap.put(param.getKey(), param.getValue());
			}
		}
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, String> param : oauthParametersMap.entrySet()) {
			builder.append(param.getKey());
			builder.append("=");
			builder.append(param.getValue());
			builder.append("&");
		}
		return builder.toString().substring(0, builder.length() -1);
	}

	private String getSignatureBaseString(){
		return requestMethod + "&" + encodeURL(requestUrl) + "&" + SignatureEncode.encode(getRequestParameters());
	}

	private String encodeURL(String str) {
		String encord = null;
		try {
			encord = URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException ignore) {
		}
		return encord;
	}

	private String getTimeStamp() {
		return Long.toString(System.currentTimeMillis() / 1000);
	}

}
