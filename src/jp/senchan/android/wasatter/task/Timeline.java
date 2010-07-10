package jp.senchan.android.wasatter.task;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import jp.senchan.android.wasatter.client.Wassr;
import jp.senchan.android.wasatter.item.Item;
import android.os.AsyncTask;
import android.widget.ListView;

public class Timeline extends
		AsyncTask<String, String, ArrayList<Item>> {
	protected ListView listview;
	protected int mode;
	private int progressCount;

	// コンストラクタ
	public Timeline(int mode) {
		this.mode = mode;
	}

	// バックグラウンドで実行する処理
	protected ArrayList<Item> doInBackground(String... param) {
		HttpResponse response = Wassr.request(mode, null);
		// HTTPレスポンスステータスを取得
		final int errorCode = response.getStatusLine().getStatusCode();
		// 400番台以上の場合、エラー処理
		if (errorCode >= 400) {
			//エラー処理
		}
		return null;
	}

	
	protected void onProgressUpdate(String... values) {
	}

	// 進行中に出す処理
	protected void onPreExecute() {
	}

	// メインスレッドで実行する処理
	
	protected void onPostExecute(ArrayList<Item> result) {
	}
	
}