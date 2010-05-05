package jp.senchan.android.wasatter2.activity;

import java.util.HashMap;

import jp.senchan.android.wasatter2.Setting;
import jp.senchan.android.wasatter2.util.XAuth;
import android.app.AlertDialog;
import android.os.Bundle;


public class XAuthLogin extends TimelineActivity {

	public RequestThread requestThread;

	class RequestThread extends Thread{
		@Override
		public void run() {
			XAuth auth = new XAuth(Setting.getTwitterId(), Setting.getTwitterPass());
			final HashMap<String,String> result = auth.request();
			handler.post(new Runnable() {

				@Override
				public void run() {
					AlertDialog.Builder adb = new AlertDialog.Builder(XAuthLogin.this);
					adb.setTitle(result.get("oauth_token"));
					adb.setMessage(result.get("oauth_token_secret"));
					adb.setPositiveButton("OK", null);
					adb.show();
				}
			});
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		new RequestThread().start();
	}


	@Override
	public void reload() {
		// TODO 自動生成されたメソッド・スタブ

	}



}
