package jp.senchan.android.wasatter.next.ui.fragment;

import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import jp.senchan.android.wasatter.next.listener.OnAddAccountButtonClickListener;
import jp.senchan.android.wasatter.next.model.Account;
import jp.senchan.android.wasatter.next.model.dataobject.AccountData;
import jp.senchan.android.wasatter.next.ui.adapter.AccountListAdapter;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.WasatterFragment;
import jp.senchan.lib.ui.BaseFragment;

public class AccountListFragment extends WasatterFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO 自動生成されたメソッド・スタブ
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.account_list, null);
		ListView listAccountList = (ListView) v.findViewById(R.id.account_list);
		List<AccountData> accountList = new Account(getActivity()).getAccountList();
		listAccountList.setAdapter(new AccountListAdapter(getActivity(), accountList));
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.account_list, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    int id = item.getItemId();
	    switch (id) {
            case R.id.menu_add_account:
                //TODO アカウント追加画面
            	OnAddAccountButtonClickListener listener = (OnAddAccountButtonClickListener) getActivity();
            	listener.onAddAccountButtonClicked();
            break;
        }
	    return true;
	}

}
