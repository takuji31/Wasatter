package jp.senchan.android.wasatter;

import jp.senchan.android.wasatter.ui.TimelineActivity;
import jp.senchan.android.wasatter3.R;
import android.content.Intent;
import android.os.Bundle;

public class ActivityMain extends WasatterActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Wasatter app = app();
		Intent intent = new Intent(app, TimelineActivity.class);
		if(app.isDebugMode()) {
			intent = new Intent(app, TimelineActivity.class);
		}
		startActivity(intent);
		finish();
	}

}
