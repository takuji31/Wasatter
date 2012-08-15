package jp.senchan.android.wasatter.app;

import java.util.ArrayList;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import jp.senchan.android.wasatter.BundleKey;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.WasatterActivity;
import jp.senchan.android.wasatter.app.fragment.TimelineFragment;
import jp.senchan.android.wasatter.utils.ServiceCodeUtil;
import jp.senchan.android.wasatter.view.SlideMenu;
import jp.senchan.android.wasatter.view.SlideMenuAdapter;
import jp.senchan.android.wasatter.view.SlideMenuItem;

public class HomeActivity extends WasatterActivity implements
		OnItemClickListener {

	private SlideMenu mMenu;
	private ArrayList<SlideMenuItem> mMenuItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		setSupportProgressBarIndeterminateVisibility(false);
		mMenu = new SlideMenu(this);

		mMenuItems = new ArrayList<SlideMenuItem>();

		Resources res = getResources();
		String[] titles = res.getStringArray(R.array.slidemenu_title);
		String[] icons = res.getStringArray(R.array.slidemenu_icon);
		int[] ids = res.getIntArray(R.array.slidemenu_id);

		for (int i = 0; i < titles.length; i++) {
			SlideMenuItem item = new SlideMenuItem();
			item.label = titles[i];
			item.icon = res.getIdentifier(icons[i], "drawable",
					getPackageName());
			item.id = ids[i];
			mMenuItems.add(item);
		}

		SlideMenuAdapter adapter = new SlideMenuAdapter(this, mMenuItems);
		mMenu.setAdapter(adapter);
		mMenu.setOnItemClickListener(this);
		mMenu.checkEnabled();

		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);

		// タイムライン表示
		FragmentManager fm = getSupportFragmentManager();
		TimelineFragment fragment = (TimelineFragment) fm
				.findFragmentById(R.id.container);
		if (fragment == null) {
			fragment = (TimelineFragment) Fragment.instantiate(this,
					TimelineFragment.class.getName(), null);
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && mMenu.isMenuShown()) {
			mMenu.hide();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		SlideMenuItem item = mMenuItems.get(pos);
		FragmentManager fm = getSupportFragmentManager();
		Fragment oldFragment = fm.findFragmentById(R.id.container);
		Bundle args = new Bundle();
		String fragmentClass = TimelineFragment.class.getName();
		if (item.id == ServiceCodeUtil.resIdToId(this, R.integer.menu_id_profile)) {
			args.putInt(BundleKey.MODE, TimelineFragment.MODE_USER_TIMELINE);
		} else if (item.id == ServiceCodeUtil.resIdToId(this, R.integer.menu_id_timeline)) {
			args.putInt(BundleKey.MODE, TimelineFragment.MODE_TIMELINE);
		} else if (item.id == ServiceCodeUtil.resIdToId(this, R.integer.menu_id_reply)) {
			args.putInt(BundleKey.MODE, TimelineFragment.MODE_MENSION);
		} else if (item.id == ServiceCodeUtil.resIdToId(this, R.integer.menu_id_odai)) {
			args.putInt(BundleKey.MODE, TimelineFragment.MODE_ODAI);
		} else if (item.id == ServiceCodeUtil.resIdToId(this, R.integer.menu_id_channel)) {
			args.putInt(BundleKey.MODE, TimelineFragment.MODE_CHANNEL_LIST);
		}

		Fragment newFragment = Fragment.instantiate(this, fragmentClass, args);
		if (oldFragment != null && newFragment != null) {
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.container, newFragment);
			ft.addToBackStack(null);
			ft.commit();
		}
		mMenu.hide();
	}

}
