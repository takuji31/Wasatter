package jp.senchan.android.wasatter.next.listener;

public interface APICallback<T> {
	public void callback(String url, T result, int status);
}
