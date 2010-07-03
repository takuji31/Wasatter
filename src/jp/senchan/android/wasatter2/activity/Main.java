package jp.senchan.android.wasatter2.activity;

import java.util.ArrayList;
import java.util.HashMap;

import jp.senchan.android.wasatter2.R;
import jp.senchan.android.wasatter2.Wasatter;
import jp.senchan.android.wasatter2.adapter.Timeline;
import jp.senchan.android.wasatter2.client.Twitter;
import jp.senchan.android.wasatter2.client.Wassr;
import jp.senchan.android.wasatter2.item.Item;
import jp.senchan.android.wasatter2.setting.SettingRoot;
import jp.senchan.android.wasatter2.setting.TwitterAccount;
import jp.senchan.android.wasatter2.setting.WassrAccount;
import jp.senchan.android.wasatter2.task.IconDownload;
import jp.senchan.android.wasatter2.task.TaskImageDownloadWithCache;
import jp.senchan.android.wasatter2.util.IntentCode;
import jp.senchan.android.wasatter2.util.ItemComparator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Main extends Activity {
	public Item selectedItem;
	public ListView listView;
	public ArrayList<Item> list;
	public Handler handler = new Handler();
	public ReloadThreadWassr reloadThreadWassr;
	public ReloadThreadTwitter reloadThreadTwitter;
	public boolean wassrLoad;
	public boolean twitterLoad;
	public boolean wassrLoadComplete = false;
	public boolean twitterLoadComplete = false;

	/**
	 * Wassrリロードスレッド
	 * @author takuji
	 *
	 */
	protected class ReloadThreadWassr extends Thread {
		private int mode;
		private Main target;
		private boolean clear;
		private ArrayList<Item> list;
		private HashMap<String, String> params;

		public ReloadThreadWassr(Main target, int mode, boolean clear,
				HashMap<String, String> params) {
			// パラメータをあらかじめセットする
			this.mode = mode;
			this.target = target;
			this.clear = clear;
			this.list = target.list;
			this.params = params;
			target.wassrLoad = WassrAccount.get(WassrAccount.LOAD_TL, false);
			target.twitterLoad = TwitterAccount.get(TwitterAccount.LOAD_TL, false);
		}

		
		public void run() {
			// タイムラインを取得
			handler.post(new Runnable() {

				
				public void run() {
					// プログレスバーを500にセット、これでダウンロードしてるっぽく見えるはず…？
					setProgressBarVisibility(true);
					setProgress(500);
				}
			});
			Wassr.getItems(mode, target, clear, list, params);
			target.wassrLoadComplete = true;
			handler.post(new Runnable() {

				
				public void run() {
					// ソートを実行
					Timeline tl = (Timeline) listView.getAdapter();
					tl.sort(new ItemComparator());
					reloadThreadWassr = null;
					if((target.twitterLoad && target.twitterLoadComplete) || !target.twitterLoad){
						//10000にセットしてプログレスバーを消す
						setProgress(10000);
						target.twitterLoadComplete = false;
						target.wassrLoadComplete = false;
						new IconDownload(target).execute();
					}else if(target.wassrLoad){
						setProgress(5000);
					}
				}
			});
		}
	};

	/**
	 * Twitterリロードスレッド
	 * @author takuji
	 *
	 */
	protected class ReloadThreadTwitter extends Thread {
		private int mode;
		private Main target;
		private boolean clear;
		private ArrayList<Item> list;
		private HashMap<String, String> params;

		public ReloadThreadTwitter(Main target, int mode, boolean clear,
				HashMap<String, String> params) {
			// パラメータをあらかじめセットする
			this.mode = mode;
			this.target = target;
			this.clear = clear;
			this.list = target.list;
			this.params = params;
			target.wassrLoad = WassrAccount.get(WassrAccount.LOAD_TL, false);
			target.twitterLoad = TwitterAccount.get(TwitterAccount.LOAD_TL, false);
		}

		
		public void run() {
			// タイムラインを取得
			handler.post(new Runnable() {

				
				public void run() {
					// プログレスバーを500にセット、これでダウンロードしてるっぽく見えるはず…？
					setProgressBarVisibility(true);
					setProgress(500);
				}
			});
			Twitter.getItems(mode, target, clear, list, params);
			target.twitterLoadComplete = true;
			handler.post(new Runnable() {

				
				public void run() {
					// ソートを実行
					Timeline tl = (Timeline) listView.getAdapter();
					tl.sort(new ItemComparator());
					reloadThreadTwitter = null;
					if((target.wassrLoad && target.wassrLoadComplete) || !target.wassrLoad){
						//10000にセットしてプログレスバーを消す
						target.twitterLoadComplete = false;
						target.wassrLoadComplete = false;
						setProgress(10000);
						new IconDownload(target).execute();
					}else if(target.twitterLoad){
						setProgress(5000);
					}
				}
			});
		}
	};


	
	/**
	 * 別スレッドから呼び出すHTTPエラーに対する処理
	 * @param code エラーコード
	 * @param message メッセージ
	 */
	public void httpError(int code, String message) {
		// TODO とりあえず表示してるけど、もっとわかりやすいメッセージに変えたい
		Toast.makeText(this, "HTTP Error! \n" + String.valueOf(code) + message,
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * HTTP以外のエラーが発生した場合のデバッグ用メソッド
	 * @param e
	 */
	public void error(Exception e) {
		// TODO とりあえず表示してるけど、もっとわかりやすいメッセージに変えたい
		Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
	}

	/**
	 * リストビューの表示を更新するメソッド、メインスレッドから呼び出すべし。
	 */
	public void updateList(){
		Timeline tl = (Timeline) this.listView.getAdapter();
		if(tl != null){
			tl.updateView();
		}
	}

	//別スレッドに投げるリロード処理とその後の処理

	/**
	 * on○○系のイベント
	 */

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// タイトルをプログレスバーに使う
		requestWindowFeature(Window.FEATURE_PROGRESS);

		setContentView(R.layout.main);
		//リストを初期化
		list = new ArrayList<Item>();
		//リストビューを取得
		listView = (ListView) findViewById(R.id.timeline);
		//リストビューにリストを代入
		listView.setAdapter(new Timeline(this, R.layout.timeline_row, list , false));

		// 色んなところからいじれるように、Static変数に突っ込む
		Wasatter.main = this;

		// リストにイベントリスナーを割り当てる
		listView.setOnItemClickListener(new TLItemClickListener());
		reload();
		//新規投稿ボタンにイベント割り当て
		ImageButton buttonNew = (ImageButton) findViewById(R.id.buttonNew);
		buttonNew.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				Intent intent = new Intent(Main.this,Update.class);
				startActivity(intent);
			}
		});
		//新規投稿ボタンにイベント割り当て
		ImageButton buttonReload = (ImageButton) findViewById(R.id.buttonReload);
		buttonReload.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
				// TODO 自動生成されたメソッド・スタブ
				reload();
			}
		});
	}

	
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
		//Resume時に必要な処理書かないと…。
	}

	
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