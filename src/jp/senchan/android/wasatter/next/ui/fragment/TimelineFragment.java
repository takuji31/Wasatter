package jp.senchan.android.wasatter.next.ui.fragment;

import java.util.ArrayList;

import com.androidquery.AQuery;

import android.app.Activity;
import android.os.Bundle;
import jp.senchan.android.wasatter.WasatterListFragment;
import jp.senchan.android.wasatter.next.model.api.WasatterStatus;

public class TimelineFragment extends WasatterListFragment {
	private AQuery mAquery;
	private ArrayList<WasatterStatus> mTimeline;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mAquery = new AQuery(getActivity(), getView());
		if (mTimeline == null) {
			//TODO ネットワーク処理
		}
	}
	
}
