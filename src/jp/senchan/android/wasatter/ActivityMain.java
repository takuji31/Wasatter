package jp.senchan.android.wasatter;

import jp.senchan.android.wasatter.next.ui.activity.DebugMenuActivity;
import jp.senchan.android.wasatter.ui.TimelineActivity;
import android.content.Intent;
import android.os.Bundle;

public class ActivityMain extends WasatterActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Wasatter app = app();
		Intent intent = new Intent(app, TimelineActivity.class);
		if(BuildConfig.DEBUG) {
			intent = new Intent(app, DebugMenuActivity.class);
		}
		startActivity(intent);
		finish();
	}

}
