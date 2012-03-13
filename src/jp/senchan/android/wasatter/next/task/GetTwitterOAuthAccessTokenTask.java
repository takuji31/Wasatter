package jp.senchan.android.wasatter.next.task;

import jp.senchan.android.wasatter.next.client.NewTwitterOAuthClient;
import jp.senchan.android.wasatter.next.listener.OnAccessTokenReceivedListener;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import android.net.Uri;
import android.os.AsyncTask;

public class GetTwitterOAuthAccessTokenTask extends AsyncTask<Uri, TwitterException, AccessToken>{

	private OnAccessTokenReceivedListener mListener;
	private NewTwitterOAuthClient mClient;
	
	public GetTwitterOAuthAccessTokenTask(OnAccessTokenReceivedListener listener, NewTwitterOAuthClient client) {
		mClient = client;
		mListener = listener;
	}
	
	@Override
	protected AccessToken doInBackground(Uri... params) {
		Uri uri = params[0];
		try {
			//認証URL取得
			return mClient.getAccessTokenFromURL(uri);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(AccessToken result) {
		super.onPostExecute(result);
		if(result == null) {
			mListener.onAccessTokenReceiveFailure();
		} else {
			mListener.onAccessTokenReceived(result);
		}
	}
}
