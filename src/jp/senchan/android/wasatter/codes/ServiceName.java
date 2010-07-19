package jp.senchan.android.wasatter.codes;

public class ServiceName {
	public static final String WASSR = "Wassr";
	public static final String TWITTER = "Twitter";

	public static String get(int serviceCode) {
		switch (serviceCode) {
		case ServiceCode.WASSR:
			return WASSR;
		case ServiceCode.TWITTER:
			return TWITTER;
		}
		return null;
	}
}
