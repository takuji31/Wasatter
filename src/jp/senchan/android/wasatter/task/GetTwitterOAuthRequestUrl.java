package jp.senchan.android.wasatter.task;

import jp.senchan.android.wasatter3.R;
import jp.senchan.android.wasatter.auth.TwitterOAuth;
import jp.senchan.android.wasatter.auth.params.OAuthTwitter;
import twitter4j.TwitterException;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationContext;
import twitter4j.http.OAuthAuthorization;
import twitter4j.http.RequestToken;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.os.AsyncTask;

public class GetTwitterOAuthRequestUrl extends AsyncTask<Void, TwitterException, RequestToken>{

	private Activity currentActivity;
	private ProgressDialog dialog;
	
	public GetTwitterOAuthRequestUrl(Activity currentActvity) {
		this.currentActivity = currentActvity;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//プログレスダイアログを表示する
		dialog = new ProgressDialog(currentActivity);
		dialog.setMessage(currentActivity.getText(R.string.progress_message_get_oauth_request_token));
		dialog.setIndeterminate(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				//ダイアログを閉じたらこのタスクをキャンセル
				GetTwitterOAuthRequestUrl.this.cancel(true);
			}
		});
		dialog.show();
	}
	
	@Override
	protected RequestToken doInBackground(Void... params) {
		Configuration conf = ConfigurationContext.getInstance();
		TwitterOAuth.auth = new OAuthAuthorization(conf, OAuthTwitter.CONSUMER_KEY, OAuthTwitter.CONSUMER_SECRET);
		try {
			//認証URL取得
			return TwitterOAuth.auth.getOAuthRequestToken(TwitterOAuth.OAUTH_CALLBACK_URL);
		} catch (TwitterException e) {
			e.printStackTrace();
			publishProgress(e);
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(RequestToken result) {
		super.onPostExecute(result);
		//ダイアログを閉じる
		dialog.dismiss();
		//取得に成功した場合は認証用URLを開く
		Uri twitterAuthUri = Uri.parse(result.getAuthorizationURL());
		Intent gotoTwitterAuth = new Intent(Intent.ACTION_VIEW,twitterAuthUri);
		currentActivity.startActivity(gotoTwitterAuth);
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
		//TODO キャンセルした旨を表示する
		//ToastUtil.show(currentActivity.getText(R.string.error_generic_task_canceled).toString());
	}

}
