package jp.senchan.android.wasatter.xauth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import jp.senchan.android.wasatter.Wasatter;

public class XAuth {

	protected String consumerKey;
	protected String consumerSecret;
	protected String token;
	protected String tokenSecret;
	protected HashMap<String, String> params;
	protected SortedMap<String, String> oauthParametersMap;
	protected String requestUrl;
	protected String requestMethod;

	protected static final String oauthVersion = "1.0";
	protected static final String oauthSignatureMethod = "HMAC-SHA1";
	protected static final String algorithm = "HmacSHA1";
	protected static final String xauthAuthMode = "client_auth";
	protected static final String tokenRequestUrl = "https://api.twitter.com/oauth/access_token";

	public XAuth() {
	}
	protected String getKey() {
		StringBuilder builder = new StringBuilder();
		builder.append(consumerSecret);
		builder.append("&");
		builder.append(tokenSecret);
		return builder.toString();
	}

	protected String getRequestParameters() {
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
	protected String encodeURL(String str) {
		String encord = null;
		try {
			encord = URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException ignore) {
		}
		return encord;
	}

	protected String getTimeStamp() {
		return Long.toString(System.currentTimeMillis() / 1000);
	}
	protected String createAuthorizationValue(){
		StringBuilder builder = new StringBuilder();
		builder.append("OAuth ");
		for (Map.Entry<String, String> param : oauthParametersMap.entrySet()) {
			builder.append(param.getKey() + "=");
			builder.append("\"" + param.getValue() + "\",");
		}
		builder.append("oauth_signature" + "=");
		builder.append("\"" +getSignatureXAuth(getSignatureBaseString(), getKey())+ "\"");
		return builder.toString();
	}
	/**
	 * xAuthの署名作成メソッド
	 * @param signatureBaseString
	 * @param keyString
	 * @return
	 */
	protected String getSignatureXAuth(String signatureBaseString, String keyString) {
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
	private String getSignatureBaseString(){
		return requestMethod + "&" + encodeURL(requestUrl) + "&" + SignatureEncode.encode(getRequestParameters());
	}
	protected SortedMap<String, String> createParametersMap() {
		SortedMap<String, String> map = new TreeMap<String, String>();
		map.put("oauth_consumer_key", consumerKey);
		map.put("oauth_nonce", UUID.randomUUID().toString());
		map.put("oauth_signature_method", oauthSignatureMethod);
		map.put("oauth_timestamp", getTimeStamp());
		map.put("oauth_version", oauthVersion);
		map.put("oauth_token", token);
		return map;
	}
}
