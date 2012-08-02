package jp.senchan.android.wasatter.ui.oauth.twitter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class OAuthCallback extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		Uri uri = intent.getData();
		try {
			String token = uri.getQueryParameter("token");
			//XXX null判定のために呼ぶ
			token.length();
//			Setting.set(WassrAccount.TOKEN, token);
		}catch (NullPointerException e) {
			//ありえない
		}
//		if(SetupMain.currentPage == SetupMain.PAGE_WASSR_AUTH) {
//			SetupMain.currentPage++;
//			Intent goBackSetup = new Intent(getBaseContext(),SetupMain.class);
//			startActivity(goBackSetup);
//		}
		this.finish();
	}
}
