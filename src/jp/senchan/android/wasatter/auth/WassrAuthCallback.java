package jp.senchan.android.wasatter.auth;

import jp.senchan.android.wasatter.setting.Setting;
import jp.senchan.android.wasatter.setting.WassrAccount;
import jp.senchan.android.wasatter.setup.SetupMain;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class WassrAuthCallback extends Activity{
	
	public static String id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		Uri uri = intent.getData();
		try {
			String token = uri.getQueryParameter("token");
			//XXX null判定のために呼ぶ
			token.length();
			WassrAuthCallback.id.length();
			Setting.set(WassrAccount.ID, WassrAuthCallback.id);
			Setting.set(WassrAccount.TOKEN, token);
		}catch (NullPointerException e) {
			//ありえない
		}
		if(SetupMain.currentPage == SetupMain.PAGE_WASSR_AUTH) {
			SetupMain.currentPage++;
			Intent goBackSetup = new Intent(getBaseContext(),SetupMain.class);
			startActivity(goBackSetup);
		}
		this.finish();
	}
}
