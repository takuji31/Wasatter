package jp.senchan.android.wasatter.task;

import jp.senchan.android.wasatter.activity.Update;
import jp.senchan.android.wasatter.client.Twitter;
import jp.senchan.android.wasatter.client.Wassr;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

public class UpdateStatus extends AsyncTask<String, String, Boolean>{

	private boolean postWassr;
	private boolean postTwitter;
	private Update activity;
	private ProgressDialog pd;
	
	private static final String MAKE_DIALOG = "make_dialog";
	private static final String SHOW_DIALOG = "show_dialog";
	private static final String CLOSE_DIALOG = "close_dialog";
	
	public UpdateStatus(Update activity,boolean postWassr,boolean postTwitter) {
		this.postWassr = postWassr;
		this.postTwitter = postTwitter;
		this.activity = activity;
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		//投稿する文章
		String status = params[0];
		publishProgress(MAKE_DIALOG);
		publishProgress(SHOW_DIALOG);
		//Wassrにポスト
		if(postWassr){
			Wassr.updateTimeLine(status, null, null);
		}
		
		if(postTwitter){
			Twitter.updateTimeline(status);
		}
		publishProgress(CLOSE_DIALOG);
		return true;
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		String task = values[0];
		if(MAKE_DIALOG.equals(task)){
			pd = new ProgressDialog(activity);
			pd.setCancelable(true);
			//キャンセルしたらこのタスク自体キャンセルする
			pd.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					UpdateStatus.this.cancel(true);
				}
			});
			//値を持たない進捗表示に変更
			pd.setIndeterminate(true);
			pd.setMessage("投稿しています...");
		}
		if(SHOW_DIALOG.equals(task)){
			pd.show();
		}
		if(CLOSE_DIALOG.equals(task)){
			pd.dismiss();
			pd = null;
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		activity.finish();
	}

}
