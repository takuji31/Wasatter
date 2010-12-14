package jp.senchan.android.wasatter.setup;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.activity.Main;
import jp.senchan.android.wasatter.auth.WassrAuthCallback;
import jp.senchan.android.wasatter.auth.params.Wassr;
import jp.senchan.android.wasatter.setting.Setting;
import jp.senchan.android.wasatter.task.GetTwitterOAuthRequestUrl;
import jp.senchan.android.wasatter.util.ToastUtil;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SetupMain extends Activity {
	private static final String KEY_PAGE = "SETUP_PAGE";
	private static final String KEY_COMPLETED = "SETUP_COMPLETED";
	private static final String KEY_STARTED = "SETUP_STARTED_v_2_0";
	private static final String KEY_ALREADY_INSTALLED = "ALREADY_INSTALLED";
	private static final int MAX_PAGE = 5;
	public static final int PAGE_MAIN = 1;
	public static final int PAGE_WASSR_AUTH = 2;
	public static final int PAGE_TWITTER_AUTH = 3;
	
	public static int currentPage;
	static {
		currentPage = Setting.get(SetupMain.KEY_PAGE, 1);
	}
	
	private Button prevButton;
	private Button nextButton;
	private Button gotoButton;
	private TextView wizardText;
	private EditText wassrId;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup_main);
		this.checkAlreadyInstalled();
		prevButton = (Button) findViewById(R.id.prevButton);
		nextButton = (Button) findViewById(R.id.nextButton);
		gotoButton = (Button) findViewById(R.id.gotoButton);
		wizardText = (TextView) findViewById(R.id.wizardText);
		wassrId = (EditText) findViewById(R.id.wassrId);
		this.progressSetup();
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 次のページへ進む処理
				currentPage++;
				SetupMain.this.progressSetup();
			}
		});
		prevButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 前のページへ戻る処理
				currentPage--;
				SetupMain.this.progressSetup();
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.progressSetup();
	}

	
	protected void progressSetup() {
		// 最後のページまで進んだか、既に完了していたら終わりにする
		if (currentPage > SetupMain.MAX_PAGE
				|| Setting.get(SetupMain.KEY_COMPLETED, false)) {
			Intent gotoMain = new Intent(this, Main.class);
			startActivity(gotoMain);
			// ウィザードが最後まで完了したことを設定ファイルに格納しておく
			Setting.set(SetupMain.KEY_COMPLETED, true);
			// Activity終了
			this.finish();
			return;
		}

		prevButton.setVisibility(View.VISIBLE);
		gotoButton.setVisibility(View.GONE);
		wassrId.setVisibility(View.GONE);
		switch (currentPage) {
			case SetupMain.PAGE_MAIN:
				// 初期ページ
				// 戻るボタンを無効にする
				prevButton.setVisibility(View.GONE);
				wizardText.setText(R.string.setup_wizard_main_text);
			break;
			case SetupMain.PAGE_WASSR_AUTH:
				//Wassrアカウント設定画面
				gotoButton.setVisibility(View.VISIBLE);
				gotoButton.setText(R.string.setup_wizard_button_goto_wassr_account);
				wizardText.setText(R.string.setup_wizard_text_wassr_account);
				wassrId.setVisibility(View.VISIBLE);
				gotoButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						WassrAuthCallback.id = wassrId.getText().toString().trim();
						if("".equals(WassrAuthCallback.id)){
							ToastUtil.show(getString(R.string.error_wassr_auth_empty_id));
							return;
						}
						Uri wassrAuthUri = Uri.parse(Wassr.AUTH_URL);
						Intent gotoWassrAuth = new Intent(Intent.ACTION_VIEW,wassrAuthUri);
						startActivity(gotoWassrAuth);
					}
				});
			break;
			case SetupMain.PAGE_TWITTER_AUTH:
				//Twitterアカウント設定画面
				gotoButton.setVisibility(View.VISIBLE);
				gotoButton.setText(R.string.setup_wizard_button_goto_twitter_account);
				wizardText.setText(R.string.setup_wizard_text_twitter_account);
				gotoButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//処理開始
						new GetTwitterOAuthRequestUrl(SetupMain.this).execute();
					}
				});
			break;
		}

		// どこまで進んだかを設定に保存する
		Setting.set(SetupMain.KEY_PAGE, currentPage);
	}

	protected void checkAlreadyInstalled() {
		boolean alreadyInstalled = Setting.get(SetupMain.KEY_ALREADY_INSTALLED,
				false);
		boolean setupStarted = Setting.get(SetupMain.KEY_STARTED,
				false);
		if (!alreadyInstalled && !setupStarted) {
			// v1.0以前からのアップグレード、もしくは初期インストールの場合は設定を全部クリアする
			Setting.clear();
		}
		// インストール済みということにする
		Setting.set(SetupMain.KEY_STARTED, true);
	}
}
