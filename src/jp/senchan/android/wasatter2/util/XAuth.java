package jp.senchan.android.wasatter2.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
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

	private static final String oauthVersion = "1.0";
	private static final String oauthSignatureMethod = "HMAC-SHA1";
	private static final String xauthAuthMode = "client_auth";
	private static final String tokenRequestUrl = "https://api.twitter.com/oauth/access_token";

	public XAuth(String userId, String password) {

		this.userId = userId;
		this.password = password;
		this.consumerKey = XAuthTwitter.CONSUMER_KEY;
		this.consumerSecret = XAuthTwitter.CONSUMER_SECRET;
	}

	public HashMap<String,String> request() {
		HashMap<String, String> result = new HashMap<String, String>();
		try {
			// HttpClientの準備
			DefaultHttpClient client = new DefaultHttpClient();
			// パラメータ
			String params = getRequestParameters();
			// ヘッダー
			String header = getxAuthHeader();

			String signParams = new SpannableStringBuilder(header).append("&")
					.append(params).toString();
			String signBaseString = getSignatureBaseString("POST",
					tokenRequestUrl, signParams);
			String key = getKey();
			String sign = getSignature(signBaseString, key);
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

	public String getKey() {
		return new SpannableStringBuilder(URLEncoder.encode(consumerSecret))
				.append("&").toString();
	}

	/**
	 * リクエストパラメータの生成
	 *
	 * @return
	 */
	public String getRequestParameters() {
		SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append("x_auth_mode=");
		sb.append(xauthAuthMode);
		sb.append("&x_auth_password=");
		sb.append(password);
		sb.append("&x_auth_username=");
		sb.append(userId);

		return sb.toString();
	}

	public String getxAuthHeader() {
		SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append("oauth_consumer_key=");
		sb.append(consumerKey);
		sb.append("&oauth_nonce=");
		sb.append(RandomStringUtils.randomAlphabetic(42));
		sb.append("&oauth_signature_method=");
		sb.append(oauthSignatureMethod);
		sb.append("&oauth_timestamp=");
		sb.append(Long.toString(System.currentTimeMillis()));
		sb.append("&oauth_version=");
		sb.append(oauthVersion);
		return sb.toString();
	}

	private String getSignature(String signatureBaseString, String keyString) {
		String signature = null;
		String algorithm = "HmacSHA1";
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

}
