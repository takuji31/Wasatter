package jp.senchan.android.wasatter.app.fragment;

import java.util.ArrayList;

import com.androidquery.AQuery;
import android.os.Bundle;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.WasatterListFragment;
import jp.senchan.android.wasatter.adapter.TimelineAdapter;
import jp.senchan.android.wasatter.client.WassrClient;
import jp.senchan.android.wasatter.model.api.WasatterStatus;
import jp.senchan.android.wasatter.next.listener.APICallback;

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
			
			new WassrClient(mAquery, app.getWassrId(), app.getWassrPass()).friendTimeline(1, new APICallback<ArrayList<WasatterStatus>>() {
				
				@Override
				public void callback(String url, ArrayList<WasatterStatus> result,
						int status) {
					if (status != 200) {
						app.toast(R.string.message_something_wrong).show();
					}
					mAdapter = new TimelineAdapter(getActivity(), result);
					setListAdapter(mAdapter);
				}
			});
		}
	}
	
}
