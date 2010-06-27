package jp.senchan.android.wasatter2.setting;

import java.util.HashMap;

import jp.senchan.android.wasatter2.R;
import jp.senchan.android.wasatter2.xauth.XAuth;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TwitterAccount extends Setting {
	public static final String SCREEN_NAME = "twitter_screen_name";
	public static final String LOAD_TL = "twitter_load_timeline";
	public static final String POST_ENABLE = "enable_twitter";
	public static final String USERNAME = "x_auth_username";
	public static final String TOKEN = "x_auth_token";
	public static final String TOKEN_SECRET = "x_auth_token_secret";
	public static final String RES_TOKEN = "oauth_token";
	public static final String RES_TOKEN_SECRET = "oauth_token_secret";
	public static final String RES_USERNAME = "screen_name";
	public String xAuthToken = null;
	public String xAuthTokenSecret = null;
	public Button authButton;
	public TextView xAuthUserName;
	public String userName;
	public Thread authThread;
	public Runnable authFailure = new Runnable() {

		
		public void run() {
			// TODO 自動生成されたメソッド・スタブ
			Toast
					.makeText(TwitterAccount.this, "認証に失敗しました",
							Toast.LENGTH_SHORT).show();
			authButton.setClickable(true);
			setProgressBarVisibility(false);
		}
	};
	public Runnable authSuccess = new Runnable() {

		
		public void run() {
			// TODO 自動生成されたメソッド・スタブ
			Toast
					.makeText(TwitterAccount.this, "認証に成功しました",
							Toast.LENGTH_SHORT).show();
			xAuthUserName.setText(userName);
			authButton.setClickable(true);
			setProgressBarVisibility(false);
			TwitterAccount.this.finish();
		}
	};

	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_twitter_account);

		// 現在認証に使われているユーザー名を取得
		xAuthUserName = (TextView) findViewById(R.id.xAuthUserName);
		xAuthUserName.setText(Setting.get(USERNAME, ""));
		// パスワードを表示のチェックボックスにイベントを割り当てる
		final EditText password = (EditText) findViewById(R.id.password);
		final EditText id = (EditText) findViewById(R.id.id);
		CheckBox showPassword = (CheckBox) findViewById(R.id.showPassword);
		showPassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// チェックされていればパスワードを表示する。
				password.setInputType(isChecked ? InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
						: InputType.TYPE_CLASS_TEXT
								| InputType.TYPE_TEXT_VARIATION_PASSWORD);

			}
		});

		// 保存とキャンセルボタンにイベントを割り当てる
		Button cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				//認証をキャンセル
				// 前の画面に戻る
				TwitterAccount.this.finish();
			}
		});

		// 認証ボタンにイベントを割り当てる
		authButton = (Button) findViewById(R.id.authButton);
		authButton.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				// 認証ボタンをクリックしたら入力チェックしてxAuth認証
				final String userId = id.getText().toString();
				final String pass = password.getText().toString();
				if ("".equals(userId) || "".equals(pass)) {
					// どちらか未入力ならあうとー！
					Toast.makeText(TwitterAccount.this,
							"ユーザー名とパスワードを入力してください。", Toast.LENGTH_SHORT).show();
					return;
				}
				// 二重送信防止
				authButton.setClickable(false);
				// プログレスバー回してみようか
				setProgressBarIndeterminate(true);
				setProgressBarVisibility(true);
				// スレッド立てる
				authThread = new Thread(new Runnable() {

					
					public void run() {
						XAuth xauth = new XAuth(userId, pass);
						HashMap<String, String> authResult = xauth.getToken();
						if (authResult.size() < 3) {
							// 多分結果が3未満なら失敗してる…？
							handler.post(authFailure);
							return;
						}
						String token = authResult.get(RES_TOKEN);
						String tokenSecret = authResult.get(RES_TOKEN_SECRET);
						userName = authResult.get(RES_USERNAME);
						// どちらかの値がなかったら失敗してる
						if (token == null || tokenSecret == null) {
							handler.post(authFailure);
							return;
						}
						// うまいこといってたら設定にトークンとか突っ込んで成功したと表示する
						Setting.set(TOKEN, token);
						Setting.set(TOKEN_SECRET, tokenSecret);
						Setting.set(USERNAME, userName);

						handler.post(authSuccess);
					}
				});
				authThread.start();
			}
		});
	}
}
