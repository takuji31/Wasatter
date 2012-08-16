package jp.senchan.android.wasatter;

import jp.senchan.android.wasatter.app.HomeActivity;
import jp.senchan.android.wasatter.next.ui.activity.DebugMenuActivity;
import jp.senchan.android.wasatter2.BuildConfig;
import android.content.Intent;
import android.os.Bundle;

public class ActivityMain extends WasatterActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Wasatter app = app();
		Intent intent = new Intent(app, HomeActivity.class);
		/*if(BuildConfig.DEBUG) {
			intent = new Intent(app, DebugMenuActivity.class);
		}*/
		startActivity(intent);
		finish();
	}

}
