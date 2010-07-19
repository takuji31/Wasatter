package jp.senchan.android.wasatter.task;

import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.activity.Update;
import jp.senchan.android.wasatter.client.Twitter;
import jp.senchan.android.wasatter.client.Wassr;
import jp.senchan.android.wasatter.codes.ServiceCode;
import jp.senchan.android.wasatter.util.ResultCode;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.text.SpannableStringBuilder;
import android.widget.Button;

public class UpdateStatus extends AsyncTask<String, String, Boolean>{

	private boolean postWassr;
	private boolean postTwitter;
	private boolean resultWassr;
	private boolean resultTwitter;
	private int resultCodeWassr;
	private int resultCodeTwitter;
	private Update activity;
	private ProgressDialog pd;
	
	private static final String MAKE_DIALOG = "make_dialog";
	private static final String SHOW_DIALOG = "show_dialog";
	private static final String CLOSE_DIALOG = "close_dialog";
	private static final String HANDLE_RESPONSE = "handle_response";
	
	public UpdateStatus(Update activity,boolean postWassr,boolean postTwitter) {
		//何を投稿するのか決定する。
		this.postWassr = postWassr;
		this.postTwitter = postTwitter;
		this.activity = activity;
		//そもそも投稿しないものは成功したってことにしておく
		resultWassr = !postWassr;
		resultTwitter = !postTwitter;
		resultCodeWassr = 0;
		resultCodeTwitter = 0;
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		//投稿する文章
		String status = params[0];
		publishProgress(MAKE_DIALOG);
		publishProgress(SHOW_DIALOG);
		//Wassrにポスト
		if(postWassr){
			int resCode = Wassr.updateTimeLine(status, null, null);
			publishProgress(HANDLE_RESPONSE,String.valueOf(resCode),String.valueOf(ServiceCode.WASSR));
		}
		
		if(postTwitter){
			int resCode = Twitter.updateTimeline(status);
			publishProgress(HANDLE_RESPONSE,String.valueOf(resCode),String.valueOf(ServiceCode.TWITTER));
		}
		publishProgress(CLOSE_DIALOG);
		return true;
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		String task = values[0];
		if(MAKE_DIALOG.equals(task)){
			pd = new ProgressDialog(activity);
			//TODO 投稿キャンセルできるようにすべきだが、ぶっちゃけあんまり効果ない。
			pd.setCancelable(false);
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
		if(HANDLE_RESPONSE.equals(task)){
			handleResponse(Integer.parseInt(values[1]),Integer.parseInt(values[2]));
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if(resultTwitter && resultWassr){
			//投稿が全部成功してたらウィンドウ閉じる
			activity.finish();
		}else{
			//どっちか一つでも失敗してたら原因とサービスを探る
			AlertDialog.Builder ab = new AlertDialog.Builder(this.activity);
			SpannableStringBuilder sb = new SpannableStringBuilder();
			ab.setTitle("投稿に失敗しました。");
			sb.append("原因は以下の通りです。\n\n");
			if(!resultWassr){
				//Wassrの投稿が失敗したら
				sb.append("Wassr：");
				switch(resultCodeWassr){
				case 401:
					sb.append("401認証エラー\n(ID、パスワードが間違っている可能性があります)\n\n");
				case 503:
					sb.append("503エラー\n(Wassrが高負荷になっている可能性があります)\n\n");
				case 502:
					sb.append("502エラー\n(Wassrが高負荷になっている可能性があります)\n\n");
				default:
					sb.append("ネットワークエラー\n(電波状態の良いところでリトライしてください)\n\n");
				break;
				}
			}
			if(!resultTwitter){
				//Wassrの投稿が失敗したら
				sb.append("Twitter：");
				switch(resultCodeTwitter){
				case 401:
					//TODO たぶんない、あるとしたらトークンの有効期限切れ
					sb.append("401認証エラー\n(ID、パスワードが間違っている可能性があります)");
				case 503:
					sb.append("503エラー\n(APIの制限回数を超えました)");
				case 502:
					sb.append("502エラー\n(Twitterが高負荷になっている可能性があります)");
				default:
					sb.append("ネットワークエラー\n(電波状態の良いところでリトライしてください)");
				break;
				}
			}
			//メッセージを設定
			ab.setMessage(sb.toString());
			ab.setPositiveButton("OK", null);
			ab.show();
		}
	}
	

	/**
	 * HTTPステータスを見て指定されたサービスへの投稿がうまくいったかを調べるメソッド
	 * @param errorCode HTTPステータス
	 * @param serviceCode 指定されたサービスのコード
	 */
	private void handleResponse(int errorCode,int serviceCode){
		switch(serviceCode){
		
		case ServiceCode.WASSR:
			resultWassr = errorCode == ResultCode.HTTP_SUCCESS;
			resultCodeWassr = errorCode;
			break;
		case ServiceCode.TWITTER:
			resultTwitter = errorCode == ResultCode.HTTP_SUCCESS;
			resultCodeTwitter = errorCode;
			break;
		}
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
		try{
			//キャンセルされたら、ボタン押せるように変更
			Button postButton = (Button) this.activity.findViewById(R.id.post_button);
			postButton.setClickable(true);
		}catch (NullPointerException e) {
			//特に何もしない
		}
	}

}
