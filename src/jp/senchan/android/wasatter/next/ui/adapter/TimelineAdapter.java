package jp.senchan.android.wasatter.next.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.next.model.api.WasatterStatus;
import jp.senchan.lib.view.ArrayListAdapter;

public class TimelineAdapter extends ArrayListAdapter<WasatterStatus> {

	public TimelineAdapter(Context context, ArrayList<WasatterStatus> list) {
		super(context, list);
	}

	@Override
	public int getViewLayoutId(int position) {
		// TODO Auto-generated method stub
		return R.layout.menu_listitem;
	}

	@Override
	public View createView(int position, WasatterStatus item, View v) {
		// TODO Auto-generated method stub
		return null;
	}

}
