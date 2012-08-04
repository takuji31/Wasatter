package jp.senchan.android.wasatter.next.task;

import jp.senchan.android.wasatter.client.NewTwitterClient;
import jp.senchan.android.wasatter.next.listener.OnAccessTokenReceivedListener;
import jp.senchan.lib.os.AsyncTaskCompat;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import android.net.Uri;

public class GetTwitterOAuthAccessTokenTask extends AsyncTaskCompat<Uri, TwitterException, AccessToken>{

	private OnAccessTokenReceivedListener mListener;
	private NewTwitterClient mClient;

	public GetTwitterOAuthAccessTokenTask(OnAccessTokenReceivedListener listener, NewTwitterClient client) {
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
