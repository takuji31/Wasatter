package jp.senchan.android.wasatter.next.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import jp.senchan.android.wasatter.next.listener.OnPageTypeSelectListener;
import jp.senchan.android.wasatter.next.ui.fragment.ConfigPageFragment;
import jp.senchan.android.wasatter.next.ui.fragment.PageTypesFragment;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.WasatterActivity;

public class ConfigHomeActivity extends WasatterActivity implements OnPageTypeSelectListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.config_home);

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.container, new PageTypesFragment());
		ft.commit();
	}

	@Override
	public void onPageTypeSelect(String type) {
		// TODO Auto-generated method stub
		Fragment f = new ConfigPageFragment();
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.container, f);
		ft.addToBackStack(null);
		ft.commit();
	}
}
