/**
 *
 */
package jp.senchan.android.wasatter.activity;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Setting;
import jp.senchan.android.wasatter.TaskGetOAuthRequestUrl;
import jp.senchan.android.wasatter.TaskSetOAuthToken;
import jp.senchan.android.wasatter.Wasatter;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.http.RequestToken;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author Senka/Takuji
 * 
 */
public class OAuthToken extends Activity {
	public Twitter twitter;
	public RequestToken request;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.oauth_dialog);
		Button cancel_button = (Button) this.findViewById(R.id.button_cancel);
		cancel_button.setOnClickListener(new CancelButtonClickListener());
		Button set_button = (Button) this.findViewById(R.id.button_set_token);
		set_button.setOnClickListener(new SetTokenClickListener());
		Button clear_button = (Button) this
				.findViewById(R.id.button_clear_token);
		clear_button.setOnClickListener(new ClearTokenClickListener());

		this.twitter = new TwitterFactory().getInstance();
		this.twitter
				.setOAuthConsumer(Wasatter.OAUTH_KEY, Wasatter.OAUTH_SECRET);
		new TaskGetOAuthRequestUrl(this).execute();

	}

	private class CancelButtonClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO 自動生成されたメソッド・スタブ
			OAuthToken.this.finish();
		}
	}

	private class SetTokenClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO 自動生成されたメソッド・スタブ
			TextView pin = (TextView) OAuthToken.this
					.findViewById(R.id.text_token);
			new TaskSetOAuthToken(OAuthToken.this).execute(pin
					.getText().toString());
		}
	}

	private class ClearTokenClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO 自動生成されたメソッド・スタブ
			AlertDialog.Builder ad = new AlertDialog.Builder(
					OAuthToken.this);
			ad.setMessage(R.string.message_confirm_clear_oauth_token);
			ad.setPositiveButton("OK", new ClearTokenOkButtonClickListener());
			ad.setNegativeButton("Cancel", null);
			ad.show();
		}

	}

	private class ClearTokenOkButtonClickListener implements
			android.content.DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO 自動生成されたメソッド・スタブ
			Setting.setTwitterToken("");
			Setting.setTwitterTokenSecret("");
			// Wasatter.makeToast("OAuthトークンをクリアしました。");
			AlertDialog.Builder ad = new AlertDialog.Builder(
					OAuthToken.this);
			ad.setMessage("OAuthトークンを削除しました。");
			ad.setPositiveButton("OK", null);
			ad.show();
		}

	}
}
