/**
 *
 */
package jp.senchan.android.wasatter.task;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.activity.Detail;
import jp.senchan.android.wasatter.client.Twitter;
import jp.senchan.android.wasatter.client.Wassr;
import jp.senchan.android.wasatter.item.Item;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author takuji
 *
 */
public class Favorite extends AsyncTask<Item, Void, Boolean> {

	private Detail detail;
	private Item item;
	private String status;
	public Favorite(Detail detail) {
		this.detail = detail;
		detail.favoriteButton.setClickable(false);
	}

	
	protected Boolean doInBackground(Item... params) {
		item = params[0];
		if (item.channel) {
			status = Wassr.channelFavorite(item);
			return !"NG".equalsIgnoreCase(status);
		} else if (Wasatter.WASSR.equals(item.service)) {
			return Wassr.favorite(item);
		} else if (Wasatter.TWITTER.equals(item.service)) {
			Twitter.favorite(item);
		}
		return false;
	}

	
	protected void onPostExecute(Boolean result) {
		TextView text_result = (TextView) detail.findViewById(R.id.text_result);
		Button button = (Button) detail.findViewById(R.id.button_favorite);
		boolean favorited = item.favorited;
		boolean isWassr = !Wasatter.TWITTER.equals(item.service);
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
