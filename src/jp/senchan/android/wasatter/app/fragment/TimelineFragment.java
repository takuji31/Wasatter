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
import android.support.v4.app.Fragment;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import jp.senchan.android.wasatter.R;
import jp.senchan.android.wasatter.Wasatter;
import jp.senchan.android.wasatter.WasatterActivity;
import jp.senchan.android.wasatter.WasatterListFragment;
import jp.senchan.android.wasatter.adapter.TimelineAdapter;
import jp.senchan.android.wasatter.app.ConfigActivity;
import jp.senchan.android.wasatter.app.PostActivity;
import jp.senchan.android.wasatter.client.TwitterAsyncClient;
import jp.senchan.android.wasatter.client.WassrClient;
import jp.senchan.android.wasatter.model.api.APICallback;
import jp.senchan.android.wasatter.model.api.WasatterStatus;
import jp.senchan.android.wasatter.utils.WasatterStatusComparator;

public class TimelineFragment extends WasatterListFragment implements OnScrollListener {
	
	private static final String sDialogTag = "VersionInfoDialogFragment";
	private static final String sStateKeyTimeline = "timeline";
	
	private AQuery mAquery;
	private AsyncTwitter mAsyncTwitter;
	private TwitterAsyncClient mTwitterClient;
	private WassrClient mWassrClient;
	private APICallback<ArrayList<WasatterStatus>> mCallback =  new APICallback<ArrayList<WasatterStatus>>() {
		
		@Override
		protected void callback(ArrayList<WasatterStatus> result, int status) {
			WasatterActivity activity = activity();
			//getActivityの結果がnullなら既に親のActivityは破棄されてます
			if (activity != null) {
				mLoadingCount--;
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
				activity().invalidateOptionsMenu();
			}
		}
	};
	
	int mPage = 0;
	int mLoadingCount = 0;
	TimelineAdapter mAdapter;
	ArrayList<WasatterStatus> mTimeline;

	
	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		mAquery = new AQuery(getActivity(), getView());
		
		if (savedInstanceState != null) {
			mTimeline = (ArrayList<WasatterStatus>) savedInstanceState.getSerializable(sStateKeyTimeline);
		}
		if (mTimeline == null) {
			loadTimeline();
		} else {
			mAdapter = new TimelineAdapter(getActivity(), mTimeline);
			setListAdapter(mAdapter);
		}
		getListView().setOnScrollListener(this);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(sStateKeyTimeline, mTimeline);
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
	public void onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.menu_reload);
		item.setEnabled(mLoadingCount == 0);
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
			VersionInfoDialogFragment fragment = (VersionInfoDialogFragment) Fragment.instantiate(getActivity(), VersionInfoDialogFragment.class.getName());
			fragment.show(getFragmentManager(), sDialogTag);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mLoadingCount == 0 && firstVisibleItem + visibleItemCount == totalItemCount) {
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
		mCallback.cancel();
		mLoadingCount = 0;
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
		//FIXME 今の実装だとリロード連打したりするとタイムラインの内容が被る
		final Wasatter app = app();
		mPage++;
		if (app.canLoadWassrTimeline()) {
			mLoadingCount++;
			mWassrClient = new WassrClient(mAquery, app.getWassrId(), app.getWassrPass());
			mWassrClient.friendTimeline(mPage, mCallback);
		}
		if (app.canLoadTwitterTimeline()) {
			mLoadingCount++;
			mTwitterClient = new TwitterAsyncClient(app);
			mAsyncTwitter = mTwitterClient.friendTimeline(mPage, mCallback);
		}
		activity().invalidateOptionsMenu();
	}

}
