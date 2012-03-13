package jp.senchan.android.wasatter.next.task;

import jp.senchan.android.wasatter.next.client.NewTwitterOAuthClient;
import jp.senchan.android.wasatter.next.listener.OnURLCreatedListener;
import twitter4j.TwitterException;
import android.os.AsyncTask;
import android.text.TextUtils;

public class GetTwitterOAuthRequestURLTask extends AsyncTask<Void, TwitterException, String>{

	private OnURLCreatedListener mListener;
	private NewTwitterOAuthClient mClient;
	
	public GetTwitterOAuthRequestURLTask(OnURLCreatedListener listener, NewTwitterOAuthClient client) {
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
