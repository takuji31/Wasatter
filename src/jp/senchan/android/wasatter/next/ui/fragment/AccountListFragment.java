package jp.senchan.android.wasatter.next.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import jp.senchan.android.wasatter.next.ui.adapter.WasadroidBaseAdapter;
import jp.senchan.android.wasatter3.R;
import jp.senchan.lib.ui.BaseFragment;

public class AccountListFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.account_list, null);
		ListView listAccountList = (ListView) v.findViewById(R.id.account_list);
		listAccountList.setAdapter(new WasadroidBaseAdapter(getActivity(), 0, null));
		return v;
	}
}
