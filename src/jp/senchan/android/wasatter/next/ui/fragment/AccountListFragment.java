package jp.senchan.android.wasatter.next.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import jp.senchan.android.wasatter.next.ui.activity.AddAccountActivity;
import jp.senchan.android.wasatter.IntentCode;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.ResultCode;
import jp.senchan.android.wasatter.WasatterListFragment;

public class AccountListFragment extends WasatterListFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO 自動生成されたメソッド・スタブ
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(activity(), android.R.layout.simple_list_item_1, activity().getResources().getStringArray(R.array.service_names)));
    }

	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(getActivity(), AddAccountActivity.class);
		intent.putExtra("service", position);
		startActivityForResult(intent, IntentCode.DUMMY);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
			case ResultCode.OK:
			break;
			default:
			break;
		}
	}
	
}
