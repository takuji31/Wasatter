package jp.senchan.android.wasatter.next.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import jp.senchan.android.wasatter.WasatterActivity;
import jp.senchan.android.wasatter.next.ui.fragment.AccountListFragment;

public class AccountListActivity extends WasatterActivity {
	private static final String TAG_LIST = "account_list";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(android.R.id.content, new AccountListFragment(), TAG_LIST);
		ft.commit();
	}
}
