package jp.senchan.android.wasatter.next.task;

import jp.senchan.android.wasatter.next.client.NewTwitterOAuthClient;
import jp.senchan.android.wasatter.next.listener.OnURLCreatedListener;
import twitter4j.TwitterException;
import android.os.AsyncTask;
import android.text.TextUtils;

public class GetTwitterOAuthRequestURL extends AsyncTask<Void, TwitterException, String>{

	private OnURLCreatedListener mListener;
	private NewTwitterOAuthClient mClient;
	
	public GetTwitterOAuthRequestURL(OnURLCreatedListener listener, NewTwitterOAuthClient client) {
		mClient = client;
		mListener = listener;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//プログレスダイアログを表示する
		/*
		dialog = new ProgressDialog(mActivity);
		dialog.setMessage(mActivity.getText(R.string.progress_message_get_oauth_request_token));
		dialog.setIndeterminate(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				//ダイアログを閉じたらこのタスクをキャンセル
				cancel(true);
			}
		});
		dialog.show();
		*/
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
