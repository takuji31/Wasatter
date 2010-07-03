package jp.senchan.android.wasatter.setting;

import jp.senchan.android.wasatter.R;
import android.app.Activity;
import android.os.Bundle;

public class Data extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_data);
		//キャッシュクリアボタンにイベントを割り当てる
		/*Button clearCache = (Button) findViewById(R.id.clearCache);
		clearCache.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				SQLiteDatabase db = Wasatter.db.getWritableDatabase();
				db.execSQL("delete from imagestore");
				ToastUtil.show("キャッシュを消去しました");
			}
		});*/

	}
}
