package jp.senchan.android.wasatter.next.ui.fragment;

import java.util.ArrayList;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

import android.os.Bundle;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.WasatterListFragment;
import jp.senchan.android.wasatter.next.client.NewWassrClient;
import jp.senchan.android.wasatter.next.listener.APICallback;
import jp.senchan.android.wasatter.next.model.api.WasatterStatus;
import jp.senchan.android.wasatter.next.model.api.WassrStatus;
import jp.senchan.android.wasatter.next.ui.adapter.TimelineAdapter;

public class TimelineFragment extends WasatterListFragment {
	private AQuery mAquery;
	private ArrayList<WasatterStatus> mTimeline;
	TimelineAdapter mAdapter;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mAquery = new AQuery(getActivity(), getView());
		if (mTimeline == null) {
			final Wasatter app = app();
			
			new NewWassrClient(app.getWassrId(), app.getWassrPass()).friendTimeline(1, mAquery, new APICallback<ArrayList<WasatterStatus>>() {
				
				@Override
				public void callback(String url, ArrayList<WasatterStatus> result,
						AjaxStatus status) {
					if (status.getCode() != 200) {
						app.toast(R.string.message_something_wrong).show();
					}
					mAdapter = new TimelineAdapter(getActivity(), result);
					setListAdapter(mAdapter);
				}
			});
		}
	}
	
}
