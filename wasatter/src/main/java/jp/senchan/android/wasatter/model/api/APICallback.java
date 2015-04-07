package jp.senchan.android.wasatter.model.api;

import android.os.Handler;

public abstract class APICallback<T> {
	
	private Handler mHandler = new Handler();
	private Runnable mTask = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			callback(mResult, mStatus);
		}
	};
	
	T mResult;
	int mStatus;
	
	protected abstract void callback(T result, int status);
	
	public void runCallback(final T result, final int status) {
		mResult = result;
		mStatus = status;
		mHandler.post(mTask);
	}
	
	public void cancel() {
		mHandler.removeCallbacks(mTask);
	}
}
