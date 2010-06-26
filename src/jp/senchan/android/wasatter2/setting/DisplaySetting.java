package jp.senchan.android.wasatter2.setting;


import jp.senchan.android.wasatter2.R;
import jp.senchan.android.wasatter2.Wasatter;
import jp.senchan.android.wasatter2.util.ToastUtil;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;


public class DisplaySetting extends Setting {
	public static final String LOAD_ICON = "load_icon";
	public static final String BODY_MULTILINE = "display_body_multi_line";

	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_display);
		// アイコンのロード設定を読み込む
		final CheckBox loadIcon = (CheckBox) findViewById(R.id.loadIcon);
		final CheckBox bodyMultiline = (CheckBox) findViewById(R.id.bodyMultiline);
		loadIcon.setChecked(Setting.get(LOAD_ICON, true));
		bodyMultiline.setChecked(Setting.get(BODY_MULTILINE, true));


		//キャッシュクリアボタンにイベントを割り当てる
		Button clearCache = (Button) findViewById(R.id.clearCache);
		clearCache.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				SQLiteDatabase db = Wasatter.db.getWritableDatabase();
				db.execSQL("delete from imagestore");
				ToastUtil.show("キャッシュを消去しました");
			}
		});

		// 保存とキャンセルボタンにイベントを割り当てる
		Button saveButton = (Button) findViewById(R.id.saveButton);
		Button cancelButton = (Button) findViewById(R.id.cancelButton);
		saveButton.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				Setting.set(LOAD_ICON, loadIcon.isChecked());
				Setting.set(BODY_MULTILINE, bodyMultiline.isChecked());
				Toast.makeText(DisplaySetting.this, MESSAGE, Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				// キャンセルボタンが押されたら前の画面に戻る
				DisplaySetting.this.finish();
			}
		});
	}
}