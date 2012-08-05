package jp.senchan.android.wasatter.app;

import java.util.ArrayList;
import java.util.Arrays;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
		//TODO 後でちゃんと書く
		SlideMenuItem[] items = new SlideMenuItem[5];
		// fill the menu-items here
		items[0] = new SlideMenuItem();
		items[1] = new SlideMenuItem();
		items[2] = new SlideMenuItem();
		items[3] = new SlideMenuItem();
		items[4] = new SlideMenuItem();
		items[0].label = "Profile";
		items[0].icon = R.drawable.ic_action_profile;
		items[1].label = "Timeline";
		items[1].icon = R.drawable.ic_action_timeline;
		items[2].label = "Reply";
		items[2].icon = R.drawable.ic_action_reply;
		items[3].label = "Odai";
		items[3].icon = R.drawable.ic_action_odai;
		items[4].label = "Channel";
		items[4].icon = R.drawable.ic_action_channel;
		ArrayList<SlideMenuItem> item = new ArrayList<SlideMenuItem>(Arrays.asList(items));
		SlideMenuAdapter adapter = new SlideMenuAdapter(this, item);
		mMenu.setAdapter(adapter);
		mMenu.checkEnabled();
		
		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);
		
		//タイムライン表示
		TimelineFragment fragment = (TimelineFragment) Fragment.instantiate(this, TimelineFragment.class.getName(), null);
		getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
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
