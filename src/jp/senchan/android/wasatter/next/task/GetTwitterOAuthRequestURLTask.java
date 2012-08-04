package jp.senchan.android.wasatter.next.task;

import jp.senchan.android.wasatter.client.NewTwitterClient;
import jp.senchan.android.wasatter.next.listener.OnURLCreatedListener;
import jp.senchan.lib.os.AsyncTaskCompat;
import twitter4j.TwitterException;
import android.text.TextUtils;

public class GetTwitterOAuthRequestURLTask extends AsyncTaskCompat<Void, TwitterException, String>{

	private OnURLCreatedListener mListener;
	private NewTwitterClient mClient;

	public GetTwitterOAuthRequestURLTask(OnURLCreatedListener listener, NewTwitterClient client) {
		mClient = client;
		mListener = listener;
	}

	@Override
	protected String doInBackground(Void... params) {
		try {
			//認証URL取得
			return mClient.getAuthorizationURL();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if(TextUtils.isEmpty(result)) {
			mListener.onURLCreateFailure();
		} else {
			mListener.onURLCreated(result);
		}
	}
}
