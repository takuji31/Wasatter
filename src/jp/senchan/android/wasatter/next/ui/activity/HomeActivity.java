package jp.senchan.android.wasatter.next.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import jp.senchan.android.wasatter.next.adapter.HomePagerAdapter;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.WasatterActivity;

public class HomeActivity extends WasatterActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		ViewPager pagerHome = (ViewPager) findViewById(R.id.pager_home);
		pagerHome.setAdapter(new HomePagerAdapter(getSupportFragmentManager()));
	}
}
