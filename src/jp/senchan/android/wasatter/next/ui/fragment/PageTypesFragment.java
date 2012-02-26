package jp.senchan.android.wasatter.next.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.next.listener.OnPageTypeSelectListener;
import jp.senchan.lib.ui.BaseListFragment;

public class PageTypesFragment extends BaseListFragment{

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Wasatter app = (Wasatter) app();
		setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, app.pageTypeNames));
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Wasatter app = (Wasatter) app();
		String type = app.pageTypes.get(position);
		((OnPageTypeSelectListener)(getActivity())).onPageTypeSelect(type);
	}

}
