package jp.senchan.android.wasatter.setup;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.activity.Main;
import jp.senchan.android.wasatter.setting.Setting;
import jp.senchan.android.wasatter.util.ToastUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SetupMain extends Activity {
	private static final String KEY_PAGE = "SETUP_PAGE";
	private static final String KEY_COMPLETED = "SETUP_COMPLETED_v_2_0";
	private static final String KEY_ALREADY_INSTALLED = "ALREADY_INSTALLED";
	private static final int MAX_PAGE = 5;
	private int currentPage;
	private Button prevButton;
	private Button nextButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup_main);
		prevButton = (Button) findViewById(R.id.prevButton);
		nextButton = (Button) findViewById(R.id.nextButton);
		currentPage = Setting.get(SetupMain.KEY_PAGE, 1);
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

	protected void progressSetup() {
		// 最後のページまで進んだか、既に完了していたら終わりにする
		if (currentPage > SetupMain.MAX_PAGE || !Setting.get(SetupMain.KEY_COMPLETED, false) ) {
			Intent gotoMain = new Intent(this, Main.class);
			startActivity(gotoMain);
			//ウィザードが最後まで完了したことを設定ファイルに格納しておく
			Setting.set(SetupMain.KEY_COMPLETED, true);
			//Activity終了
			this.finish();
			return;
		}

		prevButton.setVisibility(View.VISIBLE);
		switch (currentPage) {
		case 1:
			// 初期ページ

			// 戻るボタンを無効にする
			prevButton.setVisibility(View.GONE);
			break;
		}

		// どこまで進んだかを設定に保存する
		Setting.set(SetupMain.KEY_PAGE, currentPage);
	}
	
	protected void checkAlreadyInstalled() {
		boolean alreadyInstalled = Setting.get(SetupMain.KEY_ALREADY_INSTALLED, false);
		if(!alreadyInstalled){
			//v1.0以前からのアップグレード、もしくは初期インストールの場合は設定を全部クリアする
			Setting.clear();
		}
		//インストール済みということにする
		Setting.set(SetupMain.KEY_ALREADY_INSTALLED, true);
	}
}
