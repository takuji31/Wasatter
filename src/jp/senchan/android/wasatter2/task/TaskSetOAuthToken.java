/**
 *
 */
package jp.senchan.android.wasatter2.task;

import jp.senchan.android.wasatter2.Setting;
import jp.senchan.android.wasatter2.activity.OAuthToken;
import twitter4j.TwitterException;
import twitter4j.http.AccessToken;
import android.os.AsyncTask;

/**
 * @author takuji
 * 
 */
public class TaskSetOAuthToken extends AsyncTask<String, Void, Boolean> {
	public OAuthToken target;

	public TaskSetOAuthToken(OAuthToken target) {
		this.target = target;
	}

	@Override
	protected void onPreExecute() {
		this.target.finish();
	}

	@Override
	protected Boolean doInBackground(String... params) {
		try {
			AccessToken access_token = this.target.twitter.getOAuthAccessToken(
					this.target.request, params[0]);
			Setting.setTwitterToken(access_token.getToken());
			Setting.setTwitterTokenSecret(access_token.getTokenSecret());
		} catch (TwitterException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO OAuthToken更新完了の処理を書く
	}
}
