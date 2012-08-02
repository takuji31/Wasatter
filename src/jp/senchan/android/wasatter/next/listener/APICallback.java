package jp.senchan.android.wasatter.next.listener;

import com.androidquery.callback.AjaxStatus;

public interface APICallback<T> {
	public void callback(String url, T result, AjaxStatus status);
}
