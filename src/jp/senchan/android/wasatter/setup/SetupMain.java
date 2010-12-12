package jp.senchan.android.wasatter.setup;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.activity.Main;
import jp.senchan.android.wasatter.setting.Setting;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SetupMain extends SetupActivity {
	private static final String KEY_PAGE = "SETUP_PAGE";
	private static final int MAX_PAGE = 100;
	private Button prevButton;
	private Button nextButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup_main);
		this.progressSetup();
		prevButton = (Button) findViewById(R.id.prevButton);
		nextButton = (Button) findViewById(R.id.nextButton);
	}
	
	protected void progressSetup() {
		int currentPage = Setting.get(SetupMain.KEY_PAGE, 1);
		if( currentPage >= SetupMain.MAX_PAGE ){
			//最後のページまで完了していたら終わり
			Intent gotoMain = new Intent(this, Main.class);
			startActivity(gotoMain);
			return;
		}
		
		prevButton.setVisibility(View.VISIBLE);
		switch(currentPage) {
			case 1:
				//初期ページ
				
				//戻るボタンを無効にする
				prevButton.setVisibility(View.GONE);
			break;
		}
	}
}
