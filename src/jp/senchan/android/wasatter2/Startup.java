package jp.senchan.android.wasatter2;

import jp.senchan.android.wasatter2.activity.Main;
import jp.senchan.android.wasatter2.util.DBHelper;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Startup extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		//ベースコンテキストをstaticに突っ込む
		if(Wasatter.CONTEXT == null){
			Wasatter.CONTEXT = this.getBaseContext();
		}
		//データベースアクセスヘルパーをstaticに放り込む
		if(Wasatter.db == null){
			Wasatter.db = new DBHelper(Wasatter.CONTEXT);
		}

		//ダウンロード待ちURLを解放
		Wasatter.downloadWaitUrls.clear();

		//何か処理する、アップデート確認とかバグ報告とか


		//メイン画面を呼び出す
		Intent intentMain = new Intent(this, Main.class);
		startActivity(intentMain);
		finish();

	}
}
