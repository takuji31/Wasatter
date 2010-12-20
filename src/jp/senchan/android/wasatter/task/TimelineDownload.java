package jp.senchan.android.wasatter.task;

import java.io.IOException;
import java.util.ArrayList;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.adapter.Timeline;
import jp.senchan.android.wasatter.client.twitter.Twitter;
import jp.senchan.android.wasatter.client.wassr.Wassr;
import jp.senchan.android.wasatter.item.Item;
import jp.senchan.android.wasatter.util.ItemComparator;
import jp.senchan.android.wasatter.util.ToastUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.widget.ListView;

public class TimelineDownload extends
		AsyncTask<Void, String, Void> {
	protected ListView listview;
	protected ArrayList<Item> items;
	protected int mode;
	private static final String NETWORK_ERROR = "network_error";
	private static final String HTTP_ERROR = "http_error";
	private static final String PROGRESS = "porgress";
	private static final String UPDATE_VIEW = "update_view";

	// コンストラクタ
	public TimelineDownload(int mode_code, ListView list) {
		mode = mode_code;
		listview = list;
	}

	// バックグラウンドで実行する処理
	protected Void doInBackground(Void... param) {
		items = new ArrayList<Item>();
		publishProgress(PROGRESS,"500");
		//Wassrへリクエスト
		if(Wassr.enabled()){
			HttpResponse response = Wassr.request(mode, null);
			// HTTPレスポンスステータスを取得
			try {
				int errorCode = response.getStatusLine().getStatusCode();
				// 400番台以上の場合、エラー処理
				if (errorCode >= 400) {
					// エラー処理
					publishProgress(HTTP_ERROR,String.valueOf(errorCode));
				} else {
					try {
						HttpEntity entity = response.getEntity();
						String jsonString = EntityUtils.toString(entity);
						items.addAll(Wassr.parseJSON(jsonString, mode));
						//publishProgress(UPDATE_VIEW);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
				publishProgress(NETWORK_ERROR);
			}
			publishProgress(PROGRESS,"5000");
		}
		
		//Twitterへリクエスト
		if(Twitter.enabled() && mode != Wassr.ODAI ){
			HttpResponse response = Twitter.request(mode, null);
			// HTTPレスポンスステータスを取得
			try {
				int errorCode = response.getStatusLine().getStatusCode();
				// 400番台以上の場合、エラー処理
				if (errorCode >= 400) {
					// エラー処理
					publishProgress(HTTP_ERROR,String.valueOf(errorCode));
				} else {
					try {
						HttpEntity entity = response.getEntity();
						String jsonString = EntityUtils.toString(entity);
						items.addAll(Twitter.parseJSON(jsonString, mode));
						//publishProgress(UPDATE_VIEW);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
				publishProgress(NETWORK_ERROR);
			}
		}
		publishProgress(UPDATE_VIEW);
		publishProgress(PROGRESS,"10000");
		return null;
	}

	protected void onProgressUpdate(String... values) {
		String task = values[0];
		//ネットワークエラーならToastで表示してやる
		if(NETWORK_ERROR.equals(task)){
			ToastUtil.show("通信が切断されました。電波状態の良いところでリトライしてください。");
		}else if(HTTP_ERROR.equals(task)){
			int code = Integer.parseInt(values[1]);
			switch(code){
				case 503:
					ToastUtil.show("API制限回数を超えました");
				break;
				case 502:
					ToastUtil.show("サーバーが不安定になっています");
				break;
				default:
					ToastUtil.show("通信で不明なエラーが発生しました");
				break;
			}
		}else if(PROGRESS.equals(task)){
			int val = Integer.parseInt(values[1]);
			Wasatter.main.setProgress(val);
		}else if(UPDATE_VIEW.equals(task)){
			updateView();
		}
	}

	// 進行中に出す処理
	protected void onPreExecute() {
	}

	// メインスレッドで実行する処理

	protected void onPostExecute(Void result) {
		updateView();
		Wasatter.main.reloadButton.setClickable(true);
		new IconDownload(Wasatter.main).execute();
	}
	
	protected void updateView(){
		Wasatter.main.list = items;
		Timeline adapter = new Timeline(Wasatter.CONTEXT,
				R.layout.timeline_row, items , (mode == Wassr.CHANNEL));
		adapter.sort(new ItemComparator());
		listview.setAdapter(adapter);
	}
}
