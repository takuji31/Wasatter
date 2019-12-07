/**
 *
 */
package jp.senchan.android.wasatter.task;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.WasatterItem;
import jp.senchan.android.wasatter.WassrClient;
import jp.senchan.android.wasatter.R.id;
import jp.senchan.android.wasatter.activity.Detail;
import jp.senchan.android.wasatter.activity.Setting;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author takuji
 *
 */
public class TaskToggleFavorite extends AsyncTask<WasatterItem, Void, Boolean> {

	private Detail detail;
	private WasatterItem item;
	private String status;
	public TaskToggleFavorite(Detail detail) {
		this.detail = detail;
		detail.favoriteButton.setClickable(false);
	}

	@Override
	protected Boolean doInBackground(WasatterItem... params) {
		item = params[0];
		if (item.channel) {
			status = WassrClient.channelFavorite(item);
			return !"NG".equalsIgnoreCase(status);
		} else if (Wasatter.SERVICE_WASSR.equals(item.service)) {
			return WassrClient.favorite(item);
		} else if (Wasatter.SERVICE_TWITTER.equals(item.service)) {
			Twitter tw;
			tw = new TwitterFactory().getInstance();
			tw.setOAuthConsumer(Wasatter.OAUTH_KEY, Wasatter.OAUTH_SECRET);
			tw.setOAuthAccessToken(new AccessToken(Setting
					.getTwitterToken(), Setting.getTwitterTokenSecret()));
			try {
				twitter4j.Status st = tw.createFavorite(Long
						.parseLong(item.rid));
				return st.getText() != null;
			} catch (NumberFormatException e) {
				// TODO 自動生成された catch ブロック
				return false;
			} catch (TwitterException e) {
				// TODO 自動生成された catch ブロック
				return false;
			}
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		TextView text_result = (TextView) detail.findViewById(R.id.text_result);
		Button button = (Button) detail.findViewById(R.id.button_favorite);
		boolean favorited = item.favorited;
		boolean isWassr = !Wasatter.SERVICE_TWITTER.equals(item.service);
		String text;
		if (!isWassr) {
			text = result ? "お気に入りに追加しました。" : "お気に入りに追加できませんでした。";
			if(favorited){
				button.setText(Detail.DEL_TWITTER);
			}else{
				button.setText(Detail.ADD_TWITTER);
			}

		} else if(favorited){
			text = result ? "イイネ！しました。" : "イイネ！を取り消しできませんでした。";
		}else {
			text = result ? "イイネ！を取り消しました。" : "イイネ！できませんでした。";
		}
		if(favorited && isWassr){
			button.setText(Detail.DEL_WASSR);
		}else if(isWassr){
			button.setText(Detail.ADD_WASSR);
		}
		text_result.setText(text);
		detail.favoriteButton.setClickable(true);
	}
}
