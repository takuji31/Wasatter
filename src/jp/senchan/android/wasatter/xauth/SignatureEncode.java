package jp.senchan.android.wasatter.xauth;
import java.io.UnsupportedEncodingException;


public class SignatureEncode {
	private static final String UNRESERVEDCHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.~";

	public static final String encode(String s){
        byte[] bytes;
		try {
			bytes = s.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			//ありえない
			e.printStackTrace();
			return null;
		}
        StringBuffer builder = new StringBuffer();
        for (byte b: bytes){
        	char c = (char) b;
        	if (UNRESERVEDCHARS.indexOf(String.valueOf(c)) >= 0) {
        		builder.append(String.valueOf(c));
        	} else {
        		builder.append("%" +
        				String.valueOf(Integer.toHexString(b > 0 ? b : b + 256)).toUpperCase());
        	}
        }
        return builder.toString();
	}
}