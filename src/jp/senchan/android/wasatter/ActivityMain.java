package jp.senchan.android.wasatter;

import jp.senchan.android.wasatter.setup.SetupMain;
import jp.senchan.android.wasatter.util.DBHelper;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ActivityMain extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//ベースコンテキストをstaticに突っ込む
		if(Wasatter.CONTEXT == null){
			Wasatter.CONTEXT = this.getBaseContext();
		}
		//ダウンロード待ちURLを解放
		Wasatter.downloadWaitUrls.clear();
		//メモリ内の画像キャッシュを解放
		Wasatter.images.clear();

		//TODO バージョンチェック機能
		//TODO バグ報告機能


		//メイン画面を呼び出す
		//Intent intentMain = new Intent(this, Main.class);
		//startActivity(intentMain);
		Intent intentMain = new Intent(this, SetupMain.class);
		startActivity(intentMain);
		finish();
	}
}
