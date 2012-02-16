package jp.senchan.android.wasatter;

import jp.senchan.android.wasatter.ui.TimelineActivity;
import android.content.Intent;
import android.os.Bundle;

public class ActivityMain extends WasatterActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(getApplicationContext(), TimelineActivity.class);
		startActivity(intent);
		finish();
	}

}
