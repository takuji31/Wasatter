package jp.senchan.android.wasatter.task;

import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.util.WassrClient;
import twitter4j.TwitterException;
import android.os.AsyncTask;

public class TaskUpdate extends AsyncTask<Object, String, Void> {
	public boolean reply;
	public boolean twitter;
	public boolean wassr;
	public boolean r_twitter = false;
	public boolean r_wassr = false;
	public boolean channel;

	public TaskUpdate(boolean... params) {
		this.reply = params[0];
		this.wassr = params[1];
		this.twitter = params[2];
		this.channel = params[3];
	}

	
	protected Void doInBackground(Object... params) {
		String status = (String) params[0];
		String rid = (String) params[1];
		String channelName = (String) params[2];
		String image = (String) params[3];
		if(this.channel){
			publishProgress(Wasatter.MODE_POSTING, channelName);
			try {
				this.r_wassr = WassrClient.updateChannel(channelName,status, rid);
			} catch (TwitterException e) {
				// TODO 自動生成された catch ブロック
				publishProgress(Wasatter.MODE_ERROR, Wasatter.WASSR,
						String.valueOf(e.getStatusCode()));
				this.r_wassr = false;
			}
			return null;
		}
		if (this.wassr) {
			publishProgress(Wasatter.MODE_POSTING, Wasatter.WASSR);
			//try {
				this.r_wassr = WassrClient.updateTimeLine(status, rid,image);
			/*} catch (TwitterException e) {
				// TODO 自動生成された catch ブロック
				publishProgress(Wasatter.MODE_ERROR, Wasatter.SERVICE_WASSR,
						String.valueOf(e.getStatusCode()));
				this.r_wassr = false;
			}*/
		}
		if (this.twitter) {
			try {
				publishProgress(Wasatter.MODE_POSTING, Wasatter.TWITTER);
				this.r_twitter = jp.senchan.android.wasatter.client.Twitter.update(status, null);
			} catch (Exception e) {
				this.r_twitter = false;
			}
		}
		return null;
	}

	
	protected void onProgressUpdate(String... values) {
		// まず、何が起こってここに飛んできたか判定
		String service = values[1];
		if (Wasatter.MODE_POSTING.equals(values[0])) {
/*			TextView text_posting_service = (TextView) Wasatter.main
					.findViewById(R.id.text_update_status_service);
			LinearLayout layout = (LinearLayout) Wasatter.main
					.findViewById(R.id.layout_update_status);
			layout.setVisibility(View.VISIBLE);
			text_posting_service.setText(service);
*/		} else if (Wasatter.MODE_ERROR.equals(values[0])) {
			String error = values[2];
			Wasatter.displayHttpError(error, service);
		}
	}

	
	protected void onPostExecute(Void result) {
		//LinearLayout layout = (LinearLayout) Wasatter.main
		//		.findViewById(R.id.layout_update_status);
		//layout.setVisibility(View.GONE);
	}
}
