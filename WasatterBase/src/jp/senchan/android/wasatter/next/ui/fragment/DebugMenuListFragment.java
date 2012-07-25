package jp.senchan.android.wasatter.next.ui.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.WasatterListFragment;

public class DebugMenuListFragment extends WasatterListFragment {
	
	private String[] mClasses;
	private String[] mLabels;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Resources res = activity().getResources();
		mLabels = res.getStringArray(R.array.debug_menu_label);
		mClasses = res.getStringArray(R.array.debug_menu_class);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity(), android.R.layout.simple_list_item_1, mLabels);
		setListAdapter(adapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		if (mClasses.length <= position) {
			Log.d("Debug", "Menu index is over");
			return;
		} else {
			String klass = mClasses[position];
			StringBuilder sb = new StringBuilder(activity().getPackageName());
			try {
				Class<?> c = Class.forName(sb.append(klass).toString());
				Intent intent = new Intent(activity(), c);
				startActivity(intent);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
