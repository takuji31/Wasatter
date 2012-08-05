package jp.senchan.android.wasatter.model.api;

import android.os.Handler;

public abstract class APICallback<T> {
	
	private Handler mHandler = new Handler();
	
	protected abstract void callback(T result, int status);
	
	public void runCallback(final T result, final int status) {
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				callback(result, status);
			}
		});
	}
}
