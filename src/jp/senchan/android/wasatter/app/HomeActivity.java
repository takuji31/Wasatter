package jp.senchan.android.wasatter.app;

import java.util.ArrayList;
import java.util.Arrays;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.WasatterActivity;
import jp.senchan.android.wasatter.app.fragment.TimelineFragment;
import jp.senchan.android.wasatter.view.SlideMenu;
import jp.senchan.android.wasatter.view.SlideMenuAdapter;
import jp.senchan.android.wasatter.view.SlideMenuItem;

public class HomeActivity extends WasatterActivity {

	private SlideMenu mMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		mMenu = new SlideMenu(this);
		
		ArrayList<SlideMenuItem> items = new ArrayList<SlideMenuItem>();

		Resources res = getResources();
		String[] titles = res.getStringArray(R.array.slidemenu_title);
		String[] icons = res.getStringArray(R.array.slidemenu_icon);
		int[] ids = res.getIntArray(R.array.slidemenu_id);
		
		for (int i = 0; i < titles.length; i++) {
			SlideMenuItem item = new SlideMenuItem();
			item.label = titles[i];
			item.icon = res.getIdentifier(icons[i], "drawable", getPackageName());
			item.id = ids[i];
			items.add(item);
		}
		
		SlideMenuAdapter adapter = new SlideMenuAdapter(this, items);
		mMenu.setAdapter(adapter);
		mMenu.checkEnabled();
		
		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);
		
		//タイムライン表示
		FragmentManager fm = getSupportFragmentManager();
		TimelineFragment fragment = (TimelineFragment) fm.findFragmentById(R.id.container);
		if (fragment == null) {
			fragment = (TimelineFragment) Fragment.instantiate(this, TimelineFragment.class.getName(), null);
			fm.beginTransaction().replace(R.id.container, fragment).commit();
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (android.R.id.home == item.getItemId()) {
			mMenu.show();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

}
