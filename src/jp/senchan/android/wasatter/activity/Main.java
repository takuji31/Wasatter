package jp.senchan.android.wasatter.activity;

import java.util.ArrayList;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.adapter.Timeline;
import jp.senchan.android.wasatter.client.BaseClient;
import jp.senchan.android.wasatter.client.Wassr;
import jp.senchan.android.wasatter.item.Item;
import jp.senchan.android.wasatter.setting.SettingRoot;
import jp.senchan.android.wasatter.tag.TagMainButton;
import jp.senchan.android.wasatter.task.TimelineDownload;
import jp.senchan.android.wasatter.util.IntentCode;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Main extends Activity {
	public Item selectedItem;
	public ListView listView;
	public ArrayList<Item> list;
	public TimelineDownload reloadTask;
	public ImageButton reloadButton;
	public ImageButton clickedButton;
	public int loadTimelineMode;
	public ImageButton buttonShowTL;
	public ImageButton buttonShowReply;
	public ImageButton buttonShowOdai;
	public ImageButton buttonShowChannel;

	/**
	 * リストビューの表示を更新するメソッド、メインスレッドから呼び出すべし。
	 */
	public void updateList() {
		Timeline tl = (Timeline) this.listView.getAdapter();
		if (tl != null) {
			tl.updateView();
		}
	}

	// 別スレッドに投げるリロード処理とその後の処理

	/**
	 * on○○系のイベント
	 */

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// タイトルをプログレスバーに使う
		requestWindowFeature(Window.FEATURE_PROGRESS);
		// リストを初期化
		list = new ArrayList<Item>();

		// 初期化処理の実行
		loadTimelineMode = BaseClient.TIMELINE;
		initialize(R.id.buttonShowTL);
		reload();
	}

	/**
	 * 他のActivityから戻ってきた時の処理
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (requestCode == IntentCode.MAIN_ITEMDETAIL) {
				// 詳細から戻ってきたら、イイネの再描画
				Timeline adapter = (Timeline) this.listView.getAdapter();
				adapter.updateView();
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}

	// メニューが生成される際に起動される。
	// この中でメニューのアイテムを追加したりする。

	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
		// メニューインフレーターを取得
		this.getMenuInflater().inflate(R.menu.main, menu);
		// できたらtrueを返す
		return true;
	}

	// メニューのアイテムが選択された際に起動される。

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
		// 二重ロード防止
		reloadButton.setClickable(false);
		// 既存タスクをキャンセルする
		try {
			reloadTask.cancel(true);
		} catch (Exception e) {
		}
		// ロード開始
		reloadTask = new TimelineDownload(loadTimelineMode, listView);
		reloadTask.execute();
	}

	/**
	 * タイムラインをクリックした時のOnClickListener
	 * 
	 * @author takuji
	 * 
	 */
	private class TLItemClickListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ListView listView = (ListView) parent;
			// 選択されたアイテムを取得します
			Main.this.selectedItem = (Item) listView.getAdapter().getItem(
					position);
			Intent intent_detail = new Intent(Main.this, Detail.class);
			Main.this.startActivityForResult(intent_detail,
					IntentCode.MAIN_ITEMDETAIL);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		initialize(clickedButton.getId());
	}

	public void initialize(int selectedButtonId) {
		setContentView(R.layout.main);
		// リストビューを取得
		listView = (ListView) findViewById(R.id.timeline);
		// リストビューにリストを代入
		listView.setAdapter(new Timeline(this, R.layout.timeline_row, list,
				false));

		// 色んなところからいじれるように、Static変数に突っ込む
		Wasatter.main = this;

		// リストにイベントリスナーを割り当てる
		listView.setOnItemClickListener(new TLItemClickListener());
		// 新規投稿ボタンにイベント割り当て
		ImageButton buttonNew = (ImageButton) findViewById(R.id.buttonNew);
		buttonNew.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				Intent intent = new Intent(Main.this, Update.class);
				startActivity(intent);
			}
		});
		// 新規投稿ボタンにイベント割り当て
		reloadButton = (ImageButton) findViewById(R.id.buttonReload);
		reloadButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				reload();
			}
		});

		// 各ボタンにイベント割り当て
		buttonShowTL = (ImageButton) findViewById(R.id.buttonShowTL);
		buttonShowReply = (ImageButton) findViewById(R.id.buttonShowReply);
		buttonShowOdai = (ImageButton) findViewById(R.id.buttonShowOdai);
		buttonShowChannel = (ImageButton) findViewById(R.id.buttonShowChannel);

		buttonShowTL.setTag(new TagMainButton(BaseClient.TIMELINE, "TimeLine"));
		buttonShowReply.setTag(new TagMainButton(BaseClient.REPLY, "Mention"));
		buttonShowOdai.setTag(new TagMainButton(Wassr.ODAI, "お題ちゃん"));
		buttonShowChannel.setTag(new TagMainButton(BaseClient.CHANNEL_LIST,
				"Channel"));

		buttonShowTL.setOnClickListener(new MainButtonClickListener());
		buttonShowReply.setOnClickListener(new MainButtonClickListener());
		buttonShowOdai.setOnClickListener(new MainButtonClickListener());
		buttonShowChannel.setOnClickListener(new MainButtonClickListener());

		// ボタンの選択状態を初期化する
		clickedButton = (ImageButton) findViewById(selectedButtonId);
		buttonSelect();

		updateList();
	}

	public void buttonSelect() {
		buttonShowTL.setClickable(true);
		buttonShowReply.setClickable(true);
		buttonShowOdai.setClickable(true);
		buttonShowChannel.setClickable(true);
		clickedButton.setClickable(false);
		TagMainButton tag = (TagMainButton) clickedButton.getTag();
		SpannableStringBuilder title = new SpannableStringBuilder("Wasatter - ");
		title.append(tag.title);
		setTitle(title.toString());
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 終了時にタスクをキャンセルする
		if (isFinishing()) {
			try {
				reloadTask.cancel(true);
			} catch (Exception e) {
				// 何もしない
			}
		}
	}

	private class MainButtonClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			clickedButton = (ImageButton) v;
			TagMainButton tag = (TagMainButton) v.getTag();
			loadTimelineMode = tag.mode;
			buttonSelect();
			reload();
		}
	}
}