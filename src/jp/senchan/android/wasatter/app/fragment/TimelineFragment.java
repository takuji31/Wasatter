package jp.senchan.android.wasatter.app.fragment;

import java.util.ArrayList;
import java.util.Collections;

import twitter4j.AsyncTwitter;

import com.androidquery.AQuery;
import android.os.Bundle;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.WasatterListFragment;
import jp.senchan.android.wasatter.adapter.TimelineAdapter;
import jp.senchan.android.wasatter.client.TwitterAsyncClient;
import jp.senchan.android.wasatter.client.TwitterClient;
import jp.senchan.android.wasatter.client.WassrClient;
import jp.senchan.android.wasatter.model.api.WasatterStatus;
import jp.senchan.android.wasatter.next.listener.APICallback;
import jp.senchan.android.wasatter.utils.WasatterStatusComparator;

public class TimelineFragment extends WasatterListFragment {
	private AQuery mAquery;
	private AsyncTwitter mAsyncTwitter;
	private TwitterAsyncClient mTwitterClient;
	private WassrClient mWassrClient;
	private APICallback<ArrayList<WasatterStatus>> mCallback =  new APICallback<ArrayList<WasatterStatus>>() {
		
		@Override
		public void callback(String url, ArrayList<WasatterStatus> result,
				int status) {
			if (status != 200) {
				app().toast(R.string.message_something_wrong).show();
			}
			if (mTimeline == null) {
				initializeAdapter(result);
			} else {
				mTimeline.addAll(result);
				Collections.sort(mTimeline, new WasatterStatusComparator());
				mAdapter.notifyDataSetChanged();
			}
		}
	};
	
	TimelineAdapter mAdapter;
	ArrayList<WasatterStatus> mTimeline;

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mAquery = new AQuery(getActivity(), getView());
		if (mTimeline == null) {
			final Wasatter app = app();
			
			if (app.canLoadWassrTimeline()) {
				mWassrClient = new WassrClient(mAquery, app.getWassrId(), app.getWassrPass());
				mWassrClient.friendTimeline(1, mCallback);
			}
			if (app.canLoadTwitterTimeline()) {
				mTwitterClient = new TwitterAsyncClient(app);
				mAsyncTwitter = mTwitterClient.friendTimeline(1, mCallback);
			}
			
		}
	}
	
	@Override
	public void onDestroy() {
		if (mWassrClient != null) {
			mWassrClient.cancel();
		}
		if (mAsyncTwitter != null) {
			mAsyncTwitter.shutdown();
		}
		super.onDestroy();
	}
	
	public void initializeAdapter(ArrayList<WasatterStatus> list) {
		mTimeline = list;
		mAdapter = new TimelineAdapter(getActivity(), list);
		setListAdapter(mAdapter);
	}
	
}
