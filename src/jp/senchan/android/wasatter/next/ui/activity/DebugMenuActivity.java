package jp.senchan.android.wasatter.next.ui.activity;

import jp.senchan.android.wasatter.ui.TimelineActivity;
import jp.senchan.android.wasatter3.R;
import jp.senchan.lib.ui.BaseActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DebugMenuActivity extends BaseActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_menu);
        Button accountList = (Button) findViewById(R.id.button_account_list);
        accountList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DebugMenuActivity.this, AccountListActivity.class);
				startActivity(intent);
			}
		});
        Button home = (Button) findViewById(R.id.button_home);
        home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DebugMenuActivity.this, HomeActivity.class);
				startActivity(intent);
			}
		});
        Button configHome = (Button) findViewById(R.id.button_config_home);
        configHome.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DebugMenuActivity.this, ConfigHomeActivity.class);
				startActivity(intent);
			}
		});
        Button gotoOriginal = (Button) findViewById(R.id.button_goto_original);
        gotoOriginal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DebugMenuActivity.this, TimelineActivity.class);
				startActivity(intent);
			}
		});
    }
}