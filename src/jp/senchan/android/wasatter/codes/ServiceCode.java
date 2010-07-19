package jp.senchan.android.wasatter.codes;

public class ServiceCode {
	public static final int WASSR = 1;
	public static final int TWITTER = 2;
	public static int get(String serviceName) {
		if (ServiceName.WASSR.equals(serviceName)){
			return WASSR;
		}else if(ServiceName.TWITTER.equals(serviceName)){
			return TWITTER;
		}
		return 0;
	}
}
