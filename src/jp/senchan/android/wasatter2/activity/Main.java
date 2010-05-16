package jp.senchan.android.wasatter2.activity;

import jp.senchan.android.wasatter2.R;
import jp.senchan.android.wasatter2.Wasatter;
import jp.senchan.android.wasatter2.adapter.Timeline;
import jp.senchan.android.wasatter2.client.Twitter;
import jp.senchan.android.wasatter2.client.Wassr;
import jp.senchan.android.wasatter2.item.Item;
import jp.senchan.android.wasatter2.setting.SettingRoot;
import jp.senchan.android.wasatter2.setting.TwitterAccount;
import jp.senchan.android.wasatter2.task.TaskImageDownloadWithCache;
import jp.senchan.android.wasatter2.util.IntentCode;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Main extends TimelineActivity {
	public Item selectedItem;

	//別スレッドに投げるリロード処理とその後の処理

	/**
	 * on○○系のイベント
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 色んなところからいじれるように、Static変数に突っ込む
		Wasatter.main = this;

		// リストにイベントリスナーを割り当てる
		listView.setOnItemClickListener(new TLItemClickListener());
		reload();
		//新規投稿ボタンにイベント割り当て
		ImageButton buttonNew = (ImageButton) findViewById(R.id.buttonNew);
		buttonNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				Intent intent = new Intent(Main.this,Update.class);
				startActivity(intent);
			}
		});
		//レイアウト確認用ボタンを作る
		ImageButton layout = (ImageButton) findViewById(R.id.buttonShowMyPage);
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				Intent intent = new Intent(Main.this,TwitterAccount.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
		//Resume時に必要な処理書かないと…。
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (requestCode == IntentCode.MAIN_ITEMDETAIL) {
				Timeline adapter = (Timeline) this.listView
						.getAdapter();
				adapter.updateView();
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}

	// メニューが生成される際に起動される。
	// この中でメニューのアイテムを追加したりする。
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
		// メニューインフレーターを取得
		this.getMenuInflater().inflate(R.menu.main, menu);
		// できたらtrueを返す
		return true;
	}

	// メニューのアイテムが選択された際に起動される。
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_open_setting:
			this.openSetting();
			break;
		case R.id.menu_open_version:
			this.openVersion();
			break;
		case R.id.menu_status_update:
			this.openNewPost();
			break;
		case R.id.menu_status_reload:
			this.reload();
			break;
		default:
			break;
		}
		return true;
	}


	/**
	 * 投稿ウィンドウを開くメソッド
	 */
	public void openNewPost() {
		Intent intent_status = new Intent(this, Update.class);
		this.startActivity(intent_status);
	}

	/**
	 * 設定ダイアログを開くメソッド
	 */
	public void openSetting() {
		Intent intent_setting = new Intent(this, SettingRoot.class);
		this.startActivity(intent_setting);
	}

	public void openVersion() {
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setTitle(R.string.menu_title_version);
		ad.setMessage(R.string.app_title_version);
		ad.setPositiveButton("OK", null);
		ad.show();
	}

	/**
	 * リロードを実行するメソッド
	 */
	public void reload() {
		if(reloadThreadWassr == null){
			reloadThreadWassr = new ReloadThreadWassr(this, Wassr.TIMELINE, false, null);
			reloadThreadWassr.start();
		}
		if(reloadThreadTwitter == null){
			reloadThreadTwitter = new ReloadThreadTwitter(this, Twitter.TIMELINE, false, null);
			reloadThreadTwitter.start();
		}
	}

	public void startImageDownload() {
		new TaskImageDownloadWithCache().execute();
	}


	/**
	 * タイムラインをクリックした時のOnClickListener
	 *
	 * @author takuji
	 *
	 */
	private class TLItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ListView listView = (ListView) parent;
			// 選択されたアイテムを取得します
			Main.this.selectedItem = (Item) listView.getAdapter()
					.getItem(position);
			Intent intent_detail = new Intent(Main.this, Detail.class);
			Main.this.startActivityForResult(intent_detail,
					IntentCode.MAIN_ITEMDETAIL);
		}
	}
}