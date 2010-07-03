/**
 *
 */
package jp.senchan.android.wasatter.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import jp.senchan.android.wasatter.Setting;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.activity.Main;
import jp.senchan.android.wasatter.client.Wassr;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;

/**
 * @author takuji
 *
 */
public class IconDownload extends AsyncTask<Void, Integer, Void> {
	private int count;
	private Main activity;
	private int progressPerImage;
	private int progressCount;

	public IconDownload(Main activity) {
		count = Wasatter.downloadWaitUrls.size();
		this.activity = activity;
		// 1個辺りなんぼ値進めるか
		progressPerImage = count != 0 ? 10000 / count : 10000;
	}

	
	protected void onPreExecute() {
		// そもそもロードしない設定なら走らせない
		if (!Setting.isLoadImage()) {
			this.cancel(true);
			return;
		}
		// ダウンロードするアイコンがあったらプログレスバーを表示、なかったら何もせずに終了
		if (count == 0) {
			this.cancel(true);
			return;
		}
		activity.setProgressBarVisibility(true);
		activity.setProgressBarIndeterminate(false);
		activity.setProgress(0);
		progressCount = 0;
	}

	
	protected Void doInBackground(Void... params) {
		SQLiteDatabase db = Wasatter.db.getWritableDatabase();
		ArrayList<String> urls = Wasatter.downloadWaitUrls;
		SQLiteStatement st = db
				.compileStatement("delete from imagestore where created <= ?");
		//TODO:イテレーターを使わないように書き換える
		Iterator<String> it = urls.iterator();
		// キャッシュの期限は5日にしておこう。
		st.bindLong(1, new Date().getTime() / 1000 - 5 * 24 * 60 * 60);
		st.execute();
		try {
			while (it.hasNext()) {
				String url = it.next();
				if(Wassr.getImageWithCache(url)){
					it.remove();
				}
				publishProgress(200);
			}

		} catch (Exception e) {
			// TODO: 例外が1個でも発生したらその先何も処理されないので何とかしる
			e.printStackTrace();
		}
		return null;
	}

	
	protected void onProgressUpdate(Integer... values) {
		progressCount++;
		activity.setProgress(progressCount * progressPerImage);
		activity.updateList();
	}

	
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		//リストの再描画
		activity.updateList();
		// プログレスバーをフェードアウトさせる
		activity.setProgress(10000);
	}
}
