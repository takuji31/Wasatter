/**
 *
 */
package jp.senchan.android.wasatter.old;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import jp.senchan.android.wasatter.R;

import twitter4j.TwitterException;
import twitter4j.http.HttpClient;
import twitter4j.http.HttpResponse;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * @author takuji
 *
 */
public class TaskImageDownloadWithCache extends AsyncTask<Void, Integer, Void> {
	private int count;
	public FrameLayout layout_progress;
	public TextView progress_text_count;
	private int errorCount;

	public TaskImageDownloadWithCache() {
		// TODO 自動生成されたコンストラクター・スタブ
		count = Wasatter.downloadWaitUrls.size();
		layout_progress = (FrameLayout) Wasatter.main
				.findViewById(R.id.layout_load_image_progress);
		progress_text_count = (TextView) Wasatter.main
				.findViewById(R.id.load_image_count);
		this.errorCount = 0;
	}

	@Override
	protected void onPreExecute() {
		Wasatter.main.progress_image.setMax(count);
		// そもそもロードしない設定なら走らせない
		if (!Setting.isLoadImage()) {
			this.cancel(true);
			return;
		}
		progress_text_count.setText(new SpannableStringBuilder("0/").append(
				String.valueOf(count)).toString());
		if (count != 0) {
			layout_progress.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		// 画像のダウンロードを行うサービス
		HttpClient http = new HttpClient();
		/*
		 * http.setConnectionTimeout(5000); http.setReadTimeout(10000);
		 * http.setRetryCount(1);
		 */
		SQLiteDatabase db = Wasatter.imageStore.getWritableDatabase();
		ArrayList<String> urls = Wasatter.downloadWaitUrls;
		db.beginTransaction();
		SQLiteStatement st = db
				.compileStatement("delete from imagestore where created <= ?");
		Iterator<String> it = urls.iterator();
		// キャッシュの期限は5日にしておこう。
		st.bindLong(1, new Date().getTime() / 1000 - 5 * 24 * 60 * 60);
		st = db
				.compileStatement("insert into imagestore(url,filename,created) values(?,?,?)");
		try {
			while (it.hasNext()) {
				// for (int i=0;i<count;i++) {
				// String url = urls.get(i);
				String url = it.next();
				try {
					HttpResponse res = http.get(url);
					Bitmap bmp = BitmapFactory.decodeStream(res.asStream());
					byte[] image;
					if (bmp != null) {
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						bmp.compress(Bitmap.CompressFormat.PNG, 80, out);
						String filename = Wasatter.makeImageFileName();
						image = out.toByteArray();
						if (Wasatter.saveImage(filename, image)) {
							st.bindString(1, url);
							st.bindString(2, filename);
							// 取得した時間（秒単位）を入れておく
							st.bindLong(3, new Date().getTime() / 1000);
							st.executeInsert();
							Wasatter.images.put(url, bmp);
							it.remove();
						}
					}
					res.disconnect();
				} catch (TwitterException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
					publishProgress(e.getStatusCode());
				}
				publishProgress(200);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		db.endTransaction();
		// Wasatter.downloadWaitUrls.clear();
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		if(values[0] != 200){
			this.errorCount++;
		}
		Wasatter.main.progress_image.incrementProgressBy(1);
		SpannableStringBuilder sb = new SpannableStringBuilder(String
				.valueOf(Wasatter.main.progress_image.getProgress())).append(
				"/").append(String.valueOf(count));
		if(this.errorCount != 0){
			sb.append(" (").append(String.valueOf(this.errorCount)).append(" Error)");
		}
		progress_text_count.setText(sb.toString());
		try {
			AdapterTimeline adapter = (AdapterTimeline) Wasatter.main.ls
					.getAdapter();
			if(adapter != null){
				adapter.updateView();
			}
		} catch (ClassCastException e) {
			// お題とかに切り替わってたらスルーする
		}
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO 自動生成されたメソッド・スタブ
		super.onPostExecute(result);
		try {
			AdapterTimeline adapter = (AdapterTimeline) Wasatter.main.ls
					.getAdapter();
			adapter.updateView();
		} catch (ClassCastException e) {
			// お題とかに切り替わってたらスルーする
		} catch (NullPointerException e) {
			// 初回起動時に切り替えたりとかしたらスルーする
		}
		Wasatter.main.progress_image.setProgress(0);
		if (count != 0) {
			layout_progress.setVisibility(View.GONE);
		}
	}
}
