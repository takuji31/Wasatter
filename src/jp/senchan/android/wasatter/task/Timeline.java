package jp.senchan.android.wasatter.task;

import java.util.ArrayList;

import jp.senchan.android.wasatter.util.WasatterItem;
import android.os.AsyncTask;
import android.widget.ListView;

public class Timeline extends
		AsyncTask<String, String, ArrayList<WasatterItem>> {
	protected ListView listview;
	protected int mode;
	public static final int MODE_TIMELINE = 1;
	public static final int MODE_REPLY = 2;
	public static final int MODE_MYPOST = 3;
	public static final int MODE_ODAI = 4;
	public static final int MODE_TODO = 5;
	public static final int MODE_CHANNEL_LIST = 6;
	public static final int MODE_CHANNEL = 7;
	public static final String[] msg = new String[] { "Timeline", "Reply",
			"My post", "Odai", "TODO", "Channel list", "Channel status" };

	// コンストラクタ
	public Timeline(int mode) {
		this.mode = mode;
	}

	// バックグラウンドで実行する処理
	protected ArrayList<WasatterItem> doInBackground(String... param) {
		return null;
	}

	
	protected void onProgressUpdate(String... values) {
	}

	// 進行中に出す処理
	protected void onPreExecute() {
	}

	// メインスレッドで実行する処理
	
	protected void onPostExecute(ArrayList<WasatterItem> result) {
	}
}