package jp.senchan.android.wasatter.app.fragment;

import java.util.ArrayList;
import java.util.Collections;

import twitter4j.AsyncTwitter;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.androidquery.AQuery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.WasatterListFragment;
import jp.senchan.android.wasatter.adapter.TimelineAdapter;
import jp.senchan.android.wasatter.app.ConfigActivity;
import jp.senchan.android.wasatter.app.PostActivity;
import jp.senchan.android.wasatter.client.TwitterAsyncClient;
import jp.senchan.android.wasatter.client.TwitterClient;
import jp.senchan.android.wasatter.client.WassrClient;
import jp.senchan.android.wasatter.model.api.WasatterStatus;
import jp.senchan.android.wasatter.next.listener.APICallback;
import jp.senchan.android.wasatter.utils.WasatterStatusComparator;

public class TimelineFragment extends WasatterListFragment implements OnScrollListener {
	
	private AQuery mAquery;
	private AsyncTwitter mAsyncTwitter;
	private TwitterAsyncClient mTwitterClient;
	private WassrClient mWassrClient;
	private APICallback<ArrayList<WasatterStatus>> mCallback =  new APICallback<ArrayList<WasatterStatus>>() {
		
		@Override
		public void callback(String url, ArrayList<WasatterStatus> result,
				int status) {
			loadingCount--;
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
	
	int mPage = 0;
	int loadingCount = 0;
	TimelineAdapter mAdapter;
	ArrayList<WasatterStatus> mTimeline;

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		mAquery = new AQuery(getActivity(), getView());
		if (mTimeline == null) {
			loadTimeline();
		}
		getListView().setOnScrollListener(this);
	}
	
	@Override
	public void onDestroy() {
		cancelLoading();
		super.onDestroy();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.timeline, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.menu_post) {
			Intent intent = new Intent(getActivity(), PostActivity.class);
			startActivity(intent);
			return true;
		}
		if (id == R.id.menu_reload) {
			reloadTimeline();
			return true;
		}
		if (id == R.id.menu_config) {
			Intent intent = new Intent(getActivity(), ConfigActivity.class);
			startActivity(intent);
			return true;
		}
		if (id == R.id.menu_version_info) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (loadingCount == 0 && firstVisibleItem + visibleItemCount == totalItemCount) {
			loadTimeline();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}
	
	public void initializeAdapter(ArrayList<WasatterStatus> list) {
		mTimeline = list;
		mAdapter = new TimelineAdapter(getActivity(), list);
		setListAdapter(mAdapter);
	}
	
	private void cancelLoading() {
		if (mWassrClient != null) {
			mWassrClient.cancel();
		}
		if (mAsyncTwitter != null) {
			mAsyncTwitter.shutdown();
		}
		loadingCount = 0;
	}
	
	private void reloadTimeline() {
		cancelLoading();
		mTimeline = null;
		mAdapter = null;
		mPage = 0;
		setListAdapter(null);
		loadTimeline();
	}
	
	private void loadTimeline() {
		final Wasatter app = app();
		mPage++;
		if (app.canLoadWassrTimeline()) {
			loadingCount++;
			mWassrClient = new WassrClient(mAquery, app.getWassrId(), app.getWassrPass());
			mWassrClient.friendTimeline(mPage, mCallback);
		}
		if (app.canLoadTwitterTimeline()) {
			loadingCount++;
			mTwitterClient = new TwitterAsyncClient(app);
			mAsyncTwitter = mTwitterClient.friendTimeline(mPage, mCallback);
		}
	}

}
