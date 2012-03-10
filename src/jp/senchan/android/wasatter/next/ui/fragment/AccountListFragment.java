package jp.senchan.android.wasatter.next.ui.fragment;

import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import jp.senchan.android.wasatter.next.adapter.AccountListAdapter;
import jp.senchan.android.wasatter.next.listener.OnAddAccountButtonClickListener;
import jp.senchan.android.wasatter.next.listener.OnServiceSelectedListener;
import jp.senchan.android.wasatter.next.model.Account;
import jp.senchan.android.wasatter.next.model.dataobject.AccountData;
import jp.senchan.android.wasatter.next.ui.activity.AddAccountActivity;
import jp.senchan.android.wasatter.IntentCode;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.ResultCode;
import jp.senchan.android.wasatter.WasatterFragment;

public class AccountListFragment extends WasatterFragment implements OnServiceSelectedListener {

	ListView listAccountList;
	List<AccountData> accountList;
	Account model;

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
		listAccountList = (ListView) v.findViewById(R.id.account_list);
		model = new Account(app());
		loadAccountList();
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
	
	@Override
	public void onServiceSelected(int serviceId) {
		Intent intent = new Intent(getActivity(), AddAccountActivity.class);
		intent.putExtra("service", serviceId);
		startActivityForResult(intent, IntentCode.DUMMY);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
			case ResultCode.OK:
				loadAccountList();
			break;
			default:
			break;
		}
	}
	
	public void loadAccountList() {
		accountList = model.getAccountList();
		listAccountList.setAdapter(new AccountListAdapter(getActivity(), accountList));
	}

}
