package jp.senchan.android.wasatter.next.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.next.listener.OnAddAccountButtonClickListener;
import jp.senchan.android.wasatter.next.listener.OnServiceSelectedListener;
import jp.senchan.android.wasatter.next.ui.fragment.AccountListFragment;
import jp.senchan.android.wasatter.next.ui.fragment.dialog.AccountTypeSelectDialog;
import jp.senchan.lib.ui.BaseActivity;

public class AccountListActivity extends BaseActivity implements OnAddAccountButtonClickListener, OnServiceSelectedListener {
	private static final String TAG_LIST = "account_list";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.container, new AccountListFragment(), TAG_LIST);
		ft.commit();
	}
	
	@Override
	public void onAddAccountButtonClicked() {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		new AccountTypeSelectDialog().show(ft, "dialog");
	}

	@Override
	public void onServiceSelected(int serviceId) {
		AccountListFragment f = (AccountListFragment) getSupportFragmentManager().findFragmentByTag(TAG_LIST);
		f.onServiceSelected(serviceId);
	}
	
}
