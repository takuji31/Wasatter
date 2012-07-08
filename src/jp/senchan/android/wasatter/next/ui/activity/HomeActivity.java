package jp.senchan.android.wasatter.next.ui.activity;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.WasatterActivity;
import jp.senchan.android.wasatter.next.ui.view.SlideMenu;

public class HomeActivity extends WasatterActivity {
	
	private SlideMenu mMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		mMenu = new SlideMenu(this);
		mMenu.checkEnabled();
		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (android.R.id.home == item.getItemId()) {
			mMenu.show();
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
