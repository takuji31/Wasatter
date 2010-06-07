package jp.senchan.android.wasatter2.task;

import java.util.ArrayList;

import jp.senchan.android.wasatter2.R;
import jp.senchan.android.wasatter2.Wasatter;
import jp.senchan.android.wasatter2.adapter.AdapterTodo;
import jp.senchan.android.wasatter2.util.WasatterItem;
import jp.senchan.android.wasatter2.util.WassrClient;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;

public class TaskReloadTodo extends AsyncTask<Void, Void, ArrayList<WasatterItem>> {
	protected ListView listview;

	// コンストラクタ
	public TaskReloadTodo(ListView lv) {
		this.listview = lv;
	}

	// バックグラウンドで実行する処理
	protected ArrayList<WasatterItem> doInBackground(Void... param) {
		ArrayList<WasatterItem> ret = WassrClient.getTodo();
		return ret;
	}

	// 進行中に出す処理
	protected void onPreExecute() {
		//Wasatter.main.loading_timeline_text.setText("Loadig TODO...");
		//Wasatter.main.layout_progress_timeline.setVisibility(View.VISIBLE);
	};

	
	protected void onProgressUpdate(Void... values) {
		// TODO 自動生成されたメソッド・スタブ
		super.onProgressUpdate(values);
	}

	// メインスレッドで実行する処理
	
	protected void onPostExecute(ArrayList<WasatterItem> result) {
		// 取得結果の代入
		//Wasatter.main.list_todo = result;
		AdapterTodo adapter_todo = new AdapterTodo(this.listview.getContext(),
				R.layout.todo_row, result);
		this.listview.setAdapter(adapter_todo);
		this.listview.requestFocus();
		//Wasatter.main.layout_progress_timeline.setVisibility(View.GONE);
	}
}