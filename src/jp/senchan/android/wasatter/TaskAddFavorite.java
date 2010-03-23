/**
 *
 */
package jp.senchan.android.wasatter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import android.os.AsyncTask;
import android.widget.TextView;

/**
 * @author takuji
 *
 */
public class TaskAddFavorite extends AsyncTask<WasatterItem, Void, Boolean> {

	private ActivityItemDetail detail;
	private WasatterItem item;
	private String status;

	public TaskAddFavorite(ActivityItemDetail detail){
		this.detail = detail;
	}

	@Override
	protected Boolean doInBackground(WasatterItem... params) {
		item = params[0];
		if(item.channel){
			status = WassrClient.channelFavorite(item);
			return !"NG".equalsIgnoreCase(status);
		}else if(Wasatter.SERVICE_WASSR.equals(item.service)){
			return WassrClient.favorite(item.rid);
		}else if(Wasatter.SERVICE_TWITTER.equals(item.service)){
			Twitter tw;
			if(Setting.isTwitterOAuthEnable()){
				tw = new TwitterFactory().getInstance();
				tw.setOAuthConsumer(Wasatter.OAUTH_KEY, Wasatter.OAUTH_SECRET);
				tw.setOAuthAccessToken(new AccessToken(Setting.getTwitterToken(), Setting.getTwitterTokenSecret()));
				try {
					twitter4j.Status st = tw.createFavorite(Long.parseLong(item.rid));
					return st.getText() != null;
				} catch (NumberFormatException e) {
					// TODO 自動生成された catch ブロック
					return false;
				} catch (TwitterException e) {
					// TODO 自動生成された catch ブロック
					return false;
				}
			}
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		TextView text_result = (TextView) detail.findViewById(R.id.text_result);
		String text;
		if(Wasatter.SERVICE_WASSR.equals(item.service)){
			text = result ? "イイネ！しました。" : "イイネ！できませんでした。";
		}else{
			text = result ? "お気に入りに追加しました。" : "お気に入りに追加できませんでした。";
		}
		text_result.setText(text);
	}
}
