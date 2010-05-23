package jp.senchan.android.wasatter2.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import jp.senchan.android.wasatter2.R;
import jp.senchan.android.wasatter2.Wasatter;
import jp.senchan.android.wasatter2.adapter.Timeline;
import jp.senchan.android.wasatter2.client.Twitter;
import jp.senchan.android.wasatter2.client.Wassr;
import jp.senchan.android.wasatter2.item.Item;
import jp.senchan.android.wasatter2.setting.TwitterAccount;
import jp.senchan.android.wasatter2.setting.WassrAccount;
import jp.senchan.android.wasatter2.task.IconDownload;
import jp.senchan.android.wasatter2.util.DBHelper;
import jp.senchan.android.wasatter2.util.ItemComparator;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

public abstract class TimelineActivity extends Activity {
	public ListView listView;
	public ArrayList<Item> list;
	public Handler handler = new Handler();
	public ReloadThreadWassr reloadThreadWassr;
	public ReloadThreadTwitter reloadThreadTwitter;
	public boolean wassrLoad;
	public boolean twitterLoad;
	public boolean wassrLoadComplete = false;
	public boolean twitterLoadComplete = false;

	public abstract void reload();

	/**
	 * Wassrリロードスレッド
	 * @author takuji
	 *
	 */
	protected class ReloadThreadWassr extends Thread {
		private int mode;
		private TimelineActivity target;
		private boolean clear;
		private ArrayList<Item> list;
		private HashMap<String, String> params;

		public ReloadThreadWassr(TimelineActivity target, int mode, boolean clear,
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

		@Override
		public void run() {
			// タイムラインを取得
			handler.post(new Runnable() {

				@Override
				public void run() {
					// プログレスバーを500にセット、これでダウンロードしてるっぽく見えるはず…？
					setProgressBarVisibility(true);
					setProgress(500);
				}
			});
			target.loadCache();
			Wassr.getItems(mode, target, clear, list, params);
			target.wassrLoadComplete = true;
			handler.post(new Runnable() {

				@Override
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
		private TimelineActivity target;
		private boolean clear;
		private ArrayList<Item> list;
		private HashMap<String, String> params;

		public ReloadThreadTwitter(TimelineActivity target, int mode, boolean clear,
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

		@Override
		public void run() {
			// タイムラインを取得
			handler.post(new Runnable() {

				@Override
				public void run() {
					// プログレスバーを500にセット、これでダウンロードしてるっぽく見えるはず…？
					setProgressBarVisibility(true);
					setProgress(500);
				}
			});
			target.loadCache();
			Twitter.getItems(mode, target, clear, list, params);
			target.twitterLoadComplete = true;
			handler.post(new Runnable() {

				@Override
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


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
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

	/**
	 * 画像のキャッシュをロードするメソッド
	 */
	public void loadCache() {
		DBHelper imageStore = Wasatter.db;
		SQLiteDatabase db = imageStore.getReadableDatabase();
		SQLiteDatabase dbw = imageStore.getWritableDatabase();
		Cursor c = db.rawQuery("select * from imagestore", null);
		c.moveToFirst();
		int count = c.getCount();
		for (int i = 0; i < count; i++) {
			String url = c.getString(0);
			String imageName = c.getString(1);
			long created = c.getLong(2);
			if (created > Wasatter.cacheExpire()) {
				Wasatter.images.put(url, Wasatter.getImage(imageName));
			} else {
				SQLiteStatement st = dbw
						.compileStatement("delete from imagestore where url=?");
				st.bindString(1, url);
				st.execute();
				File file = new File(new SpannableStringBuilder(Wasatter
						.getImagePath()).append(imageName).toString());
				file.delete();
			}
			c.moveToNext();
		}
		c.close();
		db.execSQL("vacuum imagestore");
		db.execSQL("reindex imagestore");
	}


}
