package jp.senchan.android.wasatter.next.task;

import jp.senchan.android.wasatter.client.NewWassrClient;
import jp.senchan.android.wasatter.next.exception.WassrException;
import jp.senchan.android.wasatter.next.listener.OnAuthenticationResultListener;
import jp.senchan.lib.os.AsyncTaskCompat;

public class AuthenticateWassrTask extends AsyncTaskCompat<String, Integer, Boolean> {

	private OnAuthenticationResultListener listener;

	public AuthenticateWassrTask(OnAuthenticationResultListener listener) {
		this.listener = listener;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		String loginId = params[0];
		String password = params[1];

		NewWassrClient client = new NewWassrClient(loginId, password);

		//FriendTimelineを見てちゃんと認証できてるか確認
		try {
			client.friendTimeline(1);
			return true;
		} catch (WassrException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		listener.onAuthenticationResult(result);
	}

}
