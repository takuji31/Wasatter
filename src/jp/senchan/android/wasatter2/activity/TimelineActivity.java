package jp.senchan.android.wasatter2.activity;

import java.util.ArrayList;
import java.util.HashMap;

import jp.senchan.android.wasatter2.R;
import jp.senchan.android.wasatter2.adapter.Timeline;
import jp.senchan.android.wasatter2.client.Wassr;
import jp.senchan.android.wasatter2.item.Item;
import jp.senchan.android.wasatter2.util.ItemComparator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

public abstract class TimelineActivity extends Activity {
	public ListView listView;
	public ArrayList<Item> list;
	public Handler handler = new Handler();
	public ReloadThread reloadThread;

	public abstract void reload();

	protected class ReloadThread extends Thread {
		private int mode;
		private TimelineActivity target;
		private boolean clear;
		private ArrayList<Item> list;
		private HashMap<String, String> params;

		public ReloadThread(TimelineActivity target, int mode, boolean clear,
				HashMap<String, String> params) {
			// 引数に渡すパラメータをあらかじめセットする
			this.mode = mode;
			this.target = target;
			this.clear = clear;
			this.list = target.list;
			this.params = params;
		}

		@Override
		public void run() {
			// タイムラインを取得
			Wassr.getItems(mode, target, clear, list, params);
			handler.post(new Runnable() {

				@Override
				public void run() {
					// ソートを実行
					Timeline tl = (Timeline) listView.getAdapter();
					tl.sort(new ItemComparator());
					reloadThread = null;
				}
			});
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		// タイトルをプログレスに使う
		// MAXが10k固定っぽいので割り算とかしないと…
		requestWindowFeature(Window.FEATURE_PROGRESS);

		setContentView(R.layout.main);
		//リストを初期化
		list = new ArrayList<Item>();
		//リストビューを取得
		listView = (ListView) findViewById(R.id.timeline);
		//リストビューにリストを代入
		listView.setAdapter(new Timeline(this, R.layout.timeline_row, list , false));
	}

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


}
