package jp.senchan.android.wasatter.next.adapter;

import jp.senchan.android.wasatter.next.ui.fragment.TimelineFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HomePagerAdapter extends FragmentPagerAdapter {

	public HomePagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		return new TimelineFragment();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 5;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		//TODO ページタイトルの実装
		return "page" + position;
	}
}
