package jp.senchan.android.wasatter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TaskUpdate extends AsyncTask<String, String, Void> {
	public boolean reply;
	public boolean twitter;
	public boolean wassr;
	public boolean r_twitter = false;
	public boolean r_wassr = false;

	public TaskUpdate(boolean... params) {
		this.reply = params[0];
		this.wassr = params[1];
		this.twitter = params[2];
	}

	@Override
	protected Void doInBackground(String... params) {
		if (this.wassr) {
			publishProgress(Wasatter.MODE_POSTING, Wasatter.SERVICE_WASSR);
			try {
				this.r_wassr = WassrClient.updateTimeLine(params[0], params[1]);
			} catch (TwitterException e) {
				// TODO 自動生成された catch ブロック
				publishProgress(Wasatter.MODE_ERROR,Wasatter.SERVICE_WASSR,String.valueOf(e.getStatusCode()));
				this.r_wassr = false;
			}
		}
		if (this.twitter) {
			Twitter tw;
			if (Setting.isTwitterOAuthEnable()) {
				tw = new TwitterFactory().getInstance();
				tw.setOAuthConsumer(Wasatter.OAUTH_KEY, Wasatter.OAUTH_SECRET);
				tw.setOAuthAccessToken(new AccessToken(Setting
						.getTwitterToken(), Setting.getTwitterTokenSecret()));

			} else {
				tw = new TwitterFactory().getInstance(Setting.getTwitterId(),
						Setting.getTwitterPass());
			}
			try {
				publishProgress(Wasatter.MODE_POSTING, Wasatter.SERVICE_TWITTER);
				tw.updateStatus(params[0]);
				this.r_twitter = true;
			} catch (TwitterException e) {
				publishProgress(Wasatter.MODE_ERROR,Wasatter.SERVICE_TWITTER,String.valueOf(e.getStatusCode()));
				this.r_twitter = false;
			}
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(String... values) {
		// まず、何が起こってここに飛んできたか判定
		String service = values[1];
		if (Wasatter.MODE_POSTING.equals(values[0])) {
			TextView text_posting_service = (TextView) Wasatter.main
					.findViewById(R.id.text_update_status_service);
			LinearLayout layout = (LinearLayout) Wasatter.main.findViewById(R.id.layout_update_status);
			layout.setVisibility(View.VISIBLE);
			text_posting_service.setText(service);
		} else if (Wasatter.MODE_ERROR.equals(values[0])) {
			String error = values[2];
			Wasatter.displayHttpError(error, service);
		}
	}

	@Override
	protected void onPostExecute(Void result) {
		LinearLayout layout = (LinearLayout) Wasatter.main.findViewById(R.id.layout_update_status);
		layout.setVisibility(View.GONE);
	}
}
